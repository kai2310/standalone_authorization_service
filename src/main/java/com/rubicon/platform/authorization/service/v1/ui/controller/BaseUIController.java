package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.AuthorizationInterceptor;
import com.rubicon.platform.authorization.service.BaseAuthorizeController;
import com.rubicon.platform.authorization.service.NoOpPersistenceContextFactory;
import com.rubicon.platform.authorization.service.cache.DisableUserPermissionObjectCache;
import com.rubicon.platform.authorization.service.cache.cluster.DistributedInvalidationBroadcaster;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.ui.model.AbstractPermission;
import com.rubicon.platform.authorization.service.v1.ui.model.AccountFeaturePermission;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleAssignmentPermission;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import com.rubicon.platform.authorization.model.api.acm.BaseAuthorizeOperationsRequest;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.rubicon.platform.authorization.service.utils.Constants.*;

abstract public class BaseUIController extends BaseAuthorizeController
{
    @Value("${disablePermissionsAllowed}")
    private boolean disablePermissionsAllowed;

    @Value("${application_environment}")
    private String environment;

    @Autowired
    private NoOpPersistenceContextFactory noOpPersistenceContextFactory;

    @Autowired
    public DisableUserPermissionObjectCache disabledPermissionUserCache;

    @Override
    protected String getEndpointName()
    {
        return "ui_authorization";
    }


    public boolean isDisablePermissionsAllowed()
    {
        return disablePermissionsAllowed;
    }

    public void setDisablePermissionsAllowed(boolean disablePermissionsAllowed)
    {
        this.disablePermissionsAllowed = disablePermissionsAllowed;
    }

    public String getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    protected Long getRequestedUser(HttpServletRequest httpServletRequest)
    {
        Object userInfo = httpServletRequest.getAttribute(AuthorizationInterceptor.USER_INFO);
        if (!(userInfo instanceof UserSelf))
        {
            throw new UnauthorizedException("Missing or invalid access/user token.");
        }

        return ((UserSelf) userInfo).getId();
    }


    public void assertAuthorized(HttpServletRequest httpServletRequest, String action)
    {
        Long userId = getRequestedUser(httpServletRequest);

        if (arePermissionsEnforced(userId))
        {
            List<CompoundId> subjects = Arrays.asList(new CompoundId("user", userId.toString()));
            AuthorizeResponse authorizeResponse =
                    baseAuthorizeOperation(subjects, new CompoundId(Constants.MAGNITE_INTERNAL_CONTEXT),
                            Constants.AUTHORIZATION_SERVICE_NAME,
                            getEndpointName(),
                            action,
                            false);

            if (!authorizeResponse.getAuthorized())
            {
                throw new UnauthorizedException("User doesn't have the access to this service.");
            }
        }
    }

    public boolean getFeaturePermission(HttpServletRequest httpServletRequest)
    {
        return getPermission(httpServletRequest, API_RESTRICTION_RESOURCE, FEATURE_RESTRICTION_ACTION);
    }

    public boolean getPermission(HttpServletRequest httpServletRequest, String resource, String action)
    {
        Long userId = getRequestedUser(httpServletRequest);

        boolean isAuthorized = (arePermissionsEnforced(userId))
                               ? isUserAuthorized(userId.toString(), AUTHORIZATION_SERVICE_NAME, resource, action)
                               : true;

        return isAuthorized;
    }


    protected void buildPermissionObject(Long userId, AbstractPermission abstractPermission)
    {
        List<OperationRequest> operations = abstractPermission.buildOperationRequestList();

        if (arePermissionsEnforced(userId))
        {
            // Build The parameters for the authorization request
            BaseAuthorizeOperationsRequest authorizeOperationsRequest = new BaseAuthorizeOperationsRequest();


            CompoundId masterAccount = new CompoundId(Constants.MAGNITE_INTERNAL_CONTEXT);
            authorizeOperationsRequest.setAccountContext(masterAccount.toString());
            authorizeOperationsRequest.setOperations(operations);

            CompoundId subject = new CompoundId("user", userId.toString());
            Set<String> subjectSet = new HashSet<>(Arrays.asList(subject.asIdString()));

            // Verify the Users Permissions
            List<AuthorizeResponse> authorizeResponses =
                    baseAuthorizeOperations(subjectSet, authorizeOperationsRequest, false);

            for (AuthorizeResponse response : authorizeResponses)
            {
                abstractPermission.setValue(response.getAction(), response.getAuthorized());
            }
        }
        else
        {
            for (OperationRequest operation : operations)
            {
                abstractPermission.setValue(operation.getAction(), true);
            }
        }

    }

    public RoleAssignmentPermission getRoleAssignmentPermission(HttpServletRequest httpServletRequest)
    {
        Long userId = getRequestedUser(httpServletRequest);

        RoleAssignmentPermission roleAssignmentPermission = new RoleAssignmentPermission();
        buildPermissionObject(userId, roleAssignmentPermission);

        return roleAssignmentPermission;
    }


    public RoleTypePermission getRoleTypePermission(HttpServletRequest httpServletRequest)
    {
        Long userId = getRequestedUser(httpServletRequest);

        RoleTypePermission roleTypePermission = new RoleTypePermission();
        buildPermissionObject(userId, roleTypePermission);

        return roleTypePermission;
    }


    public AccountFeaturePermission getAccountFeaturePermission(HttpServletRequest httpServletRequest)
    {
        Long userId = getRequestedUser(httpServletRequest);

        AccountFeaturePermission accountFeaturePermission = new AccountFeaturePermission();
        buildPermissionObject(userId, accountFeaturePermission);

        return accountFeaturePermission;
    }

    protected boolean isUserAuthorized(String userId, String service, String resource, String action)
    {
        boolean isAuthorized;
        if (arePermissionsEnforced(Long.parseLong(userId)))
        {
            List<CompoundId> subjects = Arrays.asList(new CompoundId("user", userId));
            AuthorizeResponse authorizeResponse =
                    baseAuthorizeOperation(subjects, new CompoundId(Constants.MAGNITE_INTERNAL_CONTEXT),
                            service, resource, action, false);

            isAuthorized = authorizeResponse.getAuthorized();
        }
        else
        {
            isAuthorized = true;
        }

        return isAuthorized;
    }


    protected void disableUserPermission(HttpServletRequest httpServletRequest)
    {
        Long userId = getRequestedUser(httpServletRequest);

        Long cachedUserId = getCachedUserId(userId);

        // If the cachedUserId is null, it means we want to add an entry, if there is a value, we just
        // want to update the entry.
        if ((cachedUserId == null))
        {
            disabledPermissionUserCache.addEntry(userId);
            DistributedInvalidationBroadcaster.getInstance().processCreate("DisableUserPermission", userId);
        }
        else
        {
            disabledPermissionUserCache.updateEntry(userId);
            DistributedInvalidationBroadcaster.getInstance().processUpdate("DisableUserPermission", userId);
        }
    }

    protected boolean arePermissionsEnforced(Long userId)
    {
        Long cachedUserId = getCachedUserId(userId);

        boolean arePermissionsEnforced = true;
        if (canPermissionsBeDisabled() && (cachedUserId != null))
        {
            arePermissionsEnforced = false;
        }

        // Look in some cache and see if the user is found
        return arePermissionsEnforced;
    }


    protected boolean canPermissionsBeDisabled()
    {
        return (!getEnvironment().equals(Constants.ENV_PROD) && disablePermissionsAllowed);
    }

    protected Long getCachedUserId(Long userId)
    {
        return disabledPermissionUserCache.getItemById(userId);
    }

    public NoOpPersistenceContextFactory getNoOpPersistenceContextFactory()
    {
        return noOpPersistenceContextFactory;
    }

}
