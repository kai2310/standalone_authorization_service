package com.rubicon.platform.authorization.service.resolver;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class OperationMatchTest
{
	@Test
	public void testHasPrecedence() throws Exception
	{
		Assert.assertFalse(OperationMatch.none.hasPrecedence(OperationMatch.none));
		Assert.assertFalse(OperationMatch.none.hasPrecedence(OperationMatch.service));
		Assert.assertFalse(OperationMatch.none.hasPrecedence(OperationMatch.resource));
		Assert.assertFalse(OperationMatch.none.hasPrecedence(OperationMatch.action));

		Assert.assertTrue(OperationMatch.service.hasPrecedence(OperationMatch.none));
		Assert.assertFalse(OperationMatch.service.hasPrecedence(OperationMatch.service));
		Assert.assertFalse(OperationMatch.service.hasPrecedence(OperationMatch.resource));
		Assert.assertFalse(OperationMatch.service.hasPrecedence(OperationMatch.action));

		Assert.assertTrue(OperationMatch.resource.hasPrecedence(OperationMatch.none));
		Assert.assertTrue(OperationMatch.resource.hasPrecedence(OperationMatch.service));
		Assert.assertFalse(OperationMatch.resource.hasPrecedence(OperationMatch.resource));
		Assert.assertFalse(OperationMatch.resource.hasPrecedence(OperationMatch.action));

		Assert.assertTrue(OperationMatch.action.hasPrecedence(OperationMatch.none));
		Assert.assertTrue(OperationMatch.action.hasPrecedence(OperationMatch.service));
		Assert.assertTrue(OperationMatch.action.hasPrecedence(OperationMatch.resource));
		Assert.assertFalse(OperationMatch.action.hasPrecedence(OperationMatch.action));
	}
}
