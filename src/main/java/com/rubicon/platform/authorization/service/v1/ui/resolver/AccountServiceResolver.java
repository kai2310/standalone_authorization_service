package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.endpoint.EndpointSort;
import com.dottydingo.hyperion.core.endpoint.pipeline.phase.DefaultEndpointSortBuilder;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountFeatureLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.service.cache.cluster.DistributedInvalidationBroadcaster;
import com.rubicon.platform.authorization.service.exception.*;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecificationBuilder;
import com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.ContextIdQueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.client.publishermanagement.PublisherManagementClient;
import com.rubicon.platform.authorization.service.v1.ui.model.AccountFeaturePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.AccountFeatureTranslator;
import com.rubicon.platform.authorization.service.v1.ui.translator.AssignedAccountAccountTranslator;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.*;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rubicon.platform.authorization.service.utils.Constants.*;
import static com.rubicon.platform.authorization.model.ui.acm.EditActionEnum.add;
import static com.rubicon.platform.authorization.model.ui.acm.EditActionEnum.remove;

public class AccountServiceResolver
        extends BaseServiceResolver<Account, com.rubicon.platform.authorization.model.data.acm.Account>
{
    private AccountFeatureLoader accountFeatureLoader;
    private AccountFeatureTranslator accountFeatureTranslator;
    private AssignedAccountAccountTranslator assignedAccountAccountTranslator;
    private AccountLoader accountLoader;
    private PublisherManagementClient publisherManagementClient;

    // In the current iteration in the UI, this information should never be returned.
    public static final List<String> VALID_CONTEXT_TYPES =
            Arrays.asList(ACCOUNT_TYPE_PUBLISHER, ACCOUNT_TYPE_SEAT, ACCOUNT_TYPE_PARTNER,
                    ACCOUNT_TYPE_MARKETPLACE_VENDOR, ACCOUNT_TYPE_STREAMING_SEAT, ACCOUNT_TYPE_STREAMING_BUYER);
    public static final String BASE_FILTER_OUT_INFORMATION = "(accountId!=network;accountId!=buyer)";

    private static final String ACCOUNT_STATUS_ACTIVE = "active";
    private static final String ACCOUNT_STATUS_DELETED = "deleted";

    public static final String FILTER_OUT_DELETED_ITEMS = "status!=".concat(ACCOUNT_STATUS_DELETED);

    public static EndpointSpecification getListEndpointSpecification()
    {
        return new EndpointSpecificationBuilder("Ui Account v1")

                .addFieldMapping("id", "id", new QueryExpressionConverter(false))
                .addFieldMapping("name", "accountName")
                .addFieldMapping("contextType", "accountId")
                .addFieldMapping("contextId", "accountId", "accountIdNumeric", new ContextIdQueryExpressionConverter())
                .addFieldMapping("status", "status")

                .setValidQuery("id", "name", "contextId", "contextType", "status")
                .setValidSort("id", "name", "contextType", "contextId", "status")

                .build();
    }

    public AccountServiceResolver()
    {

    }

    @Transactional
    public PagedResponse<Account> getList(Integer currentPage, Integer size, String query, String sort,
                                          Boolean showDeleted, PersistenceContext context)
    {
        ListRequestAdapter listRequestAdapter = new ListRequestAdapter(getListEndpointSpecification());
        String dataQuery = listRequestAdapter.adaptQuery(query);
        String dataSort = listRequestAdapter.adaptSort(sort);

        // make sure show deleted is false when not defiend.
        if (showDeleted == null)
        {
            showDeleted = false;
        }

        // When there's a single quote within the query string "Ray's" is an example, we need to change the single
        // quote to double quote for the rsql parse to work correctly.
        dataQuery = replaceSingeQuotesForRSQLParser(query, dataQuery);

        // Add extra filters
        dataQuery = addExtraFilterInformation(dataQuery, showDeleted);

        Node queryNode = buildQueryExpression(dataQuery);

        EndpointSort endpointSort = new DefaultEndpointSortBuilder().buildSort(dataSort, context);

        // Translate Page Size into Old Hyperion Values
        Map<String, Integer> hyperionPagingMap = translatePagingToHyperion(currentPage, size);
        Integer start = hyperionPagingMap.get(HYPERION_PAGING_KEY_START);
        Integer limit = hyperionPagingMap.get(HYPERION_PAGING_KEY_LIMIT);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.Account> queryResult = getPersistenceOperations()
                .query(queryNode, start, limit, endpointSort, context);

        return buildPagedResponse(queryResult, size);
    }

    @Transactional
    public AccountFeature retrieveAccount(Long accountId, AccountFeaturePermission permission,
                                          PersistenceContext context)
    {
        assertNotNull(accountId, "accountId");

        List<com.rubicon.platform.authorization.model.data.acm.Account> accounts =
                getPersistenceOperations().findByIds(Collections.singletonList(accountId), context);
        assertListHasOneItem(accounts, "account", accountId);

        com.rubicon.platform.authorization.model.data.acm.Account account = accounts.get(0);
        assertValidContextType(account);

        TranslationContext translationContext = new TranslationContext();
        translationContext.putContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM,
                determineAccountFeatureActionEnum(account, permission));

        return accountFeatureTranslator.convertPersistent(account, translationContext);
    }

    @Transactional
    public AccountFeature editFeatures(EditAccountFeaturesRequest request, AccountFeaturePermission permission,
                                       PersistenceContext context)
    {
        Pair<Boolean, AccountFeature> dirtyFeaturePair = doEditFeatures(request, permission, context);

        if (dirtyFeaturePair.getLeft())
        {
            // update the account cache
            processEntityChange(context);
        }

        return dirtyFeaturePair.getRight();
    }

    protected Pair<Boolean, AccountFeature> doEditFeatures(EditAccountFeaturesRequest request,
                                                           AccountFeaturePermission permission,
                                                           PersistenceContext context)
    {
        validateEditFeatures(request);

        List<com.rubicon.platform.authorization.model.data.acm.Account> accounts =
                getPersistenceOperations().findByIds(Collections.singletonList(request.getId()), context);
        assertListHasOneItem(accounts, "account", request.getId());

        com.rubicon.platform.authorization.model.data.acm.Account account = accounts.get(0);
        assertValidContextType(account);

        // make the requested change, if necessary
        boolean dirty = false;
        List<Long> featureIds = request.getFeatureIds();

        switch (request.getAction())
        {
            case add:
            case remove:
                dirty = manageFeatureList(account, featureIds, request.getAction());
                break;
            default:
                break;
        }

        if (dirty)
        {
            // save the account changes, if any
            account = (com.rubicon.platform.authorization.model.data.acm.Account)
                    getPersistenceOperations()
                            .updateItem(Collections.singletonList(request.getId()), account, context);
        }

        TranslationContext translationContext = new TranslationContext();
        translationContext.putContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM,
                determineAccountFeatureActionEnum(account, permission));

        // provide response data
        return Pair.of(dirty, accountFeatureTranslator.convertPersistent(account, translationContext));
    }

    @Transactional()
    public AccountFeature removeAccount(Long accountId, AccountFeaturePermission accountFeaturePermission,
                                        PersistenceContext context)
    {
        assertNotNull(accountId, "accountId");
        DataUserContext userContext = (DataUserContext) context.getUserContext();

        List<com.rubicon.platform.authorization.model.data.acm.Account> accounts =
                getPersistenceOperations().findByIds(Collections.singletonList(accountId), context);
        assertListHasOneItem(accounts, "account", accountId);
        com.rubicon.platform.authorization.model.data.acm.Account account = accounts.get(0);

        validateRemoveAccount(account, accountFeaturePermission);

        // Delete the Account
        CompoundId compoundId = new CompoundId(account.getAccountId());
        try
        {
            publisherManagementClient.deleteFinancePublisher(Long.parseLong(compoundId.getId()), userContext);
        }
        catch (ServiceException e)
        {
            handleRemoveReactivateError(account, e);
        }

        // Set the account status to deleted and translate the new response.
        account.setStatus(ACCOUNT_STATUS_DELETED);
        updateAccountStatus(account.getId(), ACCOUNT_STATUS_DELETED);
        TranslationContext translationContext = new TranslationContext();
        translationContext.putContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM,
                determineAccountFeatureActionEnum(account, accountFeaturePermission));

        // trigger update on cache after transaction is committed
        // make sure account status is deleted when updating caches
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter()
        {
            public void afterCommit()
            {
                logger.info("broadcasting message to update account cache");
                // Broadcast message announcing the update to other host.
                DistributedInvalidationBroadcaster.getInstance().processUpdate("Account", accountId);
            }
        });

        return accountFeatureTranslator.convertPersistent(account, translationContext);
    }

    @Transactional()
    public AccountFeature reactivateAccount(Long accountId, AccountFeaturePermission accountFeaturePermission,
                                            PersistenceContext context)
    {
        assertNotNull(accountId, "accountId");
        DataUserContext userContext = (DataUserContext) context.getUserContext();

        List<com.rubicon.platform.authorization.model.data.acm.Account> accounts =
                getPersistenceOperations().findByIds(Collections.singletonList(accountId), context);
        assertListHasOneItem(accounts, "account", accountId);
        com.rubicon.platform.authorization.model.data.acm.Account account = accounts.get(0);

        validateReactivateAccount(account, accountFeaturePermission);

        // Reactivate the Account
        CompoundId compoundId = new CompoundId(account.getAccountId());
        try
        {
            publisherManagementClient.reactivateFinancePublisher(Long.parseLong(compoundId.getId()), userContext);
        }
        catch (ServiceException e)
        {
            handleRemoveReactivateError(account, e);
        }

        // Set the account status to deleted and translate the new response.
        account.setStatus(ACCOUNT_STATUS_ACTIVE);
        updateAccountStatus(account.getId(), ACCOUNT_STATUS_ACTIVE);

        TranslationContext translationContext = new TranslationContext();
        translationContext.putContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM,
                determineAccountFeatureActionEnum(account, accountFeaturePermission));

        // trigger update on cache after transaction is committed
        // if not, we may still load this account with deleted status
        // when updating caches
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter()
        {
            public void afterCommit()
            {
                logger.info("broadcasting message to update account cache");
                // Broadcast message announcing the update to other host.
                DistributedInvalidationBroadcaster.getInstance().processUpdate("Account", accountId);
            }
        });

        return accountFeatureTranslator.convertPersistent(account, translationContext);
    }

    @Transactional
    public PagedResponse<AssignedAccount> getAccountsByFeatureId(Long featureId, Integer pageNumber, Integer resultSize,
                                                                 boolean isEditable, PersistenceContext context)
    {
        // Grab all the account ids which use this feature
        List<Long> accountIds = accountLoader.getAccountByFeatureId(featureId);

        PagedResponse<AssignedAccount> assignedAccountPagedResponse =
                buildAssignedAccountDefaultPageResponse(pageNumber, resultSize);
        if (!CollectionUtils.isEmpty(accountIds))
        {

            // Call getList, to get the accounts which contain the feature
            PagedResponse<Account> accountPagedResponse =
                    getList(pageNumber, resultSize, String.format("id=in=(%s)", StringUtils.join(accountIds, ",")),
                            null, true, context);

            if (accountPagedResponse != null)
            {
                TranslationContext translationContext = new TranslationContext();
                translationContext.putContextItem(Constants.TRANSLATE_CONTEXT_IS_EDITABLE, isEditable);

                // Using the Page Data from the original response, populate the new page data
                assignedAccountPagedResponse.setPage(accountPagedResponse.getPage());
                assignedAccountPagedResponse.setContent(
                        assignedAccountAccountTranslator.convertPersistent(accountPagedResponse.getContent(),
                                translationContext));
            }
        }

        return assignedAccountPagedResponse;
    }


    public void assertValidContextType(com.rubicon.platform.authorization.model.data.acm.Account account)
    {
        CompoundId compoundAccountId = new CompoundId(account.getAccountId());
        if (!VALID_CONTEXT_TYPES.contains(compoundAccountId.getIdType()))
        {
            throw new NotFoundException(String.format("Cannot find account id %s", compoundAccountId.getIdType()));
        }
    }

    private void validateEditFeatures(EditAccountFeaturesRequest request)
    {
        // validate the request
        assertNotNull(request.getId(), "id");
        assertNotNull(request.getAction(), "action");

        if (CollectionUtils.isEmpty(request.getFeatureIds()) || request.getFeatureIds().size() == 0)
        {
            throw new ValidationException("Please provide at least one featureId");
        }

        List<Long> persistentAccountFeatures = accountFeatureLoader.findIds(request.getFeatureIds());
        if (persistentAccountFeatures == null || persistentAccountFeatures.size() != request.getFeatureIds().size())
        {
            throw new ValidationException(String.format("Please provide a unique list of valid feature ids"));
        }
    }

    private boolean manageFeatureList(com.rubicon.platform.authorization.model.data.acm.Account account,
                                      List<Long> featureIds,
                                      EditActionEnum action)
    {
        boolean dirty = false;
        for (Long featureId : featureIds)
        {
            if (action.equals(add) && !(account.getAccountFeatureIds().contains(featureId)))
            {
                dirty = true;
                account.getAccountFeatureIds().add(featureId);
            }
            else if (action.equals(remove) && account.getAccountFeatureIds().contains(featureId))
            {
                dirty = true;
                account.getAccountFeatureIds().remove(featureId);
            }
        }

        return dirty;
    }

    protected String replaceSingeQuotesForRSQLParser(String originalQuery, String dataQuery)
    {
        if (!StringUtils.isEmpty(originalQuery) && originalQuery.contains("'"))
        {
            Pattern pattern = Pattern.compile(".*accountName=='([^;]*)'.*");
            Matcher matcher = pattern.matcher(dataQuery);

            while (matcher.find())
            {
                String target = String.format("'%s'", matcher.group(1));
                String replacement = String.format("\"%s\"", matcher.group(1));
                dataQuery = dataQuery.replace(target, replacement);
            }
        }

        return dataQuery;
    }

    protected String addExtraFilterInformation(String dataQuery, boolean showDeleted)
    {
        dataQuery = ("".equals(dataQuery))
                    ? BASE_FILTER_OUT_INFORMATION
                    : dataQuery.concat(";").concat(BASE_FILTER_OUT_INFORMATION);


        dataQuery = (!showDeleted)
                    ? dataQuery.concat(";").concat(FILTER_OUT_DELETED_ITEMS)
                    : dataQuery;

        return dataQuery;

    }

    protected AccountFeatureActionEnum determineAccountFeatureActionEnum(
            com.rubicon.platform.authorization.model.data.acm.Account account, AccountFeaturePermission permission)
    {
        AccountFeatureActionEnum action = AccountFeatureActionEnum.none;
        // We are only allowing publishers to be deleted/reactivated through this application
        if (isPublisherAccount(account))
        {
            if (isAccountDeleted(account) && permission.isReactivateAllowed())
            {
                action = AccountFeatureActionEnum.reactivate;
            }
            else if (!isAccountDeleted(account) && permission.isRemoveAllowed())
            {
                action = AccountFeatureActionEnum.delete;
            }
        }

        return action;
    }

    protected void validateReactivateAccount(com.rubicon.platform.authorization.model.data.acm.Account account,
                                             AccountFeaturePermission accountFeaturePermission)
    {
        // Only publisher account can be deleted
        if (!isPublisherAccount(account))
        {
            throw new ValidationException("Only accounts with the contextType of 'publisher' can be reactivated.");
        }
        // Confirm that the status of the object is deleted
        else if (!isAccountDeleted(account))
        {
            throw new ValidationException("The account provided can not be reactivated.");
        }
        // Confirm the user has permission to reactive an object;
        else if (!AccountFeatureActionEnum.reactivate
                .equals(determineAccountFeatureActionEnum(account, accountFeaturePermission)))
        {
            throw new ForbiddenException("You are not allowed to reactivate an account.");
        }
    }

    protected void validateRemoveAccount(com.rubicon.platform.authorization.model.data.acm.Account account,
                                         AccountFeaturePermission accountFeaturePermission)
    {
        // Only publisher account can be deleted
        if (!isPublisherAccount(account))
        {
            throw new ValidationException("Only accounts with the contextType of 'publisher' can be deleted.");
        }
        // Confirm that the status of the object is deleted
        else if (isAccountDeleted(account))
        {
            throw new ValidationException("The account provided can not be deleted.");
        }
        // Confirm the user has permission to remove an object;
        else if (!AccountFeatureActionEnum.delete
                .equals(determineAccountFeatureActionEnum(account, accountFeaturePermission)))
        {
            throw new ForbiddenException("You are not allowed to delete an account.");
        }
    }

    protected void updateAccountStatus(Long accountId, String status)
    {
        PersistentAccount persistentAccount = accountLoader.find(accountId);
        if (persistentAccount != null)
        {
            persistentAccount.setStatus(status);
            accountLoader.save(persistentAccount);
        }
    }

    private boolean isPublisherAccount(com.rubicon.platform.authorization.model.data.acm.Account account)
    {
        CompoundId compoundId = new CompoundId(account.getAccountId());

        return compoundId.isPublisher();
    }

    private boolean isAccountDeleted(com.rubicon.platform.authorization.model.data.acm.Account account)
    {
        return account.getStatus().equals("deleted");
    }

    private void handleRemoveReactivateError(com.rubicon.platform.authorization.model.data.acm.Account account,
                                             ServiceException exception)
    {
        logger.warn("there is an issue deleting/reactivating an account", exception);
        if (exception.getResponseCode() == 404)
        {
            CompoundId compoundId = new CompoundId(account.getAccountId());
            throw new NotFoundException(
                    String.format("Cannot find %s id %d", "account", Long.parseLong(compoundId.getId())));
        }
        else
        {
            throw new ServiceUnavailableException();
        }
    }


    protected PagedResponse<AssignedAccount> buildAssignedAccountDefaultPageResponse(Integer pageNumber,
                                                                                     Integer resultsPerPage)
    {
        Page page = new Page();
        page.setSize(resultsPerPage);
        page.setTotalElements(0L);
        page.setTotalPages(0);
        page.setNumber(pageNumber);

        PagedResponse<AssignedAccount> pagedResponse = new PagedResponse<>();
        pagedResponse.setPage(page);
        pagedResponse.setContent(new ArrayList<>());
        return pagedResponse;
    }

    public void setAccountFeatureLoader(AccountFeatureLoader accountFeatureLoader)
    {
        this.accountFeatureLoader = accountFeatureLoader;
    }

    public void setAccountFeatureTranslator(AccountFeatureTranslator accountFeatureTranslator)
    {
        this.accountFeatureTranslator = accountFeatureTranslator;
    }

    public void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    public void setPublisherManagementClient(PublisherManagementClient publisherManagementClient)
    {
        this.publisherManagementClient = publisherManagementClient;
    }

    public void setAssignedAccountAccountTranslator(
            AssignedAccountAccountTranslator assignedAccountAccountTranslator)
    {
        this.assignedAccountAccountTranslator = assignedAccountAccountTranslator;
    }
}
