package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;

import java.util.Set;

/**
 */
public class ServiceRoleAssignment extends RoleAssignment
{
    private CompoundId accountId;

    public ServiceRoleAssignment()
    {
    }

    public ServiceRoleAssignment(RoleAssignment other)
    {
        setAccount(other.getAccount());
        setAccountGroupId(other.getAccountGroupId());
        setCreated(other.getCreated());
        setCreatedBy(other.getCreatedBy());
        setId(other.getId());
        setModified(other.getModified());
        setModifiedBy(other.getModifiedBy());
        setOwnerAccount(other.getOwnerAccount());
        setRealm(other.getRealm());
        setRoleId(other.getRoleId());
        setScope(other.getScope());
        setStatus(other.getStatus());
        setSubject(other.getSubject());

        if(getAccount() != null)
            accountId = new CompoundId(getAccount());
    }

    public CompoundId getAccountId()
    {
        return accountId;
    }
}
