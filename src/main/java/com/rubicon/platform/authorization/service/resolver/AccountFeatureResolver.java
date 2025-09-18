package com.rubicon.platform.authorization.service.resolver;

/**
 * User: mhellkamp
 * Date: 11/29/12
 */
public interface AccountFeatureResolver
{
	ResolvedOperationAccounts resolveOperation(String service, String resource,
											   String action, SubjectAccountMatches assignedRoleOperation);
}
