package com.rubicon.platform.authorization.service.v1.resource;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.AuthorizationInterceptor;
import com.rubicon.platform.authorization.service.BaseAuthorizeController;
import com.rubicon.platform.authorization.service.NoOpPersistenceContextFactory;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import com.rubicon.platform.authorization.model.data.acm.Role;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import com.rubicon.platform.authorization.model.api.acm.operation.OperationRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Controller
@RequestMapping(value = "/v1/operation")
public class OperationServiceController extends BaseAuthorizeController
{
    @Autowired
    private OperationServiceResolver operationServiceResolver;

    @Autowired
    private NoOpPersistenceContextFactory noOpPersistenceContextFactory;

    @Override
    protected String getEndpointName()
    {
        return "operation";
    }

    private static final String OPERATION_SERVICE = "AccessManagement";
    private static final String OPERATION_ACCOUNT_FEATURE_UPSERT_ACTION = "account-feature-upsert";
    private static final String OPERATION_ROLE_UPSERT_ACTION = "role-upsert";

    private static final String ROLE_HYPERION_ENDPOINT_NAME = "Role";
    private static final String ACCOUNT_FEATURE_HYPERION_ENDPOINT_NAME = "AccountFeature";

    @RequestMapping(value = "/role/upsert ", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<Role> upsertOperationToRole(
            @RequestBody final OperationRequest operationRequest,
            final HttpServletRequest httpServletRequest
    ) {
        Callable<Role> callable = new Callable<Role>() {
            @Override
            public Role call() throws Exception {

                validateUserPermission(httpServletRequest, OPERATION_SERVICE, getEndpointName(), OPERATION_ROLE_UPSERT_ACTION);
                validateRequestedOperation(operationRequest.getOperation());
                return operationServiceResolver.upsertOperationToRole(operationRequest,
                        noOpPersistenceContextFactory.createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.PUT, httpServletRequest));
            }
        };

        return submit(callable, getTimer("upsertOperationToRole"), httpServletRequest);
    }

    @RequestMapping(value = "/account-feature/upsert", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<AccountFeature> upsertOperationToAccountFeature(
            @RequestBody final OperationRequest operationRequest,
            final HttpServletRequest httpServletRequest
    ) {

        Callable<AccountFeature> callable = new Callable<AccountFeature>() {
            @Override
            public AccountFeature call() throws Exception {

                validateUserPermission(httpServletRequest, OPERATION_SERVICE, getEndpointName(), OPERATION_ACCOUNT_FEATURE_UPSERT_ACTION);
                validateRequestedOperation(operationRequest.getOperation());
                return operationServiceResolver.upsertOperationToAccountFeature(operationRequest,
                        noOpPersistenceContextFactory.createPersistenceContext(ACCOUNT_FEATURE_HYPERION_ENDPOINT_NAME, HttpMethod.PUT, httpServletRequest));
            }
        };

        return submit(callable, getTimer("upsertOperationToAccountFeature"), httpServletRequest);
    }

    // validate operation to insert/update
    // resource and action are allowed to be *, but service is not
    protected void validateRequestedOperation(Operation operation)
    {
        if (operation == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Missing operation to update/insert.");
        }

        if (StringUtils.isBlank(operation.getService()))
        {
            throw new AuthorizationServiceException(HttpStatus.SC_UNPROCESSABLE_ENTITY, "Missing parameter \"service\".");
        }

        if (operation.getService().equals("*"))
        {
            throw new AuthorizationServiceException(HttpStatus.SC_UNPROCESSABLE_ENTITY, "Parameter \"service\" can not be a wildcard.");
        }

        if (StringUtils.isBlank(operation.getResource()))
        {
            throw new AuthorizationServiceException(HttpStatus.SC_UNPROCESSABLE_ENTITY, "Missing parameter \"resource\".");
        }

        if (StringUtils.isBlank(operation.getAction()))
        {
            throw new AuthorizationServiceException(HttpStatus.SC_UNPROCESSABLE_ENTITY, "Missing parameter \"action\".");
        }
    }
    // validate if user has the operation tied to master context
    protected void validateUserPermission(HttpServletRequest httpServletRequest, String service, String resource, String action)
    {
        Object user = httpServletRequest.getAttribute(AuthorizationInterceptor.USER_INFO);
        if(!(user instanceof UserSelf))
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Missing or invalid access/user token.");

        List<CompoundId> subjects = Arrays.asList(new CompoundId("user/" + ((UserSelf) user).getId().toString()));

        AuthorizeResponse authorizeResponse =
                baseAuthorizeOperation(subjects, new CompoundId(Constants.MAGNITE_INTERNAL_CONTEXT),
                service,
                resource,
                action,
                false);

        if (!authorizeResponse.getAuthorized()) {
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "User doesn't have the access to this service.");
        }
    }

}
