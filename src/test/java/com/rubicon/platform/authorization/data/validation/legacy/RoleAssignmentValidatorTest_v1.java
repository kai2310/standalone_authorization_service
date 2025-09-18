package com.rubicon.platform.authorization.data.validation.legacy;

import com.dottydingo.hyperion.api.exception.NotFoundException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.api.legacy.RoleAssignment_v1;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentUniqueCheck;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.data.validation.BaseValidatorFixture;
import junit.framework.Assert;
import org.apache.commons.collections.map.SingletonMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public class RoleAssignmentValidatorTest_v1 extends BaseValidatorFixture
{
	private RoleAssignmentValidator_v1 validator;
	private AccountLoader accountLoader;
	private RoleLoader roleLoader;
	private CompoundId ownerAccount1 = new CompoundId("owner","1");

	@Before
	public void setup()
	{
		validator = new RoleAssignmentValidator_v1();
        accountLoader = Mockito.mock(AccountLoader.class);
		RoleAssignmentUniqueCheck roleAssignmentUniqueCheck = Mockito.mock(RoleAssignmentUniqueCheck.class);
		validator.setAccountLoader(accountLoader);
		validator.setRoleAssignmentUniqueCheck(roleAssignmentUniqueCheck);
		validator.setAccountGroupMap(Collections.singletonMap("publisher",1L));
		roleLoader = Mockito.mock(RoleLoader.class);
		validator.setRoleLoader(roleLoader);
		PersistentRole persistentRole = new PersistentRole();
		Mockito.when(roleLoader.find(Mockito.anyLong())).thenReturn(persistentRole);
	}

	@Test
	public void testCreate_Required()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setOwnerAccount("owner/1");
		roleAssignment.setSubject("subject/1");
		roleAssignment.setAccount("context/1");
		roleAssignment.setRoleId(1L);

		validator.validateCreate(roleAssignment,null);
	}

	@Test
	public void testCreate_DuplicateScope()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setOwnerAccount("owner/1");
		roleAssignment.setSubject("subject/1");
		roleAssignment.setAccount("context/1");
		roleAssignment.setRoleId(1L);
		roleAssignment.setScope(Util.asList("foo","bar","foo"));
		assertFailsCreate(ValidationException.class,"The field \"scope\" contains duplicate values.",validator, roleAssignment);

		roleAssignment.setScope(Util.asList("foo","bar","baz"));
		validator.validateCreate(roleAssignment,null);
	}

	@Test
	public void testCreate_MissingRequired()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		assertFailsCreate(ValidationException.class,"A value must be specified for ownerAccount",validator, roleAssignment);
		roleAssignment.setOwnerAccount("publisher/123");

		assertFailsCreate(ValidationException.class,"A value must be specified for subject",validator, roleAssignment);
		roleAssignment.setSubject("subject/1");
		assertFailsCreate(ValidationException.class,"A value must be specified for accountContext",validator,
				roleAssignment);
		roleAssignment.setAccount("context/1");
		assertFailsCreate(ValidationException.class,"A value must be specified for roleId",validator, roleAssignment);
		roleAssignment.setRoleId(1L);
		validator.validateCreate(roleAssignment,null);
	}

	@Test
	public void testCreate_OwnerAccount()
	{
		Mockito.when(accountLoader.findByAccountId(new CompoundId("context","1"))).thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setSubject("subject/1");
		roleAssignment.setAccount("publisher/*");
		roleAssignment.setRoleId(1L);

		roleAssignment.setOwnerAccount("*/1234");
		assertFailsCreate(ValidationException.class,"accountType only be \"*\" if accountId is also \"*\".",validator,
				roleAssignment);
		roleAssignment.setOwnerAccount("foo/123");

		Mockito.when(accountLoader.findByAccountId(new CompoundId("foo","123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsCreate(NotFoundException.class, "No account found for id=\"foo/123\"", validator, roleAssignment);

		validator.validateCreate(roleAssignment,null);
	}

	@Test
	public void testCreate_AccountContext()
	{
		Mockito.when(accountLoader.findByAccountId(new CompoundId("owner","1"))).thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setOwnerAccount("owner/1");
		roleAssignment.setSubject("subject/1");
		roleAssignment.setRoleId(1L);

		roleAssignment.setAccount("*/1234");
		assertFailsCreate(ValidationException.class,"accountType can not be a wildcard.",validator, roleAssignment);
		roleAssignment.setAccount("foo/*");

		assertFailsCreate(ValidationException.class, "Unsupported account type: \"foo\"", validator, roleAssignment);

		roleAssignment.setAccount("publisher/123");
		Mockito.when(accountLoader.findByAccountId(new CompoundId("publisher","123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsCreate(NotFoundException.class, "No account found for id=\"publisher/123\"", validator, roleAssignment);

		validator.validateCreate(roleAssignment,null);
	}

	@Test
	public void testUpdate_Required()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
		persistentRoleAssignment.setSubject(new CompoundId("subject","1"));
		persistentRoleAssignment.setAccount(new CompoundId("context", "1"));
		persistentRoleAssignment.setRoleId(1L);
		validator.validateUpdate(roleAssignment, persistentRoleAssignment,null);

		roleAssignment.setOwnerAccount("owner/1");
		roleAssignment.setSubject("subject/1");
		roleAssignment.setAccount("context/1");
		roleAssignment.setRoleId(1L);

		validator.validateUpdate(roleAssignment,persistentRoleAssignment,null);
	}

	@Test
	public void testUpdate_DuplicateScope()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setScope(Util.asList("foo","bar","foo"));

		PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
		persistentRoleAssignment.setSubject(new CompoundId("subject","1"));
		persistentRoleAssignment.setAccount(new CompoundId("context", "1"));
		persistentRoleAssignment.setRoleId(1L);

		assertFailsUpdate(ValidationException.class,"The field \"scope\" contains duplicate values.",validator,
				roleAssignment,persistentRoleAssignment);

		roleAssignment.setScope(Util.asList("foo","bar","baz"));
		validator.validateUpdate(roleAssignment,persistentRoleAssignment,null);
	}

	@Test
	public void testUpdate_MissingRequired()
	{
		Mockito.when(accountLoader.findByAccountId(Mockito.any(CompoundId.class)))
				.thenReturn(new PersistentAccount());

        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setSubject("");
		assertFailsUpdate(ValidationException.class,"A value must be specified for subject",validator, roleAssignment,persistentRoleAssignment);
		roleAssignment.setSubject(null);
		roleAssignment.setAccount("");
		assertFailsUpdate(ValidationException.class,"A value must be specified for accountContext",validator,
				roleAssignment,persistentRoleAssignment);
		roleAssignment.setAccount(null);
		validator.validateUpdate(roleAssignment,persistentRoleAssignment,null);
	}

	@Test
	public void testUpdate_OwnerAccount()
	{
		Mockito.when(accountLoader.findByAccountId(new CompoundId("context","1"))).thenReturn(new PersistentAccount());

		PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
		persistentRoleAssignment.setSubject(new CompoundId("subject","1"));
		persistentRoleAssignment.setAccount(new CompoundId("context", "1"));
		persistentRoleAssignment.setRoleId(1L);

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setSubject("subject/1");
		roleAssignment.setAccount("context/1");
		roleAssignment.setRoleId(1L);

		roleAssignment.setOwnerAccount("*/1234");
		assertFailsUpdate(ValidationException.class,
				"accountType only be \"*\" if accountId is also \"*\".",
				validator,
				roleAssignment,
                persistentRoleAssignment);
		roleAssignment.setOwnerAccount("foo/123");

		Mockito.when(accountLoader.findByAccountId(new CompoundId("foo","123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsUpdate(NotFoundException.class, "No account found for id=\"foo/123\"", validator, roleAssignment,persistentRoleAssignment);

		validator.validateUpdate(roleAssignment, persistentRoleAssignment,null);
	}

	@Test
	public void testUpdate_AccountContext()
	{
		Mockito.when(accountLoader.findByAccountId(new CompoundId("owner","1"))).thenReturn(new PersistentAccount());

		PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
		persistentRoleAssignment.setSubject(new CompoundId("subject","1"));
		persistentRoleAssignment.setAccount(new CompoundId("context", "1"));
		persistentRoleAssignment.setRoleId(1L);

		RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
		roleAssignment.setOwnerAccount("owner/1");
		roleAssignment.setSubject("subject/1");
		roleAssignment.setRoleId(1L);

		roleAssignment.setAccount("*/1234");
		assertFailsUpdate(ValidationException.class, "accountType can not be a wildcard.", validator, roleAssignment,persistentRoleAssignment);
		roleAssignment.setAccount("foo/123");

		Mockito.when(accountLoader.findByAccountId(new CompoundId("foo","123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsUpdate(NotFoundException.class, "No account found for id=\"foo/123\"", validator, roleAssignment,persistentRoleAssignment);

		validator.validateUpdate(roleAssignment, persistentRoleAssignment,null);
	}

	@Test
	public void testValidateSubject()
	{
		try
		{
			validator.validateSubject("*/*");
			Assert.fail();
		}
		catch (ValidationException ignore){}

		try
		{
			validator.validateSubject("*/foo");
			Assert.fail();
		}
		catch (ValidationException ignore){}

		validator.validateSubject("foo/*");
		validator.validateSubject("foo/1");
	}
}
