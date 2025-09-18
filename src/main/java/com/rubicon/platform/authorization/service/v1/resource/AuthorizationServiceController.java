package com.rubicon.platform.authorization.service.v1.resource;

import com.rubicon.platform.authorization.data.model.*;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.service.BaseAuthorizeController;
import com.rubicon.platform.authorization.service.resolver.*;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.api.acm.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * User: mhellkamp
 * Date: 9/12/12
 */
@Controller
@RequestMapping(value = "/v1/authorization")
public class AuthorizationServiceController extends BaseAuthorizeController
{
    private Logger logger = LoggerFactory.getLogger(AuthorizationServiceController.class);

    private IdParser idParser = IdParser.STANDARD_ID_PARSER;

    @Autowired
    private RoleResolver roleResolver;

    @Autowired
    SubjectAccountMatchResolver subjectAccountMatchResolver;

    @Autowired
    private AccountFeatureResolver accountFeatureResolver;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<AuthorizeResponse> authorize(
            @RequestParam(value = "subjects", required = false) final String subjects,
            @RequestParam(value = "accountContext", required = false) final String accountContext,
            @RequestParam(value = "service", required = false) final String service,
            @RequestParam(value = "resource", required = false) final String resource,
            @RequestParam(value = "action", required = false) final String action,
            @RequestParam(value = "authorizedAccounts", required = false, defaultValue = "true") final boolean returnAuthorizedAccounts,
            HttpServletRequest httpServletRequest)
    {
        Callable<AuthorizeResponse> callable = new Callable<AuthorizeResponse>()
        {
            @Override
            public AuthorizeResponse call() throws Exception
            {
                if (StringUtils.isEmpty(subjects))
                {
                    throw new AuthorizationServiceException(422, "Missing parameter \"subjects\".");
                }

                Set<String> subjectSet = new HashSet<String>();
                String[] subjectArray = StringUtils.split(subjects, ",");
                Collections.addAll(subjectSet, subjectArray);

                AuthorizeRequest request = new AuthorizeRequest();
                request.setSubjects(subjectSet);
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
            HttpServletRequest httpServletRequest,
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

        Set<String> subjects = authorizeRequest.getSubjects();
        String accountContext = authorizeRequest.getAccountContext();
        String service = authorizeRequest.getService();
        String resource = authorizeRequest.getResource();
        String action = authorizeRequest.getAction();

        if (subjects == null || subjects.isEmpty())
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"subjects\".");
        }

        validateAuthorizeRequest(authorizeRequest);

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


        Set<String> subjectTypes = new HashSet<String>();
        List<CompoundId> subjectIds = new ArrayList<CompoundId>();
        for (String s : subjects)
        {
            CompoundId id = null;
            try
            {
                id = idParser.parseId(s);
            }
            catch (IllegalArgumentException e)
            {
                throw new AuthorizationServiceException(422, e.getMessage());
            }
            if (id == null)
            {
                throw new AuthorizationServiceException(422, "Subject can not be blank");
            }
            if (id.hasWildcards())
            {
                throw new AuthorizationServiceException(422, "Subjects may not contain wildcards.");
            }

            subjectTypes.add(id.getIdType());
            subjectIds.add(id);
        }

        return authorizeOperation(subjectIds, accountId, service, resource, action, returnAuthorizedAccounts);
    }

    private AuthorizeResponse authorizeOperation(List<CompoundId> subjectIds, CompoundId accountId, String service,
                                                 String resource, String action, boolean returnAuthorizedAccounts)
    {

        AuthorizeResponse response =
                baseAuthorizeOperation(subjectIds, accountId, service, resource, action, returnAuthorizedAccounts);

        return response;
    }

    @RequestMapping(value = "/authorizeOperations", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<AuthorizeOperationsResponse> authorizeOperations(
            @RequestParam(value = "authorizedAccounts", required = false, defaultValue = "true") final boolean returnAuthorizedAccounts,
            HttpServletRequest httpServletRequest,
            @RequestBody final AuthorizeOperationsRequest authorizeOperationsRequest)
    {
        Callable<AuthorizeOperationsResponse> callable = new Callable<AuthorizeOperationsResponse>()
        {
            @Override
            public AuthorizeOperationsResponse call() throws Exception
            {
                logger.debug("{}", authorizeOperationsRequest);

                Set<String> subjects = authorizeOperationsRequest.getSubjects();

                if (subjects == null || subjects.isEmpty())
                {
                    throw new AuthorizationServiceException(422, "Missing parameter \"subjects\".");
                }

                AuthorizeOperationsResponse response = new AuthorizeOperationsResponse();

                List<AuthorizeResponse> responses =
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
        return "authorization_v1";
    }
}
