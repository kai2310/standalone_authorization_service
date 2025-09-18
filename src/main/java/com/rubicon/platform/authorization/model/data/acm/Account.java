package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.BaseApiObject;
import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Endpoint("Account")
@JsonPropertyOrder({"id","accountId","accountName","status","source","accountFeatureIds","created","modified"})
public class Account extends BaseApiObject<Long> implements Serializable
{
    private Date created;
    private Date modified;

    private String accountId;

    private String accountName;

    private String status;

    private String source;

    private Set<Long> accountFeatureIds;

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Set<Long> getAccountFeatureIds()
    {
        return accountFeatureIds;
    }

    public void setAccountFeatureIds(Set<Long> accountFeatureIds)
    {
        this.accountFeatureIds = accountFeatureIds;
    }
}
