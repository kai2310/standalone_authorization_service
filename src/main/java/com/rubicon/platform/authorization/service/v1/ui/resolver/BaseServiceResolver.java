package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.api.BaseApiObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeEvent;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeListener;
import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.exception.NotFoundException;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.exception.ValidationException;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.*;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.rubicon.platform.authorization.service.utils.Constants.*;

abstract public class BaseServiceResolver<A, D extends BaseApiObject<Long>>
{
    protected Logger logger = LoggerFactory.getLogger(BaseServiceResolver.class);

    private DefaultObjectTranslator<A, D> translator;
    private ServiceExceptionMappingDecorator persistenceOperations;

    public PagedResponse<A> buildPagedResponse(QueryResult<D> queryResult, Integer maxItemsPerPage)
    {
        return buildPagedResponse(queryResult, maxItemsPerPage, new TranslationContext());
    }

    public PagedResponse<A> buildPagedResponse(QueryResult<D> queryResult, Integer maxItemsPerPage,
                                               TranslationContext context)
    {
        Page page = new Page(queryResult.getStart(), maxItemsPerPage, queryResult.getTotalCount(),
                queryResult.getResponseCount());


        List<A> apiContentList = translator.convertPersistent(queryResult.getItems(), context);

        PagedResponse<A> pagedResponse = new PagedResponse<>();
        pagedResponse.setPage(page);
        pagedResponse.setContent(apiContentList);

        return pagedResponse;
    }

    public Node buildQueryExpression(String query)
    {
        try
        {
            return new RSQLParser().parse(query);
        }
        catch (RSQLParserException ex)
        {
            logger.warn("Invalid query: {}", query);

            throw new BadRequestException("Please provide a valid query.");
        }
    }

    protected TranslationContext buildRoleTypePermissionTranslationContext(RoleTypePermission roleTypePermission)
    {
        TranslationContext translationContext = new TranslationContext();

        translationContext.putContextItem(TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION, roleTypePermission);

        return translationContext;
    }

    protected TranslationContext buildEditableFeatureTranslationContext(boolean isEditable)
    {
        TranslationContext translationContext = new TranslationContext();
        translationContext.putContextItem(TRANSLATE_CONTEXT_IS_EDITABLE, isEditable);

        return translationContext;
    }

    public void assertNotNull(Object object, String fieldName)
    {
        if (null == object)
        {
            throw new ValidationException(String.format("%s is required", fieldName));
        }
    }

    // assertItemListFound
    public <O> void assertListHasOneItem(List<O> itemList, String item, Long itemId)
    {
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != 1)
        {
            throw new NotFoundException(String.format("Cannot find %s id %d", item, itemId));
        }
    }

    public void assertRoleTypeEditable(RoleTypePermission roleTypePermission, RoleTypeEnum roleTypeEnum,
                                       String entityName)
    {
        boolean isEditable = roleTypePermission.isRoleTypeEditable(roleTypeEnum);
        if (!isEditable)
        {
            throw new UnauthorizedException(
                    String.format("You are attempting to create/modify a %s for a role type you do not have access to.",
                            entityName));
        }
    }

    public Map<String, Integer> translatePagingToHyperion(Integer page, Integer size)
    {
        Map<String, Integer> pagingMap = new HashMap();


        Integer start = (page <= 1)
                        ? 1
                        : ((page - 1) * size) + 1;

        pagingMap.put(HYPERION_PAGING_KEY_LIMIT, size);
        pagingMap.put(HYPERION_PAGING_KEY_START, start);


        return pagingMap;
    }

    // Potentially abstract this out, as its also used in the Operations Resolver
    protected void processEntityChange(PersistenceContext context)
    {
        if (context.getEntityPlugin().hasEntityChangeListeners())
        {
            // get cache change listener to update cache in this case
            List entityChangeListeners = context.getEntityPlugin().getEntityChangeListeners();
            Iterator listenerIterator = entityChangeListeners.iterator();

            while (listenerIterator.hasNext())
            {
                EntityChangeListener entityChangeListener = (EntityChangeListener) listenerIterator.next();
                Iterator eventIterator = context.getEntityChangeEvents().iterator();

                while (eventIterator.hasNext())
                {
                    EntityChangeEvent event = (EntityChangeEvent) eventIterator.next();
                    entityChangeListener.processEntityChange(event);
                }
            }
        }
    }

    // validate ui operation edit request
    public void validateEditOperation(EditBaseOperationRequest request)
    {
        // validate the request
        assertNotNull(request.getId(), "id");
        assertNotNull(request.getAction(), "action");
        assertNotNull(request.getOperation(), "operation");
        assertNotNull(request.getOperation().getService(), "operation service");
        assertNotNull(request.getOperation().getResource(), "operation resource");
        assertNotNull(request.getOperation().getAction(), "operation action");
    }

    // determine if provided ui operation is in the data operation list
    public int indexOfOperation(List<com.rubicon.platform.authorization.model.data.acm.Operation> existingOperations,
                                Operation proposedOperation)
    {
        int result = -1;
        int counter = 0;
        for (com.rubicon.platform.authorization.model.data.acm.Operation existingOperation : existingOperations)
        {
            if (existingOperation.getService().equals(proposedOperation.getService())
                && existingOperation.getResource().equals(proposedOperation.getResource())
                && existingOperation.getAction().equals(proposedOperation.getAction()))
            {
                result = counter;
                break;
            }
            counter++;
        }
        return result;
    }

    // convert ui operation into data model
    public com.rubicon.platform.authorization.model.data.acm.Operation dataOperation(Operation operation)
    {
        List<String> properties = operation.getProperties() != null
                                  ? trimStringList(operation.getProperties())
                                  : null;

        return new com.rubicon.platform.authorization.model.data.acm.Operation(
                operation.getService(), operation.getResource(), operation.getAction(), properties);
    }

    // if properties are present in the request when adding or editing an operation, we will validate they are distinct
    public void validateOperationProperties(EditBaseOperationRequest request)
    {
        if (!CollectionUtils.isEmpty(request.getOperation().getProperties())
            && (EditActionEnum.add.equals(request.getAction()) || EditActionEnum.edit.equals(request.getAction())))
        {
            validateDistinctCollection(trimStringList(request.getOperation().getProperties()), "properties");
        }
    }

    // validates that all items in the collection of strings are unique
    public void validateDistinctCollection(Collection<String> collection, String fieldName)
    {
        Set<String> distinctItems = new HashSet<>(collection);
        if (collection.size() != distinctItems.size())
        {
            throw new ValidationException(String.format("Please remove duplicated item(s) from %s.", fieldName));
        }
    }

    // if the index is -1, this means that the specified operation does not exist for the given role
    // we only care about validating operation exists when editing properties on operation
    public void validateOperationIndexWhenEditing(EditActionEnum action, int index)
    {
        if (EditActionEnum.edit.equals(action) && index == -1)
        {
            throw new NotFoundException("Cannot find specified operation. Please provide a valid operation.");
        }
    }

    // trim all strings of leading and trailing whitespace for given list
    public List<String> trimStringList(List<String> stringList)
    {
        List<String> trimmedList = new ArrayList<>();

        for (String string : stringList)
        {
            trimmedList.add(StringUtils.trimWhitespace(string));
        }

        return trimmedList;
    }

    public DefaultObjectTranslator<A, D> getTranslator()
    {
        return translator;
    }

    public void setTranslator(DefaultObjectTranslator<A, D> translator)
    {
        this.translator = translator;
    }

    public ServiceExceptionMappingDecorator getPersistenceOperations()
    {
        return persistenceOperations;
    }

    public void setPersistenceOperations(ServiceExceptionMappingDecorator persistenceOperations)
    {
        this.persistenceOperations = persistenceOperations;
    }
}

