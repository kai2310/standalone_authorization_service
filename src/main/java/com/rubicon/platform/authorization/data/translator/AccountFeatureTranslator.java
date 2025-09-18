package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultAuditingTranslator;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 9/10/12
 */
public class AccountFeatureTranslator extends DefaultAuditingTranslator<AccountFeature,PersistentAccountFeature>
{

	public AccountFeatureTranslator()
	{
		super(AccountFeature.class, PersistentAccountFeature.class);
	}

    @Override
    protected void beforeConvert(ObjectWrapper<AccountFeature> clientObjectWrapper,
                                 ObjectWrapper<PersistentAccountFeature> persistentObjectWrapper,
                                 PersistenceContext context)
    {
        super.beforeConvert(clientObjectWrapper, persistentObjectWrapper,context);
        AccountFeature client = clientObjectWrapper.getWrappedObject();

        if(client.getRealm() == null)
            client.setRealm("");
    }

	@Override
	protected void afterConvert(ObjectWrapper<AccountFeature> clientObjectWrapper,
								ObjectWrapper<PersistentAccountFeature> persistentObjectWrapper,
								PersistenceContext context)
	{
		PersistentAccountFeature persistent = persistentObjectWrapper.getWrappedObject();
		persistent.setStatus(Status.ACTIVE);

		super.afterConvert(clientObjectWrapper, persistentObjectWrapper, context);
	}

	@Override
	protected List<FieldMapper> getCustomFieldMappers()
	{
		List<FieldMapper> mappers = new ArrayList<FieldMapper>();
		mappers.addAll(super.getCustomFieldMappers());
		mappers.add(new DeniedOperationFieldMapper());
		mappers.add(new AllowedOperationFieldMapper());
		return mappers;
	}
}
