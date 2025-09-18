package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.exception.*;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.test.testmodel.FakeAPIModel;
import com.rubicon.platform.authorization.test.testmodel.FakeDataModel;
import com.rubicon.platform.authorization.test.testmodel.FakeServiceResolver;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static com.rubicon.platform.authorization.service.utils.Constants.HYPERION_PAGING_KEY_LIMIT;
import static com.rubicon.platform.authorization.service.utils.Constants.HYPERION_PAGING_KEY_START;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class TestBaseServiceResolver extends TestAbstract
{
    private FakeServiceResolver serviceResolver;

    @Before
    public void setup()
    {
        serviceResolver = new FakeServiceResolver();
    }

    @Test
    public void testBuildPagedResponse()
    {
        QueryResult<FakeDataModel> queryResult = new QueryResult<>();
        queryResult.setStart(1);
        queryResult.setTotalCount(2);
        queryResult.setResponseCount(2);
        queryResult
                .setItems(Arrays.asList(new FakeDataModel(1L, "Fake Model 1"), new FakeDataModel(2L, "Famek Model 2")));

        PagedResponse<FakeAPIModel> pagedResponse = serviceResolver.buildPagedResponse(queryResult, 5);

        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getPage());
        assertNotNull(pagedResponse.getContent());

        Page page = pagedResponse.getPage();
        assertThat(page.getSize(), equalTo(5));
        assertThat(page.getNumber(), equalTo(1));
        assertThat(page.getTotalPages(), equalTo(1));
        assertThat(page.getTotalElements(), equalTo(2L));

        List<FakeAPIModel> content = pagedResponse.getContent();
        assertThat(content.size(), equalTo(2));
    }

    @DataProvider
    public static Object[][] buildQueryExpressionDataProvider()
    {
        return new Object[][]{
                {"status!=deleted"},
                {"blue==green;yellow!=purple"},
                {"blue==yellow,yellow==blue"},
        };
    }

    @Test
    @UseDataProvider("buildQueryExpressionDataProvider")
    public void testBuildQueryExpression(String queryString)
    {
        serviceResolver = new FakeServiceResolver();

        // Just Verify No errors are thrown
        serviceResolver.buildQueryExpression(queryString);
    }

    @DataProvider
    public static Object[][] buildQueryExpressionWithExceptionDataProvider()
    {
        return new Object[][]{
                {";status==blue"},
                {"blue=="},
                {"blue=green"},
                {"blue!==green"},
                {"blue==yellow&&yellow==blue"},
                {"blue==yellow||yellow==blue"},
        };
    }

    @UseDataProvider("buildQueryExpressionWithExceptionDataProvider")
    @Test(expected = BadRequestException.class)
    public void testBuildQueryExpressionExpectingException(String queryString)
    {
        serviceResolver = new FakeServiceResolver();

        // Just Verify No errors are thrown
        serviceResolver.buildQueryExpression(queryString);
    }

    @DataProvider
    public static Object[][] translatePagingToHyperionDataProvider()
    {
        return new Object[][]{
                // PAGE, SIZE, START, LIMIT
                {1, 20, 1, 20},
                {2, 20, 21, 20},
                {3, 5, 11, 5},
                {25, 20, 481, 20}
        };
    }

    @Test
    @UseDataProvider("translatePagingToHyperionDataProvider")
    public void testTestTranslatePagingToHyperion(Integer page, Integer size, Integer start, Integer limit)
    {
        Map<String, Integer> hyperionMap = serviceResolver.translatePagingToHyperion(page, size);

        assertThat(hyperionMap.get(HYPERION_PAGING_KEY_START), equalTo(start));
        assertThat(hyperionMap.get(HYPERION_PAGING_KEY_LIMIT), equalTo(limit));
    }


    @DataProvider
    public static Object[][] assertListHasOneItemDataProvider()
    {
        FakeDataModel oneFakeDataModel = new FakeDataModel();
        FakeDataModel twoFakeDataModel = new FakeDataModel();

        return new Object[][]{
                {Arrays.asList(oneFakeDataModel, twoFakeDataModel), true},
                {null, true},
                {new ArrayList<>(), true},
                {Arrays.asList(oneFakeDataModel), false}
        };
    }

    @Test()
    @UseDataProvider("assertListHasOneItemDataProvider")
    public void testAssertListHasOneItem(List<FakeDataModel> itemList, boolean throwErrorExpected)
    {
        if (throwErrorExpected)
        {
            expectedException.expect(NotFoundException.class);
        }

        serviceResolver.assertListHasOneItem(itemList, "hello", 1L);
    }

    @DataProvider
    public static Object[][] validateEditOperationDataProvider()
    {
        return new Object[][]{
                {null, OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, EditActionEnum.add,
                        true, ValidationException.class, "id is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, null, OPERATION_RESOURCE, OPERATION_ACTION, EditActionEnum.add,
                        true, ValidationException.class, "operation service is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, OPERATION_RESOURCE, null, OPERATION_ACTION, EditActionEnum.add,
                        true, ValidationException.class, "operation resource is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, OPERATION_SERVICE, OPERATION_RESOURCE, null, EditActionEnum.add,
                        true, ValidationException.class, "operation action is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, null, null, null, EditActionEnum.add, true, ValidationException.class,
                        "operation is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, null,
                        true, ValidationException.class, "action is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION,
                        EditActionEnum.add, false, null, null},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION,
                        EditActionEnum.remove, false, null, null},
        };
    }

    @Test
    @UseDataProvider("validateEditOperationDataProvider")
    public void validateEditOperationTest(Long featureId, String service, String resource, String action,
                                          EditActionEnum editAction, boolean isExceptionThrown,
                                          Class<? extends ServiceException> classType, String errorMsg)
    {
        if (isExceptionThrown)
        {
            expectedException.expect(classType);
            expectedException.expectMessage(errorMsg);
        }

        com.rubicon.platform.authorization.model.ui.acm.Operation operation = null;
        if (service != null || resource != null || action != null)
        {
            operation = new com.rubicon.platform.authorization.model.ui.acm.Operation(service, resource, action);
        }

        serviceResolver.validateEditOperation(new EditBaseOperationRequest(featureId, operation, editAction));
    }

    @DataProvider
    public static Object[][] indexOfOperationDataProvider()
    {
        return new Object[][]{
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, 0},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, -1},
                {OPERATION_SERVICE, OPERATION_2_RESOURCE, OPERATION_ACTION, -1},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_2_ACTION, -1}
        };
    }

    @Test
    @UseDataProvider("indexOfOperationDataProvider")
    public void indexOfOperationTest(String service, String resource, String action, int expected)
    {
        List<Operation> existingOperations =
                Arrays.asList(new Operation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION));

        com.rubicon.platform.authorization.model.ui.acm.Operation operation =
                new com.rubicon.platform.authorization.model.ui.acm.Operation();
        operation.setService(service);
        operation.setResource(resource);
        operation.setAction(action);

        Assert.assertThat(serviceResolver.indexOfOperation(existingOperations, operation), equalTo(expected));
    }

    @DataProvider
    public static Object[][] dataOperationDataProvider()
    {
        return new Object[][]{
                {OPERATION_PROPERTIES},
                {null}
        };
    }

    @Test
    @UseDataProvider("dataOperationDataProvider")
    public void dataOperationTest(List<String> properties)
    {
        com.rubicon.platform.authorization.model.ui.acm.Operation operation =
                new com.rubicon.platform.authorization.model.ui.acm.Operation();
        operation.setService(OPERATION_SERVICE);
        operation.setResource(OPERATION_RESOURCE);
        operation.setAction(OPERATION_ACTION);
        operation.setProperties(properties);

        Operation dataOperation = serviceResolver.dataOperation(operation);

        Assert.assertThat(dataOperation.getService(), equalTo(OPERATION_SERVICE));
        Assert.assertThat(dataOperation.getResource(), equalTo(OPERATION_RESOURCE));
        Assert.assertThat(dataOperation.getAction(), equalTo(OPERATION_ACTION));
        Assert.assertThat(dataOperation.getProperties(), equalTo(properties));
    }

    @DataProvider
    public static Object[][] assertRoleTypeEditableDataProvider()
    {
        return new Object[][]{
                {true, null},
                {false, UnauthorizedException.class}
        };
    }


    @Test
    @UseDataProvider("assertRoleTypeEditableDataProvider")
    public void testAssertRoleTypeEditable(boolean isEditable, Class<Exception> exceptionClass)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditInternal(isEditable);

        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
        }

        serviceResolver.assertRoleTypeEditable(roleTypePermission, RoleTypeEnum.internal, "role");
    }

    @DataProvider
    public static Object[][] validateOperationPropertiesDataProvider()
    {
        return new Object[][]{
                {EditActionEnum.add, true, false, false},
                {EditActionEnum.edit, true, false, false},
                {EditActionEnum.remove, true, false, false},
                {EditActionEnum.add, false, false, false},
                {EditActionEnum.edit ,false, false, false},
                {EditActionEnum.remove, false, false, false},
                {EditActionEnum.add, false, true, true},
                {EditActionEnum.edit, false, true, true},
                {EditActionEnum.remove, false, true, false},
        };
    }

    @Test
    @UseDataProvider("validateOperationPropertiesDataProvider")
    public void validateOperationPropertiesTest(EditActionEnum action, boolean missingProperties,
                                                boolean duplicateProperties, boolean exceptionExpected)
    {
        EditBaseOperationRequest request = new EditBaseOperationRequest(DATA_SERVICE_ROLE_ID,
                getUiOperation(false, false, false, false, missingProperties),
                action);
        // Duplicate the properties in request object to trigger error
        if (duplicateProperties)
        {
            List<String> duplicatedProperties = Arrays.asList("Properties1", "Properties1");
            request.getOperation().setProperties(duplicatedProperties);
        }

        if (exceptionExpected)
        {
            expectedException.expect(ValidationException.class);
            expectedException.expectMessage(String.format("Please remove duplicated item(s) from properties"));
        }

        serviceResolver.validateOperationProperties(request);
    }

    @DataProvider
    public static Object[][] validateDistinctCollectionDataProvider()
    {
        return new Object[][]{
                {Arrays.asList("test", "test1", "test 2"), false},
                {Arrays.asList("test", "test1", "test 1"), false},
                {Arrays.asList("test", "test1", "test1"), true}
        };
    }

    @Test
    @UseDataProvider("validateDistinctCollectionDataProvider")
    public void validateDistinctCollectionTest(List<String> listToValidate, boolean exceptionExpected)
    {
        if (exceptionExpected)
        {
            expectedException.expect(ValidationException.class);
            expectedException.expectMessage(String.format("Please remove duplicated item(s) from %s.", "testList"));
        }

        serviceResolver.validateDistinctCollection(listToValidate, "testList");
    }

    @DataProvider
    public static Object[][] validateOperationIndexWhenEditingDataProvider()
    {
        return new Object[][]{
                {EditActionEnum.add, -1, false},
                {EditActionEnum.remove, -1, false},
                {EditActionEnum.edit, 0, false},
                {EditActionEnum.edit, -1, true}
        };
    }

    @Test
    @UseDataProvider("validateOperationIndexWhenEditingDataProvider")
    public void validateOperationIndexWhenEditingTest(EditActionEnum action, int index, boolean exceptionExpected)
    {
        if (exceptionExpected)
        {
            expectedException.expect(NotFoundException.class);
            expectedException.expectMessage("Cannot find specified operation. Please provide a valid operation.");
        }

        serviceResolver.validateOperationIndexWhenEditing(action, index);
    }

    @Test
    public void trimStringListTest()
    {
        List<String> listToTrim = Arrays.asList(" test 1", "test 2 ", " test 3 ");
        List<String> expectedTrimmedList = Arrays.asList("test 1", "test 2", "test 3");

        List<String> actualTrimmedList = serviceResolver.trimStringList(listToTrim);

        assertEquals(expectedTrimmedList, actualTrimmedList);
    }
}
