package com.rubicon.platform.authorization.service.v2.resource;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.service.BaseAuthorizeController;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import com.rubicon.platform.authorization.service.resolver.*;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeResponse;
import com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeOperationsRequest;
import com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeOperationsResponse;
import com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeRequest;
import com.rubicon.platform.authorization.model.data.acm.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * User: jlukas
 * Date: 10/08/2015
 */
@Controller
@RequestMapping(value = "/v2/authorization")
public class AuthorizationServiceController_v2 extends BaseAuthorizeController
{
    private Logger logger = LoggerFactory.getLogger(AuthorizationServiceController_v2.class);

    private IdParser idParser = IdParser.STANDARD_ID_PARSER;

    @Autowired
    private UserInfoResolver userInfoResolver;

    @Autowired
    SubjectAccountMatchResolver subjectAccountMatchResolver;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<AuthorizeResponse> authorize(
            @RequestParam(value = "user_token", required = false) final String userToken,
            @RequestParam(value = "access_token", required = false) final String accessToken,
            @RequestParam(value = "accountContext", required = false) final String accountContext,
            @RequestParam(value = "service", required = false) final String service,
            @RequestParam(value = "resource", required = false) final String resource,
            @RequestParam(value = "action", required = false) final String action,
            @RequestParam(value = "authorizedAccounts", required = false, defaultValue = "true") final boolean returnAuthorizedAccounts,
            final HttpServletRequest httpServletRequest)
    {
        Callable<AuthorizeResponse> callable = new Callable<AuthorizeResponse>()
        {
            @Override
            public AuthorizeResponse call() throws Exception
            {
                AuthorizeRequest request = new AuthorizeRequest();
                request.setAccessToken(accessToken);
                request.setUserToken(userToken);
                request.setAccountContext(accountContext);
                request.setService(service);
                request.setResource(resource);
                request.setAction(action);

                return authorize(request, returnAuthorizedAccounts);
            }
        };

        return submit(callable, getTimer("authorize_get"), httpServletRequest);
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<AuthorizeResponse> authorizePost(
            @RequestParam(value = "authorizedAccounts", required = false, defaultValue = "true") final boolean authorizedAccounts,
            final HttpServletRequest httpServletRequest,
            @RequestBody final AuthorizeRequest authorizeRequest)
    {
        Callable<AuthorizeResponse> callable = new Callable<AuthorizeResponse>()
        {
            @Override
            public AuthorizeResponse call() throws Exception
            {

                return authorize(authorizeRequest, authorizedAccounts);
            }
        };

        return submit(callable, getTimer("authorize_post"), httpServletRequest);
    }

    private AuthorizeResponse authorize(AuthorizeRequest authorizeRequest, boolean returnAuthorizedAccounts)
    {
        logger.debug("{}", authorizeRequest);

        String userToken = authorizeRequest.getUserToken();
        String accessToken = authorizeRequest.getAccessToken();
        String accountContext = authorizeRequest.getAccountContext();
        String service = authorizeRequest.getService();
        String resource = authorizeRequest.getResource();
        String action = authorizeRequest.getAction();

        validateAuthorizeRequest(authorizeRequest);

        UserInfo userInfo = null;

        if (StringUtils.isNotBlank(userToken) || StringUtils.isNotBlank(accessToken))
        {
            String cid = MDC.get(RequestLogFilter.CID);
            userInfo = userInfoResolver.getUserInfo(userToken, accessToken, cid);
        }
        else
        {
            throw new AuthorizationServiceException(401, "Missing parameter \"userToken\" or \"accessToken\".");
        }

        if (userInfo == null)
        {
            throw new AuthorizationServiceException(401,
                    "Unable to resolve user information for given token, expired or invalid.");
        }

        List<CompoundId> subjectIds = new ArrayList<>();

        if (userInfo.getUserId() == null)
        {
            throw new AuthorizationServiceException(422, "Invalid ID for user.");
        }
        else
        {
            subjectIds.add(new CompoundId("user/" + userInfo.getUserId().toString()));
        }

        CompoundId accountId;
        try
        {
            accountId = idParser.parseId(accountContext);
        }
        catch (IllegalArgumentException e)
        {
            throw new AuthorizationServiceException(422, e.getMessage());
        }
        if (accountId != null && accountId.hasWildcards())
        {
            throw new AuthorizationServiceException(422, "AccountContext may not contain wildcards.");
        }

        return authorizeOperation(accountId, service, resource, action, returnAuthorizedAccounts, userInfo, subjectIds);
    }

    private AuthorizeResponse authorizeOperation(CompoundId accountId, String service, String resource, String action,
                                                 boolean returnAuthorizedAccounts, UserInfo userInfo,
                                                 List<CompoundId> subjectIds)
    {

        com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse
                temp = baseAuthorizeOperation(subjectIds, accountId, service, resource, action,
                returnAuthorizedAccounts);
        AuthorizeResponse response = new AuthorizeResponse(temp);
        response.setUserInfo(userInfo);

        return response;
    }

    @RequestMapping(value = "/authorizeOperations", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<AuthorizeOperationsResponse> authorizeOperations(
            @RequestParam(value = "authorizedAccounts", required = false, defaultValue = "true") final boolean returnAuthorizedAccounts,
            final HttpServletRequest httpServletRequest,
            @RequestBody final AuthorizeOperationsRequest authorizeOperationsRequest)
    {
        Callable<AuthorizeOperationsResponse> callable = new Callable<AuthorizeOperationsResponse>()
        {
            @Override
            public AuthorizeOperationsResponse call() throws Exception
            {
                logger.debug("{}", authorizeOperationsRequest);
                UserInfo userInfo = null;

                String userToken = authorizeOperationsRequest.getUserToken();
                String accessToken = authorizeOperationsRequest.getAccessToken();

                if (StringUtils.isNotBlank(userToken) || StringUtils.isNotBlank(accessToken))
                {
                    String cid = MDC.get(RequestLogFilter.CID);
                    userInfo = userInfoResolver.getUserInfo(userToken, accessToken, cid);
                }
                else
                {
                    throw new AuthorizationServiceException(401, "Missing parameter \"userToken\" or \"accessToken\".");
                }

                Set<String> subjects = new HashSet<>();

                if (userInfo != null && userInfo.getUserId() != null)
                {
                    String subject = "user/" + userInfo.getUserId().toString();
                    subjects.add(subject);
                }
                else
                {
                    throw new AuthorizationServiceException(401,
                            "Unable to resolve user information for given token, expired or invalid.");
                }

                AuthorizeOperationsResponse response = new AuthorizeOperationsResponse();

                response.setUserInfo(userInfo);
                List<com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse> responses =
                        baseAuthorizeOperations(subjects, authorizeOperationsRequest, returnAuthorizedAccounts);
                response.setAuthorizeResponses(responses);

                return response;
            }
        };
        return submit(callable, getTimer("authorizeOperations"), httpServletRequest);
    }

    @Override
    protected String getEndpointName()
    {
        return "authorization_v2";
    }
}
