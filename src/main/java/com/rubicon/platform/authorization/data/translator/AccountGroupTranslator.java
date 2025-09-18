package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.translation.DefaultAuditingTranslator;
import com.dottydingo.hyperion.core.translation.DefaultFieldMapper;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class AccountGroupTranslator extends BaseStatusTranslator<AccountGroup,PersistentAccountGroup>
{
    public AccountGroupTranslator()
    {
        super(AccountGroup.class, PersistentAccountGroup.class);
    }

    @Override
    protected List<FieldMapper> getCustomFieldMappers()
    {
        List<FieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultFieldMapper("accountIds","accountIds",new AccountIdsValueConverter()));

        return mappers;
    }
}
