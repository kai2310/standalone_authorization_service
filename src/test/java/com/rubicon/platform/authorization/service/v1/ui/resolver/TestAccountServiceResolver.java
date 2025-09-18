package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import com.rubicon.platform.authorization.data.persistence.AccountFeatureLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.service.exception.ForbiddenException;
import com.rubicon.platform.authorization.service.exception.NotFoundException;
import com.rubicon.platform.authorization.service.exception.ValidationException;
import com.rubicon.platform.authorization.service.persistence.ServiceExceptionMappingDecorator;
import com.rubicon.platform.authorization.service.v1.ui.client.publishermanagement.PublisherManagementClient;
import com.rubicon.platform.authorization.service.v1.ui.model.AccountFeaturePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.AccountFeatureTranslator;
import com.rubicon.platform.authorization.service.v1.ui.translator.AccountTranslator;
import com.rubicon.platform.authorization.service.v1.ui.translator.AssignedAccountAccountTranslator;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
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
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class TestAccountServiceResolver extends TestAbstract
{
    public static Integer DATA_SERVICE_ACCOUNT_TOTAL_COUNT = 20;

    protected AccountServiceResolver resolver;
    protected ServiceExceptionMappingDecorator accountPersistenceOperations;
    protected AccountFeatureLoader accountFeatureLoader;
    protected AccountLoader accountLoader;
    protected AccountFeatureTranslator accountFeatureTranslator;
    protected AssignedAccountAccountTranslator assignedAccountAccountTranslator;

    @Before
    public void setup()
    {
        accountPersistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);

        accountFeatureLoader = Mockito.mock(AccountFeatureLoader.class);
        when(accountFeatureLoader.find(anyLong())).thenReturn(getPersistentAccountFeature());
        when(accountFeatureLoader.findIds(anyList())).thenReturn(Arrays.asList(getPersistentAccountFeature()));

        accountFeatureTranslator = new AccountFeatureTranslator(getMockAccountFeatureCache());
        accountFeatureTranslator.init();

        assignedAccountAccountTranslator = new AssignedAccountAccountTranslator();
        assignedAccountAccountTranslator.init();

        AccountTranslator accountTranslator = new AccountTranslator();
        accountTranslator.init();

        accountLoader = Mockito.mock((AccountLoader.class));
        when(accountLoader.getAccountByFeatureId(anyLong())).thenReturn(
                Collections.singletonList(DATA_SERVICE_ACCOUNT_ID));


        resolver = new AccountServiceResolver();
        resolver.setPersistenceOperations(accountPersistenceOperations);
        resolver.setAccountFeatureLoader(accountFeatureLoader);
        resolver.setAccountFeatureTranslator(accountFeatureTranslator);
        resolver.setAccountLoader(accountLoader);
        resolver.setAssignedAccountAccountTranslator(assignedAccountAccountTranslator);
        resolver.setTranslator(accountTranslator);
    }

    @Test
    public void testGetList()
    {
        AccountTranslator accountTranslator = new AccountTranslator();
        accountTranslator.init();

        AccountServiceResolver resolver = new AccountServiceResolver();
        resolver.setTranslator(accountTranslator);


        ServiceExceptionMappingDecorator accountPersistenceOperations = Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(accountPersistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(getQueryResults());

        resolver.setPersistenceOperations(accountPersistenceOperations);


        PagedResponse<Account> pagedResponse =
                resolver.getList(1, DATA_SERVICE_ACCOUNT_TOTAL_COUNT, null, null, null, null);

        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getPage());
        assertNotNull(pagedResponse.getContent());

        Page page = pagedResponse.getPage();
        assertThat(DATA_SERVICE_ACCOUNT_TOTAL_COUNT, equalTo(page.getSize()));
        assertThat(1, equalTo(page.getTotalPages()));
        assertThat(1, equalTo(page.getNumber()));

        List<Account> accountList = pagedResponse.getContent();
        assertThat(2, equalTo(accountList.size()));
        Account account = accountList.get(0);


        String[] accountContext = DATA_SERVICE_ACCOUNT_COMBO_ID.split("/");
        Long accountContextId = Long.parseLong(accountContext[1]);

        assertThat(DATA_SERVICE_ACCOUNT_ID, equalTo(account.getId()));
        assertThat(DATA_SERVICE_ACCOUNT_NAME, equalTo(account.getName()));
        assertThat(accountContext[0], equalTo(account.getContextType()));
        assertThat(accountContextId, equalTo(account.getContextId()));
    }


    @DataProvider
    public static Object[][] testEditFeaturesDataProvider()
    {
        List<Long> dataFeatureIds = Arrays.asList(DATA_SERVICE_ACCOUNT_FEATURE_ID);
        List<Long> dataFeatureTwoIds = Arrays.asList(DATA_SERVICE_ACCOUNT_FEATURE_2_ID);

        return new Object[][]{
                {new EditAccountFeaturesRequest(null, dataFeatureIds, EditActionEnum.add), 1, false,
                        ValidationException.class, "id is required"},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, null, EditActionEnum.add), 1, false,
                        ValidationException.class, "Please provide at least one featureId"},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureIds, null), 1, false,
                        ValidationException.class, "action is required"},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureIds, EditActionEnum.add), 1, true,
                        ValidationException.class, "Please provide a unique list of valid feature ids"},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID,
                        Arrays.asList(DATA_SERVICE_ACCOUNT_FEATURE_ID, DATA_SERVICE_ACCOUNT_FEATURE_ID),
                        EditActionEnum.add), 1, false, ValidationException.class,
                        "Please provide a unique list of valid feature ids"},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureIds, EditActionEnum.add), 1, false,
                        null, null},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureTwoIds, EditActionEnum.add), 2,
                        false, null, null},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureIds, EditActionEnum.remove), 0,
                        false, null, null},
                {new EditAccountFeaturesRequest(DATA_SERVICE_ACCOUNT_ID, dataFeatureTwoIds, EditActionEnum.remove), 1,
                        false, null, null}
        };
    }

    @Test
    @UseDataProvider("testEditFeaturesDataProvider")
    public void testEditFeatures(EditAccountFeaturesRequest request, Integer expectedFeatureCount,
                                 Boolean accountFeatureNotFound, Class<Exception> exceptionClass, String exceptionMessage)
    {
        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }
        if (accountFeatureNotFound)
        {
            when(accountFeatureLoader.find(anyLong())).thenReturn(null);
            when(accountFeatureLoader.findIds(anyList())).thenReturn(null);
        }

        when(accountPersistenceOperations
                .findByIds((List<Long>)anyList(), (PersistenceContext) any()))
                .thenReturn(getDataAccountList());
        when(accountPersistenceOperations.updateItem((List<Long>)anyList(),
                (com.rubicon.platform.authorization.model.data.acm.Account)any(), (PersistenceContext) any()))
                .thenAnswer(new Answer<com.rubicon.platform.authorization.model.data.acm.Account>()
                {
                    @Override
                    public com.rubicon.platform.authorization.model.data.acm.Account answer(InvocationOnMock invocation)
                    {
                        Object[] args = invocation.getArguments();
                        return (com.rubicon.platform.authorization.model.data.acm.Account) args[1];
                    }
                });

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());

        AccountFeature accountFeature = resolver.editFeatures(request, new AccountFeaturePermission(), context);

        assertThat(accountFeature.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
        assertThat(accountFeature.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(accountFeature.getFeatures().size(), equalTo(expectedFeatureCount));
        assertThat(accountFeature.getAllowedAction(), equalTo(AccountFeatureActionEnum.none));

        List<Reference> featureReferences = accountFeature.getFeatures();
        Reference featureReference;
        switch (expectedFeatureCount)
        {
            case 2:
                featureReference = null;
                for (Reference item : featureReferences)
                {
                    if (item.getId().equals(DATA_SERVICE_ACCOUNT_FEATURE_2_ID))
                    {
                        featureReference = item;
                    }
                }
                assertNotNull(featureReference);
                assertThat(featureReference.getName(), equalTo(DATA_SERVICE_FEATURE_2_NAME));
            case 1:
                featureReference = null;
                for (Reference item : featureReferences)
                {
                    if (item.getId().equals(DATA_SERVICE_ACCOUNT_FEATURE_ID))
                    {
                        featureReference = item;
                    }
                }
                assertNotNull(featureReference);
                assertThat(featureReference.getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
            case 0:
        }
    }


    @DataProvider
    public static Object[][] testRetrieveAccountDataProvider()
    {
        return new Object[][]{
                {DATA_SERVICE_ACCOUNT_ID, true, null, null},
                {DATA_SERVICE_ACCOUNT_ID, false, NotFoundException.class, "Cannot find account id 225"}
        };
    }

    @Test
    @UseDataProvider("testRetrieveAccountDataProvider")
    public void testRetrieveAccount(Long accountId, Boolean accountFound, Class<Exception> exceptionClass, String exceptionMessage)
    {
        if (null != exceptionClass)
        {
            expectedException.expect(exceptionClass);
            expectedException.expectMessage(exceptionMessage);
        }

        if (accountFound)
        {
            when(accountPersistenceOperations
                    .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                    .thenReturn(getDataAccountList());
        }

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());

        AccountFeaturePermission permission = new AccountFeaturePermission(true, true);
        AccountFeature accountFeature = resolver.retrieveAccount(accountId, permission, context);

        assertThat(accountFeature.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
        assertThat(accountFeature.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(accountFeature.getStatus(), equalTo(DATA_SERVICE_STATUS));
        assertThat(accountFeature.getAllowedAction(), equalTo(AccountFeatureActionEnum.delete));
        assertThat(accountFeature.getFeatures().size(), equalTo(1));
        assertThat(accountFeature.getFeatures().get(0).getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        assertThat(accountFeature.getFeatures().get(0).getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
    }

    @DataProvider
    public static Object[][] testAssertValidContextTypeDataProvider()
    {
        return new Object[][]{
                {"buyer/123", false},
                {"network/123", false},
                {"partner/123", true},
                {"publisher/123", true},
                {"seat/123", true},
                {"afakecontext/123", false},
                {"mp-vendor/123", true}
        };
    }

    @Test
    @UseDataProvider("testAssertValidContextTypeDataProvider")
    public void testAssertValidContextType(String accountId, boolean isValidContext)
    {
        if (!isValidContext)
        {
            expectedException.expect(NotFoundException.class);
        }

        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setAccountId(accountId);

        resolver.assertValidContextType(account);
    }


    @DataProvider
    public static Object[][] replaceSingeQuotesForRSQLParserDataProvider()
    {
        return new Object[][]{
                {"name==\"*Ray's*\";status==active", true},
                {"status==active;name==\"Ray's\"", false},
                {"name==\"*'*\"", true},
                {"status==pending", false},
                {null, true},
                {"", false},
                {"contextId==\"1282\"", true},
                {"status==pending;contextId==\"1282\"", false},
                {"name==\"**\";contextId==\"123\"", true}
        };
    }

    @Test
    @UseDataProvider("replaceSingeQuotesForRSQLParserDataProvider")
    public void replaceSingeQuotesForRSQLParserTest(String query, boolean showDeleted)
    {
        AccountTranslator accountTranslator = new AccountTranslator();
        accountTranslator.init();

        AccountServiceResolver resolver = new AccountServiceResolver();
        resolver.setTranslator(accountTranslator);


        ServiceExceptionMappingDecorator accountPersistenceOperations =
                Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(accountPersistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(getQueryResults());

        resolver.setPersistenceOperations(accountPersistenceOperations);

        // This Test should not ever throw any errors. If it does, its due to RSQL parsing.
        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());

        resolver.getList(1, 1, query, null, showDeleted, context);
    }


    @DataProvider
    public static Object[][] determineAccountFeatureActionEnumTestDataProvider()
    {
        return new Object[][]{
                {"active", true, true, AccountFeatureActionEnum.delete},
                {"active", true, false, AccountFeatureActionEnum.none},
                {"active", false, true, AccountFeatureActionEnum.delete},
                {"active", false, false, AccountFeatureActionEnum.none},
                {"deleted", true, true, AccountFeatureActionEnum.reactivate},
                {"deleted", true, false, AccountFeatureActionEnum.reactivate},
                {"deleted", false, true, AccountFeatureActionEnum.none},
                {"deleted", false, false, AccountFeatureActionEnum.none},

                // Random Status that isn't active or deleted
                {"pending", true, true, AccountFeatureActionEnum.delete},
        };
    }

    @Test
    @UseDataProvider("determineAccountFeatureActionEnumTestDataProvider")
    public void determineAccountFeatureActionEnumTest(String accountStatus, boolean isRemoveAllowed,
                                                      boolean isReactivateAllowed,
                                                      AccountFeatureActionEnum expectedResult)
    {

        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setStatus(accountStatus);
        AccountFeaturePermission permission = new AccountFeaturePermission(isReactivateAllowed, isRemoveAllowed);

        when(accountPersistenceOperations
                .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                .thenReturn(Stream.of(account).collect(Collectors.toList()));

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());

        AccountFeature accountFeature = resolver.retrieveAccount(14L, permission, context);

        // Confirm we have the correct allowed action
        assertThat(accountFeature.getAllowedAction(), equalTo(expectedResult));
    }

    @Test
    public void removeAccountTest()
    {
        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setStatus("active");
        // Setting the isAllowed to reactive to false, so we can confirm none on the allowedAction
        AccountFeaturePermission permission = new AccountFeaturePermission(true, false);

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());
        context.setUserContext(new DataUserContext(null, null, null));

        PublisherManagementClient publisherManagementClient = Mockito.mock(PublisherManagementClient.class);

        when(accountPersistenceOperations
                .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                .thenReturn(Stream.of(account).collect(Collectors.toList()));

        doNothing().when(publisherManagementClient).deleteFinancePublisher(anyLong(), (DataUserContext) any());

        AccountServiceResolver resolver = spy(new AccountServiceResolver());
        resolver.setPersistenceOperations(accountPersistenceOperations);
        resolver.setAccountFeatureLoader(accountFeatureLoader);
        resolver.setAccountFeatureTranslator(accountFeatureTranslator);
        resolver.setPublisherManagementClient(publisherManagementClient);

        doNothing().when(resolver).updateAccountStatus(anyLong(), anyString());

        TransactionSynchronizationManager.initSynchronization();
        AccountFeature accountFeature = resolver.removeAccount(14L, permission, context);
        TransactionSynchronizationManager.clear();

        assertThat(accountFeature.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
        assertThat(accountFeature.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(accountFeature.getStatus(), equalTo("deleted"));
        assertThat(accountFeature.getAllowedAction(), equalTo(AccountFeatureActionEnum.none));
        assertThat(accountFeature.getFeatures().size(), equalTo(1));
        assertThat(accountFeature.getFeatures().get(0).getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        assertThat(accountFeature.getFeatures().get(0).getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
    }


    @DataProvider
    public static Object[][] removeAccountValidationTestDataProvider()
    {
        return new Object[][]{
                {"seat/123", "active", true, ValidationException.class,
                        "Only accounts with the contextType of 'publisher' can be deleted."},
                {"publisher/123", "deleted", true, ValidationException.class,
                        "The account provided can not be deleted."},
                {"publisher/123", "active", false, ForbiddenException.class,
                        "You are not allowed to delete an account."}
        };
    }

    @Test
    @UseDataProvider("removeAccountValidationTestDataProvider")
    public void removeAccountValidationTest(String compooundAccountId, String status, boolean canDelete,
                                            Class<Exception> exceptionClass, String exceptionMessage)
    {
        expectedException.expect(exceptionClass);
        expectedException.expectMessage(exceptionMessage);

        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setAccountId(compooundAccountId);
        account.setStatus(status);
        AccountFeaturePermission permission = new AccountFeaturePermission(canDelete, true);

        when(accountPersistenceOperations
                .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                .thenReturn(Stream.of(account).collect(Collectors.toList()));


        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());
        context.setUserContext(new DataUserContext(null, null, null));

        AccountFeature accountFeature = resolver.removeAccount(14L, permission, context);
    }


    @Test
    public void reactivateAccountTest()
    {
        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setStatus("deleted");
        // Setting the isAllowed to delete to false, so we can confirm none on the allowedAction
        AccountFeaturePermission permission = new AccountFeaturePermission(false, true);

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());
        context.setUserContext(new DataUserContext(null, null, null));

        PublisherManagementClient publisherManagementClient = Mockito.mock(PublisherManagementClient.class);

        when(accountPersistenceOperations
                .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                .thenReturn(Stream.of(account).collect(Collectors.toList()));

        doNothing().when(publisherManagementClient).deleteFinancePublisher(anyLong(), (DataUserContext) any());

        AccountServiceResolver resolver = spy(new AccountServiceResolver());
        resolver.setPersistenceOperations(accountPersistenceOperations);
        resolver.setAccountFeatureLoader(accountFeatureLoader);
        resolver.setAccountFeatureTranslator(accountFeatureTranslator);
        resolver.setPublisherManagementClient(publisherManagementClient);

        doNothing().when(resolver).updateAccountStatus(anyLong(), anyString());

        TransactionSynchronizationManager.initSynchronization();
        AccountFeature accountFeature = resolver.reactivateAccount(14L, permission, context);
        TransactionSynchronizationManager.clear();

        assertThat(accountFeature.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
        assertThat(accountFeature.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(accountFeature.getStatus(), equalTo("active"));
        assertThat(accountFeature.getAllowedAction(), equalTo(AccountFeatureActionEnum.none));
        assertThat(accountFeature.getFeatures().size(), equalTo(1));
        assertThat(accountFeature.getFeatures().get(0).getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        assertThat(accountFeature.getFeatures().get(0).getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
    }


    @DataProvider
    public static Object[][] reactivateAccountValidationTestDataProvider()
    {
        return new Object[][]{
                {"seat/123", "active", true, ValidationException.class,
                        "Only accounts with the contextType of 'publisher' can be reactivated."},
                {"publisher/123", "active", true, ValidationException.class,
                        "The account provided can not be reactivated."},
                {"publisher/123", "deleted", false, ForbiddenException.class,
                        "You are not allowed to reactivate an account."}
        };
    }

    @Test
    @UseDataProvider("reactivateAccountValidationTestDataProvider")
    public void reactivateAccountValidationTest(String compooundAccountId, String status, boolean canReactivate,
                                                Class<Exception> exceptionClass, String exceptionMessage)
    {
        expectedException.expect(exceptionClass);
        expectedException.expectMessage(exceptionMessage);

        com.rubicon.platform.authorization.model.data.acm.Account account = getDataServiceAccount();
        account.setAccountId(compooundAccountId);
        account.setStatus(status);
        AccountFeaturePermission permission = new AccountFeaturePermission(true, canReactivate);

        when(accountPersistenceOperations
                .findByIds((List<Long>) anyList(), (PersistenceContext) any()))
                .thenReturn(Stream.of(account).collect(Collectors.toList()));

        PersistenceContext context = new PersistenceContext();
        context.setEntityPlugin(new EntityPlugin());
        context.setUserContext(new DataUserContext(null, null, null));

        AccountFeature accountFeature = resolver.reactivateAccount(14L, permission, context);
    }


    @DataProvider
    public static Object[][] getAccountsByFeatureIdTestDataProvider()
    {
        return new Object[][]{
                {Collections.singletonList(DATA_SERVICE_ACCOUNT_ID), true},
                {Collections.singletonList(DATA_SERVICE_ACCOUNT_ID), false},
                {null, true},
                {null, false},
        };
    }


    @Test
    @UseDataProvider("getAccountsByFeatureIdTestDataProvider")
    public void getAccountsByFeatureIdTest(List<Long> accountIds, boolean isEditable)
    {
        AccountLoader accountLoader = Mockito.mock((AccountLoader.class));
        when(accountLoader.getAccountByFeatureId(anyLong())).thenReturn(accountIds);


        ServiceExceptionMappingDecorator accountPersistenceOperations =
                Mockito.mock(ServiceExceptionMappingDecorator.class);
        when(accountPersistenceOperations
                .query((Node) any(), anyInt(), anyInt(), (EndpointSort) any(), (PersistenceContext) any()))
                .thenReturn(getQueryResults());

        resolver.setPersistenceOperations(accountPersistenceOperations);
        resolver.setAccountLoader(accountLoader);
        PagedResponse<AssignedAccount> pagedResponse =
                resolver.getAccountsByFeatureId(DATA_SERVICE_ACCOUNT_FEATURE_ID, 1, DATA_SERVICE_ACCOUNT_TOTAL_COUNT,
                        isEditable, null);

        assertThat(pagedResponse, notNullValue());
        assertThat(pagedResponse.getPage(), notNullValue());
        assertThat(pagedResponse.getContent(), notNullValue());

        
        Page page = pagedResponse.getPage();
        assertThat(page.getNumber(), equalTo(1));
        assertThat(page.getSize(), equalTo(DATA_SERVICE_ACCOUNT_TOTAL_COUNT));

        if (CollectionUtils.isEmpty(accountIds))
        {
            assertThat(page.getTotalPages(), equalTo(0));
            assertThat(page.getTotalElements(), equalTo(0L));
        }
        else
        {
            assertThat(page.getTotalPages(), equalTo(1));
            assertThat(page.getTotalElements(), equalTo(20L));


            List<AssignedAccount> assignedAccounts = pagedResponse.getContent();
            assertThat(assignedAccounts.size(), equalTo(2));
            for (AssignedAccount assignedAccount : assignedAccounts)
            {
                assertThat(assignedAccount.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
                assertThat(assignedAccount.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
                assertThat(assignedAccount.getEditable(), equalTo(isEditable));
            }

        }
    }


    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.Account> getQueryResults()
    {
        List<com.rubicon.platform.authorization.model.data.acm.Account> accountList = new ArrayList<>();
        accountList.add(getDataServiceAccount());
        accountList.add(getDataServiceAccount());

        QueryResult<com.rubicon.platform.authorization.model.data.acm.Account> accountQueryResult = new QueryResult<>();
        accountQueryResult.setItems(accountList);
        accountQueryResult.setResponseCount(accountList.size());
        accountQueryResult.setStart(1);
        accountQueryResult.setTotalCount(DATA_SERVICE_ACCOUNT_TOTAL_COUNT);

        return accountQueryResult;
    }

    protected List<com.rubicon.platform.authorization.model.data.acm.Account> getDataAccountList()
    {
        List<com.rubicon.platform.authorization.model.data.acm.Account> accountList = new ArrayList<>();
        accountList.add(getDataServiceAccount());

        return accountList;
    }

    protected PersistentAccountFeature getPersistentAccountFeature()
    {
        PersistentAccountFeature persistentAccountFeature = new PersistentAccountFeature();
        persistentAccountFeature.setId(DATA_SERVICE_ACCOUNT_FEATURE_ID);
        persistentAccountFeature.setLabel(DATA_SERVICE_FEATURE_NAME);

        return persistentAccountFeature;
    }
}
