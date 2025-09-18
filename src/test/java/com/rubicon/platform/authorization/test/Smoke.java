package com.rubicon.platform.authorization.test;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.model.data.acm.*;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import junit.framework.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class Smoke
{
	private static final String baseUrl = "http://localhost:8080/access/api/";
	private static final String baseDataUrl = baseUrl + "data/";
	private static final String baseSvcUrl = baseUrl + "v1/authorization/";
	private SmokeDataClient<Account> accountClient = new SmokeDataClient<Account>(baseDataUrl +"Account",Account.class);
	private SmokeDataClient<AccountFeature> accountFeatureClient = new SmokeDataClient<AccountFeature>(
			baseDataUrl +"AccountFeature",AccountFeature.class);
	private SmokeDataClient<Role> roleClient = new SmokeDataClient<Role>(baseDataUrl +"Role",Role.class);
	private SmokeDataClient<RoleAssignment> roleAssignmentClient = new SmokeDataClient<RoleAssignment>(
			baseDataUrl +"RoleAssignment",RoleAssignment.class);

	private SmokeAuthClient authClient = new SmokeAuthClient(baseSvcUrl);

	private Account createAccount(String id,Set<Long> accountFeatureIds) throws Exception
	{
		Account account = new Account();
		account.setAccountId(id);
		account.setAccountName("Test account");
		account.setStatus("active");
		account.setAccountFeatureIds(accountFeatureIds);
		return accountClient.create(account);
	}

	private AccountFeature createAccountFeature(String label, List<Operation> allowed,List<Operation> denied)
	{
		AccountFeature accountFeature = new AccountFeature();
		accountFeature.setLabel(label);
		accountFeature.setAllowedOperations(allowed);
		accountFeature.setDeniedOperations(denied);
		return accountFeatureClient.create(accountFeature);
	}

	private Role createRole(String label, List<Operation> allowed,List<Operation> denied)
	{
		Role role = new Role();
		role.setOwnerAccount("*/*");
		role.setLabel(label);
		role.setAllowedOperations(allowed);
		role.setDeniedOperations(denied);
		return roleClient.create(role);
	}

	private RoleAssignment createRoleAssignment(String account,String subject,Long roleId)
	{
		RoleAssignment roleAssignment = new RoleAssignment();
		roleAssignment.setOwnerAccount("*/*");
		roleAssignment.setAccount(account);
		roleAssignment.setRoleId(roleId);
		roleAssignment.setSubject(subject);
		return roleAssignmentClient.create(roleAssignment);
	}

	public void run() throws Exception
	{
		try
		{
			AccountFeature omsFeature = createAccountFeature("OMS Feature",
					Util.asList(
							Util.createOperation("OMS","order","get"),
							Util.createOperation("OMS","order","put"),
							Util.createOperation("OMS","order","post"),
							Util.createOperation("OMS","order","delete")
					),
					Collections.EMPTY_LIST
			);

			AccountFeature issFeature = createAccountFeature("ISS Feature",
					Util.asList(
							Util.createOperation("iss", "historical", "execute"),
							Util.createOperation("iss", "coverage", "execute")
					),
					Collections.EMPTY_LIST
			);

			Account account = createAccount("mark/1",Util.asSet(omsFeature.getId(),issFeature.getId()));
			Account account2 = createAccount("mark/2",Util.asSet(omsFeature.getId(),issFeature.getId()));
			Account account3 = createAccount("mark/3",Util.asSet(issFeature.getId()));

			Role demandRole = createRole("Demand Role",
					Util.asList(
							Util.createOperation("OMS", "*", "*"),
							Util.createOperation("iss", "*", "*")
					),
					Collections.EMPTY_LIST
			);

			Role excludeIssRole = createRole("No ISS Role",
					Collections.EMPTY_LIST,
					Util.asList(
							Util.createOperation("iss", "*", "*")
					)
			);

			Role addLimitedIssRole = createRole("Limited ISS Role",
					Util.asList(
							Util.createOperation("iss", "coverage", "*")
					),
					Collections.EMPTY_LIST
			);

			createRoleAssignment("mark/1","muser/123",demandRole.getId());
			createRoleAssignment("mark/2","muser/123",demandRole.getId());
			createRoleAssignment("mark/3","muser/123",demandRole.getId());
			createRoleAssignment("mark/3","muser/123",excludeIssRole.getId());
			createRoleAssignment("mark/3","muser/123",addLimitedIssRole.getId());

			AuthorizeResponse response = authClient.authorize("muser/123","OMS","order","POST");
			Assert.assertTrue(response.getAuthorized());
			Assert.assertEquals(2,response.getAuthorizedAccounts().size());

			response = authClient.authorize("muser/123","iss","historical","execute");
			Assert.assertTrue(response.getAuthorized());
			Assert.assertEquals(2,response.getAuthorizedAccounts().size());


			response = authClient.authorize("muser/123","mark/3","iss","coverage","execute");
			Assert.assertTrue(response.getAuthorized());
			Assert.assertEquals(1,response.getAuthorizedAccounts().size());

			response = authClient.authorize("muser/123","mark/3","iss","historical","execute");
			Assert.assertFalse(response.getAuthorized());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			roleAssignmentClient.cleanup();
			roleClient.cleanup();
			accountClient.cleanup();
			accountFeatureClient.cleanup();
		}

	}


	public static void main(String[] args) throws Exception
	{
		Smoke smoke = new Smoke();
		smoke.run();
	}
}
