package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.codahale.metrics.MetricRegistry;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentLoader;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.model.data.idm.User;
import com.rubicon.platform.authorization.service.cache.RoleAssignmentObjectCache;
import com.rubicon.platform.authorization.service.cache.ServiceRoleAssignment;
import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.exception.NotFoundException;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.exception.ValidationException;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecificationBuilder;
import com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.SortFieldV2Formatter;
import com.rubicon.platform.authorization.service.v1.ui.client.idm.EntityResponse;
import com.rubicon.platform.authorization.service.v1.ui.client.idm.IdmUserDataClient;
import com.rubicon.platform.authorization.service.v1.ui.client.leftovers.LeftoverDataClient;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleAssignmentPermission;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.rubicon.platform.authorization.service.utils.Constants.*;

public class RoleAssignmentServiceResolver
        extends BaseServiceResolver<RoleAssignment, com.rubicon.platform.authorization.model.data.acm.RoleAssignment>
{
    public static final String OWNER_ACCOUNT = MAGNITE_INTERNAL_CONTEXT;
    public static final int MAX_HYPERION_RECORDS = 100;

    protected static final Logger logger = LoggerFactory.getLogger(RoleAssignmentServiceResolver.class);

    protected RoleAssignmentLoader roleAssignmentLoader;
    protected RoleLoader roleLoader;
    protected AccountLoader accountLoader;
    protected RoleAssignmentObjectCache roleAssignmentObjectCache;
    private IdmUserDataClient idmUserDataClient;
    private LeftoverDataClient leftoverDataClient;
    private MetricRegistry metricRegistry;
    private User defaultUser;

    private static final String IDENTITY_MANAGEMENT_GET_USER_FAIL_METER =
            "api-clients.identity-management.issues.get-users.fail_meter";


    public static EndpointSpecification getUserListEndpointSpecification()
    {
        return new EndpointSpecificationBuilder("Ui User v1", new SortFieldV2Formatter())

                .addFieldMapping("userId", "id", new QueryExpressionConverter(false))
                .addFieldMapping("username", "username")

                .setValidQuery("userId", "username")
                .setValidSort("userId", "username")

                .build();
    }

    public RoleAssignmentServiceResolver()
    {
        this.defaultUser = new User();
        this.defaultUser.setUsername("<Unknown Username>");
    }


    @Transactional
    public PagedResponse<UserRoleAssignment> listUsers(
            Integer page, Integer size, String query, String sort, boolean addInitialRoleAssignment,
            PersistenceContext persistenceContext)
    {
        List<UserRoleAssignment> userRoleAssignments = new ArrayList<>();
        DataUserContext userContext = (DataUserContext) persistenceContext.getUserContext();

        Integer dataStart = ((page - 1) * size) + 1;

        if (dataStart < 1)
        {
            throw new BadRequestException(
                    String.format("Invalid page '%d' and size '%d' combination", page, size));
        }

        ListRequestAdapter listRequestAdapter = new ListRequestAdapter(getUserListEndpointSpecification());
        String dataQuery = listRequestAdapter.adaptQuery(query);
        String dataSort = listRequestAdapter.adaptSort(sort);

        String filterString = "status==ACTIVE";
        dataQuery = ("".equals(dataQuery))
                    ? filterString
                    : dataQuery.concat(";").concat(filterString);

        EntityResponse<User> users = idmUserDataClient.getUsers(dataStart, size, dataQuery, dataSort, userContext);

        for (User user : users.getEntries())
        {
            Long userId = user.getId();

            // Using the Role Assignment Cache to look up a users role assignments instead of querying the database
            Collection<Long> roleAssignmentIds =
                    roleAssignmentObjectCache.getIdMap().getIdsForSubject("user/" + userId.toString());

            UserRoleAssignment userRoleAssignment = new UserRoleAssignment();
            userRoleAssignment.setUserId(userId);
            userRoleAssignment.setDisplayAddButton(
                    canUserAddInitialRoleAssignment(addInitialRoleAssignment, roleAssignmentIds.size()));
            userRoleAssignment.setUsername(user.getUsername());

            userRoleAssignments.add(userRoleAssignment);
        }

        PagedResponse<UserRoleAssignment> result = new PagedResponse<>();
        result.setPage(new Page(users.getPage().getStart(), size,
                users.getPage().getTotalCount(), users.getPage().getResponseCount()));
        result.setContent(userRoleAssignments);

        return result;
    }

    @Transactional
    public UserRoleAssignment getUserRoleAssignments(Long userId, RoleTypePermission roleTypePermission,
                                                     boolean addInitialRoleAssignment,
                                                     PersistenceContext persistenceContext)
    {
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> dataRoleAssignments =
                getUserRoleAssignments(userId.toString(), MAX_HYPERION_RECORDS, persistenceContext);

        UserRoleAssignment userRoleAssignment =
                buildUserRoleAssignment(userId, dataRoleAssignments, roleTypePermission, addInitialRoleAssignment,
                        persistenceContext);

        return userRoleAssignment;
    }

    @Transactional
    public UserRoleAssignment createRoleAssignment(RoleAssignmentRequest roleAssignmentRequest,
                                                   RoleTypePermission roleTypePermission,
                                                   RoleAssignmentPermission roleAssignmentPermission,
                                                   PersistenceContext persistenceContext)
    {
        UserRoleAssignment userRoleAssignment =
                doCreateRoleAssignment(roleAssignmentRequest, roleTypePermission, roleAssignmentPermission,
                        persistenceContext);

        // update the role assignment cache.
        processEntityChange(persistenceContext);

        return userRoleAssignment;
    }

    protected UserRoleAssignment doCreateRoleAssignment(RoleAssignmentRequest roleAssignmentRequest,
                                                        RoleTypePermission roleTypePermission,
                                                        RoleAssignmentPermission roleAssignmentPermission,
                                                        PersistenceContext persistenceContext)
    {
        boolean addInitialRoleAssignment = roleAssignmentPermission.isAssignInitialRoleAssignment();

        // Validate the Object
        assertNotNull(roleAssignmentRequest.getUserId(), "userId");
        assertNotNull(roleAssignmentRequest.getRoleId(), "roleId");

        // Validate that the given user can add the initial role assignment
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> dataRoleAssignments =
                getUserRoleAssignments(roleAssignmentRequest.getUserId().toString(), MAX_HYPERION_RECORDS, persistenceContext);

        assertCreateInitialRoleAssignment(addInitialRoleAssignment, dataRoleAssignments);
        assertAssignAccountGroup(roleAssignmentPermission, roleAssignmentRequest);

        // verify accountId or accountGroup is populated
        if (roleAssignmentRequest.getAccountId() == null && roleAssignmentRequest.getAccountGroup() == null)
        {
            throw new ValidationException("Please provide an accountId or accountGroup.");
        }
        else if (roleAssignmentRequest.getAccountId() != null && roleAssignmentRequest.getAccountGroup() != null)
        {
            throw new ValidationException("You can not provide an accountId or accountGroup at the same time.");
        }


        Long userId = roleAssignmentRequest.getUserId();

        // Verify the user can modify the role type
        PersistentRole role = getDataServiceRole(roleAssignmentRequest.getRoleId());
        assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.getById(role.getRoleTypeId()), "role assignment");

        com.rubicon.platform.authorization.model.data.acm.RoleAssignment roleAssignment =
                createDataRoleAssignment(roleAssignmentRequest.getRoleId(), "user/" + userId.toString(),
                        getAccountGroupId(roleAssignmentRequest.getAccountGroup()),
                        getCompoundAccount(roleAssignmentRequest.getAccountId()), persistenceContext);

        com.rubicon.platform.authorization.model.data.acm.RoleAssignment dataRoleAssignment = null;
        dataRoleAssignment = (com.rubicon.platform.authorization.model.data.acm.RoleAssignment) getPersistenceOperations()
                .createOrUpdateItem(roleAssignment, persistenceContext);

        UserRoleAssignment userRoleAssignment =
                buildUserRoleAssignment(userId, Arrays.asList(dataRoleAssignment), roleTypePermission,
                        addInitialRoleAssignment,
                        persistenceContext);

        CompoundId compoundAccount = dataRoleAssignment.getAccount() == null
                                     ?
                                     null
                                     : new CompoundId(dataRoleAssignment.getAccount());
        performAuthorizationEntryChange(userId.toString(), dataRoleAssignment.getAccountGroupId(), compoundAccount,
                true);

        return userRoleAssignment;
    }

    @Transactional
    public void removeRoleAssignment(Long roleAssignmentId, RoleTypePermission roleTypePermission,
                                     PersistenceContext persistenceContext)
    {
        doRemoveRoleAssignment(roleAssignmentId, roleTypePermission, persistenceContext);

        // update the role assignment cache.
        processEntityChange(persistenceContext);
    }


    protected void doRemoveRoleAssignment(Long roleAssignmentId, RoleTypePermission roleTypePermission,
                                          PersistenceContext persistenceContext)
    {
        // Verify the Role Assignments exist
        PersistentRoleAssignment roleAssignment = roleAssignmentLoader.find(roleAssignmentId);

        if (roleAssignment == null)
        {
            throw new NotFoundException("The role assignment you are looking for does not exist.");
        }
        Long roleId = roleAssignment.getRoleId();

        // Determine if the user can delete the given role assignment
        PersistentRole role = getDataServiceRole(roleId);
        assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.getById(role.getRoleTypeId()), "role assignment");

        // Delete the given role assignment
        getPersistenceOperations().deleteItem(Arrays.asList(roleAssignmentId), persistenceContext);

        // delete entry in authorization table if it exists
        // if there is no role assignment with the context
        if (!hasRoleAssignmentWithSameAccountContext(roleAssignment))
        {
            performAuthorizationEntryChange(roleAssignment.getSubject().getId(), roleAssignment.getAccountGroupId(),
                    roleAssignment.getAccount(), false);
        }
    }

    @Transactional
    public PagedResponse<AssignedUser> getAssignedUsers(Long roleId, Integer pageNumber, Integer resultSize,
                                                        RoleTypePermission roleTypePermission, boolean isEditable,
                                                        PersistenceContext context)
    {
        List<Long> roleAssigmentIds =
                roleAssignmentLoader.getRoleAssignmentByRoleId(roleId);

        PagedResponse<AssignedUser> pagedResponse;
        if (roleAssigmentIds.size() > 0)
        {
            // Convert API Paging to Hyperion Paging
            Map<String, Integer> hyperionPagingMap = translatePagingToHyperion(pageNumber, resultSize);
            Integer start = hyperionPagingMap.get(HYPERION_PAGING_KEY_START);
            Integer limit = hyperionPagingMap.get(HYPERION_PAGING_KEY_LIMIT);

            Node queryNode = buildQueryExpression(String.format("id=in=(%s)", StringUtils.join(roleAssigmentIds, ",")));

            QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResults =
                    getRoleAssignmentQueryResults(queryNode, start, limit, null, context);

            // Get Role assignments data service objects
            pagedResponse = new PagedResponse<>();
            if (queryResults != null && !CollectionUtils.isEmpty(queryResults.getItems()))
            {
                List<AssignedUser> assignedUsers =
                        buildAssignedUserList(queryResults, roleTypePermission, isEditable, context);

                // Set up the page response
                pagedResponse.setPage(new Page(queryResults.getStart(), limit, queryResults.getTotalCount(),
                        queryResults.getResponseCount()));
                pagedResponse.setContent(assignedUsers);
            }
            else
            {
                // Build out the counting response when we don't have any results.
                pagedResponse =
                        buildCountQueryResults(getPersistenceOperations().query(queryNode, 1, limit, null, context),
                                limit, pageNumber, resultSize);
            }
        }
        else
        {
            pagedResponse = buildAssignedUserDefaultPageResponse(pageNumber, resultSize);
        }


        return pagedResponse;
    }


    protected boolean canUserAddInitialRoleAssignment(boolean addInitialRoleAssignment,
                                                      int existingRoleAssignmentCount)
    {
        return ((existingRoleAssignmentCount == 0 && addInitialRoleAssignment) || existingRoleAssignmentCount > 0);
    }


    protected void assertCreateInitialRoleAssignment(boolean addInitialRoleAssignment,
                                                     List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignments)
    {
        // If the user has no role assignments and the requesting user can not add the initial role assignment,
        // we need to throw an error.
        if (!canUserAddInitialRoleAssignment(addInitialRoleAssignment, roleAssignments.size()))
        {
            throw new UnauthorizedException("You are not allowed to add a Role assignment to this user.");
        }
    }

    public void assertAssignAccountGroup(RoleAssignmentPermission roleAssignmentPermission,
                                         RoleAssignmentRequest roleAssignmentRequest)
    {
        AccountGroupEnum accountGroup = roleAssignmentRequest.getAccountGroup();

        if (accountGroup != null && !roleAssignmentPermission.isAuthorizedForAccountGroup(accountGroup))
        {
            throw new UnauthorizedException(String.format("You are not allowed to assign %s to this role assignment.",
                    accountGroup.name()));
        }
    }

    protected String getUsername(DataUserContext userContext, Long userId)
    {
        String username;
        User user = idmUserDataClient.getUserById(userId, userContext);
        if (null == user)
        {
            throw new NotFoundException("User not found.");
        }
        else
        {
            username = user.getUsername();
        }
        return username;
    }


    protected PersistentRole getDataServiceRole(Long roleId)
    {
        // Determine if the user can delete the given role assignment
        PersistentRole role = roleLoader.find(roleId);
        if (role == null)
        {
            throw new ValidationException("The role associated with the role assignment does not exist.");
        }

        return role;
    }


    protected Long getAccountGroupId(AccountGroupEnum accountGroupEnum)
    {
        Long accountGroupId = null;
        if (accountGroupEnum != null)
        {
            accountGroupId = accountGroupEnum.getAccountGroupEnumId();
        }

        return accountGroupId;
    }

    protected String getCompoundAccount(Long accountId)
    {
        String accountString = null;
        if (accountId != null)
        {
            PersistentAccount persistentAccount = accountLoader.find(accountId);
            if (persistentAccount != null)
            {
                accountString = persistentAccount.getAccountId().toString();
            }
        }

        return accountString;
    }

    protected List<AssignedUser> buildAssignedUserList(
            QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResults,
            RoleTypePermission roleTypePermission, boolean isEditable,
            PersistenceContext context)
    {
        List<AssignedUser> assignedUsers = new ArrayList<>();

        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> dataRoleAssignments =
                queryResults.getItems();
        Set<Long> userIds = new HashSet<>();

        // Build out the initial part of the role assignment object, username/status are omitted in this phase
        for (com.rubicon.platform.authorization.model.data.acm.RoleAssignment dataRoleAssignment : dataRoleAssignments)
        {
            CompoundId compoundId = new CompoundId(dataRoleAssignment.getSubject());
            Long userId = Long.parseLong(compoundId.getId());
            userIds.add(userId);

            RoleAssignment roleAssignment =
                    getTranslator().convertPersistent(dataRoleAssignment, translationContext);

            // Adding assigned User to a list
            assignedUsers.add(AssignedUser.builder().withId(userId)
                    .withRoleAssignmentId(dataRoleAssignment.getId())
                    .withAccount(roleAssignment.getAccountReference())
                    .withEditable(isEditable && roleAssignment.getEditable())
                    .build());
        }

        populateUserForAssignedUsers(assignedUsers, userIds, context);

        return assignedUsers;
    }

    protected UserRoleAssignment buildUserRoleAssignment(Long userId,
                                                         List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> dataRoleAssignments,
                                                         RoleTypePermission roleTypePermission,
                                                         boolean addInitialRoleAssignment,
                                                         PersistenceContext persistenceContext)
    {
        TranslationContext translationContext = buildRoleTypePermissionTranslationContext(roleTypePermission);
        DataUserContext userContext = (DataUserContext) persistenceContext.getUserContext();

        UserRoleAssignment userRoleAssignment = new UserRoleAssignment();
        userRoleAssignment.setUserId(userId);
        userRoleAssignment.setDisplayAddButton(
                canUserAddInitialRoleAssignment(addInitialRoleAssignment, dataRoleAssignments.size()));
        userRoleAssignment.setUsername(getUsername(userContext, userId));
        userRoleAssignment.setRoleAssignments(
                getTranslator().convertPersistent(dataRoleAssignments, translationContext));

        return userRoleAssignment;
    }


    protected List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> getUserRoleAssignments(String userId,
                                                                                                      int maxRecords,
                                                                                                      PersistenceContext persistenceContext)
    {
        String userFilterString = "subject==user/".concat(userId);
        Node query = buildQueryExpression(userFilterString);
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignments = new ArrayList<>();

        int start = 1;
        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignmentQueryResult;
        do
        {
            roleAssignmentQueryResult =
                    getPersistenceOperations().query(query, start, maxRecords, null, persistenceContext);

            roleAssignments.addAll(roleAssignmentQueryResult.getItems());

            // Make a list of items to append the items too.
            start += maxRecords;
        }
        // for example, maxRecords = 100
        // if we have 101 records, we will have 2 queries
        while (start <= roleAssignmentQueryResult.getTotalCount());

        return roleAssignments;
    }

    // check whether there is role assignment having same account context
    public boolean hasRoleAssignmentWithSameAccountContext(PersistentRoleAssignment roleAssignment)
    {
        Set<Long> accountGroupSet = (roleAssignment.getAccountGroupId() == null)
                                    ? new HashSet<>()
                                    : new HashSet<>(Arrays.asList(roleAssignment.getAccountGroupId()));

        List<ServiceRoleAssignment> roleAssignmentList =
                roleAssignmentObjectCache.getPermissions(Arrays.asList(roleAssignment.getSubject()),
                        roleAssignment.getAccount(), accountGroupSet);

        boolean result = true;
        // the requested role assignment should be still in cache
        // so it must be at least one role assignment with same context
        // if its id is same as requested one
        // it means no role assignment has same context
        if (roleAssignmentList.size() == 1 && roleAssignmentList.get(0).getId().equals(roleAssignment.getId()))
        {
            result = false;
        }

        return result;

    }

    // add/delete authorization entry if role assignment is tied to publisher/seat
    // if not, will do nothing
    public void performAuthorizationEntryChange(String userId, Long accountGroupId,
                                                CompoundId accountContext, boolean isToAdd)
    {
        String resourceId = null;
        String resourceType = null;

        // if role assignment is tied to account group
        if (accountGroupId != null && accountGroupId != 0)
        {
            // when context type is publisher, seat or marketplace vendor
            // it will be wildcard for this context type in authorization table
            if (accountGroupId.equals(Constants.ACCOUNT_GROUP_ALL_PUBLISHER_ID))
            {
                resourceId = Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID;
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_ACCOUNT_RESOURCE_TYPE;
            }
            else if (accountGroupId.equals(Constants.ACCOUNT_GROUP_ALL_SEAT_ID))
            {
                resourceId = Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID;
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_SEAT_RESOURCE_TYPE;
            }
            else if (accountGroupId.equals(Constants.ACCOUNT_GROUP_ALL_MARKETPLACE_VENDOR_ID))
            {
                resourceId = Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID;
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_MARKETPLACE_VENDOR_RESOURCE_TYPE;
            }
        }
        else
        {
            String contextType = accountContext.getIdType();
            resourceId = accountContext.getId();

            // if role assignment is tied to specific account and its publisher or seat
            if (Constants.ACCOUNT_TYPE_PUBLISHER.equals(contextType))
            {
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_ACCOUNT_RESOURCE_TYPE;
            }
            else if (Constants.ACCOUNT_TYPE_SEAT.equals(contextType))
            {
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_SEAT_RESOURCE_TYPE;
            }
            else if (Constants.ACCOUNT_TYPE_MARKETPLACE_VENDOR.equals(contextType))
            {
                resourceType = Constants.REVV_LEFTOVERS_AUTHORIZATION_MARKETPLACE_VENDOR_RESOURCE_TYPE;
            }
        }

        // if resourceId and resourceType are set
        // perform authorization entry change
        if (resourceId != null && resourceType != null)
        {
            if (isToAdd)
            {
                leftoverDataClient.addAuthorizationEntry(userId, resourceId, resourceType);
            }
            else
            {
                leftoverDataClient.deleteAuthorizationEntry(userId, resourceId, resourceType);
            }
        }
    }

    protected void populateUserForAssignedUsers(List<AssignedUser> assignedUsers, Set<Long> userIds,
                                                PersistenceContext context)
    {
        DataUserContext userContext = (DataUserContext) context.getUserContext();
        try
        {
            EntityResponse<User> entityResponse = idmUserDataClient.getUsers(1, userIds.size(),
                    String.format("id=in=(%s)", StringUtils.join(userIds, ",")), null, userContext);

            if (entityResponse != null && !CollectionUtils.isEmpty(entityResponse.getEntries()))
            {
                Map<Long, User> userMap = new HashMap<>();
                for (User user : entityResponse.getEntries())
                {
                    userMap.put(user.getId(), user);
                }

                for (int index = 0; index < assignedUsers.size(); index++)
                {
                    AssignedUser assignedUser = assignedUsers.get(index);
                    User user = userMap.getOrDefault(assignedUser.getId(), this.defaultUser);

                    assignedUser.setName(user.getUsername());
                    assignedUser.setStatus((user.getStatus() != null)
                                           ? user.getStatus().name().toLowerCase(Locale.ROOT)
                                           : null);
                }
            }
        }
        catch (Exception e)
        {
            // Catching an issue trying to return users will allow the API to return, but the user information will not
            // be populated.
            logger.warn("There was an error trying to get users from IDM.", e);
            metricRegistry.meter(IDENTITY_MANAGEMENT_GET_USER_FAIL_METER).mark();
            // Note as this service is internal, its okay to be a little more clear about why we are seeing an error
        }

    }

    protected PagedResponse<AssignedUser> buildAssignedUserDefaultPageResponse(Integer pageNumber,
                                                                               Integer resultsPerPage)
    {
        Page page = new Page();
        page.setSize(resultsPerPage);
        page.setTotalElements(0L);
        page.setTotalPages(0);
        page.setNumber(pageNumber);

        PagedResponse<AssignedUser> pagedResponse = new PagedResponse<>();
        pagedResponse.setPage(page);
        pagedResponse.setContent(new ArrayList<>());
        return pagedResponse;
    }

    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> getRoleAssignmentQueryResults(
            Node query, Integer start, Integer limit, EndpointSort sort, PersistenceContext context)
    {
        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResult = null;
        try
        {

            queryResult = getPersistenceOperations().query(query, start, limit, sort, context);
        }
        catch (BadRequestException e)
        {
            // If we receive an error with a different error message, please throw it.
            if (!e.getMessage().contains("valid query"))
            {
                throw e;
            }
        }

        return queryResult;
    }

    protected PagedResponse<AssignedUser> buildCountQueryResults(
            QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResults, Integer limit,
            Integer pageNumber, Integer resultSize)
    {

        PagedResponse<AssignedUser> pagedResponse = buildAssignedUserDefaultPageResponse(pageNumber, resultSize);
        if (queryResults != null && queryResults.getTotalCount() > 0L)
        {
            // IF the count query result return data, we need to calculate the page information properly.
            Long totalCount = queryResults.getTotalCount();
            Integer totalPages = Long.valueOf(totalCount / limit + (totalCount % limit > 0
                                                                    ? 1
                                                                    : 0)).intValue();
            Page page = new Page();
            page.setSize(0);
            page.setTotalElements(totalCount);
            page.setTotalPages(totalPages);
            page.setNumber(pageNumber);

            pagedResponse.setPage(page);
            pagedResponse.setContent(new ArrayList<>());
        }

        return pagedResponse;
    }

    protected com.rubicon.platform.authorization.model.data.acm.RoleAssignment createDataRoleAssignment(Long roleId,
                                                                                                  String subject,
                                                                                                  Long accountGroupId,
                                                                                                  String account,
                                                                                                  PersistenceContext persistenceContext)
    {
        return createDataRoleAssignment(roleId, subject, accountGroupId, account, null, persistenceContext);
    }

    protected com.rubicon.platform.authorization.model.data.acm.RoleAssignment createDataRoleAssignment(Long roleId,
                                                                                                  String subject,
                                                                                                  Long accountGroupId,
                                                                                                  String account,
                                                                                                  List<String> scope,
                                                                                                  PersistenceContext persistenceContext)
    {
        com.rubicon.platform.authorization.model.data.acm.RoleAssignment roleAssignment =
                new com.rubicon.platform.authorization.model.data.acm.RoleAssignment();

        roleAssignment.setRoleId(roleId);
        roleAssignment.setSubject(subject);
        roleAssignment.setRealm(REALM_NAME);
        roleAssignment.setOwnerAccount(OWNER_ACCOUNT);
        roleAssignment.setAccountGroupId(accountGroupId);
        roleAssignment.setAccount(account);
        roleAssignment.setScope(scope);

        return (com.rubicon.platform.authorization.model.data.acm.RoleAssignment) getPersistenceOperations()
                .createOrUpdateItem(roleAssignment, persistenceContext);
    }

    public void setRoleAssignmentLoader(RoleAssignmentLoader roleAssignmentLoader)
    {
        this.roleAssignmentLoader = roleAssignmentLoader;
    }

    public void setRoleLoader(RoleLoader roleLoader)
    {
        this.roleLoader = roleLoader;
    }

    public void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    public void setRoleAssignmentObjectCache(RoleAssignmentObjectCache roleAssignmentObjectCache)
    {
        this.roleAssignmentObjectCache = roleAssignmentObjectCache;
    }

    public void setIdmUserDataClient(IdmUserDataClient idmUserDataClient)
    {
        this.idmUserDataClient = idmUserDataClient;
    }

    public void setLeftoverDataClient(
            LeftoverDataClient leftoverDataClient)
    {
        this.leftoverDataClient = leftoverDataClient;
    }

    public void setMetricRegistry(MetricRegistry metricRegistry)
    {
        this.metricRegistry = metricRegistry;
    }
}
