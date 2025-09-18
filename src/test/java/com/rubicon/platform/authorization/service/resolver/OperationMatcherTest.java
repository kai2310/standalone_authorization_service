package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.service.cache.ServiceOperation;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class OperationMatcherTest
{
	private final ServiceOperation action = new ServiceOperation(new Operation("service","resource","action"));
	private final ServiceOperation service = new ServiceOperation(new Operation("service","*","*"));
	private final ServiceOperation resource = new ServiceOperation(new Operation("service","resource","*"));

	private OperationMatcher matcher = new OperationMatcher();

	@Test
	public void testMatchOperation_Action()
	{
		Assert.assertEquals(OperationMatch.none,
				matcher.matchOperation(action, "bar","null"));

		Assert.assertEquals(OperationMatch.none,
				matcher.matchOperation(action, "resource","null"));

		Assert.assertEquals(OperationMatch.action,
				matcher.matchOperation(action, "resource","action"));
	}

	@Test
	public void testMatchOperation_Resource()
	{
		Assert.assertEquals(OperationMatch.none,
				matcher.matchOperation(resource, "bar","null"));

		Assert.assertEquals(OperationMatch.resource,
				matcher.matchOperation(resource, "resource","null"));

		Assert.assertEquals(OperationMatch.resource,
				matcher.matchOperation(resource, "resource","action"));
	}

	@Test
	public void testMatchOperation_Service()
	{

		Assert.assertEquals(OperationMatch.service,
				matcher.matchOperation(service, "bar","null"));

		Assert.assertEquals(OperationMatch.service,
				matcher.matchOperation(service, "resource","null"));

		Assert.assertEquals(OperationMatch.service,
				matcher.matchOperation(service, "Resource","action"));
	}
}
