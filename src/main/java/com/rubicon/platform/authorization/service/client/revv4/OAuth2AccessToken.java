package com.rubicon.platform.authorization.service.client.revv4;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class OAuth2AccessToken
{
    private String accessToken;
    private long expiresIn;
    private long expires;
    private String scope;
    private String tokenType = "bearer";
    private String refreshToken;
    private String userId;
    private String userName;
    private String email;
    private String contextType;
    private String accountId;
    private String accountName;
    private Map<String,Object> additionalInformation = new HashMap<String, Object>();

    @org.codehaus.jackson.annotate.JsonProperty("access_token")
    @com.fasterxml.jackson.annotation.JsonProperty("access_token")
    public String getAccessToken()
    {
        return accessToken;
    }

    protected void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    @org.codehaus.jackson.annotate.JsonProperty("expires_in")
    @com.fasterxml.jackson.annotation.JsonProperty("expires_in")
    public long getExpiresIn()
    {
        return expiresIn;
    }

    protected void setExpiresIn(long expires)
    {
        this.expiresIn = expires;
        this.expires = System.currentTimeMillis() + (expires * 1000);
    }



    @org.codehaus.jackson.annotate.JsonProperty("scope")
    @com.fasterxml.jackson.annotation.JsonProperty("scope")
    public String getScope()
    {
        return scope;
    }

    protected void setScope(String scope)
    {
        this.scope = scope;
    }

    @org.codehaus.jackson.annotate.JsonProperty("token_type")
    @com.fasterxml.jackson.annotation.JsonProperty("token_type")
    public String getTokenType()
    {
        return tokenType;
    }

    protected void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }

    @org.codehaus.jackson.annotate.JsonProperty("refresh_token")
    @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
    public String getRefreshToken()
    {
        return refreshToken;
    }

    protected void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    @org.codehaus.jackson.annotate.JsonProperty("user_id")
    @com.fasterxml.jackson.annotation.JsonProperty("user_id")
    public String getUserId()
    {
        return userId;
    }

    protected void setUserId(String userId)
    {
        this.userId = userId;
    }

    @org.codehaus.jackson.annotate.JsonProperty("username")
    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public String getUserName()
    {
        return userName;
    }

    protected void setUserName(String userName)
    {
        this.userName = userName;
    }

    @org.codehaus.jackson.annotate.JsonProperty("email")
    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public String getEmail()
    {
        return email;
    }

    protected void setEmail(String email)
    {
        this.email = email;
    }

    @org.codehaus.jackson.annotate.JsonProperty("context_type")
    @com.fasterxml.jackson.annotation.JsonProperty("context_type")
    public String getContextType()
    {
        return contextType;
    }

    protected void setContextType(String contextType)
    {
        this.contextType = contextType;
    }

    @org.codehaus.jackson.annotate.JsonProperty("account_id")
    @com.fasterxml.jackson.annotation.JsonProperty("account_id")
    public String getAccountId()
    {
        return accountId;
    }

    protected void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    @org.codehaus.jackson.annotate.JsonProperty("account_name")
    @com.fasterxml.jackson.annotation.JsonProperty("account_name")
    public String getAccountName()
    {
        return accountName;
    }

    protected void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    @org.codehaus.jackson.annotate.JsonAnyGetter()
    @com.fasterxml.jackson.annotation.JsonAnyGetter()
    public Map<String, Object> getAdditionalInformation()
    {
        return additionalInformation;
    }

    @org.codehaus.jackson.annotate.JsonAnySetter()
    @com.fasterxml.jackson.annotation.JsonAnySetter()
    protected void setAdditionalInformation(Map<String, Object> additionalInformation)
    {
        this.additionalInformation = additionalInformation;
    }

    public boolean isExpired()
    {
        return expires != 0 && expires < System.currentTimeMillis();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("OAuth2AccessToken{");
        sb.append("accessToken='").append(accessToken).append('\'');
        sb.append(", expires=").append(expires);
        sb.append(", scope='").append(scope).append('\'');
        sb.append(", tokenType='").append(tokenType).append('\'');
        sb.append(", refreshToken='").append(refreshToken).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", contextType='").append(contextType).append('\'');
        sb.append(", accountId='").append(accountId).append('\'');
        sb.append(", accountName='").append(accountName).append('\'');
        sb.append(", additionalInformation=").append(additionalInformation);
        sb.append(", expired=").append(isExpired());
        sb.append('}');
        return sb.toString();
    }
}

