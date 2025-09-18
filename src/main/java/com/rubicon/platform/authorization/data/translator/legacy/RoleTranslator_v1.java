package com.rubicon.platform.authorization.data.translator.legacy;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultAuditingTranslator;
import com.dottydingo.hyperion.core.translation.DefaultFieldMapper;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.api.legacy.Role_v1;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.translator.AllowedOperationFieldMapper;
import com.rubicon.platform.authorization.data.translator.CompoundIdValueConverter;
import com.rubicon.platform.authorization.data.translator.DeniedOperationFieldMapper;
import com.rubicon.platform.authorization.model.data.acm.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 9/11/12
 */
public class RoleTranslator_v1 extends DefaultAuditingTranslator<Role_v1,PersistentRole>
{
	public RoleTranslator_v1()
	{
		super(Role_v1.class, PersistentRole.class);
	}

    private Long defaultRoleTypeId ;

    public void setDefaultRoleTypeId(Long defaultRoleTypeId)
    {
        this.defaultRoleTypeId = defaultRoleTypeId;
    }

    @Override
    protected void beforeConvert(ObjectWrapper<Role_v1> clientObjectWrapper,
                                 ObjectWrapper<PersistentRole> persistentObjectWrapper, PersistenceContext context)
    {
        super.beforeConvert(clientObjectWrapper, persistentObjectWrapper, context);

        Role_v1 client = clientObjectWrapper.getWrappedObject();
        if(client.getRealm() == null)
            client.setRealm("");

    }

    @Override
    protected void afterConvert(ObjectWrapper<Role_v1> clientObjectWrapper,
                                ObjectWrapper<PersistentRole> persistentObjectWrapper, PersistenceContext context)
    {
        super.afterConvert(clientObjectWrapper, persistentObjectWrapper, context);
        PersistentRole persistentRole = persistentObjectWrapper.getWrappedObject();
        if(persistentRole.getRoleTypeId() == null)
        {
            persistentRole.setRoleTypeId(defaultRoleTypeId);
        }

        if(persistentRole.getStatus() == null)
            persistentRole.setStatus(Status.ACTIVE);
    }

    @Override
	protected List<FieldMapper> getCustomFieldMappers()
	{
		List<FieldMapper> mappers = new ArrayList<FieldMapper>();
		mappers.addAll(super.getCustomFieldMappers());
		mappers.add(new DefaultFieldMapper("ownerAccount","ownerAccount",new CompoundIdValueConverter()));
		mappers.add(new DeniedOperationFieldMapper());
		mappers.add(new AllowedOperationFieldMapper());

		return mappers;
	}

}
