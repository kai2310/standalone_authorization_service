package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RoleQueryResponse implements Serializable
{
    private List<Long> roleIds;

    public List<Long> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds)
    {
        this.roleIds = roleIds;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleQueryResponse that = (RoleQueryResponse) o;
        return Objects.equals(roleIds, that.roleIds);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(roleIds);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("RoleQueryResponse{");
        sb.append("roleIds=").append(roleIds);
        sb.append('}');
        return sb.toString();
    }
}