package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.translator.DefaultObjectFieldMapper;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountTranslator
        extends DefaultObjectTranslator<Account, com.rubicon.platform.authorization.model.data.acm.Account>
{
    public AccountTranslator()
    {
        super(Account.class, com.rubicon.platform.authorization.model.data.acm.Account.class);
    }

    @Override
    public Account convertPersistent(com.rubicon.platform.authorization.model.data.acm.Account persistent,
                                     TranslationContext translationContext)
    {
        Account account = super.convertPersistent(persistent, translationContext);

        // Convert the compound id to multiple fields
        String[] contextParts = persistent.getAccountId().split("/");
        account.setContextType(contextParts[0]);
        account.setContextId(Long.parseLong(contextParts[1]));


        return account;
    }

    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultObjectFieldMapper("name", "accountName", null));

        return mappers;
    }

}
