package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"userId", "username", "displayAddButton", "roleAssignments"})
public class UserRoleAssignment
{
    private Long userId;
    private String username;
    private Boolean displayAddButton;
    private List<RoleAssignment> roleAssignments;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Boolean getDisplayAddButton()
    {
        return displayAddButton;
    }

    public void setDisplayAddButton(Boolean displayAddButton)
    {
        this.displayAddButton = displayAddButton;
    }

    public List<RoleAssignment> getRoleAssignments()
    {
        return roleAssignments;
    }

    public void setRoleAssignments(List<RoleAssignment> roleAssignments)
    {
        this.roleAssignments = roleAssignments;
    }
}
