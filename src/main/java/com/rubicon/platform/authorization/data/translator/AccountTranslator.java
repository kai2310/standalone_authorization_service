package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.*;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.model.data.acm.Account;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 9/10/12
 */
public class AccountTranslator extends BaseTranslator<Account,PersistentAccount>
{
    @Override
	protected Account createClientInstance()
	{
		return new Account();
	}

	@Override
	protected PersistentAccount createPersistentInstance()
	{
		return new PersistentAccount();
	}

	@Override
	protected List<FieldMapper> getCustomFieldMappers()
	{
		List<FieldMapper> mappers = new ArrayList<FieldMapper>();
		mappers.addAll(super.getCustomFieldMappers());
		mappers.add(new ReadOnlyFieldMapper("source"));
		mappers.add(new DefaultFieldMapper("accountFeatureIds","accountFeatureIds",new AccountFeatureIdsValueConverter()));
		mappers.add(new DefaultFieldMapper("accountId","accountId",new CompoundIdValueConverter()));
		return mappers;
	}

	@Override
	protected void afterConvert(ObjectWrapper<Account> clientObjectWrapper,
								ObjectWrapper<PersistentAccount> persistentObjectWrapper, PersistenceContext context)
	{
		super.afterConvert(clientObjectWrapper,	persistentObjectWrapper, context);
		Date now = new Date();
		PersistentAccount account = persistentObjectWrapper.getWrappedObject();
		account.setCreated(now);
		account.setModified(now);
		account.setSource("endpoint");
	}

	@Override
	protected boolean afterCopy(ObjectWrapper<Account> clientObjectWrapper,
							 ObjectWrapper<PersistentAccount> persistentObjectWrapper, PersistenceContext context)
	{
		boolean dirty = super.afterCopy(clientObjectWrapper, persistentObjectWrapper,context);
		if(context.isDirty() || dirty)
		{
			persistentObjectWrapper.getWrappedObject().setModified(new Date());
			dirty = true;
		}
		return dirty;
	}
}
