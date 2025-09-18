package com.rubicon.platform.authorization.service.v1.ui.controller;


import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.service.v1.ui.model.AccountFeaturePermission;
import com.rubicon.platform.authorization.service.v1.ui.resolver.AccountServiceResolver;
import com.rubicon.platform.authorization.model.ui.acm.Account;
import com.rubicon.platform.authorization.model.ui.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.EditAccountFeaturesRequest;
import com.rubicon.platform.authorization.model.ui.acm.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

import static com.rubicon.platform.authorization.service.utils.Constants.EDIT_ACCOUNT_FEATURES_ACTION;

@RequestMapping(value = "/v1/authorization/account")
public class AccountServiceController extends BaseUIController
{
    @Autowired
    protected AccountServiceResolver accountServiceResolver;

    private static final String ACCOUNT_HYPERION_ENDPOINT_NAME = "Account";

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listAccounts(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer resultSize,
            @RequestParam(value = "query", required = false) final String query,
            @RequestParam(value = "sort", required = false) final String sort,
            @RequestParam(value = "show_deleted", required = false, defaultValue = "false") final Boolean showDeleted,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "list_account");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                PagedResponse<Account> response =
                        accountServiceResolver.getList(pageNumber, resultSize, query, sort, showDeleted, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.account.list"), httpServletRequest);
    }


    @RequestMapping(value = "/feature", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<HttpEntity> addRemoveFeature(
            @RequestBody final EditAccountFeaturesRequest editAccountFeaturesRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, EDIT_ACCOUNT_FEATURES_ACTION);

                AccountFeaturePermission accountFeaturePermission = getAccountFeaturePermission(httpServletRequest);

                // Using PUT here as this action is always an data update operation
                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                AccountFeature response =
                        accountServiceResolver
                                .editFeatures(editAccountFeaturesRequest, accountFeaturePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.account.edit-features"), httpServletRequest);
    }


    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> retrieveAccount(
            @PathVariable("accountId") final Long accountId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "retrieve_account");

                AccountFeaturePermission accountFeaturePermission = getAccountFeaturePermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                AccountFeature response =
                        accountServiceResolver.retrieveAccount(accountId, accountFeaturePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.account.retrieve-by-id"), httpServletRequest);
    }

    @RequestMapping(value = "/remove/{accountId}", method = RequestMethod.DELETE)
    @ResponseBody
    public DeferredResult<HttpEntity> deleteAccount(
            @PathVariable("accountId") final Long accountId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "delete_account");

                AccountFeaturePermission accountFeaturePermission = getAccountFeaturePermission(httpServletRequest);

                // Using PUT here as this action is always an data update operation
                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                AccountFeature response =
                        accountServiceResolver.removeAccount(accountId, accountFeaturePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.account.remove"), httpServletRequest);
    }


    @RequestMapping(value = "/reactivate/{accountId}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<HttpEntity> reactivateAccount(
            @PathVariable("accountId") final Long accountId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "reactivate_account");

                AccountFeaturePermission accountFeaturePermission = getAccountFeaturePermission(httpServletRequest);

                // Using PUT here as this action is always an data update operation
                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                AccountFeature response =
                        accountServiceResolver.reactivateAccount(accountId, accountFeaturePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.account.reactivate"), httpServletRequest);
    }


}
