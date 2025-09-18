package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsHolder;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import com.rubicon.platform.authorization.model.data.acm.Role;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.rubicon.platform.authorization.Util.*;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class CacheRoleResolverTest
{
	private CacheRoleResolver cacheRoleResolver = new CacheRoleResolver();
	private CompoundId account = new CompoundId("publisher","6005");


	@Test
	public void testCheckRole_NoAccess() throws Exception
	{
		assertNotAuthorized(buildRole(
				asList(
						createOperation("service","resource","otheraction"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST
		));
	}

	@Test
	public void testCheckRole_Allowed_ExplicitAction() throws Exception
	{
		assertAuthorized(buildRole(
				asList(
						createOperation("service","resource","action"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST
		));
	}

	@Test
	public void testCheckRole_Allowed_Wildcards() throws Exception
	{
		assertAuthorized(buildRole(
				asList(
						createOperation("service","resource","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST
		));

		assertAuthorized(buildRole(
				asList(
						createOperation("service","*","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST
		));

		assertNotAuthorized(buildRole(
				asList(
						createOperation("myservice","*","*"),
						createOperation("otherservice","otherresource","action")
				),
				Collections.EMPTY_LIST
		));

	}

	@Test
	public void testCheckRole_Denied_ExplicitAction() throws Exception
	{
		assertNotAuthorized(buildRole(
				asList(
						createOperation("otherservice","otherresource","action")
				),
				asList(
						createOperation("service","resource","action")
				)
		));
	}

	@Test
	public void testCheckRole_Denied_Wildcard() throws Exception
	{
		assertNotAuthorized(buildRole(
				asList(
						createOperation("otherservice","otherresource","action")
				),
				asList(
						createOperation("service","*","*")
				)
		));

	}

	@Test
	public void testCheckRole_Precedence() throws Exception
	{
		assertAuthorized(buildRole(
				asList(
						createOperation("service","resource","action")
				),
				asList(
						createOperation("service","resource","*")
				)
		));

		assertAuthorized(buildRole(
				asList(
						createOperation("service","resource","action")
				),
				asList(
						createOperation("service","*","*")
				)
		));

		assertNotAuthorized(buildRole(
				asList(
						createOperation("service","resource","*")
				),
				asList(
						createOperation("service","resource","action")
				)
		));

		assertNotAuthorized(buildRole(
				asList(
						createOperation("service","*","*")
				),
				asList(
						createOperation("service","resource","*")
				)
		));

		// if precedence is the same then deny wins
		assertNotAuthorized(buildRole(
				asList(
						createOperation("service","*","*")
				),
				asList(
						createOperation("service","*","*")
				)
		));

	}

	private void assertAuthorized(Role role)
	{
		OperationMatchResult result = cacheRoleResolver.checkRole("service","resource","action",
				new ServiceOperationsHolder<>(role),new RoleAssignment());
		Assert.assertTrue(result.isAuthorized());
	}

	private void assertNotAuthorized(Role role)
	{
		OperationMatchResult result = cacheRoleResolver.checkRole("service","resource","action",
				new ServiceOperationsHolder<>(role),new RoleAssignment());
		Assert.assertFalse(result.isAuthorized());
	}

	private Role buildRole(List<Operation> allowed, List<Operation> denied)
	{
		Role role = new Role();
		role.setAllowedOperations(allowed);
		role.setDeniedOperations(denied);
		return role;
	}
}
