package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.exception.NotFoundException;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.exception.ValidationException;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.v1.ui.translator.FeatureTranslator;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class TestFeatureServiceResolver extends TestAbstract
{
    public static Integer DATA_SERVICE_TOTAL_COUNT = 20;
    public static int TOTAL_RECORDS_IN_RESULTS = 1;
    protected FeatureServiceResolver resolver;
    protected PersistenceContext context = new PersistenceContext();
    protected FeatureTranslator featureTranslator;

    @Before
    public void setup()
    {
        featureTranslator = new FeatureTranslator();
        featureTranslator.init();

        resolver = new FeatureServiceResolver();
        resolver.setPersistenceOperations(setupUiServicePersistenceOperations(getQueryResults(TOTAL_RECORDS_IN_RESULTS),
                getFeatureList(1), 1, getDataServiceAccountFeature()));
        resolver.setTranslator(featureTranslator);
    }

    @Test
    public void testGetList()
    {
        int resultCount = 2;
        boolean isEditable = true;

        // Change the Results Returned
        resolver.setPersistenceOperations(setupUiServicePersistenceOperations(getQueryResults(resultCount),
                getFeatureList(1)));

        PagedResponse<Feature> pagedResponse =
                resolver.getList(1, DATA_SERVICE_TOTAL_COUNT, "", "", isEditable, context);

        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getPage());
        assertNotNull(pagedResponse.getContent());

        Page page = pagedResponse.getPage();
        assertThat(DATA_SERVICE_TOTAL_COUNT, equalTo(page.getSize()));
        assertThat(1, equalTo(page.getTotalPages()));
        assertThat(1, equalTo(page.getNumber()));

        List<Feature> featureList = pagedResponse.getContent();
        assertThat(resultCount, equalTo(featureList.size()));
        Feature feature = featureList.get(0);

        assertThat(DATA_SERVICE_ACCOUNT_FEATURE_ID, equalTo(feature.getId()));
        assertThat(DATA_SERVICE_FEATURE_NAME, equalTo(feature.getName()));
        assertThat(isEditable, equalTo(feature.getEditable()));

        List<Operation> operations = feature.getAllowedOperations();

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
        boolean isEditable = false;

        Feature feature = resolver.getById(DATA_SERVICE_ACCOUNT_FEATURE_ID, isEditable, context);

        assertThat(DATA_SERVICE_ACCOUNT_FEATURE_ID, equalTo(feature.getId()));
        assertThat(DATA_SERVICE_FEATURE_NAME, equalTo(feature.getName()));
        assertThat(isEditable, equalTo(feature.getEditable()));

        List<Operation> allowedOperations = feature.getAllowedOperations();

        for (Operation operation : allowedOperations)
        {
            assertThat(OPERATION_SERVICE, equalTo(operation.getService()));
            assertThat(OPERATION_RESOURCE, equalTo(operation.getResource()));
            assertThat(OPERATION_ACTION, equalTo(operation.getAction()));
        }

        List<Operation> deniedOperations = feature.getDeniedOperations();

        for (Operation operation : deniedOperations)
        {
            assertThat(OPERATION_SERVICE, equalTo(operation.getService()));
            assertThat(OPERATION_RESOURCE, equalTo(operation.getResource()));
            assertThat(OPERATION_ACTION, equalTo(operation.getAction()));
        }
    }

    @Test
    public void createTest()
    {
        FeatureRequest createFeatureRequest = new FeatureRequest("test_feature");
        resolver.setPersistenceOperations(setupCreationUiServicePersistenceOperations(getDataServiceAccountFeature()));

        Feature feature = resolver.create(createFeatureRequest, true, getPersistentContext());

        Assert.assertNotNull(feature);
        Assert.assertThat(feature.getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        Assert.assertThat(feature.getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
        Assert.assertTrue(feature.getEditable());
    }

    @DataProvider
    public static Object[][] createExceptionDataProvider()
    {
        return new Object[][]{
                {true, null, ValidationException.class, "feature creation request body is required"},
                {true, new FeatureRequest(), ValidationException.class, "feature name is required"},
                {false, null, UnauthorizedException.class, "You are not authorized to make changes to features."}
        };
    }

    @Test
    @UseDataProvider("createExceptionDataProvider")
    public void createTest_Exception(boolean isEditable, FeatureRequest createFeatureRequest,
                                     Class<? extends ServiceException> classType, String errorMsg)
    {
        expectedException.expect(classType);
        expectedException.expectMessage(errorMsg);

        resolver.create(createFeatureRequest, isEditable, context);
    }

    @Test
    public void createTest_HyperionException()
    {
        String exceptionMessage = "exception message";
        ServiceExceptionMappingDecorator persistenceOperations =
                Mockito.mock(ServiceExceptionMappingDecorator.class);
        Mockito.doThrow(new ServiceException(HttpStatus.CONFLICT.value(), exceptionMessage))
                .when(persistenceOperations)
                .createOrUpdateItem(Mockito.any(ApiObject.class), Mockito.any(PersistenceContext.class));

        resolver.setPersistenceOperations(persistenceOperations);

        expectedException.expect(ServiceException.class);
        expectedException.expectMessage(exceptionMessage);

        resolver.create(new FeatureRequest("test"), true, context);
    }


    @Test
    public void updateTest()
    {
        FeatureRequest featureRequest =
                new FeatureRequest(DATA_SERVICE_ACCOUNT_FEATURE_ID, DATA_SERVICE_FEATURE_NAME);

        Feature feature = resolver.update(featureRequest, true, getPersistentContext());

        Assert.assertNotNull(feature);
        Assert.assertThat(feature.getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        Assert.assertThat(feature.getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
        Assert.assertTrue(feature.getEditable());
    }




    protected List<com.rubicon.platform.authorization.model.data.acm.AccountFeature> getFeatureList(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.AccountFeature> featureList = new ArrayList<>();
        for (int index = 0; index < resultCount; index++)
        {
            featureList.add(getDataServiceAccountFeature());
        }

        return featureList;
    }

    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.AccountFeature> getQueryResults(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.AccountFeature> featureList = getFeatureList(resultCount);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.AccountFeature> featureQueryResult =
                new QueryResult<>();
        featureQueryResult.setItems(featureList);
        featureQueryResult.setResponseCount(featureList.size());
        featureQueryResult.setStart(1);
        featureQueryResult.setTotalCount(DATA_SERVICE_TOTAL_COUNT);

        return featureQueryResult;
    }

    @Test
    public void editOperationTest_ValidationException()
    {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("You are not authorized to edit this feature.");

        resolver.editOperation(new EditFeatureOperationRequest(DATA_SERVICE_ACCOUNT_FEATURE_ID,
                        new Operation(OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION), EditActionEnum.add), false,
                context);
    }

    @DataProvider
    public static Object[][] editOperationDataProvider()
    {
        return new Object[][]{
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.add, false, EditOperationEnum.allowed, false},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.edit, true, EditOperationEnum.allowed, false},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.remove, true, EditOperationEnum.allowed, false},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.add, true, EditOperationEnum.allowed, false},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, null, EditActionEnum.edit, false, EditOperationEnum.allowed, true},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.remove, false, EditOperationEnum.allowed, false},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.add, false, EditOperationEnum.denied, false},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, null, EditActionEnum.edit, false, EditOperationEnum.denied, false},
                {OPERATION_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.remove, true, EditOperationEnum.denied, false},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.add, true, EditOperationEnum.denied, false},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.edit, false, EditOperationEnum.denied, true},
                {OPERATION_2_SERVICE, OPERATION_RESOURCE, OPERATION_ACTION, OPERATION_PROPERTIES, EditActionEnum.remove, false, EditOperationEnum.denied, false}
        };
    }

    @Test
    @UseDataProvider("editOperationDataProvider")
    public void editOperationTest(String service, String resource, String action, List<String> properties,
                                  EditActionEnum editAction, boolean dirty, EditOperationEnum operationType,
                                  boolean expectException)
    {
        if (expectException)
        {
            expectedException.expect(NotFoundException.class);
        }

        ServiceExceptionMappingDecorator persistenceOperations =
                setupUiServicePersistenceOperations(getQueryResults(TOTAL_RECORDS_IN_RESULTS),
                        getFeatureList(1));
        Mockito.when(persistenceOperations
                .updateItem(Mockito.anyList(), Mockito.any(ApiObject.class), Mockito.isA(PersistenceContext.class)))
                .thenReturn(getDataServiceAccountFeature());
        resolver.setPersistenceOperations(persistenceOperations);

        Operation operation = new Operation(service, resource, action);
        operation.setProperties(properties);

        Feature feature =
                resolver.editOperation(
                        new EditFeatureOperationRequest(DATA_SERVICE_ACCOUNT_FEATURE_ID, operation, editAction, operationType), true,
                        getPersistentContext());

        int count = dirty
                    ? 1
                    : 0;
        Mockito.verify(resolver.getPersistenceOperations(), Mockito.times(count))
                .updateItem(Mockito.anyList(), Mockito.any(ApiObject.class), Mockito.any(PersistenceContext.class));

        Assert.assertThat(feature.getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        Assert.assertThat(feature.getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
        Assert.assertTrue(feature.getEditable());

        List<Operation> actualOperations = EditOperationEnum.denied.equals(operationType)
                                           ? feature.getDeniedOperations()
                                           : feature.getAllowedOperations();

        Assert.assertThat(actualOperations.size(), equalTo(1));
        Operation actualOperation = actualOperations.get(0);
        Assert.assertThat(actualOperation.getService(), equalTo(OPERATION_SERVICE));
        Assert.assertThat(actualOperation.getResource(), equalTo(OPERATION_RESOURCE));
        Assert.assertThat(actualOperation.getAction(), equalTo(OPERATION_ACTION));
        Assert.assertThat(actualOperation.getProperties(), equalTo(OPERATION_PROPERTIES));
    }

    @DataProvider
    public static Object[][] removeDataProvider()
    {
        return new Object[][]{
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, false, true, UnauthorizedException.class, "You are not authorized to delete this feature."},
                {null, true, true, ValidationException.class, "featureId is required"},
                {DATA_SERVICE_ACCOUNT_FEATURE_ID, true, false, null, null}
        };
    }

    @Test
    @UseDataProvider("removeDataProvider")
    public void testRemove(Long featureId, boolean isEditable, boolean exceptionThrown,
                           Class<? extends ServiceException> classType, String exceptionMsg)
    {
        if (exceptionThrown)
        {
            expectedException.expect(classType);
            expectedException.expectMessage(exceptionMsg);
        }

        resolver.remove(featureId, isEditable, getPersistentContext());
    }
}
