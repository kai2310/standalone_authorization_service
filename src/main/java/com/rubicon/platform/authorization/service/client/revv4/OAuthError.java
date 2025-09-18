package com.rubicon.platform.authorization.service.client.revv4;

public class OAuthError
{
    private String error;
    private String errorDescription;

    @org.codehaus.jackson.annotate.JsonProperty("error")
    @com.fasterxml.jackson.annotation.JsonProperty("error")
    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    @org.codehaus.jackson.annotate.JsonProperty("error_description")
    @com.fasterxml.jackson.annotation.JsonProperty("error_description")
    public String getErrorDescription()
    {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription)
    {
        this.errorDescription = errorDescription;
    }
}

