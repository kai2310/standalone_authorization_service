package com.rubicon.platform.authorization;

import com.rubicon.platform.authorization.data.model.PersistentOperation;
import com.rubicon.platform.authorization.data.util.JsonUtil;
import com.rubicon.platform.authorization.model.data.acm.Operation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public class Util
{
	public static <T> Set<T> asSet(T... items)
	{
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(items));

		return set;
	}

	public static <T> List<T> asList(T... items)
	{
		return Arrays.asList(items);
	}


	public static Operation createOperation(String service, String resource, String action)
	{
		return createOperation(service, resource, action,null);
	}

	public static Operation createOperation(String service,String resource,String action,List<String> properties)
	{
		Operation operation = new Operation();
		operation.setService(service);
		operation.setResource(resource);
		operation.setAction(action);
		operation.setProperties(properties);
		return operation;
	}

	public static PersistentOperation createPersistentOperation(String service,String resource,String action,boolean deny)
	{
		return createPersistentOperation(service, resource, action,deny,null);
	}

	public static PersistentOperation createPersistentOperation(String service,String resource,String action,boolean deny,
																List<String> properties)
	{
		PersistentOperation operation = new PersistentOperation();
		operation.setAction(action);
		operation.setDeny(deny);
		operation.setProperties(JsonUtil.toString(properties));
		operation.setResource(resource);
		operation.setService(service);
		return operation;
	}
}
