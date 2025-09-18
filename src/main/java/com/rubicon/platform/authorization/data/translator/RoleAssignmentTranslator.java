package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.*;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.util.JsonUtil;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class RoleAssignmentTranslator extends BaseRoleAssignmentTranslator<RoleAssignment>
{
	public RoleAssignmentTranslator()
	{
		super(RoleAssignment.class);
	}

	@Override
	protected void convertPersistent(RoleAssignment client, PersistentRoleAssignment persistent,
									 PersistenceContext context)
	{
		super.convertPersistent(client, persistent, context);
		if(persistent.getAccountGroupId().equals(EMPTY_ACCOUNT_GROUP))
			client.setAccountGroupId(null);
		if(persistent.getAccount().equals(EMPTY_ACCOUNT))
			client.setAccount(null);
	}

	@Override
	protected List<FieldMapper> getCustomFieldMappers()
	{
		List<FieldMapper> mappers = new ArrayList<FieldMapper>();
		mappers.addAll(super.getCustomFieldMappers());
		mappers.add(new DefaultFieldMapper("account","account", new CompoundIdValueConverter()));

		return mappers;
	}

}
