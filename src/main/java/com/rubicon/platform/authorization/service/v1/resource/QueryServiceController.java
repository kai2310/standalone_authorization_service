package com.rubicon.platform.authorization.service.v1.resource;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentLoader;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.service.AuthorizationInterceptor;
import com.rubicon.platform.authorization.service.BaseController;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.api.acm.AccountFeatureQueryResponse;
import com.rubicon.platform.authorization.model.api.acm.RoleQueryResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.Callable;

/**
 */
@Controller
@RequestMapping(value = "/v1/query")
public class QueryServiceController extends BaseController
{
    private IdParser idParser = IdParser.STANDARD_ID_PARSER;
    @Autowired
    private RoleAssignmentLoader roleAssignmentLoader;

    @Autowired
    private OperationQueryResolver operationQueryResolver;


    private boolean requireAccessToken = false;

    public void setRequireAccessToken(boolean requireAccessToken)
    {
        this.requireAccessToken = requireAccessToken;
    }

    @RequestMapping(value = "/subjects",method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ServiceSubjectQueryResponse> querySubjects(
            @RequestParam(value = "accountId",required = false) final String accountId,
            @RequestParam(value = "accountGroupId",required = false) final Long accountGroupId,
            @RequestParam(value = "roleId",required = false) final Long roleId,
            final HttpServletRequest httpServletRequest)
    {
        Callable<ServiceSubjectQueryResponse> callable = new Callable<ServiceSubjectQueryResponse>()
        {
            @Override
            public ServiceSubjectQueryResponse call() throws Exception
            {

                checkAuthorization(httpServletRequest);
                if(StringUtils.isNotBlank(accountId) && accountGroupId != null)
                    throw new ServiceException(422,String.format("Can not specify both accountId and accountGroupId."));

                if(StringUtils.isBlank(accountId) && accountGroupId == null && roleId == null)
                    throw new ServiceException(422,String.format("Must specify at least one parameter: accountId,accountGroupId,roleId"));

                CompoundId account = null;
                if(StringUtils.isNotBlank(accountId))
                {
                    try
                    {
                        account = idParser.parseId(accountId);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new ServiceException(422,String.format("Invalid accountId: %s",e.getMessage()));
                    }
                }
                List<CompoundId> subjects = roleAssignmentLoader.findSubjects(account, accountGroupId, roleId);
                ServiceSubjectQueryResponse response = new ServiceSubjectQueryResponse();
                response.setSubjects(subjects);
                return response;
            }
        };

        return submit(callable,getTimer("subjects"), httpServletRequest);

    }

    @RequestMapping(value = "/roles",method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<RoleQueryResponse> queryRoles(
            @RequestParam(value = "service",required = true) final String service,
            @RequestParam(value = "resource",required = true)final String resource,
            @RequestParam(value = "action",required = true) final String action,
            final HttpServletRequest httpServletRequest)
    {
        Callable<RoleQueryResponse> callable = new Callable<RoleQueryResponse>()
        {
            @Override
            public RoleQueryResponse call() throws Exception
            {
                checkAuthorization(httpServletRequest);

                if(service.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"service\" can not be a wildcard.");

                if(resource.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"resource\" can not be a wildcard.");

                if(action.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"action\" can not be a wildcard.");

                List<Long> roleIds = operationQueryResolver.getMatchingRoles(service, resource, action);
                RoleQueryResponse response = new RoleQueryResponse();
                response.setRoleIds(roleIds);
                return response;
            }
        };
        return submit(callable,getTimer("roles"), httpServletRequest);
    }

    @RequestMapping(value = "/accountFeatures",method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<AccountFeatureQueryResponse> queryAccountFeatures(
            @RequestParam(value = "service",required = true) final String service,
            @RequestParam(value = "resource",required = true)final String resource,
            @RequestParam(value = "action",required = true) final String action,
            final HttpServletRequest httpServletRequest)
    {
        Callable<AccountFeatureQueryResponse> callable = new Callable<AccountFeatureQueryResponse>()
        {
            @Override
            public AccountFeatureQueryResponse call() throws Exception
            {
                checkAuthorization(httpServletRequest);

                if(service.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"service\" can not be a wildcard.");

                if(resource.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"resource\" can not be a wildcard.");

                if(action.equals("*"))
                    throw new AuthorizationServiceException(422,"Parameter \"action\" can not be a wildcard.");

                List<Long> roleIds = operationQueryResolver.getMatchingAccountFeatures(service, resource, action);
                AccountFeatureQueryResponse response = new AccountFeatureQueryResponse();
                response.setAccountFeatureId(roleIds);
                return response;
            }
        };
        return submit(callable,getTimer("accountFeatures"), httpServletRequest);
    }


    @Override
    protected String getEndpointName()
    {
        return "query";
    }

    protected void checkAuthorization(HttpServletRequest httpServletRequest)
    {
        if(!requireAccessToken) return;

        Object user = httpServletRequest.getAttribute(AuthorizationInterceptor.USER_INFO);
        if(!(user instanceof UserSelf))
            throw new ServiceException(401,"Missing or invalid access/user token.");
    }
}
