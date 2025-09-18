package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.CompoundId;

/**
 * User: mhellkamp
 * Date: 12/5/12
 */
public interface RoleAssignmentUniqueCheck
{
	boolean exists(CompoundId subjectId, CompoundId account, Long roleId, Long accountGroupId);
}
