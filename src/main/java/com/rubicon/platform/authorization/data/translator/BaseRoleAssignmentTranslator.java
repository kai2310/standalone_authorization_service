package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.*;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.util.JsonUtil;
import com.rubicon.platform.authorization.model.data.acm.BaseRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BaseRoleAssignmentTranslator<C extends BaseRoleAssignment> extends DefaultAuditingTranslator<C,PersistentRoleAssignment>
{
    public static final CompoundId EMPTY_ACCOUNT = new CompoundId("","");
    public final static Long EMPTY_ACCOUNT_GROUP = 0L;

	protected BaseRoleAssignmentTranslator(Class<C> clientClass)
	{
		super(clientClass, PersistentRoleAssignment.class);
	}

	@Override
    protected List<FieldMapper> getCustomFieldMappers()
    {
        List<FieldMapper> mappers = new ArrayList<FieldMapper>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultFieldMapper("ownerAccount", "ownerAccount", new CompoundIdValueConverter()));
        mappers.add(new DefaultFieldMapper("subject","subject", new CompoundIdValueConverter()));
        mappers.add(new DefaultFieldMapper("scope","scope",new ListConverter()));

        return mappers;
    }

	@Override
    protected void beforeConvert(ObjectWrapper<C> clientObjectWrapper,
                                 ObjectWrapper<PersistentRoleAssignment> persistentObjectWrapper,
                                 PersistenceContext context)
    {
        super.beforeConvert(clientObjectWrapper, persistentObjectWrapper, context);

        // set defaults for ownerAccount
        C roleAssignment = clientObjectWrapper.getWrappedObject();
        if(StringUtils.isEmpty(roleAssignment.getOwnerAccount()))
            roleAssignment.setOwnerAccount("*/*");

        if(roleAssignment.getRealm() == null)
            roleAssignment.setRealm("");
    }

    @Override
    protected void afterConvert(ObjectWrapper<C> clientObjectWrapper,
                                ObjectWrapper<PersistentRoleAssignment> persistentObjectWrapper,
                                PersistenceContext context)
    {
        super.afterConvert(clientObjectWrapper, persistentObjectWrapper, context);

        PersistentRoleAssignment persistent = persistentObjectWrapper.getWrappedObject();
        persistent.setStatus(Status.ACTIVE);

        if(persistent.getAccount() == null)
            persistent.setAccount(EMPTY_ACCOUNT);

        if(persistent.getAccountGroupId() == null)
            persistent.setAccountGroupId(EMPTY_ACCOUNT_GROUP);

    }



    private class ListConverter implements ValueConverter<List<String>,String>
	{

		@Override
		public List<String> convertToClientValue(String persistentValue, PersistenceContext context)
		{
			return JsonUtil.toList(persistentValue);
		}

		@Override
		public String convertToPersistentValue(List<String> clientValue, PersistenceContext context)
		{
			return JsonUtil.toString(clientValue);
		}
	}
}
