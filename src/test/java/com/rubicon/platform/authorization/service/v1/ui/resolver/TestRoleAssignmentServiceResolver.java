package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.TestAbstract;
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
import com.rubicon.platform.authorization.service.cache.SubjectIdMap;
import com.rubicon.platform.authorization.service.exception.*;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.ui.client.idm.EntityResponse;
import com.rubicon.platform.authorization.service.v1.ui.client.idm.IdmUserDataClient;
import com.rubicon.platform.authorization.service.v1.ui.client.idm.Page;
import com.rubicon.platform.authorization.service.v1.ui.client.leftovers.LeftoverDataClient;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleAssignmentPermission;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.RoleAssignmentTranslator;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.REALM_NAME;
import static com.rubicon.platform.authorization.service.v1.ui.resolver.RoleAssignmentServiceResolver.OWNER_ACCOUNT;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class TestRoleAssignmentServiceResolver extends TestAbstract
{
    protected RoleAssignmentTranslator roleAssignmentTranslator;
    protected RoleAssignmentServiceResolver resolver;
    public static final int MAX_QUERY_RESULTS = 2;

    public static final String ROLE_ASSIGNMENT_USERNAME = "arubiconuser@email.com";
    public static final Long ROLE_ASSIGNMENT_USER_ID = 477L;
    public static final long INVALID_DATA_ID = DATA_SERVICE_ROLE_ID + DATA_SERVICE_ROLE_ASSIGNMENT_ID;

    @Before
    public void setup()
    {
        roleAssignmentTranslator = new RoleAssignmentTranslator(getRoleObjectCache(), getAccountObjectCache(),
                getAccountGroupObjectCache(), getDeletedAccountCache());
        roleAssignmentTranslator.init();

        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignments = getRoleAssignmentList(1);


        resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(
                setupUiServicePersistenceOperations(getQueryResults(MAX_QUERY_RESULTS), roleAssignments, 2,
                        roleAssignments.get(0)));
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(getPersistentRoleAssignment()));
        resolver.setRoleLoader(getMockRoleLoader());
        resolver.setAccountLoader(getMockAccountLoader());
        resolver.setIdmUserDataClient(getIdmUserDataClient());
        resolver.setLeftoverDataClient(getLeftoverDataClient());
    }


    @Test
    public void testGetUserRoleAssignments()
    {
        Long userId = 55L;
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        UserRoleAssignment userRoleAssignment =
                resolver.getUserRoleAssignments(userId, roleTypePermission, true, getPersistentContext());

        assertNotNull(userRoleAssignment);

        assertThat(userRoleAssignment.getUserId(), equalTo(userId));
        assertThat(userRoleAssignment.getUsername(), equalTo(ROLE_ASSIGNMENT_USERNAME));

        assertNotNull(userRoleAssignment.getRoleAssignments());
        assertThat(userRoleAssignment.getRoleAssignments().size(), equalTo(MAX_QUERY_RESULTS));

        RoleAssignment roleAssignment = userRoleAssignment.getRoleAssignments().get(0);
        assertNotNull(roleAssignment.getAccountReference());
        assertNotNull(roleAssignment.getRole());
        assertFalse(roleAssignment.getEditable());
    }


    @DataProvider
    public static Object[][] testGetUserRoleAssignmentsWithInvalidUserDataProvider()
    {
        return new Object[][]{
                {true, null, ServiceUnavailableException.class},
                {false, null, NotFoundException.class}
        };
    }


    @Test()
    @UseDataProvider("testGetUserRoleAssignmentsWithInvalidUserDataProvider")
    public void testGetUserRoleAssignments_withInvalidUser(boolean throwRequestError, User user,
                                                           Class<? extends ServiceException> classType)
    {
        expectedException.expect(classType);

        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(
                setupUiServicePersistenceOperations(getQueryResults(MAX_QUERY_RESULTS), getRoleAssignmentList(1)));
        resolver.setIdmUserDataClient(getIdmUserDataClient(throwRequestError, user));
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(getPersistentRoleAssignment()));
        resolver.setRoleLoader(getMockRoleLoader());

        RoleTypePermission roleTypePermission = new RoleTypePermission();

        UserRoleAssignment userRoleAssignment =
                resolver.getUserRoleAssignments(55L, roleTypePermission, true, getPersistentContext());
    }


    @Test
    public void testRemoveRoleAssignment()
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditBuyer(true);
        roleTypePermission.setEditInternal(true);
        roleTypePermission.setEditSeller(true);
        roleTypePermission.setEditService(true);

        resolver.setRoleAssignmentObjectCache(getMockRoleAssignmentCache(Long.valueOf(USER_ID)));

        resolver.removeRoleAssignment(DATA_SERVICE_ROLE_ASSIGNMENT_ID, roleTypePermission, getPersistentContext());
    }


    @DataProvider
    public static Object[][] removeRoleAssignmentExpectingErrorsDataProvider()
    {
        return new Object[][]{
                {INVALID_DATA_ID, DATA_SERVICE_ROLE_ID, NotFoundException.class,},
                {DATA_SERVICE_ROLE_ASSIGNMENT_ID, INVALID_DATA_ID, ValidationException.class},
                {DATA_SERVICE_ROLE_ASSIGNMENT_ID, DATA_SERVICE_ROLE_ID, UnauthorizedException.class}
        };
    }


    @Test
    @UseDataProvider("removeRoleAssignmentExpectingErrorsDataProvider")
    public void testRemoveRoleAssignment_ExpectingErrors(Long roleAssignmentId, Long roleId,
                                                         Class<? extends ServiceException> classType)
    {
        expectedException.expect(classType);

        // Set up the Role Assignment for Testing
        PersistentRoleAssignment roleAssignment = getPersistentRoleAssignment();
        roleAssignment.setId(roleAssignmentId);
        roleAssignment.setRoleId(roleId);

        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(
                setupUiServicePersistenceOperations(getQueryResults(MAX_QUERY_RESULTS), getRoleAssignmentList(1)));
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleLoader(getMockRoleLoader());
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(roleAssignment));

        RoleTypePermission roleTypePermission = new RoleTypePermission();
        resolver.removeRoleAssignment(roleAssignmentId, roleTypePermission, getPersistentContext());
    }

    @Test
    public void testCreateRoleAssignment()
    {
        Long userId = 55L;
        Long accountId = DATA_SERVICE_ACCOUNT_ID;

        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest();
        roleAssignmentRequest.setUserId(userId);
        roleAssignmentRequest.setRoleId(DATA_SERVICE_ROLE_ID);
        roleAssignmentRequest.setAccountId(accountId);
        roleAssignmentRequest.setAccountGroup(null);

        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditInternal(true);

        RoleAssignmentPermission roleAssignmentPermission = new RoleAssignmentPermission();
        roleAssignmentPermission.setAssignInitialRoleAssignment(true);
        roleAssignmentPermission.setAssignAllPublisher(true);
        roleAssignmentPermission.setAssignAllSeat(true);

        UserRoleAssignment userRoleAssignment =
                resolver.createRoleAssignment(roleAssignmentRequest, roleTypePermission, roleAssignmentPermission,
                        getPersistentContext());

        assertNotNull(userRoleAssignment);

        assertThat(userRoleAssignment.getUserId(), equalTo(userId));
        assertThat(userRoleAssignment.getUsername(), equalTo(ROLE_ASSIGNMENT_USERNAME));

        assertNotNull(userRoleAssignment.getRoleAssignments());
        assertThat(userRoleAssignment.getRoleAssignments().size(), equalTo(1));

        RoleAssignment roleAssignment = userRoleAssignment.getRoleAssignments().get(0);

        assertNotNull(roleAssignment.getAccountReference());
        AccountReference accountReference = roleAssignment.getAccountReference();

        assertThat(accountReference.getType(), equalTo(AccountReferenceTypeEnum.publisher));
        assertThat(accountReference.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(accountReference.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));

        assertNotNull(roleAssignment.getRole());
        Reference role = roleAssignment.getRole();
        assertThat(role.getName(), equalTo(DATA_SERVICE_ROLE_NAME));
        assertThat(role.getId(), equalTo(DATA_SERVICE_ROLE_ID));


        assertTrue(roleAssignment.getEditable());

    }


    @DataProvider
    public static Object[][] createRoleAssignmentValidatePropertiesDataProvider()
    {
        return new Object[][]{
                {null, DATA_SERVICE_ROLE_ID, 6L, null, true, true, true, ValidationException.class},
                {55L, null, 6L, null, true, true, true, ValidationException.class},
                {55L, DATA_SERVICE_ROLE_ID, null, null, true, true, true, ValidationException.class},
                {55L, DATA_SERVICE_ROLE_ID, 6L, AccountGroupEnum.ALL_PUBLISHERS, true, true, true,
                        ValidationException.class},
                {55L, DATA_SERVICE_ROLE_ID, null, AccountGroupEnum.ALL_SEATS, false, true, true,
                        UnauthorizedException.class},
                {55L, DATA_SERVICE_ROLE_ID, null, AccountGroupEnum.ALL_SEATS, false, true, false,
                        UnauthorizedException.class},
                {55L, DATA_SERVICE_ROLE_ID, null, AccountGroupEnum.ALL_PUBLISHERS, false, false, true,
                        UnauthorizedException.class},


        };
    }

    @Test
    @UseDataProvider("createRoleAssignmentValidatePropertiesDataProvider")
    public void testCreateRoleAssignment_ValidateProperties(Long userId, Long roleId, Long accountId,
                                                            AccountGroupEnum accountGroup, boolean isEditable,
                                                            boolean assignPublisher, boolean assignSeat,
                                                            Class<Exception> exceptionClass)
    {
        expectedException.expect(exceptionClass);
        RoleAssignmentPermission roleAssignmentPermission = new RoleAssignmentPermission();
        roleAssignmentPermission.setAssignInitialRoleAssignment(true);
        roleAssignmentPermission.setAssignAllPublisher(assignPublisher);
        roleAssignmentPermission.setAssignAllSeat(assignSeat);

        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest();
        roleAssignmentRequest.setUserId(userId);
        roleAssignmentRequest.setRoleId(roleId);
        roleAssignmentRequest.setAccountId(accountId);
        roleAssignmentRequest.setAccountGroup(accountGroup);

        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditInternal(isEditable);

        resolver.createRoleAssignment(roleAssignmentRequest, roleTypePermission, roleAssignmentPermission,
                getPersistentContext());
    }


    @DataProvider
    public static Object[][] canUserAddInitialRoleAssignmentDataProvider()
    {
        return new Object[][]{
                {0, false, false},
                {0, true, true},
                {1, false, true},
                {1, true, true}
        };
    }

    @Test
    @UseDataProvider("canUserAddInitialRoleAssignmentDataProvider")
    public void testCanUserAddInitialRoleAssignment(int getResultsTotal, boolean canAddInitialRoleAssignment,
                                                    boolean expectedResult)
    {
        int getResults = 0;
        boolean addInitialRoleAssignment = false;
        boolean expected = false;

        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(
                setupUiServicePersistenceOperations(getQueryResults(getResultsTotal), null, 2, null));
        resolver.setIdmUserDataClient(getIdmUserDataClient());
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(getPersistentRoleAssignment()));
        resolver.setRoleLoader(getMockRoleLoader());
        resolver.setAccountLoader(getMockAccountLoader());

        Long userId = 55L;
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        UserRoleAssignment userRoleAssignment =
                resolver.getUserRoleAssignments(userId, roleTypePermission, canAddInitialRoleAssignment,
                        getPersistentContext());

        assertThat(userRoleAssignment.getDisplayAddButton(), equalTo(expectedResult));
    }


    @Test(expected = UnauthorizedException.class)
    public void testAssertCanUserAddInitialRoleAssignment()
    {
        RoleAssignmentPermission roleAssignmentPermission = new RoleAssignmentPermission();


        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(setupUiServicePersistenceOperations(getQueryResults(0), null, 2, null));
        resolver.setIdmUserDataClient(getIdmUserDataClient());
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(getPersistentRoleAssignment()));
        resolver.setRoleLoader(getMockRoleLoader());
        resolver.setAccountLoader(getMockAccountLoader());

        RoleTypePermission roleTypePermission = new RoleTypePermission();

        RoleAssignmentRequest roleAssignmentRequest = new RoleAssignmentRequest();
        roleAssignmentRequest.setUserId(56L);
        roleAssignmentRequest.setRoleId(DATA_SERVICE_ROLE_ID);
        roleAssignmentRequest.setAccountId(60L);
        roleAssignmentRequest.setAccountGroup(null);

        UserRoleAssignment userRoleAssignment =
                resolver.createRoleAssignment(roleAssignmentRequest, roleTypePermission, roleAssignmentPermission,
                        getPersistentContext());
    }

    @DataProvider
    public static Object[][] listUsersDataProvider()
    {
        return new Object[][]{
                {null, null, false},
                {null, null, true},
                {"", "", false},
                {"", "", true},
                {"userId==" + ROLE_ASSIGNMENT_USER_ID, "", false},
                {"userId==" + ROLE_ASSIGNMENT_USER_ID, "", true}
        };
    }

    @Test
    @UseDataProvider("listUsersDataProvider")
    public void testListUsers(String query, String sort, Boolean addInitialRoleAssignment)
    {
        resolver.setRoleAssignmentObjectCache(getMockRoleAssignmentCache(ROLE_ASSIGNMENT_USER_ID));

        PagedResponse<UserRoleAssignment> response =
                resolver.listUsers(1, 5, query, sort, addInitialRoleAssignment, getPersistentContext());

        assertThat(response.getPage().getNumber(), equalTo(1));
        assertThat(response.getPage().getSize(), equalTo(5));
        assertThat(response.getPage().getTotalPages(), equalTo(1));
        assertThat(response.getPage().getTotalElements(), equalTo(1L));
        assertThat(response.getContent().size(), equalTo(1));

        UserRoleAssignment userRoleAssignment = response.getContent().get(0);
        assertThat(userRoleAssignment.getUserId(), equalTo(ROLE_ASSIGNMENT_USER_ID));
        assertThat(userRoleAssignment.getUsername(), equalTo(ROLE_ASSIGNMENT_USERNAME));
        assertThat(userRoleAssignment.getDisplayAddButton(), equalTo(addInitialRoleAssignment));
    }


    @DataProvider
    public static Object[][] hasRoleAssignmentWithSameAccountContextDataProvider()
    {
        CompoundId accountContext = new CompoundId(Constants.ACCOUNT_TYPE_PUBLISHER, AUTHORIZATION_RESOURCE_ID);
        return new Object[][]{
                {Constants.ACCOUNT_GROUP_ALL_PUBLISHER_ID, null, true, true},
                {Constants.ACCOUNT_GROUP_ALL_PUBLISHER_ID, null, false, false},
                {null, accountContext, true, true},
                {null, accountContext, false, false}
        };
    }

    @Test
    @UseDataProvider("hasRoleAssignmentWithSameAccountContextDataProvider")
    public void hasRoleAssignmentWithSameAccountContextTest(Long accountGroupId, CompoundId accountContext,
                                                            boolean hasRoleAssignment, boolean expected)
    {
        List<ServiceRoleAssignment> serviceRoleAssignmentList = new ArrayList<>();
        serviceRoleAssignmentList.add(getServiceRoleAssignment(ROLE_ASSIGNMENT_ID));

        if (hasRoleAssignment)
        {
            serviceRoleAssignmentList.add(getServiceRoleAssignment(ROLE_ASSIGNMENT_ID+1L));
        }

        RoleAssignmentObjectCache roleAssignmentCache = mock(RoleAssignmentObjectCache.class);
        Mockito.when(
                roleAssignmentCache.getPermissions(anyList(), Mockito.any(CompoundId.class), anySet()))
                .thenReturn(serviceRoleAssignmentList);
        resolver.setRoleAssignmentObjectCache(roleAssignmentCache);

        Assert.assertEquals(expected, resolver.hasRoleAssignmentWithSameAccountContext(
                getPersistentRoleAssignment(accountGroupId, accountContext)));
    }

    @DataProvider
    public static Object[][] performAuthorizationEntryChangeDataProvider()
    {
        return new Object[][]{
                {Constants.ACCOUNT_GROUP_ALL_PUBLISHER_ID, null, true},
                {Constants.ACCOUNT_GROUP_ALL_SEAT_ID, null, true},
                {Constants.ACCOUNT_GROUP_ALL_MARKETPLACE_VENDOR_ID, null, true},
                {-1L, null, false},
                {null, new CompoundId("publisher", "2763"), true},
                {null, new CompoundId("seat", "1234"), true},
                {null, new CompoundId(Constants.ACCOUNT_TYPE_MARKETPLACE_VENDOR, "1234"), true},
                {null, new CompoundId("partner", "1234"), false}
        };
    }

    @Test
    @UseDataProvider("performAuthorizationEntryChangeDataProvider")
    public void addAuthorizationEntryChangeTest(Long accountGroupId, CompoundId account,
                                                boolean triggered)
    {
        LeftoverDataClient leftoverDataClient = mock(LeftoverDataClient.class);
        doNothing().when(leftoverDataClient).addAuthorizationEntry(anyString(), anyString(), anyString());
        resolver.setLeftoverDataClient(leftoverDataClient);

        resolver.performAuthorizationEntryChange(USER_ID, accountGroupId, account,true);
        if (triggered)
        {
            verify(leftoverDataClient).addAuthorizationEntry(anyString(), anyString(), anyString());
        }
    }

    @Test
    @UseDataProvider("performAuthorizationEntryChangeDataProvider")
    public void deleteAuthorizationEntryChangeTest(Long accountGroupId, CompoundId account,
                                                   boolean triggered)
    {
        LeftoverDataClient leftoverDataClient = mock(LeftoverDataClient.class);
        doNothing().when(leftoverDataClient).deleteAuthorizationEntry(anyString(), anyString(), anyString());
        resolver.setLeftoverDataClient(leftoverDataClient);

        resolver.performAuthorizationEntryChange(USER_ID, accountGroupId, account,false);
        if (triggered)
        {
            verify(leftoverDataClient).deleteAuthorizationEntry(anyString(), anyString(), anyString());
        }
    }

    @DataProvider
    public static Object[][] assertAssignAccountGroupDataProvider()
    {
        return new Object[][]{
                {false, false, false, AccountGroupEnum.ALL_PUBLISHERS, true},
                {true, false, false, AccountGroupEnum.ALL_PUBLISHERS, false},
                {false, false, false, AccountGroupEnum.ALL_SEATS, true},
                {false, true, false, AccountGroupEnum.ALL_SEATS, false},
                {false, false, false, AccountGroupEnum.ALL_MARKETPLACE_VENDORS, true},
                {false, false, true, AccountGroupEnum.ALL_MARKETPLACE_VENDORS, false},
        };
    }

    @Test
    @UseDataProvider("assertAssignAccountGroupDataProvider")
    public void assertAssignAccountGroupTest(boolean canAssignAllPublisher, boolean canAssignAllSeat,
                                             boolean canAssignAllMarketplaceVendor,
                                             AccountGroupEnum accountGroup,
                                             boolean exceptionThrown)
    {
        if (exceptionThrown)
        {
            expectedException.expect(UnauthorizedException.class);
        }

        RoleAssignmentPermission roleAssignmentPermission = new RoleAssignmentPermission();
        roleAssignmentPermission.setAssignAllPublisher(canAssignAllPublisher);
        roleAssignmentPermission.setAssignAllSeat(canAssignAllSeat);
        roleAssignmentPermission.setAssignAllMarketplaceVendor(canAssignAllMarketplaceVendor);

        RoleAssignmentRequest request = new RoleAssignmentRequest();
        request.setAccountGroup(accountGroup);

        resolver.assertAssignAccountGroup(roleAssignmentPermission, request);
    }

    @Test
    public void getAssignedUsersTest()
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        PagedResponse<AssignedUser> pagedResponse =
                resolver.getAssignedUsers(DATA_SERVICE_ROLE_ID, 1, 20, roleTypePermission, true,
                        getPersistentContext());

        assertThat(pagedResponse, notNullValue());
        assertThat(pagedResponse.getPage(), notNullValue());
        assertThat(pagedResponse.getContent(), notNullValue());

        com.rubicon.platform.authorization.model.ui.acm.Page page = pagedResponse.getPage();
        assertThat(page.getSize(), equalTo(20));
        assertThat(page.getTotalPages(), equalTo(1));
        assertThat(page.getNumber(), equalTo(1));
        assertThat(page.getTotalElements(), equalTo(2L));

        List<AssignedUser> assignedUsers = pagedResponse.getContent();
        assertThat(assignedUsers.size(), equalTo(2));
        for (AssignedUser user : assignedUsers)
        {
            assertThat(user.getId(), equalTo(Long.parseLong(USER_ID)));
            assertThat(user.getName(), equalTo("<Unknown Username>"));
            assertThat(user.getStatus(), nullValue());
            assertThat(user.getRoleAssignmentId(), equalTo(DATA_SERVICE_ROLE_ASSIGNMENT_ID));
            assertThat(user.getEditable(), equalTo(false));
            assertThat(user.getAccount(), notNullValue());
            AccountReference accountReference = user.getAccount();

            assertThat(accountReference.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
            assertThat(accountReference.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
            assertThat(accountReference.getType(), equalTo(AccountReferenceTypeEnum.publisher));
        }
    }

    @DataProvider
    public static Object[][] getAssignedUsersWhenNoDataIsFoundDataProvider()
    {
        return new Object[][]{
                {2, 2, 0, 2L, 1, 2}, // When no results are found, but we still need to set up the count
                {0, 1, 20, 0L, 0, 1}, // when no results are found
        };
    }


    @Test
    @UseDataProvider("getAssignedUsersWhenNoDataIsFoundDataProvider")
    public void getAssignedUsersWhenNoDataIsFound(int maxQueryResults, Integer requestPageNumber, Integer expectedSize,
                                                  Long expectedTotalElements, Integer expectedTotalPages,
                                                  Integer expectedNumber)
    {
        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResults =
                getQueryResults(maxQueryResults);

        ServiceExceptionMappingDecorator persistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(persistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(null)
                .thenReturn(queryResults);

        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(persistenceOperations);
        resolver.setTranslator(roleAssignmentTranslator);
        resolver.setRoleAssignmentLoader(getMockRoleAssignmentLoader(getPersistentRoleAssignment()));
        resolver.setRoleLoader(getMockRoleLoader());
        resolver.setAccountLoader(getMockAccountLoader());
        resolver.setIdmUserDataClient(getIdmUserDataClient());
        resolver.setLeftoverDataClient(getLeftoverDataClient());


        RoleTypePermission roleTypePermission = new RoleTypePermission();
        PagedResponse<AssignedUser> pagedResponse =
                resolver.getAssignedUsers(DATA_SERVICE_ROLE_ID, requestPageNumber, 20, roleTypePermission, true,
                        getPersistentContext());


        assertThat(pagedResponse, notNullValue());
        assertThat(pagedResponse.getPage(), notNullValue());
        assertThat(pagedResponse.getContent(), notNullValue());
        assertThat(pagedResponse.getContent().size(), equalTo(0));

        com.rubicon.platform.authorization.model.ui.acm.Page page = pagedResponse.getPage();
        assertThat(page.getSize(), equalTo(expectedSize));
        assertThat(page.getTotalElements(), equalTo(expectedTotalElements));
        assertThat(page.getTotalPages(), equalTo(expectedTotalPages));
        assertThat(page.getNumber(), equalTo(expectedNumber));
    }

    @DataProvider
    public static Object[][] createDataRoleAssignmentDataProvider()
    {
        return new Object[][]{
                {null, DATA_SERVICE_ACCOUNT_GROUP_ID, Arrays.asList("sso:publisher/1234567")},
                {DATA_SERVICE_ACCOUNT_COMBO_ID, null, Arrays.asList("sso:publisher/1234567")},
        };
    }

    @Test
    @UseDataProvider("createDataRoleAssignmentDataProvider")
    public void createDataRoleAssignment(String account, Long accountGroupId, List<String> scope)
    {
        com.rubicon.platform.authorization.model.data.acm.RoleAssignment roleAssignment = getDataServiceRoleAssignment();
        roleAssignment.setRealm(REALM_NAME);
        roleAssignment.setOwnerAccount(OWNER_ACCOUNT);
        roleAssignment.setScope(scope);
        roleAssignment.setAccount(account);
        roleAssignment.setAccountGroupId(accountGroupId);

        RoleAssignmentServiceResolver resolver = new RoleAssignmentServiceResolver();
        resolver.setPersistenceOperations(
                setupUiServicePersistenceOperations(getQueryResults(MAX_QUERY_RESULTS), Arrays.asList(roleAssignment),
                        1, roleAssignment));

        com.rubicon.platform.authorization.model.data.acm.RoleAssignment created =
                resolver.createDataRoleAssignment(DATA_SERVICE_ROLE_ID, SUBJECT_ID.asIdString(), null,
                        DATA_SERVICE_ACCOUNT_COMBO_ID, null, getPersistentContext());

        assertEquals(Long.valueOf(DATA_SERVICE_ROLE_ASSIGNMENT_ID), roleAssignment.getId());
        assertEquals(Long.valueOf(DATA_SERVICE_ROLE_ID), roleAssignment.getRoleId());
        assertEquals(REALM_NAME, roleAssignment.getRealm());
        assertEquals(OWNER_ACCOUNT, roleAssignment.getOwnerAccount());

        if (account != null)
        {
            assertEquals(account, roleAssignment.getAccount());
        }
        else
        {
            assertNull(roleAssignment.getAccount());
        }

        if (accountGroupId != null)
        {
            assertEquals(accountGroupId, roleAssignment.getAccountGroupId());
        }
        else
        {
            assertNull(roleAssignment.getAccountGroupId());
        }

        assertEquals(scope, roleAssignment.getScope());
    }
    
    protected List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> getRoleAssignmentList(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignmentList = new ArrayList<>();
        for (int index = 0; index < resultCount; index++)
        {
            roleAssignmentList.add(getDataServiceRoleAssignment(ACCOUNT_REFERENCE_ACCOUNT));
        }

        return roleAssignmentList;
    }

    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> getQueryResults(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> roleAssignmentList =
                getRoleAssignmentList(resultCount);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleAssignment> queryResult =
                new QueryResult<>();
        queryResult.setItems(roleAssignmentList);
        queryResult.setResponseCount(roleAssignmentList.size());
        queryResult.setStart(1);
        queryResult.setTotalCount(resultCount);

        return queryResult;
    }

    protected User getUser()
    {
        User user = new User();
        user.setUsername(ROLE_ASSIGNMENT_USERNAME);
        user.setId(ROLE_ASSIGNMENT_USER_ID);

        return user;
    }

    protected IdmUserDataClient getIdmUserDataClient()
    {
        return getIdmUserDataClient(false, getUser());
    }

    protected IdmUserDataClient getIdmUserDataClient(boolean throwRequestError, User user)
    {
        IdmUserDataClient idmUserDataClient = Mockito.mock(IdmUserDataClient.class);

        if (throwRequestError)
        {
            when(idmUserDataClient.getUsers(
                    anyInt(), anyInt(), anyString(), anyString(), any(DataUserContext.class)))
                    .thenThrow(ServiceUnavailableException.class);
            when(idmUserDataClient.getUserById(
                    anyLong(), any(DataUserContext.class)))
                    .thenThrow(ServiceUnavailableException.class);
        }
        else
        {

            EntityResponse<User> userEntityResponse = buildEntityResponse(user);

            when(idmUserDataClient.getUsers(
                    anyInt(), anyInt(), anyString(), anyString(), any(DataUserContext.class)))
                    .thenReturn(userEntityResponse);
            when(idmUserDataClient.getUserById(
                    anyLong(), any(DataUserContext.class)))
                    .thenReturn(user);
        }

        return idmUserDataClient;
    }


    protected PersistentRoleAssignment getPersistentRoleAssignment()
    {
        PersistentRoleAssignment roleAssignment = new PersistentRoleAssignment();
        roleAssignment.setRoleId(DATA_SERVICE_ROLE_ID);
        roleAssignment.setId(DATA_SERVICE_ROLE_ASSIGNMENT_ID);

        return roleAssignment;
    }

    protected PersistentAccount getPersistentAccount()
    {
        PersistentAccount account = new PersistentAccount();
        account.setId(DATA_SERVICE_ACCOUNT_ID);
        account.setAccountName(DATA_SERVICE_ACCOUNT_NAME);
        account.setAccountId(new CompoundId(DATA_SERVICE_ACCOUNT_COMBO_ID));
        account.setStatus(DATA_SERVICE_STATUS);

        return account;
    }

    protected PersistentRole getPersistentRole()
    {
        PersistentRole role = new PersistentRole();
        role.setId(DATA_SERVICE_ROLE_ID);
        role.setRoleTypeId(DATA_SERVICE_ROLE_TYPE_ID);

        return role;
    }


    protected RoleAssignmentLoader getMockRoleAssignmentLoader(PersistentRoleAssignment roleAssignment)
    {
        RoleAssignmentLoader roleAssignmentLoader = spy(new RoleAssignmentLoader());

        doReturn(roleAssignment).when(roleAssignmentLoader).find(DATA_SERVICE_ROLE_ASSIGNMENT_ID);
        doReturn(null).when(roleAssignmentLoader).find(INVALID_DATA_ID);
        doReturn(Collections.singletonList(roleAssignment.getId())).when(roleAssignmentLoader)
                .getRoleAssignmentByRoleId(anyLong());

        return roleAssignmentLoader;
    }


    protected RoleLoader getMockRoleLoader()
    {
        RoleLoader roleLoader = spy(new RoleLoader());

        doReturn(getPersistentRole()).when(roleLoader).find(DATA_SERVICE_ROLE_ID);
        doReturn(null).when(roleLoader).find(INVALID_DATA_ID);

        return roleLoader;
    }

    protected AccountLoader getMockAccountLoader()
    {
        AccountLoader accountLoader = spy(new AccountLoader());

        doReturn(getPersistentAccount()).when(accountLoader).find(anyLong());

        return accountLoader;
    }

    protected RoleAssignmentObjectCache getMockRoleAssignmentCache(Long userId)
    {
        SubjectIdMap subjectIdMap = new SubjectIdMap();
        subjectIdMap.mapIdToSubject(DATA_SERVICE_ROLE_ASSIGNMENT_ID, "subject/" + userId);
        RoleAssignmentObjectCache roleAssignmentCache = Mockito.mock(RoleAssignmentObjectCache.class);
        when(roleAssignmentCache.getIdMap()).thenReturn(subjectIdMap);
        when(roleAssignmentCache.getPermissions(anyList(), any(CompoundId.class), anySet())).thenReturn(new ArrayList<>());
        return roleAssignmentCache;
    }

    protected LeftoverDataClient getLeftoverDataClient()
    {
        LeftoverDataClient leftoverDataClient = mock(LeftoverDataClient.class);
        doNothing().when(leftoverDataClient).addAuthorizationEntry(anyString(), anyString(), anyString());
        doNothing().when(leftoverDataClient).deleteAuthorizationEntry(anyString(), anyString(), anyString());

        return leftoverDataClient;
    }

    protected <T extends ApiObject> EntityResponse<T> buildEntityResponse(T responseObject)
    {
        EntityResponse<T> entityResponse = new EntityResponse<T>();
        if (responseObject == null)
        {
            Page page = new Page();
            page.setStart(0);
            page.setResponseCount(0);
            page.setTotalCount(0L);
            entityResponse.setPage(page);
            entityResponse.setEntries(new ArrayList<T>());
        }
        else
        {
            Page page = new Page();
            page.setStart(1);
            page.setResponseCount(1);
            page.setTotalCount(1L);
            entityResponse.setPage(page);
            entityResponse.setEntries(Arrays.asList(responseObject));
        }


        return entityResponse;
    }

}
