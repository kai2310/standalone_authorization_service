package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.Util;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * User: mhellkamp
 * Date: 11/30/12
 */
public class AssignedAccountOperationTest
{
	private AccountProperties roleProperties;
	private AssignedAccountOperation operation;

	@Before
	public void setup()
	{
		roleProperties = new AccountProperties();
		operation = new AssignedAccountOperation();
		operation.setRoleAssignedProperties(roleProperties);
	}

	@Test
	public void testResolveAllowedProperties_NoneSet()
	{
		// if allowed is not set on role or account properties then nothing gets returned
		Assert.assertNull(operation.resolveAllowedProperties());
	}

	@Test
	public void testResolveAllowedProperties_RoleAllowedSet()
	{
		// if only role properties are set then only return them

		roleProperties.addAllowedProperties(Util.asList("1", "2"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(2, properties.size());
		Assert.assertTrue(properties.contains("1"));
		Assert.assertTrue(properties.contains("2"));

	}

	@Test
	public void testResolveAllowedProperties_AccountAllowedSet()
	{
		// if only account properties are set then only return them

		operation.addAllowedProperties(Util.asList("1","2"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(2,properties.size());
		Assert.assertTrue(properties.contains("1"));
		Assert.assertTrue(properties.contains("2"));

	}

	@Test
	public void testResolveAllowedProperties_BothAllowedSet()
	{
		// if role and account properties are set then return only role properties that also
		// exist in the account properties

		roleProperties.addAllowedProperties(Util.asList("1","2","3"));
		operation.addAllowedProperties(Util.asList("1","3"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(2,properties.size());
		Assert.assertTrue(properties.contains("1"));
		Assert.assertTrue(properties.contains("3"));
	}

	@Test
	public void testResolveAllowedProperties_RoleAllowedAndDeniedSet()
	{
		// if role allowed and denied are set then return allowed - denied

		roleProperties.addAllowedProperties(Util.asList("1","2"));
		roleProperties.addDeniedProperties(Util.asList("2", "3"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(1,properties.size());
		Assert.assertTrue(properties.contains("1"));

	}

	@Test
	public void testResolveAllowedProperties_RoleAllowedAndAccountDeniedSet()
	{
		// if role allowed and account denied are set then return allowed - denied

		roleProperties.addAllowedProperties(Util.asList("1","2"));
		operation.addDeniedProperties(Util.asList("2","3"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(1,properties.size());
		Assert.assertTrue(properties.contains("1"));

	}

	@Test
	public void testResolveAllowedProperties_RoleDeniedAndAccountAllowedSet()
	{
		// if role allowed and account denied are set then return allowed - denied

		operation.addAllowedProperties(Util.asList("1","2"));
		roleProperties.addDeniedProperties(Util.asList("2","3"));
		Set<String> properties = operation.resolveAllowedProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(1,properties.size());
		Assert.assertTrue(properties.contains("1"));

	}

	@Test
	public void testResolveDeniedProperties_NoneSet()
	{
		// if denied is not set on role or account properties then nothing gets returned
		Assert.assertNull(operation.resolveDeniedProperties());
	}

	@Test
	public void testResolveDeniedProperties_AllowedSet()
	{
		// if allowed is set then any denied properties will have been removed from the allowed ones, don't
		// return anything

		roleProperties.addAllowedProperties(Util.asList("1","2"));
		operation.addAllowedProperties(Util.asList("1","2"));
		roleProperties.addDeniedProperties(Util.asList("2", "3"));
		operation.addDeniedProperties(Util.asList("2","3"));

		Assert.assertNull(operation.resolveDeniedProperties());
	}

	@Test
	public void testResolveDeniedProperties_RoleDeniedSet()
	{
		// if denied is only set on the role then just return that
		roleProperties.addDeniedProperties(Util.asList("2","3"));

		Set<String> properties = operation.resolveDeniedProperties();
		Assert.assertNotNull(properties);
		Assert.assertEquals(2, properties.size());
		Assert.assertTrue(properties.contains("2"));
		Assert.assertTrue(properties.contains("3"));
	}

	@Test
	public void testResolveDeniedProperties_AccountDeniedSet()
	{
		// if denied is only set on the account then just return that
		operation.addDeniedProperties(Util.asList("2","3"));

		Set<String> properties = operation.resolveDeniedProperties();
		Assert.assertNotNull(properties);
		Assert.assertEquals(2,properties.size());
		Assert.assertTrue(properties.contains("2"));
		Assert.assertTrue(properties.contains("3"));
	}

	@Test
	public void testResolveDeniedProperties_RoleAndAccountDeniedSet()
	{
		// if denied is only set on the account then just return that
		roleProperties.addDeniedProperties(Util.asList("2","3"));
		operation.addDeniedProperties(Util.asList("4","5"));

		Set<String> properties = operation.resolveDeniedProperties();
		Assert.assertNotNull(properties);
		Assert.assertEquals(4, properties.size());
		Assert.assertTrue(properties.contains("2"));
		Assert.assertTrue(properties.contains("3"));
		Assert.assertTrue(properties.contains("4"));
		Assert.assertTrue(properties.contains("5"));
	}
}
