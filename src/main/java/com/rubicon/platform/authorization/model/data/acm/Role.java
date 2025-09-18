package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Endpoint(value="Role",version = 2)
@JsonPropertyOrder({"id","roleTypeId","label","description","realm","ownerAccount","allowedOperations","deniedOperations",
        "status","created","createdBy","modified","modifiedBy"})
public class Role extends BaseRole
{
    private Long roleTypeId;

    public Long getRoleTypeId()
    {
        return roleTypeId;
    }

    public void setRoleTypeId(Long roleTypeId)
    {
        this.roleTypeId = roleTypeId;
    }

    @Override
    public String toString()
    {
        return "Role" +
               "\n" + "{" +
               "\n" + "id=" + getId() +
               "\n" + "roleTypeId=" + roleTypeId +
               "\n" + "ownerAccount='" + getOwnerAccount() + '\'' + "," +
               "\n" + "status=" + getStatus() + "," +
               "\n" + "label='" + getLabel() + '\'' + "," +
               "\n" + "realm='" + getRealm() + '\'' + "," +
               "\n" + "description='" + getDescription() + '\'' + "," +
               "\n" + "allowedOperations=" + getAllowedOperations() + "," +
               "\n" + "deniedOperations=" + getDeniedOperations() + "," +
               "\n" + "created=" + getCreated() + "," +
               "\n" + "createdBy=" + getCreatedBy() + "," +
               "\n" + "}";
    }
}
