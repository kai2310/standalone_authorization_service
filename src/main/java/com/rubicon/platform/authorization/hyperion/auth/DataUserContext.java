package com.rubicon.platform.authorization.hyperion.auth;

import com.dottydingo.service.endpoint.context.UserContext;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;

public class DataUserContext extends UserContext
{

    private UserSelf userSelf;
    private String accessToken;
    private String correlationId;

    public DataUserContext(UserSelf userSelf, String accessToken, String correlationId)
    {
        this.userSelf = userSelf;
        this.accessToken = accessToken;
        this.correlationId = correlationId;
    }

    @Override
    public String getUserName()
    {
        return userSelf.getUsername() ;
    }

    @Override
    public String getUserId()
    {
        return  userSelf.getId().toString();
    }

    public UserSelf getUserSelf()
    {
        return userSelf;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getCorrelationId()
    {
        return correlationId;
    }
}
