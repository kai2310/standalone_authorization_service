package com.rubicon.platform.authorization.model.data.acm;

public class BaseRole extends BaseRoleApiObject
{
    private String ownerAccount;
    private com.rubicon.platform.authorization.model.data.acm.Status status;

    public String getOwnerAccount()
    {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount)
    {
        this.ownerAccount = ownerAccount;
    }

    public com.rubicon.platform.authorization.model.data.acm.Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}
