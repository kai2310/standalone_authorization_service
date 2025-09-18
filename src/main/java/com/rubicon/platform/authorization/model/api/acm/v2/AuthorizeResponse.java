package com.rubicon.platform.authorization.model.api.acm.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rubicon.platform.authorization.model.data.acm.UserInfo;

import java.io.Serializable;

@JsonPropertyOrder({"service","resource","action","authorized","reason","userInfo","authorizedAccounts","scope"})
public class AuthorizeResponse extends com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse implements Serializable
{
    public AuthorizeResponse(){};
    public AuthorizeResponse(com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse base){
        this.setAction(base.getAction());
        this.setAuthorized(base.getAuthorized());
        this.setAuthorizedAccounts(base.getAuthorizedAccounts());
        this.setReason(base.getReason());
        this.setResource(base.getResource());
        this.setScope(base.getScope());
        this.setService(base.getService());
    }

    private UserInfo userInfo;

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
        final StringBuilder sb = new StringBuilder("AuthorizeResponse{");
        sb.append("userInfo=").append(userInfo);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizeResponse that = (AuthorizeResponse) o;

        return userInfo != null
               ? userInfo.equals(that.userInfo)
               : that.userInfo == null;

    }

    @Override
    public int hashCode()
    {
        return userInfo != null
               ? userInfo.hashCode()
               : 0;
    }
}
