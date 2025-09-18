package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultFieldMapper;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.model.data.acm.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 9/11/12
 */
public class RoleTranslator extends BaseStatusTranslator<Role,PersistentRole>
{
	public RoleTranslator()
	{
		super(Role.class, PersistentRole.class);
	}

    @Override
    protected void beforeConvert(ObjectWrapper<Role> clientObjectWrapper,
                                 ObjectWrapper<PersistentRole> persistentObjectWrapper, PersistenceContext context)
    {
        super.beforeConvert(clientObjectWrapper, persistentObjectWrapper, context);

        Role client = clientObjectWrapper.getWrappedObject();
        if(client.getRealm() == null)
            client.setRealm("");
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
