package com.rubicon.platform.authorization.service.v1.ui.controller;


import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.service.v1.ui.resolver.AccountServiceResolver;
import com.rubicon.platform.authorization.service.v1.ui.resolver.FeatureServiceResolver;
import com.rubicon.platform.authorization.model.ui.acm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

import static com.rubicon.platform.authorization.service.utils.Constants.EDIT_ACCOUNT_FEATURES_ACTION;
import static com.rubicon.platform.authorization.service.utils.Constants.UI_AUTHORIZATION_RESOURCE;

@RequestMapping(value = "/v1/authorization/feature")
public class FeatureServiceController extends BaseUIController
{
    @Autowired
    protected FeatureServiceResolver featureServiceResolver;

    @Autowired
    protected AccountServiceResolver accountServiceResolver;


    private static final String FEATURE_HYPERION_ENDPOINT_NAME = "AccountFeature";
    private static final String ACCOUNT_HYPERION_ENDPOINT_NAME = "Account";


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listFeatures(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer resultSize,
            @RequestParam(value = "query", required = false) final String query,
            @RequestParam(value = "sort", required = false) final String sort,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "list_feature");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);
                boolean isEditable = getFeaturePermission(httpServletRequest);

                PagedResponse<Feature> response =
                        featureServiceResolver.getList(pageNumber, resultSize, query, sort, isEditable, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.feature.list"), httpServletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/{featureId}", method = RequestMethod.GET)
    public DeferredResult<HttpEntity> retrieveById(
            @PathVariable(value = "featureId") final Long featureId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "retrieve_feature");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                boolean isEditable = getFeaturePermission(httpServletRequest);

                Feature feature = featureServiceResolver.getById(featureId, isEditable, context);

                return new HttpEntity<>(feature);
            }
        };

        return submit(callable, getTimer("v1.feature.retrieve-by-id"), httpServletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<HttpEntity> create(
            @RequestBody final FeatureRequest featureRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable callable = new Callable()
        {
            @Override
            public Object call() throws Exception
            {
                assertAuthorized(httpServletRequest, "create_feature");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.POST,
                                httpServletRequest);

                Feature feature = featureServiceResolver
                        .create(featureRequest, getFeaturePermission(httpServletRequest), context);

                return new HttpEntity<>(feature);
            }
        };

        return submit(callable, getTimer("v1.feature.create"), httpServletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<HttpEntity> updateFeature(
            @RequestBody final FeatureRequest featureRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable callable = new Callable()
        {
            @Override
            public Object call() throws Exception
            {
                assertAuthorized(httpServletRequest, "update_feature");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                Feature feature = featureServiceResolver
                        .update(featureRequest, getFeaturePermission(httpServletRequest), context);

                return new HttpEntity<>(feature);
            }
        };

        return submit(callable, getTimer("v1.feature.update"), httpServletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/operation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<HttpEntity> editOperation(
            @RequestBody final EditFeatureOperationRequest editFeatureOperationRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable callable = new Callable()
        {
            @Override
            public Object call() throws Exception
            {
                assertAuthorized(httpServletRequest, "feature_operation");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.POST,
                                httpServletRequest);

                Feature feature = featureServiceResolver
                        .editOperation(editFeatureOperationRequest, getFeaturePermission(httpServletRequest), context);

                return new HttpEntity<>(feature);
            }
        };

        return submit(callable, getTimer("v1.feature.edit-operation"), httpServletRequest);
    }

    @RequestMapping(value = "/remove/{featureId}", method = RequestMethod.DELETE)
    public DeferredResult<HttpEntity> remove(
            @PathVariable(value = "featureId") final Long featureId,
            final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "remove_feature");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.DELETE,
                                httpServletRequest);

                featureServiceResolver
                        .remove(featureId, getFeaturePermission(httpServletRequest), context);

                return new HttpEntity<>(HttpStatus.OK);
            }
        };

        return submit(callable, getTimer("v1.feature.remove"), httpServletRequest);
    }

    @RequestMapping(value = "/assigned-accounts", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listFeatures(
            @RequestParam(value = "featureId", required = true) final Long featureId,
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer resultSize,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "retrieve_feature_assigned_accounts");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ACCOUNT_HYPERION_ENDPOINT_NAME, HttpMethod.GET, httpServletRequest);
                boolean isEditable =
                        getPermission(httpServletRequest, UI_AUTHORIZATION_RESOURCE, EDIT_ACCOUNT_FEATURES_ACTION);

                PagedResponse<AssignedAccount> response =
                        accountServiceResolver.getAccountsByFeatureId(featureId, pageNumber, resultSize, isEditable,
                                context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.feature.get_assigned_accounts"), httpServletRequest);
    }
}
