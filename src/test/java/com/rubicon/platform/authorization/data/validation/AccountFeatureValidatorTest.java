package com.rubicon.platform.authorization.data.validation;


import com.dottydingo.hyperion.api.exception.ValidationException;
import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import com.rubicon.platform.authorization.data.persistence.AccountFeatureLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static com.rubicon.platform.authorization.Util.asList;
import static com.rubicon.platform.authorization.Util.createOperation;
import static com.rubicon.platform.authorization.Util.createPersistentOperation;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public class AccountFeatureValidatorTest extends BaseValidatorFixture
{
	private AccountFeatureValidator validator = new AccountFeatureValidator();
	private AccountFeatureLoader accountFeatureLoader;

	@Before
	public void setup()
	{
		accountFeatureLoader = Mockito.mock(AccountFeatureLoader.class);
		validator.setAccountFeatureLoader(accountFeatureLoader);

		Mockito.when(accountFeatureLoader.isLabelUnique(Mockito.anyString())).thenReturn(true);
		Mockito.when(accountFeatureLoader.isLabelUnique(Mockito.anyString(),Mockito.anyLong())).thenReturn(true);
	}

	@Test
	public void testCreate()
	{

		AccountFeature accountFeature = new AccountFeature();
		assertFailsCreate(ValidationException.class,"A value must be specified for label",validator, accountFeature);
		accountFeature.setLabel("");
		assertFailsCreate(ValidationException.class, "A value must be specified for label", validator,
				accountFeature);
		accountFeature.setLabel("foo");
		validator.validateCreate(accountFeature,null);
	}

	@Test
	public void testCreate_Operations_Duplicate()
	{
		AccountFeature accountFeature = new AccountFeature();
		accountFeature.setLabel("label");


		accountFeature.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action", Collections.singletonList("foo")),
						createOperation("service","resource","action2"),
						createOperation("service","resource","action2")
				)
		);
		assertFailsCreate(ValidationException.class,
				"Duplicate operation detected in allowedOperations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature);

		accountFeature.setAllowedOperations(null);
		accountFeature.setDeniedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action", Collections.singletonList("foo")),
						createOperation("service", "resource", "action2"),
						createOperation("service", "resource", "action2")
				)
		);
		assertFailsCreate(ValidationException.class,
				"Duplicate operation detected in deniedOperations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature);

		accountFeature.setAllowedOperations(
				asList(
						createOperation("service","resource","action"),
						createOperation("service","resource","action", Collections.singletonList("prop")),
						createOperation("service","resource","action2")
				)
		);

		accountFeature.setDeniedOperations(
				asList(
						createOperation("service","resource","action", Collections.singletonList("foo")),
						createOperation("service","resource","action2")
				)
		);

		assertFailsCreate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature);

	}

	@Test
	public void testUpdate()
	{
        PersistentAccountFeature persistentAccountFeature = new PersistentAccountFeature();
		AccountFeature accountFeature = new AccountFeature();
		accountFeature.setLabel("");
		assertFailsUpdate(ValidationException.class,"A value must be specified for label",validator, accountFeature);

		accountFeature.setLabel(null);
		validator.validateUpdate(accountFeature, persistentAccountFeature,null);

		accountFeature.setLabel("foo");
		validator.validateUpdate(accountFeature, persistentAccountFeature,null);
	}

	@Test
	public void testUpdate_Operations_Duplicate()
	{
		AccountFeature accountFeature = new AccountFeature();
		PersistentAccountFeature persistentAccountFeature = new PersistentAccountFeature();


		accountFeature.setAllowedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action", Collections.singletonList("foo")),
						createOperation("service", "resource", "action2"),
						createOperation("service", "resource", "action2")
				)
		);
		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in allowedOperations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature,
				persistentAccountFeature);

		accountFeature.setAllowedOperations(null);
		accountFeature.setDeniedOperations(
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
				accountFeature,
				persistentAccountFeature);

		accountFeature.setAllowedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action2")
				)
		);

		accountFeature.setDeniedOperations(
				asList(
						createOperation("service", "resource", "action", Collections.singletonList("foo")),
						createOperation("service", "resource", "action2")
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature,
				persistentAccountFeature);

	}

	@Test
	public void testUpdate_Operations_DuplicateAfterUpdate()
	{
		AccountFeature accountFeature = new AccountFeature();
		PersistentAccountFeature persistentAccountFeature = new PersistentAccountFeature();



		accountFeature.setAllowedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action2")
				)
		);

		persistentAccountFeature.setOperations(
				asList(
						createPersistentOperation("service",
								"resource",
								"action",
								true,
								Collections.singletonList("foo")),
						createPersistentOperation("service", "resource", "action2", true)
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature,
				persistentAccountFeature);

		accountFeature.setAllowedOperations(null);
		accountFeature.setDeniedOperations(
				asList(
						createOperation("service", "resource", "action"),
						createOperation("service", "resource", "action", Collections.singletonList("prop")),
						createOperation("service", "resource", "action2")
				)
		);

		persistentAccountFeature.setOperations(
				asList(
						createPersistentOperation("service",
								"resource",
								"action",
								false,
								Collections.singletonList("foo")),
						createPersistentOperation("service", "resource", "action2", false)
				)
		);

		assertFailsUpdate(ValidationException.class,
				"Duplicate operation detected in both allowed and denied operations: Operation{service='service', resource='resource', action='action2', properties=null}",
				validator,
				accountFeature,
				persistentAccountFeature);

	}

}
