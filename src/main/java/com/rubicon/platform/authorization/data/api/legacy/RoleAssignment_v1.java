package com.rubicon.platform.authorization.data.api.legacy;

import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rubicon.platform.authorization.model.data.acm.BaseRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;

@Endpoint(value = "RoleAssignment",version = 1)
@JsonPropertyOrder({"id","label","description","ownerAccount","subject","account","accountGroupId","realm","roleId",
		"scope",
		"created","createdBy","modified","modifiedBy"})
public class RoleAssignment_v1 extends BaseRoleAssignment
{

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RoleAssignment that = (RoleAssignment) o;

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
		sb.append('}');
		return sb.toString();
	}
}
