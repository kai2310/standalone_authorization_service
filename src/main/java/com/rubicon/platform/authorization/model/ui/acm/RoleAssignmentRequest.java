package com.rubicon.platform.authorization.model.ui.acm;

public class RoleAssignmentRequest
{
    private Long userId;
    private Long roleId;
    private Long accountId;
    private AccountGroupEnum accountGroup;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public AccountGroupEnum getAccountGroup()
    {
        return accountGroup;
    }

    public void setAccountGroup(AccountGroupEnum accountGroup)
    {
        this.accountGroup = accountGroup;
    }
}
