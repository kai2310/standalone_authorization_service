package com.rubicon.platform.authorization.model.api.acm.v2;

import com.rubicon.platform.authorization.model.api.acm.BaseAuthorizeRequest;

import java.io.Serializable;

public class AuthorizeRequest extends BaseAuthorizeRequest implements Serializable
{
    private String accessToken;
    private String userToken;

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getUserToken()
    {
        return userToken;
    }

    public void setUserToken(String userToken)
    {
        this.userToken = userToken;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeRequest{");
        sb.append("accessToken=").append(accessToken);
        sb.append(", userToken=").append(userToken);
        sb.append(", accountContext='").append(getAccountContext()).append('\'');
        sb.append(", service='").append(getService()).append('\'');
        sb.append(", resource='").append(getResource()).append('\'');
        sb.append(", action='").append(getAction()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof com.rubicon.platform.authorization.model.api.acm.v2.AuthorizeRequest)) return false;

        AuthorizeRequest that = (AuthorizeRequest) o;

        if(!super.equals(that)) return false;
        else
        {

            if (accessToken != null
                ? !accessToken.equals(that.accessToken)
                : that.accessToken != null)
            {
                return false;
            }
            return !(userToken != null
                     ? !userToken.equals(that.userToken)
                     : that.userToken != null);
        }
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();

        result = 31 * result + (accessToken != null
                                ? accessToken.hashCode()
                                : 0);
        result = 31 * result + (userToken != null
                                ? userToken.hashCode()
                                : 0);
        return result;
    }
}
