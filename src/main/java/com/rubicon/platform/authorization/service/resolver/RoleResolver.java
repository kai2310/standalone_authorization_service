package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;

import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/17/12
 */
public interface RoleResolver
{
	ResolvedSubjectAccounts resolveOperation(List<CompoundId> subjectIds, CompoundId accountId, String service,
									   String endpoint, String action);
}
