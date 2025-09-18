package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "status", "roleAssignmentId", "account", "editable"})
public class AssignedUser
{
    private Long id;
    private String name;
    private String status;
    private Long roleAssignmentId;
    private AccountReference account;
    private Boolean editable;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getRoleAssignmentId()
    {
        return roleAssignmentId;
    }

    public void setRoleAssignmentId(Long roleAssignmentId)
    {
        this.roleAssignmentId = roleAssignmentId;
    }

    public AccountReference getAccount()
    {
        return account;
    }

    public void setAccount(AccountReference account)
    {
        this.account = account;
    }

    public Boolean getEditable()
    {
        return editable;
    }

    public void setEditable(Boolean editable)
    {
        this.editable = editable;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Long id;
        private String name;
        private String status;
        private Long roleAssignmentId;
        private AccountReference account;
        private Boolean editable;

        public Builder withId(Long id)
        {
            this.id = id;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withStatus(String status)
        {
            this.status = status;
            return this;
        }

        public Builder withRoleAssignmentId(Long roleAssignmentId)
        {
            this.roleAssignmentId = roleAssignmentId;
            return this;
        }

        public Builder withAccount(AccountReference account)
        {
            this.account = account;
            return this;
        }

        public Builder withEditable(Boolean editable)
        {
            this.editable = editable;
            return this;
        }

        public AssignedUser build()
        {
            AssignedUser assignedUser = new AssignedUser();
            assignedUser.setId(id);
            assignedUser.setName(name);
            assignedUser.setStatus(status);
            assignedUser.setRoleAssignmentId(roleAssignmentId);
            assignedUser.setAccount(account);
            assignedUser.setEditable(editable);
            return assignedUser;
        }
    }
}
