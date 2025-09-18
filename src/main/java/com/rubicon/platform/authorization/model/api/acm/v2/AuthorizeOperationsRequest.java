package com.rubicon.platform.authorization.model.api.acm.v2;

import com.rubicon.platform.authorization.model.api.acm.BaseAuthorizeOperationsRequest;

import java.io.Serializable;

/**
 * User: jlukas
 * Date: 10/07/15
 */
public class AuthorizeOperationsRequest extends BaseAuthorizeOperationsRequest implements Serializable
{
    private String userToken;
    private String accessToken;

    public String getUserToken()
    {
        return userToken;
    }

    public void setUserToken(String userToken)
    {
        this.userToken = userToken;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }


    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeOperationsRequest{");
        sb.append("accessToken=").append(accessToken);
        sb.append(", userToken=").append(userToken);
        sb.append(", accountContext='").append(getAccountContext()).append('\'');
        sb.append(", operations=").append(getOperations());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthorizeOperationsRequest that = (AuthorizeOperationsRequest) o;

        if (userToken != null
            ? !userToken.equals(that.userToken)
            : that.userToken != null)
        {
            return false;
        }
        return accessToken != null
               ? accessToken.equals(that.accessToken)
               : that.accessToken == null;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (userToken != null
                                ? userToken.hashCode()
                                : 0);
        result = 31 * result + (accessToken != null
                                ? accessToken.hashCode()
                                : 0);
        return result;
    }
}
