package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Endpoint(value = "RoleAssignment",version = 2)
@JsonPropertyOrder({"id","label","description","ownerAccount","subject","account","accountGroupId","realm","roleId",
        "scope","status", "created","createdBy","modified","modifiedBy"})
public class RoleAssignment extends BaseRoleAssignment
{
    private Status status;

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        com.rubicon.platform.authorization.model.data.acm.RoleAssignment
                that = (com.rubicon.platform.authorization.model.data.acm.RoleAssignment) o;

        if (getId() != null
            ? !getId().equals(that.getId())
            : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return getId() != null
               ? getId().hashCode()
               : 0;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("RoleAssignment");
        sb.append("{ownerAccount='").append(ownerAccount).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", account='").append(account).append('\'');
        sb.append(", accountGroupId='").append(accountGroupId).append('\'');
        sb.append(", roleId=").append(roleId);
        sb.append(", scope=").append(scope);
        sb.append(", realm=").append(realm);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
