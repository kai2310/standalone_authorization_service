package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.endpoint.pipeline.phase.DefaultEndpointSortBuilder;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecificationBuilder;
import com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.*;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.rubicon.platform.authorization.service.utils.Constants.*;

public class FeatureServiceResolver
        extends BaseServiceResolver<Feature, com.rubicon.platform.authorization.model.data.acm.AccountFeature>
{
    private static final Set<String> FEATURE_REQUEST_FIELDS =
            new HashSet<>(Arrays.asList("id", "label"));
    private static final String ITEM_NAME = "feature";

    public static EndpointSpecification getListEndpointSpecification()
    {
        return new EndpointSpecificationBuilder("Ui Feature v1")

                .addFieldMapping("id", "id", new QueryExpressionConverter(false))
                .addFieldMapping("name", "label")

                .setValidQuery("id", "name")
                .setValidSort("id", "name")

                .build();
    }

    public FeatureServiceResolver()
    {
    }

    @Transactional
    public PagedResponse<Feature> getList(Integer currentPage, Integer size, String query, String sort,
                                          boolean isEditable, PersistenceContext persistenceContext)
    {
        ListRequestAdapter listRequestAdapter = new ListRequestAdapter(getListEndpointSpecification());
        String dataQuery = listRequestAdapter.adaptQuery(query);
        String dataSort = listRequestAdapter.adaptSort(sort);

        Node queryNode = null;
        if (!StringUtils.isEmpty(dataQuery))
        {
            queryNode = buildQueryExpression(dataQuery);
        }

        EndpointSort endpointSort = new DefaultEndpointSortBuilder().buildSort(dataSort, persistenceContext);

        // For the List to only return the id and label.
        persistenceContext.setRequestedFields(FEATURE_REQUEST_FIELDS);

        // Translate Page Size into Old Hyperion Values
        Map<String, Integer> hyperionPagingMap = translatePagingToHyperion(currentPage, size);
        Integer start = hyperionPagingMap.get(HYPERION_PAGING_KEY_START);
        Integer limit = hyperionPagingMap.get(HYPERION_PAGING_KEY_LIMIT);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.AccountFeature> queryResult =
                getPersistenceOperations().query(queryNode, start, limit, endpointSort, persistenceContext);

        return buildPagedResponse(queryResult, size, buildEditableFeatureTranslationContext(isEditable));
    }

    @Transactional
    public Feature getById(Long featureId, boolean isEditable, PersistenceContext context)
    {
        List<com.rubicon.platform.authorization.model.data.acm.AccountFeature> accountFeatures =
                getPersistenceOperations().findByIds(Arrays.asList(featureId), context);

        // Verify the AccountFeature was found.
        assertListHasOneItem(accountFeatures, ITEM_NAME, featureId);

        return getTranslator()
                .convertPersistent(accountFeatures.get(0), buildEditableFeatureTranslationContext(isEditable));
    }

    public Feature create(FeatureRequest featureRequest, boolean isEditable, PersistenceContext context)
    {
        Feature feature = doCreate(featureRequest, isEditable, context);
        // trigger cache refresh
        processEntityChange(context);

        return feature;
    }

    // create feature
    @Transactional
    protected Feature doCreate(FeatureRequest featureRequest, boolean isEditable, PersistenceContext context)
    {
        validateFeatureRequest(featureRequest, isEditable);

        AccountFeature dataFeature = new AccountFeature();
        dataFeature.setLabel(featureRequest.getName());
        dataFeature.setRealm(REALM_NAME);
        // only id and label are needed
        context.setRequestedFields(FEATURE_REQUEST_FIELDS);

        dataFeature = (AccountFeature) getPersistenceOperations().createOrUpdateItem(dataFeature, context);

        return getTranslator().convertPersistent(dataFeature, buildEditableFeatureTranslationContext(isEditable));
    }


    public Feature update(FeatureRequest featureRequest, boolean isEditable, PersistenceContext context)
    {
        Feature feature = doUpdate(featureRequest, isEditable, context);

        // trigger cache refresh
        processEntityChange(context);

        return feature;
    }


    @Transactional
    protected Feature doUpdate(FeatureRequest featureRequest, boolean isEditable, PersistenceContext context)
    {
        validateFeatureRequest(featureRequest, isEditable);
        assertNotNull(featureRequest.getId(), "id");

        List<com.rubicon.platform.authorization.model.data.acm.AccountFeature> accountFeatures =
                getPersistenceOperations()
                        .findByIds(Collections.singletonList(featureRequest.getId()), context);
        assertListHasOneItem(accountFeatures, "feature", featureRequest.getId());

        AccountFeature dataFeature = accountFeatures.get(0);
        dataFeature.setLabel(featureRequest.getName());

        // only id and label are needed
        context.setRequestedFields(FEATURE_REQUEST_FIELDS);

        dataFeature = (AccountFeature) getPersistenceOperations().updateItem(
                Arrays.asList(featureRequest.getId()), dataFeature, context);

        // convert into ui model
        Feature feature =
                getTranslator().convertPersistent(dataFeature, buildEditableFeatureTranslationContext(isEditable));

        // trigger cache refresh
        processEntityChange(context);

        return feature;
    }

    public Feature editOperation(EditFeatureOperationRequest request, boolean isEditable, PersistenceContext context)
    {
        Pair<Boolean, Feature> dirtyFeaturePair = doEditOperation(request, isEditable, context);

        // trigger cache refresh if something changed.
        if (dirtyFeaturePair.getLeft() == true)
        {
            processEntityChange(context);
        }

        return dirtyFeaturePair.getRight();
    }


    // add/remove operation in feature
    @Transactional
    protected Pair<Boolean, Feature> doEditOperation(EditFeatureOperationRequest request, boolean isEditable,
                                                     PersistenceContext context)
    {
        // double check user is allowed to edit the feature
        if (!isEditable)
        {
            throw new UnauthorizedException("You are not authorized to edit this feature.");
        }

        // validate request body to confirm feature id, operation and edit action are present
        validateEditOperation(request);
        // validate properties on the operation if they are present when adding or editing operation
        validateOperationProperties(request);

        // get feature from data service
        Long featureId = request.getId();
        List<AccountFeature> featureList = getPersistenceOperations().findByIds(Arrays.asList(featureId), context);
        assertListHasOneItem(featureList, ITEM_NAME, featureId);

        AccountFeature accountFeature = featureList.get(0);

        Operation operation = request.getOperation();

        boolean isDeniedOperation = EditOperationEnum.denied.equals(request.getOperationType());
        List<com.rubicon.platform.authorization.model.data.acm.Operation> operationList = isDeniedOperation
                                                                                    ? accountFeature.getDeniedOperations()
                                                                                    : accountFeature.getAllowedOperations();

        // find out if operation exists in the feature
        int index = indexOfOperation(operationList, operation);
        validateOperationIndexWhenEditing(request.getAction(), index);

        boolean dirty = false;

        // determine if feature operations are changed
        switch (request.getAction())
        {
            case add:
                if (index == -1)
                {
                    dirty = true;
                    operationList.add(dataOperation(operation));
                }
                break;
            case edit:
                if (operation.getProperties() != null)
                {
                    dirty = true;
                    operationList.get(index).setProperties(trimStringList(operation.getProperties()));
                }
                break;
            case remove:
                if (index != -1)
                {
                    dirty = true;
                    operationList.remove(index);
                }
                break;
            default:
                break;
        }

        // if operation list is modified for feature
        // save feature using data service
        if (dirty)
        {
            accountFeature = (com.rubicon.platform.authorization.model.data.acm.AccountFeature) getPersistenceOperations()
                    .updateItem(Arrays.asList(featureId), accountFeature, context);
        }

        return Pair.of(dirty,
                getTranslator().convertPersistent(accountFeature, buildEditableFeatureTranslationContext(isEditable)));
    }

    public void remove(Long featureId, boolean isEditable, PersistenceContext persistenceContext)
    {
        doRemove(featureId, isEditable, persistenceContext);

        processEntityChange(persistenceContext);
    }

    // remove feature
    @Transactional
    protected void doRemove(Long featureId, boolean isEditable, PersistenceContext persistenceContext)
    {
        // double check if user is authorized to delete the feature
        if (!isEditable)
        {
            throw new UnauthorizedException("You are not authorized to delete this feature.");
        }

        assertNotNull(featureId, "featureId");

        // get feature from data service
        // to make sure provided featureId is valid
        List<Long> featureIdList = Collections.singletonList(featureId);
        List<AccountFeature> featureList = getPersistenceOperations().findByIds(featureIdList, persistenceContext);
        assertListHasOneItem(featureList, ITEM_NAME, featureId);

        getPersistenceOperations().deleteItem(featureIdList, persistenceContext);
    }

    protected void validateFeatureRequest(FeatureRequest request, boolean isEditable)
    {
        // double-check if user is allowed to create feature
        if (!isEditable)
        {
            throw new UnauthorizedException("You are not authorized to make changes to features.");
        }

        // validate non-empty feature name is provided
        assertNotNull(request, "feature creation request body");
        assertNotNull(request.getName(), "feature name");
    }
}
