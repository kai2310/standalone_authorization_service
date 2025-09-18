package com.rubicon.platform.authorization.model.api.acm;

import java.util.List;
import java.util.Objects;

public class AccountFeatureQueryResponse
{
    private List<Long> accountFeatureId;

    public List<Long> getAccountFeatureId()
    {
        return accountFeatureId;
    }

    public void setAccountFeatureId(List<Long> accountFeatureId)
    {
        this.accountFeatureId = accountFeatureId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountFeatureQueryResponse that = (AccountFeatureQueryResponse) o;
        return Objects.equals(accountFeatureId, that.accountFeatureId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(accountFeatureId);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AccountFeatureQueryResponse{");
        sb.append("accountFeatureId=").append(accountFeatureId);
        sb.append('}');
        return sb.toString();
    }
}
