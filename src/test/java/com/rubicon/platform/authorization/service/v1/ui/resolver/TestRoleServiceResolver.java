package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.exception.ValidationException;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.RoleTranslator;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TestRoleServiceResolver extends TestAbstract
{
    public static Integer DATA_SERVICE_TOTAL_COUNT = 20;
    public static int TOTAL_RECORDS_IN_RESULTS = 1;
    public static final String NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG =
            "You are attempting to create/modify a role for a role type you do not have access to.";
    protected PersistenceContext context = new PersistenceContext();
    protected RoleServiceResolver resolver = new RoleServiceResolver();
    protected RoleTranslator roleTranslator;

    @Before
    public void setup()
    {
        roleTranslator = new RoleTranslator();
        roleTranslator.init();

        context.setEntityPlugin(new EntityPlugin());

        resolver = new RoleServiceResolver();
        resolver.setPersistenceOperations(setupPersistenceOperations(TOTAL_RECORDS_IN_RESULTS));
        resolver.setTranslator(roleTranslator);
    }

    @Test
    public void testGetList()
    {
        int resultCount = 2;
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        resolver.setPersistenceOperations(setupPersistenceOperations(resultCount));

        PersistenceContext context = new PersistenceContext();

        PagedResponse<Role> pagedResponse =
                resolver.getList(1, DATA_SERVICE_TOTAL_COUNT, null, null, roleTypePermission, context);

        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getPage());
        assertNotNull(pagedResponse.getContent());

        Page page = pagedResponse.getPage();
        assertThat(DATA_SERVICE_TOTAL_COUNT, equalTo(page.getSize()));
        assertThat(1, equalTo(page.getTotalPages()));
        assertThat(1, equalTo(page.getNumber()));

        // We are getting one cause the default
        List<Role> roleList = pagedResponse.getContent();
        assertThat(2, equalTo(roleList.size()));
        Role role = roleList.get(0);

        assertThat(DATA_SERVICE_ROLE_ID, equalTo(role.getId()));
        assertThat(DATA_SERVICE_ROLE_NAME, equalTo(role.getName()));
        assertThat(RoleTypeEnum.getById(DATA_SERVICE_ROLE_TYPE_ID), equalTo(role.getType()));

        List<Operation> operations = role.getAllowedOperations();

        for (Operation operation : operations)
        {
            assertThat(OPERATION_SERVICE, equalTo(operation.getService()));
            assertThat(OPERATION_RESOURCE, equalTo(operation.getResource()));
            assertThat(OPERATION_ACTION, equalTo(operation.getAction()));
        }
    }

    @Test
    public void testGetById()
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        resolver.setPersistenceOperations(setupPersistenceOperations(1));

        PersistenceContext context = new PersistenceContext();

        Role role = resolver.getById(15L, roleTypePermission, context);

        assertThat(DATA_SERVICE_ROLE_ID, equalTo(role.getId()));
        assertThat(DATA_SERVICE_ROLE_NAME, equalTo(role.getName()));
        assertThat(RoleTypeEnum.getById(DATA_SERVICE_ROLE_TYPE_ID), equalTo(role.getType()));

        List<Operation> allowedOperations = role.getAllowedOperations();

        for (Operation operation : allowedOperations)
        {
            assertThat(OPERATION_SERVICE, equalTo(operation.getService()));
            assertThat(OPERATION_RESOURCE, equalTo(operation.getResource()));
            assertThat(OPERATION_ACTION, equalTo(operation.getAction()));
        }

        List<Operation> deniedOperations = role.getDeniedOperations();

        for (Operation operation : deniedOperations)
        {
            assertThat(OPERATION_SERVICE, equalTo(operation.getService()));
            assertThat(OPERATION_RESOURCE, equalTo(operation.getResource()));
            assertThat(OPERATION_ACTION, equalTo(operation.getAction()));
        }


    }

    @DataProvider
    public static Object[][] testCreateDataProvider()
    {
        return new Object[][]{
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.buyer), false, false, false, false, false, false,
                        false, false,
                        UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.buyer), true, false, false, false, false, false,
                        false, false,
                        null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.internal), false, false, false, false, false,
                        false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.internal), false, true, false, false, false,
                        false, false, false, null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.seller), false, false, false, false, false, false,
                        false, false,
                        UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.seller), false, false, true, false, false, false,
                        false, false,
                        null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.service), false, false, false, false, false,
                        false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.service), false, false, false, true, false, false,
                        false, false,
                        null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.product), false, false, false, false, false,
                        false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.product), false, false, false, false, true, false,
                        false, false,
                        null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.marketplace_vendor), false, false, false, false,
                        false, false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.marketplace_vendor), false, false, false, false,
                        false, true, false, false, null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_seat), false, false, false, false,
                        false, false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_seat), false, false, false, false,
                        false, false, true, false, null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_buyer), false, false, false, false,
                        false, false, false, false, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_buyer), false, false, false, false,
                        false, false, false, true, null, null}
        };
    }

    @Test
    @UseDataProvider("testCreateDataProvider")
    public void testCreate(RoleRequest request, Boolean canEditBuyer, Boolean canEditInternal,
                           Boolean canEditSeller, Boolean canEditService, Boolean canEditProduct,
                           Boolean canEditMarketplaceVendor, Boolean canEditStreamingSeat,
                           Boolean canEditStreamingBuyer,
                           Class<Exception> exceptionClass, String exceptionMessage)
    {
        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }

        RoleTypePermission roleTypePermission =
                buildRoleTypePermission(canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                        canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer);
        Role role = resolver.create(request, roleTypePermission, context);

        assertThat(role, notNullValue());
        assertThat(role.getId(), equalTo(DATA_SERVICE_ROLE_ID));
        assertThat(role.getName(), equalTo(request.getName()));
        assertThat(role.getType(), equalTo(request.getType()));
        assertThat(role.getAllowedOperations(), nullValue());
        assertThat(role.getEditable(), equalTo(roleTypePermission.isRoleTypeEditable(request.getType())));
    }

    @DataProvider
    public static Object[][] editOperationsDataProvider()
    {
        return new Object[][]{
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, true),
                        EditActionEnum.add), false, 1, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new EditRoleOperationRequest(null, getUiOperation(false, false, false, false, true),
                        EditActionEnum.add), false, 1, ValidationException.class, "id is required"},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, true),
                        null), false, 1, ValidationException.class, "action is required"},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, true, false, false, true),
                        EditActionEnum.add), false, 1, ValidationException.class, "operation service is required"},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, true, false, true),
                        EditActionEnum.add), false, 1, ValidationException.class, "operation resource is required"},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, true, true),
                        EditActionEnum.add), false, 1, ValidationException.class, "operation action is required"},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, false),
                        EditActionEnum.add), true, 1, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(true, false, false, false, false),
                        EditActionEnum.add), true, 2, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, false),
                        EditActionEnum.remove), true, 0, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(true, false, false, false, true),
                        EditActionEnum.remove), true, 1, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, false),
                        EditActionEnum.add, EditOperationEnum.denied), true, 1, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(true, false, false, false, false),
                        EditActionEnum.add, EditOperationEnum.denied), true, 2, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, false),
                        EditActionEnum.remove, EditOperationEnum.denied), true, 0, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(true, false, false, false, true),
                        EditActionEnum.remove, EditOperationEnum.denied), true, 1, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, false),
                        EditActionEnum.edit), true, 1, null, null},
                {new EditRoleOperationRequest(DATA_SERVICE_ROLE_ID, getUiOperation(false, false, false, false, true),
                        EditActionEnum.edit), true, 1, null, null}
        };
    }

    @Test
    @UseDataProvider("editOperationsDataProvider")
    public void testEditOperations(EditRoleOperationRequest request, Boolean canEditInternal,
                                   Integer expectedOperations, Class<Exception> exceptionClass,
                                   String exceptionMessage)
    {
        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }

        RoleTypePermission roleTypePermission =
                buildRoleTypePermission(false, canEditInternal, false, false, false, false, false, false);
        Role role = resolver.editOperations(request, roleTypePermission, context);

        assertThat(role, notNullValue());

        List<Operation> operations = EditOperationEnum.denied.equals(request.getOperationType())
                                     ? role.getDeniedOperations()
                                     : role.getAllowedOperations();
        switch (expectedOperations)
        {
            case 2:
                assertThat(operations.get(1).getService(), equalTo(OPERATION_2_SERVICE));
                assertThat(operations.get(1).getResource(), equalTo(OPERATION_2_RESOURCE));
                assertThat(operations.get(1).getAction(), equalTo(OPERATION_2_ACTION));
                assertThat(operations.get(1).getProperties(), equalTo(OPERATION_2_PROPERTIES));
            case 1:
                assertThat(operations.size(), equalTo(expectedOperations));
                assertThat(operations.get(0).getService(), equalTo(OPERATION_SERVICE));
                assertThat(operations.get(0).getResource(), equalTo(OPERATION_RESOURCE));
                assertThat(operations.get(0).getAction(), equalTo(OPERATION_ACTION));
                assertThat(operations.get(0).getProperties(), equalTo(OPERATION_PROPERTIES));
                break;
            case 0:
                assertThat(operations, nullValue());
                break;
        }
    }


    @DataProvider
    public static Object[][] testUpdateDataProvider()
    {
        return new Object[][]{
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.buyer), false, false, false,
                        false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.buyer), true, false, false,
                        false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.internal), false, false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.internal), false, true,
                        false, false, false, false, false, false, null, null},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.seller), false, false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.seller), false, false,
                        true, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.service), false, false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.service), false, false,
                        false, true, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.product), false, false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.product), false, false,
                        false, false, true, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.marketplace_vendor), false,
                        false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.marketplace_vendor), false,
                        false,
                        false, false, false, true, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_seat), false,
                        false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_seat), false,
                        false,
                        false, false, false, false, true, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_buyer), false,
                        false,
                        false, false, false, false, false, false, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {new RoleRequest(DATA_SERVICE_ROLE_ID, DATA_SERVICE_ROLE_NAME, RoleTypeEnum.streaming_buyer), false,
                        false,
                        false, false, false, false, false, true, UnauthorizedException.class,
                        NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG}
        };
    }

    @Test
    @UseDataProvider("testUpdateDataProvider")
    public void testUpdate(RoleRequest request, Boolean canEditBuyer, Boolean canEditInternal,
                           Boolean canEditSeller, Boolean canEditService, Boolean canEditProduct,
                           Boolean canEditMarketplaceVendor, Boolean canEditStreamingSeat,
                           Boolean canEditStreamingBuyer,
                           Class<Exception> exceptionClass, String exceptionMessage)
    {
        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }

        RoleTypePermission roleTypePermission =
                buildRoleTypePermission(canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                        canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer);
        Role role = resolver.update(request, roleTypePermission, context);

        assertThat(role, notNullValue());
        assertThat(role.getId(), equalTo(DATA_SERVICE_ROLE_ID));
        assertThat(role.getName(), equalTo(request.getName()));
        assertThat(role.getType(), equalTo(request.getType()));
        assertThat(role.getEditable(), equalTo(roleTypePermission.isRoleTypeEditable(request.getType())));
    }

    @DataProvider
    public static Object[][] removeDataProvider()
    {
        return new Object[][]{
                {null, false, true, ValidationException.class, "roleId is required"},
                {DATA_SERVICE_ROLE_ID, false, true, UnauthorizedException.class, NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG},
                {DATA_SERVICE_ROLE_ID, true, false, null, null}
        };
    }

    @Test
    @UseDataProvider("removeDataProvider")
    public void testRemove(Long roleId, boolean canEditInternal, boolean exceptionThrown,
                           Class<? extends ServiceException> classType, String exceptionMsg)
    {
        if (exceptionThrown)
        {
            expectedException.expect(classType);
            expectedException.expectMessage(exceptionMsg);
        }

        RoleTypePermission roleTypePermission =
                buildRoleTypePermission(false, canEditInternal, false, false, false, false, false, false);

        resolver.remove(roleId, roleTypePermission, context);
    }


    protected ServiceExceptionMappingDecorator setupPersistenceOperations(int resultCount)
    {
        ServiceExceptionMappingDecorator persistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(persistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(getQueryResults(resultCount));


        when(persistenceOperations.findByIds((List) any(), (PersistenceContext) any()))
                .thenReturn(getRoleList(1));

        when(persistenceOperations.createOrUpdateItem((ApiObject) any(), (PersistenceContext) any()))
                .thenAnswer(new Answer<ApiObject>()
                {
                    @Override
                    public ApiObject answer(InvocationOnMock invocation) throws Throwable
                    {
                        com.rubicon.platform.authorization.model.data.acm.Role role =
                                (com.rubicon.platform.authorization.model.data.acm.Role) invocation.getArguments()[0];
                        role.setId(DATA_SERVICE_ROLE_ID);
                        return role;
                    }
                });

        when(persistenceOperations.updateItem(anyList(), (ApiObject) any(), (PersistenceContext) any()))
                .thenAnswer(new Answer<ApiObject>()
                {
                    @Override
                    public ApiObject answer(InvocationOnMock invocation) throws Throwable
                    {
                        return (com.rubicon.platform.authorization.model.data.acm.Role) invocation.getArguments()[1];
                    }
                });

        return persistenceOperations;
    }


    protected List<com.rubicon.platform.authorization.model.data.acm.Role> getRoleList(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.Role> roleList = new ArrayList<>();
        for (int index = 0; index < resultCount; index++)
        {
            roleList.add(getDataServiceRole());
        }

        return roleList;
    }


    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.Role> getQueryResults(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.Role> roleList = getRoleList(resultCount);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.Role> queryResult =
                new QueryResult<>();
        queryResult.setItems(roleList);
        queryResult.setResponseCount(roleList.size());
        queryResult.setStart(1);
        queryResult.setTotalCount(DATA_SERVICE_TOTAL_COUNT);

        return queryResult;
    }

}
