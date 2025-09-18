package com.rubicon.platform.authorization.data.auth;

import com.dottydingo.hyperion.api.exception.AuthorizationException;
import com.dottydingo.hyperion.core.endpoint.HyperionContext;
import com.dottydingo.hyperion.core.endpoint.pipeline.auth.AuthorizationContext;
import com.dottydingo.hyperion.core.endpoint.pipeline.auth.AuthorizationProvider;
import com.dottydingo.hyperion.core.endpoint.pipeline.auth.NoOpAuthorizationContext;
import com.dottydingo.service.endpoint.context.UserContext;
import com.rubicon.platform.authorization.hyperion.exception.AuthenticationException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class AcmDataAuthorizationProvider implements AuthorizationProvider
{
    private boolean requireValidUser = true;

    public void setRequireValidUser(boolean requireValidUser)
    {
        this.requireValidUser = requireValidUser;
    }

    @Override
    public AuthorizationContext authorize(HyperionContext context) throws AuthorizationException
    {
        UserContext userContext = context.getUserContext();
        if (requireValidUser && (userContext == null || StringUtils.isEmpty(userContext.getUserId())))
        {
            throw new AuthenticationException("Missing or invalid access token.");
        }

        return new NoOpAuthorizationContext();
    }
}
