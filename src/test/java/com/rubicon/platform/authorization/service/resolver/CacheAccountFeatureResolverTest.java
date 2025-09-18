package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.service.cache.ServiceOperationsHolder;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.rubicon.platform.authorization.Util.asList;
import static com.rubicon.platform.authorization.Util.*;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class CacheAccountFeatureResolverTest
{
	private CacheAccountFeatureResolver resolver = new CacheAccountFeatureResolver();

	@Test
	public void testCheckAccountFeature_NoAccess() throws Exception
	{

		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("service", "resource", "otheraction"),
						createOperation("otherservice", "otherresource", "action")
				),
				Collections.EMPTY_LIST)
		);
	}

	@Test
	public void testCheckAccountFeature_Allowed_ExplicitAction() throws Exception
	{

		assertAuthorized(buildAccountFeature(
				asList(
						createOperation("service","resource","action"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST)
		);
	}

	@Test
	public void testCheckAccountFeature_Allowed_Wildcards() throws Exception
	{
		assertAuthorized(buildAccountFeature(
				asList(
						createOperation("service","resource","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST)
		);

		assertAuthorized(buildAccountFeature(
				asList(
						createOperation("service","*","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST)
		);

		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("myservice","*","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST)
		);

	}

	@Test
	public void testCheckAccountFeature_Denied_ExplicitAction() throws Exception
	{
		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("otherservice","otherresource","action")
				),
				asList(
						createOperation("service","resource","action")
				))
		);
	}

	@Test
	public void testCheckAccountFeature_Denied_Wildcard() throws Exception
	{
		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("otherservice","otherresource","action")
				),
				asList(
						createOperation("service","resource","*")
				))
		);

		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("otherservice","otherresource","action")
				),
				asList(
						createOperation("service","*","*")
				))
		);


	}

	@Test
	public void testCheckAccountFeature_Precedence() throws Exception
	{
		assertAuthorized(buildAccountFeature(
				asList(
						createOperation("service","resource","action")
				),
				asList(
						createOperation("service","resource","*")
				))
		);

		assertAuthorized(buildAccountFeature(
				asList(
						createOperation("service","resource","action")
				),
				asList(
						createOperation("service","*","*")
				))
		);

		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("service","resource","*")
				),
				asList(
						createOperation("service","resource","action")
				))
		);

		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("service","*","*")
				),
				asList(
						createOperation("service","resource","*")
				))
		);

		// if precedence is the same then deny wins
		assertNotAuthorized(buildAccountFeature(
				asList(
						createOperation("service","*","*")
				),
				asList(
						createOperation("service","*","*")
				))
		);

	}

	private void assertAuthorized(AccountFeature accountFeature)
	{
		AssignedAccountOperation match = new AssignedAccountOperation();
		resolver.checkAccountFeature(match,"service","resource","action",
                new ServiceOperationsHolder<AccountFeature>(accountFeature));
		Assert.assertTrue(match.isAuthorized());
	}

	private void assertNotAuthorized(AccountFeature accountFeature)
	{
		AssignedAccountOperation match = new AssignedAccountOperation();
		resolver.checkAccountFeature(match,"service","resource","action",
                new ServiceOperationsHolder<AccountFeature>(accountFeature));
		Assert.assertFalse(match.isAuthorized());
	}

	private AccountFeature buildAccountFeature(List<Operation> allowed, List<Operation> denied)
	{
		AccountFeature accountFeature = new AccountFeature();
		accountFeature.setAllowedOperations(allowed);
		accountFeature.setDeniedOperations(denied);
		return accountFeature;
	}
}
