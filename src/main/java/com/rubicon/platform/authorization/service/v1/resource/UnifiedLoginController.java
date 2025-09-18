package com.rubicon.platform.authorization.service.v1.resource;

import com.rubicon.platform.authorization.model.api.idm.User;
import com.rubicon.platform.authorization.service.BaseAuthorizeController;
import com.rubicon.platform.authorization.service.cache.CacheHelper;
import com.rubicon.platform.authorization.service.client.model.UnifiedLoginUserInfo;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.data.acm.UserInfo;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import com.rubicon.platform.authorization.model.api.acm.BaseAuthorizeOperationsRequest;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;
import com.rubicon.platform.authorization.model.api.acm.UnifiedLoginAuthorizeOperationRequest;
import com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeOperationsResponse;
import net.sf.ehcache.Ehcache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@RequestMapping(value = "/v1/unified-login", produces = MediaType.APPLICATION_JSON_VALUE)
public class UnifiedLoginController extends BaseAuthorizeController
{
    @Qualifier("auth0TokenCache")
    @Autowired
    private Ehcache auth0TokenCache;

    @Qualifier("userEmailCache")
    @Autowired
    private Ehcache userEmailCache;

    @RequestMapping(value = "/authorize-operations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeferredResult<AuthorizeOperationsResponse> authorizeOperations(
            final HttpServletRequest httpServletRequest,
            @RequestBody final UnifiedLoginAuthorizeOperationRequest authorizeOperationsRequest)
    {
        Callable<AuthorizeOperationsResponse> callable = new Callable<AuthorizeOperationsResponse>()
        {
            @Override
            public AuthorizeOperationsResponse call()
            {
                // since the method to check permission: baseAuthorizeOperations
                // is in class BaseAuthorizeController
                // so adding all logic in the controller instead of creating a resolver
                validateUnifiedLoginAuthorizeOperationRequest(authorizeOperationsRequest);
                String token = authorizeOperationsRequest.getToken();

                // Get the Token from Auth0 Then Cache it.
                UnifiedLoginUserInfo unifiedLoginUserInfo = CacheHelper.get(auth0TokenCache, token);
                validateUnifiedLoginUserInfo(unifiedLoginUserInfo);

                // get the user from IDM, will cache it
                User idmUser = CacheHelper.get(userEmailCache, unifiedLoginUserInfo.getEmail());

                // make sure corresponding user is in DV+
                validateIdmUser(idmUser);
                Set<String> subjects = Collections.singleton("user/".concat(idmUser.getId().toString()));

                // construct response
                AuthorizeOperationsResponse response = new AuthorizeOperationsResponse();
                response.setUserInfo(convertToUserInfo(idmUser));
                List<AuthorizeResponse> responses =
                        baseAuthorizeOperations(subjects,
                                convertToBaseAuthorizeOperationsRequest(unifiedLoginUserInfo.getContextType(),
                                        unifiedLoginUserInfo.getContextId(),
                                        authorizeOperationsRequest.getOperations()),
                                true);
                response.setAuthorizeResponses(responses);

                return response;
            }
        };
        return submit(callable, getTimer("authorize_operations"), httpServletRequest);
    }

    // make sure a token is provided
    protected void validateUnifiedLoginAuthorizeOperationRequest(UnifiedLoginAuthorizeOperationRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getToken()))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"token\".");
        }
    }

    // make sure we have user email, context type and id populated
    protected void validateUnifiedLoginUserInfo(UnifiedLoginUserInfo unifiedLoginUserInfo)
    {
        if (unifiedLoginUserInfo == null || unifiedLoginUserInfo.getEmail() == null ||
            unifiedLoginUserInfo.getContextId() == null || unifiedLoginUserInfo.getContextType() == null)
        {
            throw new AuthorizationServiceException(422,
                    "Unable to resolve user and its context information from Auth0 for given token.");
        }
    }

    // make sure we can find streaming user in DV+
    protected void validateIdmUser(User idmUser)
    {
        if (idmUser == null)
        {
            throw new AuthorizationServiceException(422,
                    "Unable to find the corresponding user in DV+ for given token.");
        }
    }

    private UserInfo convertToUserInfo(User idmUser)
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(idmUser.getId());
        userInfo.setUsername(idmUser.getUsername());
        userInfo.setEmail(idmUser.getEmail());
        userInfo.setFirstName(idmUser.getFirstName());
        userInfo.setLastName(idmUser.getLastName());

        return userInfo;
    }

    private BaseAuthorizeOperationsRequest convertToBaseAuthorizeOperationsRequest(String contextType, String contextId,
                                                                                   List<OperationRequest> operationList)
    {
        BaseAuthorizeOperationsRequest request = new BaseAuthorizeOperationsRequest();
        request.setAccountContext(contextType.concat("/").concat(contextId));
        request.setOperations(operationList);

        return request;
    }

    @Override
    protected String getEndpointName()
    {
        return "unified_login";
    }
}
