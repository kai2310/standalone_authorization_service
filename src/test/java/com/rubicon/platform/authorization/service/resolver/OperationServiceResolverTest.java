package com.rubicon.platform.authorization.service.resolver;

import com.dottydingo.hyperion.core.persistence.ExceptionMappingDecorator;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.v1.resource.OperationServiceResolver;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import com.rubicon.platform.authorization.model.data.acm.Role;
import com.rubicon.platform.authorization.model.api.acm.operation.OperationRequest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class OperationServiceResolverTest
{
    private final static String TEST_OPERATION_SERVICE = "acm";
    private final static String TEST_OPERATION_RESOURCE = "operation";
    private final static String TEST_OPERATION_WITH_PROPERTIES_ACTION = "upsert";
    private final static String TEST_OPERATION_WITHOUT_PROPERTIES_ACTION = "get";
    private final static String TEST_OPERATION_WITH_DUPLICATE_ACTION = "duplicate";
    private final static String NEW_OPERATION_ACTION = "post";

    private final static Long NON_EXISTING_ID = 9999L;
    private final static Long VALID_ID = 100L;
    private final static Long DUPLICATE_ID = 200L;
    private final static List<String> TEST_OPERATION_PROPERTIES = Arrays.asList("1", "2");


    private ExceptionMappingDecorator rolePersistenceOperations;
    private ExceptionMappingDecorator accountFeaturePersistenceOperations;
    private OperationServiceResolver operationServiceResolver;
    private PersistenceContext context;

    @Before
    public void setup()
    {
        // setup persistence context
        context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());

        rolePersistenceOperations = Mockito.mock(ExceptionMappingDecorator.class);
        when(rolePersistenceOperations.findByIds(Arrays.asList(NON_EXISTING_ID), context)).thenReturn(null);
        when(rolePersistenceOperations.findByIds(Arrays.asList(VALID_ID), context)).thenReturn(Arrays.asList(getTestRole()));
        when(rolePersistenceOperations.findByIds(Arrays.asList(DUPLICATE_ID), context)).thenReturn(Arrays.asList(getTestRoleWithDuplicatedOperations()));

        accountFeaturePersistenceOperations = Mockito.mock(ExceptionMappingDecorator.class);
        when(accountFeaturePersistenceOperations.findByIds(Arrays.asList(NON_EXISTING_ID), context)).thenReturn(null);
        when(accountFeaturePersistenceOperations.findByIds(Arrays.asList(VALID_ID), context)).thenReturn(Arrays.asList(getTestAccountFeature()));
        when(accountFeaturePersistenceOperations.findByIds(Arrays.asList(DUPLICATE_ID), context)).thenReturn(Arrays.asList(getTestAccountFeatureWithDuplicatedOperation()));

        operationServiceResolver = new OperationServiceResolver();
        operationServiceResolver.setRolePersistenceOperations(rolePersistenceOperations);
        operationServiceResolver.setAccountFeaturePersistenceOperations(accountFeaturePersistenceOperations);
    }

    @Test(expected = ServiceException.class)
    @UseDataProvider("operationRequestWIthInvalidIdOrOperation")
    public void testUpsertOperationToRoleWithInvalidRoleId(OperationRequest operationRequest)
    {
        operationServiceResolver.upsertOperationToRole(operationRequest, context);
    }

    @Test(expected = ServiceException.class)
    @UseDataProvider("operationRequestWIthInvalidIdOrOperation")
    public void testUpsertOperationToAccountFeatureWithInvalidRoleId(OperationRequest operationRequest)
    {
        operationServiceResolver.upsertOperationToAccountFeature(operationRequest, context);
    }

    @Test
    public void testAddOperationToRole()
    {
        when(rolePersistenceOperations.updateItem(eq(Arrays.asList(VALID_ID)), isA(Role.class), isA(PersistenceContext.class))).thenReturn(getRoleWithNewOperation());

        Role actual = operationServiceResolver.upsertOperationToRole(getOperationRequestWithNewOperationToAdd(), context);
        verify(rolePersistenceOperations).findByIds(eq(Arrays.asList(VALID_ID)), isA(PersistenceContext.class));
        verify(rolePersistenceOperations).updateItem(isA(List.class), isA(Role.class), isA(PersistenceContext.class));

        CollectionUtils.isEqualCollection(actual.getAllowedOperations(), getAllowedOperationsWithNewOperation());
    }

    @Test
    public void testAddOperationToAccountFeature()
    {
        when(accountFeaturePersistenceOperations.updateItem(eq(Arrays.asList(VALID_ID)), isA(AccountFeature.class), isA(PersistenceContext.class))).thenReturn(getAccountFeatureWithNewOperation());

        AccountFeature actual = operationServiceResolver.upsertOperationToAccountFeature(getOperationRequestWithNewOperationToAdd(), context);
        verify(accountFeaturePersistenceOperations).findByIds(eq(Arrays.asList(VALID_ID)), isA(PersistenceContext.class));
        verify(accountFeaturePersistenceOperations).updateItem(isA(List.class), isA(AccountFeature.class), isA(PersistenceContext.class));

        CollectionUtils.isEqualCollection(actual.getAllowedOperations(), getAllowedOperationsWithNewOperation());
    }

    @Test
    public void testUpdateOperationInRole()
    {
        when(rolePersistenceOperations.updateItem(eq(Arrays.asList(VALID_ID)), isA(Role.class), isA(PersistenceContext.class)))
                .thenReturn(getRoleWithOperationUpdate());

        Role actual = operationServiceResolver.upsertOperationToRole(getOperationUpdateWithOperationToUpdate(), context);
        verify(rolePersistenceOperations).findByIds(eq(Arrays.asList(VALID_ID)), isA(PersistenceContext.class));

        CollectionUtils.isEqualCollection(actual.getAllowedOperations(), getUpdatedAllowedOperations());
    }

    @Test
    public void testUpdateOperationInAccountFeature()
    {
        when(accountFeaturePersistenceOperations.updateItem(eq(Arrays.asList(VALID_ID)), isA(AccountFeature.class), isA(PersistenceContext.class)))
                .thenReturn(getAccountFeatureWithOperationUpdate());

        AccountFeature actual = operationServiceResolver.upsertOperationToAccountFeature(getOperationUpdateWithOperationToUpdate(), context);
        verify(accountFeaturePersistenceOperations).findByIds(eq(Arrays.asList(VALID_ID)), isA(PersistenceContext.class));

        CollectionUtils.isEqualCollection(actual.getAllowedOperations(), getUpdatedAllowedOperations());
    }

    @DataProvider
    public static Object[][] operationRequestWIthInvalidIdOrOperation()
    {
        return new Object[][]{{getOperationRequestWithNullRoleId()}, {getOperationRequestWithNonExistingRoleId()},
                {getOperationRequestWithNullOperation()}, {getOperationRequestWithMissingService()},
                {getOperationRequestWithMissingResource()}, {getOperationRequestWithMissingAction()},
                {getOperationRequestWithInvalidProperties()}, {getOperationRequestWithNullProperties()},
                {getOperationRequestWithInvalidMatchedOperation()}, {getOperationRequestWithDuplicatedMatch()},
                {getOperationRequestWithEmptyStringProperties()}, {getOperationRequestWithAllSpaceProperties()}};
    }

    private static Operation getTestOperationWithProperties()
    {
        return generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_PROPERTIES_ACTION, TEST_OPERATION_PROPERTIES);
    }

    private static Operation getTestOperationWithoutProperties()
    {

        return generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITHOUT_PROPERTIES_ACTION, new ArrayList<String>());
    }

    private static Operation getTestDuplicatedOperation()
    {
        return generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_DUPLICATE_ACTION, TEST_OPERATION_PROPERTIES);
    }

    private static OperationRequest getOperationRequestWithNullRoleId()
    {
        return generateOperationRequest(null, getTestOperationWithProperties());
    }

    private static OperationRequest getOperationRequestWithNonExistingRoleId()
    {
        return generateOperationRequest(NON_EXISTING_ID, getTestOperationWithProperties());
    }

    private static OperationRequest getOperationRequestWithNullOperation()
    {
        return generateOperationRequest(VALID_ID, null);
    }

    private static OperationRequest getOperationRequestWithMissingService()
    {
        return generateOperationRequest(VALID_ID, generateOperation("", TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_PROPERTIES_ACTION, TEST_OPERATION_PROPERTIES));
    }

    private static OperationRequest getOperationRequestWithMissingResource()
    {

        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, "", TEST_OPERATION_WITH_PROPERTIES_ACTION, TEST_OPERATION_PROPERTIES));
    }

    private static OperationRequest getOperationRequestWithMissingAction()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, "", TEST_OPERATION_PROPERTIES));
    }

    private static OperationRequest getOperationRequestWithNullProperties()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_PROPERTIES_ACTION, null));
    }

    private static OperationRequest getOperationRequestWithInvalidProperties()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_PROPERTIES_ACTION, new ArrayList<String>()));
    }

    private static OperationRequest getOperationRequestWithInvalidMatchedOperation()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITHOUT_PROPERTIES_ACTION, TEST_OPERATION_PROPERTIES));
    }

    private static OperationRequest getOperationRequestWithEmptyStringProperties()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITHOUT_PROPERTIES_ACTION, Arrays.asList("")));
    }

    private static OperationRequest getOperationRequestWithAllSpaceProperties()
    {
        return generateOperationRequest(VALID_ID, generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITHOUT_PROPERTIES_ACTION, Arrays.asList("  ")));
    }

    private Operation getNewOperationToAdd()
    {
        return generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, NEW_OPERATION_ACTION, TEST_OPERATION_PROPERTIES);
    }

    private OperationRequest getOperationRequestWithNewOperationToAdd()
    {
        return generateOperationRequest(VALID_ID, getNewOperationToAdd());
    }

    private Operation getOperationToUpdate()
    {
        return generateOperation(TEST_OPERATION_SERVICE, TEST_OPERATION_RESOURCE, TEST_OPERATION_WITH_PROPERTIES_ACTION, Arrays.asList("1"));
    }

    private OperationRequest getOperationUpdateWithOperationToUpdate()
    {
        return generateOperationRequest(VALID_ID, getOperationToUpdate());
    }

    private static OperationRequest getOperationRequestWithDuplicatedMatch()
    {
        return generateOperationRequest(DUPLICATE_ID, getTestDuplicatedOperation());
    }

    private Role getTestRole()
    {
        Role role = new Role();
        role.setId(VALID_ID);
        role.setAllowedOperations(getAllowedOperationsList());

        return role;
    }

    private Role getTestRoleWithDuplicatedOperations()
    {
        Role duplicated = new Role();
        duplicated.setId(DUPLICATE_ID);
        List<Operation> allowed = getAllowedOperationsList();
        allowed.add(getTestDuplicatedOperation());
        allowed.add(getTestDuplicatedOperation());
        duplicated.setAllowedOperations(allowed);

        return duplicated;
    }

    private AccountFeature getTestAccountFeature()
    {
        AccountFeature accountFeature = new AccountFeature();
        accountFeature.setId(VALID_ID);
        accountFeature.setAllowedOperations(getAllowedOperationsList());

        return accountFeature;
    }

    private AccountFeature getTestAccountFeatureWithDuplicatedOperation()
    {
        AccountFeature duplicated = new AccountFeature();
        duplicated.setId(DUPLICATE_ID);
        List<Operation> allowed = getAllowedOperationsList();
        allowed.add(getTestDuplicatedOperation());
        allowed.add(getTestDuplicatedOperation());
        duplicated.setAllowedOperations(allowed);

        return duplicated;
    }

    private Role getRoleWithNewOperation()
    {
        Role role = getTestRole();
        role.getAllowedOperations().add(getNewOperationToAdd());
        return role;
    }

    private AccountFeature getAccountFeatureWithNewOperation()
    {
        AccountFeature accountFeature = getTestAccountFeature();
        accountFeature.getAllowedOperations().add(getNewOperationToAdd());
        return accountFeature;
    }

    private Role getRoleWithOperationUpdate()
    {
        Role role = getTestRole();
        role.setAllowedOperations(getUpdatedAllowedOperations());

        return role;
    }

    private AccountFeature getAccountFeatureWithOperationUpdate()
    {
        AccountFeature accountFeature = getTestAccountFeature();
        accountFeature.setAllowedOperations(getUpdatedAllowedOperations());

        return accountFeature;
    }

    private List<Operation> getAllowedOperationsList()
    {
        return new ArrayList<>(Arrays.asList(getTestOperationWithProperties(), getTestOperationWithoutProperties()));
    }

    private List<Operation> getAllowedOperationsWithNewOperation()
    {
        return new ArrayList<>(Arrays.asList(getTestOperationWithProperties(), getTestOperationWithoutProperties(), getNewOperationToAdd()));
    }

    private List<Operation> getUpdatedAllowedOperations()
    {
        return new ArrayList<>(Arrays.asList(getOperationToUpdate(), getTestOperationWithoutProperties()));
    }

    private static OperationRequest generateOperationRequest(Long id, Operation operation)
    {
        OperationRequest operationRequest = new OperationRequest();
        operationRequest.setId(id);
        operationRequest.setOperation(operation);

        return operationRequest;
    }

    private static Operation generateOperation(String service, String resource, String action, List<String> properties)
    {
        Operation operation = new Operation();
        operation.setService(service);
        operation.setResource(resource);
        operation.setAction(action);
        operation.setProperties(properties);
        return operation;
    }
}
