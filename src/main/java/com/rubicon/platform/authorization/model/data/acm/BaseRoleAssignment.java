package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;

import java.io.Serializable;
import java.util.List;

public class BaseRoleAssignment extends BaseAuditableApiObject<Long> implements Serializable
{
    protected String ownerAccount;
    protected String subject;
    protected String account;
    protected Long roleId;
    protected List<String> scope;
    protected String realm;
    protected Long accountGroupId;

    public String getOwnerAccount()
    {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount)
    {
        this.ownerAccount = ownerAccount;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public List<String> getScope()
    {
        return scope;
    }

    public void setScope(List<String> scope)
    {
        this.scope = scope;
    }

    public Long getAccountGroupId()
    {
        return accountGroupId;
    }

    public void setAccountGroupId(Long accountGroupId)
    {
        this.accountGroupId = accountGroupId;
    }
}
