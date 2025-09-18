package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.PersistentOperation;
import com.rubicon.platform.authorization.data.util.JsonUtil;
import com.rubicon.platform.authorization.model.data.acm.Operation;

/**
 * User: mhellkamp
 * Date: 11/28/12
 */
public class BaseOperationFieldMapper
{
	protected Operation createOperation(PersistentOperation operation)
	{
		Operation roleOperation = new Operation();
		roleOperation.setService(operation.getService());
		roleOperation.setResource(operation.getResource());
		roleOperation.setAction(operation.getAction());
		roleOperation.setProperties(JsonUtil.toList(operation.getProperties()));
		return roleOperation;
	}

	protected PersistentOperation createPersistentOperation(Operation operation, boolean deny)
	{
		PersistentOperation persistentOperation = new PersistentOperation();
		persistentOperation.setService(operation.getService());
		persistentOperation.setResource(operation.getResource());
		persistentOperation.setAction(operation.getAction());
		persistentOperation.setProperties(JsonUtil.toString(operation.getProperties()));
		persistentOperation.setDeny(deny);
		return persistentOperation;
	}
}
