package com.rubicon.platform.authorization.model.api.acm.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import com.rubicon.platform.authorization.model.data.acm.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jlukas
 * Date: 10/07/15
 */
@JsonPropertyOrder({"userInfo", "authorizedResponses"})
public class AuthorizeOperationsResponse implements Serializable
{
    private List<AuthorizeResponse> authorizeResponses = new ArrayList<AuthorizeResponse>();
    private UserInfo userInfo;

    public List<AuthorizeResponse> getAuthorizeResponses()
    {
        return authorizeResponses;
    }

    public void setAuthorizeResponses(List<AuthorizeResponse> authorizeResponses)
    {
        this.authorizeResponses = authorizeResponses;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeOperationsResponse{");
        sb.append("authorizeResponses=").append(authorizeResponses);
        sb.append(", userInfo=").append(userInfo);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizeOperationsResponse that = (AuthorizeOperationsResponse) o;

        if (authorizeResponses != null
            ? !authorizeResponses.equals(that.authorizeResponses)
            : that.authorizeResponses != null)
        {
            return false;
        }
        return userInfo != null
               ? userInfo.equals(that.userInfo)
               : that.userInfo == null;

    }

    @Override
    public int hashCode()
    {
        int result = authorizeResponses != null
                     ? authorizeResponses.hashCode()
                     : 0;
        result = 31 * result + (userInfo != null
                                ? userInfo.hashCode()
                                : 0);
        return result;
    }
}
