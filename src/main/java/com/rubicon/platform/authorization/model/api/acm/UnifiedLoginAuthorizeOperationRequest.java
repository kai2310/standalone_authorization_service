package com.rubicon.platform.authorization.model.api.acm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnifiedLoginAuthorizeOperationRequest
{
    private String token;
    private List<OperationRequest> operations;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public List<OperationRequest> getOperations()
    {
        return operations;
    }

    public void setOperations(List<OperationRequest> operations)
    {
        this.operations = operations;
    }
}
