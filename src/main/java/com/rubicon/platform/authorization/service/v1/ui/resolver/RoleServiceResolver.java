package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.endpoint.pipeline.phase.DefaultEndpointSortBuilder;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecificationBuilder;
import com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.RoleTypeQueryArgumentConverter;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.*;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.rubicon.platform.authorization.service.utils.Constants.*;

public class RoleServiceResolver
        extends BaseServiceResolver<Role, com.rubicon.platform.authorization.model.data.acm.Role>
{
    public static final String OWNER_ACCOUNT_WILDCARD = "*/*";
    public static final String[] ROLE_REQUEST_FIELDS = {"id", "label", "roleTypeId"};

    public static EndpointSpecification getListEndpointSpecification()
    {
        return new EndpointSpecificationBuilder("Ui Role v1")

                .addFieldMapping("id", "id", new QueryExpressionConverter(false))
                .addFieldMapping("name", "label")
                .addFieldMapping("type", "roleTypeId", "roleTypeLabel",
                        new RoleTypeQueryArgumentConverter(), new QueryExpressionConverter(false))

                .setValidQuery("id", "name", "type")
                .setValidSort("id", "name", "type")

                .build();
    }

    public RoleServiceResolver()
    {
    }

    @Transactional
    public PagedResponse<Role> getList(Integer currentPage, Integer size, String query, String sort,
                                       RoleTypePermission roleTypePermission, PersistenceContext persistenceContext)
    {
        ListRequestAdapter listRequestAdapter = new ListRequestAdapter(getListEndpointSpecification());
        String dataQuery = listRequestAdapter.adaptQuery(query);
        String dataSort = listRequestAdapter.adaptSort(sort);

        String filterString = roleTypePermission.buildFilterString();
        dataQuery = ("".equals(dataQuery))
                    ? filterString
                    : dataQuery.concat(";").concat(filterString);

        Node queryNode = buildQueryExpression(dataQuery);

        EndpointSort endpointSort = new DefaultEndpointSortBuilder().buildSort(dataSort, persistenceContext);

        // For the List to only return the fields needed in this request
        persistenceContext.setRequestedFields(new LinkedHashSet<>(Arrays.asList(ROLE_REQUEST_FIELDS)));

        // Translate Page Size into Old Hyperion Values
        Map<String, Integer> hyperionPagingMap = translatePagingToHyperion(currentPage, size);
        Integer start = hyperionPagingMap.get(HYPERION_PAGING_KEY_START);
        Integer limit = hyperionPagingMap.get(HYPERION_PAGING_KEY_LIMIT);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.Role> queryResult =
                getPersistenceOperations().query(queryNode, start, limit, endpointSort, persistenceContext);

        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        translationContext.putContextItem("roleTypePermission", roleTypePermission);

        return buildPagedResponse(queryResult, size, translationContext);
    }


    @Transactional
    public Role getById(Long roleId, RoleTypePermission roleTypePermission, PersistenceContext persistenceContext)
    {
        String filterString = roleTypePermission.buildFilterString();
        // Adding the id to the filter
        filterString = filterString.concat(";id==").concat(roleId.toString());

        // Build out the query node
        Node queryNode = buildQueryExpression(filterString);

        // limit the amount of results to 2, so too many items don't return, but more than one can return
        // to prove we have an issue
        QueryResult<com.rubicon.platform.authorization.model.data.acm.Role> queryResult =
                getPersistenceOperations().query(queryNode, 1, 2, null, persistenceContext);

        assertListHasOneItem(queryResult.getItems(), "roleId", roleId);

        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);

        return getTranslator().convertPersistent(queryResult.getItems().get(0), translationContext);
    }


    public Role create(RoleRequest roleRequest, RoleTypePermission roleTypePermission,
                       PersistenceContext persistenceContext)
    {
        Role role = doCreate(roleRequest, roleTypePermission, persistenceContext);

        // update the role cache
        processEntityChange(persistenceContext);

        return role;
    }

    @Transactional
    protected Role doCreate(RoleRequest roleRequest, RoleTypePermission roleTypePermission,
                            PersistenceContext persistenceContext)
    {
        validateRoleRequest(roleRequest, roleTypePermission);

        com.rubicon.platform.authorization.model.data.acm.Role dataRole =
                new com.rubicon.platform.authorization.model.data.acm.Role();

        dataRole.setLabel(roleRequest.getName());
        dataRole.setRoleTypeId(roleRequest.getType().getRoleTypeEnumId());
        dataRole.setRealm(REALM_NAME);
        dataRole.setOwnerAccount(OWNER_ACCOUNT_WILDCARD);

        persistenceContext.setRequestedFields(new LinkedHashSet<>(Arrays.asList(ROLE_REQUEST_FIELDS)));
        dataRole = (com.rubicon.platform.authorization.model.data.acm.Role) getPersistenceOperations().createOrUpdateItem(
                dataRole, persistenceContext);

        // provide response data
        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        return getTranslator().convertPersistent(dataRole, translationContext);
    }

    public Role update(RoleRequest roleRequest, RoleTypePermission roleTypePermission,
                       PersistenceContext persistenceContext)
    {
        Role role = doUpdate(roleRequest, roleTypePermission, persistenceContext);

        // update the role cache
        processEntityChange(persistenceContext);

        return role;
    }

    @Transactional
    protected Role doUpdate(RoleRequest roleRequest, RoleTypePermission roleTypePermission,
                            PersistenceContext persistenceContext)
    {
        assertNotNull(roleRequest.getId(), "id");
        validateRoleRequest(roleRequest, roleTypePermission);


        List<com.rubicon.platform.authorization.model.data.acm.Role> roles =
                getPersistenceOperations()
                        .findByIds(Collections.singletonList(roleRequest.getId()), persistenceContext);
        assertListHasOneItem(roles, "role", roleRequest.getId());

        // can this user create roles of this role type?
        assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.getById(roles.get(0).getRoleTypeId()), "role");

        com.rubicon.platform.authorization.model.data.acm.Role dataRole = roles.get(0);
        dataRole.setLabel(roleRequest.getName());
        dataRole.setRoleTypeId(roleRequest.getType().getRoleTypeEnumId());

        persistenceContext.setRequestedFields(new LinkedHashSet<>(Arrays.asList(ROLE_REQUEST_FIELDS)));
        dataRole = (com.rubicon.platform.authorization.model.data.acm.Role) getPersistenceOperations()
                .updateItem(Arrays.asList(dataRole.getId()), dataRole, persistenceContext);

        // provide response data
        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        return getTranslator().convertPersistent(dataRole, translationContext);
    }

    public Role editOperations(EditRoleOperationRequest request, RoleTypePermission roleTypePermission,
                               PersistenceContext context)
    {
        Pair<Boolean, Role> dirtyRolePair = doEditOperations(request, roleTypePermission, context);

        if (dirtyRolePair.getLeft() == true)
        {
            processEntityChange(context);
        }

        return dirtyRolePair.getRight();
    }

    @Transactional
    protected Pair<Boolean, Role> doEditOperations(EditRoleOperationRequest request,
                                                   RoleTypePermission roleTypePermission,
                                                   PersistenceContext context)
    {
        validateEditOperation(request);
        // validate properties on the operation if they are present when adding or editing operation
        validateOperationProperties(request);

        List<com.rubicon.platform.authorization.model.data.acm.Role> roles =
                getPersistenceOperations().findByIds(Collections.singletonList(request.getId()), context);
        assertListHasOneItem(roles, "role", request.getId());

        com.rubicon.platform.authorization.model.data.acm.Role role = roles.get(0);

        // can this user create roles of this role type?
        assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.getById(role.getRoleTypeId()), "role");

        // make the requested change, if necessary
        boolean dirty = false;
        boolean isDeniedOperation = EditOperationEnum.denied.equals(request.getOperationType());

        Operation operation = request.getOperation();

        int index = isDeniedOperation
                    ? indexOfOperation(role.getDeniedOperations(), operation)
                    : indexOfOperation(role.getAllowedOperations(), operation);

        validateOperationIndexWhenEditing(request.getAction(), index);

        List<com.rubicon.platform.authorization.model.data.acm.Operation> operations = isDeniedOperation
                                                                                 ? role.getDeniedOperations()
                                                                                 : role.getAllowedOperations();
        switch (request.getAction())
        {
            case add:
                if (index == -1)
                {
                    dirty = true;
                    operations.add(dataOperation(operation));
                }
                break;
            case edit:
                if (operation.getProperties() != null)
                {
                    dirty = true;
                    operations.get(index).setProperties(trimStringList(operation.getProperties()));
                }
                break;
            case remove:
                if (index != -1)
                {
                    dirty = true;
                    operations.remove(index);
                }
                break;
            default:
                break;
        }
        if (dirty)
        {
            // save the role changes, if any
            role = (com.rubicon.platform.authorization.model.data.acm.Role) getPersistenceOperations()
                    .updateItem(Collections.singletonList(request.getId()), role, context);
        }

        // provide response data
        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        return Pair.of(dirty, getTranslator().convertPersistent(role, translationContext));
    }

    public void remove(Long roleId, RoleTypePermission roleTypePermission, PersistenceContext persistenceContext)
    {
        doRemove(roleId, roleTypePermission, persistenceContext);

        // update the role cache
        processEntityChange(persistenceContext);
    }

    // remove role
    @Transactional
    protected void doRemove(Long roleId, RoleTypePermission roleTypePermission, PersistenceContext persistenceContext)
    {
        assertNotNull(roleId, "roleId");
        List<Long> roleIdList = Collections.singletonList(roleId);
        List<com.rubicon.platform.authorization.model.data.acm.Role> roles =
                getPersistenceOperations()
                        .findByIds(roleIdList, persistenceContext);
        assertListHasOneItem(roles, "role", roleId);

        com.rubicon.platform.authorization.model.data.acm.Role role = roles.get(0);

        // can this user remove a role of this role type?
        assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.getById(role.getRoleTypeId()), "role");

        getPersistenceOperations().deleteItem(roleIdList, persistenceContext);
    }

    protected void validateRoleRequest(RoleRequest roleRequest, RoleTypePermission roleTypePermission)
    {
        assertNotNull(roleRequest.getName(), "name");
        assertNotNull(roleRequest.getType(), "type");

        // can this user create roles of this role type?
        assertRoleTypeEditable(roleTypePermission, roleRequest.getType(), "role");
    }
}
