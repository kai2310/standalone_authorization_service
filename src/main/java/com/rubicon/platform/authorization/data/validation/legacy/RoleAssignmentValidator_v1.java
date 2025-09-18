package com.rubicon.platform.authorization.data.validation.legacy;

import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.api.legacy.RoleAssignment_v1;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentUniqueCheck;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.data.validation.BaseAccountValidator;
import com.rubicon.platform.authorization.data.validation.WildcardValidator;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * User: mhellkamp
 * Date: 9/12/12
 */
public class RoleAssignmentValidator_v1 extends BaseAccountValidator<RoleAssignment_v1,PersistentRoleAssignment>
{
	private RoleAssignmentUniqueCheck roleAssignmentUniqueCheck;
	private RoleLoader roleLoader;
	private Map<String,Long> accountGroupMap;

	public void setRoleAssignmentUniqueCheck(RoleAssignmentUniqueCheck roleAssignmentUniqueCheck)
	{
		this.roleAssignmentUniqueCheck = roleAssignmentUniqueCheck;
	}

	public void setRoleLoader(RoleLoader roleLoader)
	{
		this.roleLoader = roleLoader;
	}

	public void setAccountGroupMap(Map<String, Long> accountGroupMap)
	{
		this.accountGroupMap = accountGroupMap;
	}

	@Override
	public void validateCreate(RoleAssignment_v1 clientObject,PersistenceContext context)
	{
		assertRequired("ownerAccount",clientObject.getOwnerAccount());
		validateAccount(clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);
		assertRequired("subject",clientObject.getSubject());
		CompoundId subjectId = validateSubject(clientObject.getSubject());
		assertRequired("accountContext",clientObject.getAccount());
		CompoundId accountId = validateAccount(clientObject.getAccount(), WildcardValidator.idWildcardAllowed);
		assertRequired("roleId",clientObject.getRoleId());
		assertNoDuplicates("scope",clientObject.getScope());
        validateLength("realm",clientObject.getRealm(),64);

		if(clientObject.getAccountGroupId() != null)
			throw new ValidationException("The accountGroupId field is read only in this version.");

		if(accountId != null && accountId.isWildcardId() && !accountGroupMap.containsKey(accountId.getIdType()))
			throw new ValidationException(String.format("Unsupported account type: \"%s\"",accountId.getIdType()));

		if(clientObject.getRoleId() != null)
		{
			PersistentRole role = roleLoader.find(clientObject.getRoleId());
			if(role == null)
				throw new ValidationException(
						String.format("No Role found for id=%d", clientObject.getRoleId()));

			if(StringUtils.isNotEmpty(clientObject.getRealm())
			   && StringUtils.isNotEmpty(role.getRealm()))
			{
				if(!clientObject.getRealm().equals(role.getRealm()))
					throw new ValidationException(
							String.format("RoleAssignment realm \"%s\" does not match Role realm %s\".",
									clientObject.getRealm(),role.getRealm()));
			}
		}

		if(roleAssignmentUniqueCheck.exists(subjectId,accountId,clientObject.getRoleId(), null))
			throw new ValidationException(
					String.format("A RoleAssignment already exists for subject:\"%s\" and roleId:%d for account:\"%s\"",
							subjectId,clientObject.getRoleId(),accountId));

	}

	@Override
	public void validateUpdate(RoleAssignment_v1 clientObject, PersistentRoleAssignment persistentObject,PersistenceContext context)
	{
		// can't edit a deleted item
		if(persistentObject.getStatus() == Status.DELETED)
			throw new ValidationException("The RoleAssignment can not be edited because it has been marked as deleted.");

		assertNotBlank("ownerAccount", clientObject.getOwnerAccount());
		validateAccount(clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);
		assertNotBlank("subject", clientObject.getSubject());
		assertNoDuplicates("scope",clientObject.getScope());
        assertNotChanged("realm",clientObject.getRealm(),persistentObject.getRealm());
        assertNotChanged("accountGroupId",clientObject.getAccountGroupId(),persistentObject.getAccountGroupId());

		CompoundId  subjectId = null;
		if(clientObject.getSubject() != null)
			subjectId = validateSubject(clientObject.getSubject());

		assertNotBlank("accountContext", clientObject.getAccount());
		CompoundId accountId = validateAccount(clientObject.getAccount(), WildcardValidator.idWildcardAllowed);

		boolean subjectChanged = subjectId != null && !subjectId.equals(persistentObject.getSubject());
		boolean accountChanged = accountId != null && !accountId.equals(persistentObject.getAccount());
		boolean roleChanged = clientObject.getRoleId() != null && !clientObject.getRoleId().equals(persistentObject.getRoleId());

		if(roleChanged)
		{
			PersistentRole role = roleLoader.find(clientObject.getRoleId());
			if(role == null)
				throw new ValidationException(
						String.format("No Role found for id=%d", clientObject.getRoleId()));

			String realm = resolveValue(clientObject.getRealm(),persistentObject.getRealm());
			if(StringUtils.isNotEmpty(realm)
			   && StringUtils.isNotEmpty(role.getRealm()))
			{
				if(!clientObject.getRealm().equals(role.getRealm()))
					throw new ValidationException(
							String.format("RoleAssignment realm \"%s\" does not match Role realm %s\".",
									clientObject.getRealm(),role.getRealm()));
			}
		}

		if(subjectChanged || accountChanged || roleChanged)
		{
			subjectId = subjectId != null ? subjectId : persistentObject.getSubject();
			accountId = accountId != null ? accountId : persistentObject.getOwnerAccount();
			Long roleId = clientObject.getRoleId() != null ? clientObject.getRoleId() : persistentObject.getRoleId();

			if(roleAssignmentUniqueCheck.exists(subjectId,accountId,roleId, clientObject.getAccountGroupId()))
				throw new ValidationException(
						String.format("A RoleAssignment already exists for subject:\"%s\" and roleId:%d for account:\"%s\"",
								subjectId,clientObject.getRoleId(),accountId));
		}
	}

	protected CompoundId validateSubject(String subject)
	{

		CompoundId id = null;
		try
		{
			id =  idParser.parseId(subject);
		}
		catch (IllegalArgumentException e)
		{
			throw new BadRequestException(e.getMessage());
		}

		if(!WildcardValidator.idWildcardAllowed.isValid(id))
		{
			throw new ValidationException("subjectType can not be a wildcard.");
		}

		return id;
	}
}
