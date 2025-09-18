package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mhellkamp
 * Date: 11/30/12
 */
public class ResolvedOperationAccounts
{
	private static Logger logger = LoggerFactory.getLogger(ResolvedOperationAccounts.class);

	private Map<CompoundId,AssignedAccountOperation> accountAssignedOperationMap
			= new HashMap<CompoundId, AssignedAccountOperation>();


	public AssignedAccountOperation getAccountAssignedOperation(CompoundId account)
	{
		AssignedAccountOperation operation = accountAssignedOperationMap.get(account);
		if(operation == null)
		{
			operation = new AssignedAccountOperation();
			accountAssignedOperationMap.put(account,operation);
		}
		return operation;
	}

	public List<CompoundId> getAuthorizedAccounts()
	{
		logger.debug("Checking for authorized accounts");
		List<CompoundId> accounts = new ArrayList<CompoundId>();
		for (Map.Entry<CompoundId, AssignedAccountOperation> entry : accountAssignedOperationMap.entrySet())
		{
			if(entry.getValue().isAuthorized())
				accounts.add(entry.getKey());
		}

		logger.debug("Found {} authorized accounts.",accounts.size());
		return accounts;
	}

}
