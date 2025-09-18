package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;
import java.util.List;

public class BaseAuthorizeOperationsRequest implements Serializable
{
    private String accountContext;
    private List<OperationRequest> operations;

    public String getAccountContext()
    {
        return accountContext;
    }

    public void setAccountContext(String accountContext)
    {
        this.accountContext = accountContext;
    }

    public List<OperationRequest> getOperations()
    {
        return operations;
    }

    public void setOperations(List<OperationRequest> operations)
    {
        this.operations = operations;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeOperationsRequest{");
        sb.append(", accountContext='").append(accountContext).append('\'');
        sb.append(", operations=").append(operations);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseAuthorizeOperationsRequest that = (BaseAuthorizeOperationsRequest) o;

        if (accountContext != null
            ? !accountContext.equals(that.accountContext)
            : that.accountContext != null)
        {
            return false;
        }
        return operations != null
               ? operations.equals(that.operations)
               : that.operations == null;

    }

    @Override
    public int hashCode()
    {
        int result = accountContext != null
                     ? accountContext.hashCode()
                     : 0;
        result = 31 * result + (operations != null
                                ? operations.hashCode()
                                : 0);
        return result;
    }
}
