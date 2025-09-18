package com.rubicon.platform.authorization.data.validation;


import com.dottydingo.hyperion.api.exception.NotFoundException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.data.persistence.RoleTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;

import static com.rubicon.platform.authorization.Util.*;
import static com.rubicon.platform.authorization.Util.createPersistentOperation;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public class RoleValidatorTest extends BaseValidatorFixture
{
	private RoleValidator validator;
	private AccountLoader accountLoader;
    private RoleTypeLoader roleTypeLoader;
	private RoleLoader roleLoader;

	@Before
	public void setup()
	{
		validator = new RoleValidator();
		accountLoader = Mockito.mock(AccountLoader.class);
		validator.setAccountLoader(accountLoader);
        roleTypeLoader = Mockito.mock(RoleTypeLoader.class);
        validator.setRoleTypeLoader(roleTypeLoader);
		roleLoader = Mockito.mock(RoleLoader.class);
		validator.setRoleLoader(roleLoader);

        Mockito.when(roleTypeLoader.exists(1L)).thenReturn(true);
        Mockito.when(roleTypeLoader.exists(2L)).thenReturn(false);

		Mockito.when(roleLoader.isLabelUnique(Mockito.anyString())).thenReturn(true);
		Mockito.when(roleLoader.isLabelUnique(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
	}

	@Test
	public void testCreate_RequiredFields()
	{
		Role role = new Role();
		assertFailsCreate(ValidationException.class,"A value must be specified for label",validator,role);

		role.setLabel("label");
		assertFailsCreate(ValidationException.class,"A value must be specified for ownerAccount",validator,role);

		role.setOwnerAccount("*/*");
        assertFailsCreate(ValidationException.class,"A value must be specified for roleTypeId",validator,role);


        role.setRoleTypeId(1L);
		validator.validateCreate(role,null);

		Mockito.verifyZeroInteractions(accountLoader);
	}

	@Test
	public void testCreate_OwnerAccount()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/1234");
        role.setRoleTypeId(1L);
		assertFailsCreate(ValidationException.class,"accountType only be \"*\" if accountId is also \"*\".",validator,role);
		role.setOwnerAccount("foo/123");


		Mockito.when(accountLoader.findByAccountId(new CompoundId("foo", "123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsCreate(NotFoundException.class, "No account found for id=\"foo/123\"", validator, role);

		validator.validateCreate(role,null);
	}

    @Test
    public void testCreate_RoleTypeId()
    {
        Role role = new Role();
        role.setLabel("label");
        role.setOwnerAccount("*/1234");
        role.setRoleTypeId(2L);
        role.setOwnerAccount("foo/123");
        Mockito.when(accountLoader.findByAccountId(new CompoundId("foo", "123"))).thenReturn(new PersistentAccount());

        assertFailsCreate(ValidationException.class, "RoleType id 2 does not exist.", validator, role);

        role.setRoleTypeId(1L);
        validator.validateCreate(role,null);
    }

	@Test
	public void testCreate_Operations()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/*");
        role.setRoleTypeId(1L);

		role.setAllowedOperations(
				asList(createOperation(null,null,null))
		);

		assertFailsCreate(ValidationException.class, "A value must be specified for allowedOperations.service", validator, role);

		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service2","resource","action")
				)
		);

        Mockito.when(roleTypeLoader.exists(1L)).thenReturn(true);
		validator.validateCreate(role,null);
	}

	@Test
	public void testCreate_Operations_DuplicateProperties()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/*");
        role.setRoleTypeId(1L);

		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action",asList("foo","bar","foo"))
				)
		);

		assertFailsCreate(ValidationException.class, "The field \"allowedOperations.properties\" contains duplicate values.", validator, role);
		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action",asList("foo","bar","baz"))
				)
		);

		validator.validateCreate(role,null);
	}

	@Test
	public void testCreate_Operations_UnsupportedWildcards()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/*");
        role.setRoleTypeId(1L);

		role.setAllowedOperations(
				asList(
						createOperation("service","*","action"),
						createOperation("service","foo","*")
				)
		);

		assertFailsCreate(ValidationException.class, "allowedOperations.action must be a wildcard if allowedOperations.resource is a wildcard.", validator, role);
		role.setAllowedOperations(
				asList(
						createOperation("service","*","*"),
						createOperation("service","foo","*")
				)
		);

		validator.validateCreate(role,null);
	}



	@Test
	public void testCreate_Operations_Duplicate()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/*");
        role.setRoleTypeId(1L);


		role.setAllowedOperations(
                asList(
                        createOperation("service", "resource", "action"),
                        createOperation("service", "resource", "action", Collections.singletonList("prop")),
                        createOperation("service", "resource", "action", Collections.singletonList("foo")),
                        createOperation("service", "resource", "action2"),
                        createOperation("service", "resource", "action2")
                )
        );
		assertFailsCreate(ValidationException.class, "Duplicate operation detected in allowedOperations: Operation{service='service', resource='resource', action='action2', properties=null}", validator, role);

		role.setAllowedOperations(null);
		role.setDeniedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action", Collections.singletonList("foo")),
						createOperation("service", "resource", "action2"),
						createOperation("service", "resource", "action2")
				)
		);
		assertFailsCreate(ValidationException.class, "Duplicate operation detected in deniedOperations: Operation{service='service', resource='resource', action='action2', properties=null}", validator, role);

		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action2")
				)
		);

		role.setDeniedOperations(
				asList(
						createOperation("service","resource","action", Collections.singletonList("foo")),
						createOperation("service","resource","action2")
				)
		);

		assertFailsCreate(ValidationException.class, "Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}", validator, role);

	}

	@Test
	public void testUpdate_RequiredFields()
	{
		Role role = new Role();
        PersistentRole persistentRole = new PersistentRole();
		validator.validateUpdate(role,persistentRole,null);

		role.setLabel("");
		assertFailsUpdate(ValidationException.class,"A value must be specified for label",validator,role);

		role.setLabel(null);
		validator.validateUpdate(role,persistentRole,null);

		Mockito.verifyZeroInteractions(accountLoader);
	}

    @Test
    public void testUpdate_RoleTypeId()
    {
        Role role = new Role();
        role.setRoleTypeId(2L);
        PersistentRole persistentRole = new PersistentRole();
        persistentRole.setRoleTypeId(5L);

        assertFailsUpdate(ValidationException.class,"RoleType id 2 does not exist.",validator,role,persistentRole);

        role.setRoleTypeId(1L);
        validator.validateUpdate(role,persistentRole,null);

        Mockito.verifyZeroInteractions(accountLoader);
    }

	@Test
	public void testUpdate_OwnerAccount()
	{
		Role role = new Role();
		role.setLabel("label");
		role.setOwnerAccount("*/1234");

        PersistentRole persistentRole = new PersistentRole();

		assertFailsCreate(ValidationException.class,"accountType only be \"*\" if accountId is also \"*\".",validator,role);
		role.setOwnerAccount("foo/123");

		Mockito.when(accountLoader.findByAccountId(new CompoundId("foo","123"))).thenReturn(null).thenReturn(new PersistentAccount());
		assertFailsUpdate(NotFoundException.class, "No account found for id=\"foo/123\"", validator, role,persistentRole);

		validator.validateUpdate(role, persistentRole, null);
	}

	@Test
	public void testUpdate_Operations()
	{
		Role role = new Role();
		PersistentRole persistentRole = new PersistentRole();

		role.setAllowedOperations(
				asList(createOperation(null,null,null))
		);

		assertFailsUpdate(ValidationException.class, "A value must be specified for allowedOperations.service", validator, role);

		role.setAllowedOperations(
				asList(createOperation("service","resource","action"))
		);


		validator.validateUpdate(role, persistentRole,null);
	}

	@Test
	public void testUpdate_Operations_UnsupportedWildcards()
	{
		Role role = new Role();
		PersistentRole persistentRole = new PersistentRole();
		role.setLabel("label");
		role.setOwnerAccount("*/*");

		role.setAllowedOperations(
				asList(
						createOperation("service","*","action"),
						createOperation("service","foo","*")
				)
		);

		assertFailsUpdate(ValidationException.class, "allowedOperations.action must be a wildcard if allowedOperations.resource is a wildcard.",
				validator, role,persistentRole);
		role.setAllowedOperations(
				asList(
						createOperation("service","*","*"),
						createOperation("service","foo","*")
				)
		);

		validator.validateUpdate(role,persistentRole,null);
	}


	@Test
	public void testUpdate_Operations_DuplicateProperties()
	{
		Role role = new Role();
		PersistentRole persistentRole = new PersistentRole();
		role.setLabel("label");
		role.setOwnerAccount("*/*");

		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action",asList("foo","bar","foo"))
				)
		);

		assertFailsUpdate(ValidationException.class, "The field \"allowedOperations.properties\" contains duplicate values.",
				validator, role,persistentRole);
		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action",asList("foo","bar","baz"))
				)
		);

		validator.validateUpdate(role,persistentRole,null);
	}

	@Test
	public void testUpdate_Operations_Duplicate()
	{
		Role role = new Role();
		PersistentRole persistentRole = new PersistentRole();


		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action", Collections.singletonList("foo")),
						createOperation("service","resource","action2"),
						createOperation("service","resource","action2")
				)
		);
		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in allowedOperations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				role,
				persistentRole);

		role.setAllowedOperations(null);
		role.setDeniedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action", Collections.singletonList("foo")),
						createOperation("service", "resource", "action2"),
						createOperation("service", "resource", "action2")
				)
		);
		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in deniedOperations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				role,
				persistentRole);

		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action2")
				)
		);

		role.setDeniedOperations(
				asList(
						createOperation("service","resource","action", Collections.singletonList("foo")),
						createOperation("service","resource","action2")
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				role,
				persistentRole);

	}

	@Test
	public void testUpdate_Operations_DuplicateAfterUpdate()
	{
		Role role = new Role();
		PersistentRole persistentRole = new PersistentRole();



		role.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action2")
				)
		);

		persistentRole.setOperations(
				asList(
						createPersistentOperation("service",
								"resource",
								"action",
								true,
								Collections.singletonList("foo")),
						createPersistentOperation("service", "resource", "action2",true)
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				role,
				persistentRole);

		role.setAllowedOperations(null);
		role.setDeniedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action2")
				)
		);

		persistentRole.setOperations(
				asList(
						createPersistentOperation("service",
								"resource",
								"action",
								false,
								Collections.singletonList("foo")),
						createPersistentOperation("service", "resource", "action2",false)
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				role,
				persistentRole);

	}


}
