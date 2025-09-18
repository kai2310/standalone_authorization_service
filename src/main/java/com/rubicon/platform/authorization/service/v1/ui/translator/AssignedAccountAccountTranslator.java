package com.rubicon.platform.authorization.service.v1.ui.translator;


import com.rubicon.platform.authorization.model.ui.acm.Account;
import com.rubicon.platform.authorization.model.ui.acm.AssignedAccount;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;

import java.util.ArrayList;
import java.util.List;

public class AssignedAccountAccountTranslator extends DefaultObjectTranslator<AssignedAccount, Account>
{
    public AssignedAccountAccountTranslator()
    {
        super(AssignedAccount.class, Account.class);
    }

    @Override
    public AssignedAccount convertPersistent(Account persistent, TranslationContext translationContext)
    {
        AssignedAccount assignedAccount = super.convertPersistent(persistent, translationContext);
        assignedAccount.setEditable((boolean) translationContext.getContextItem(Constants.TRANSLATE_CONTEXT_IS_EDITABLE));

        return assignedAccount;

    }

    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());

        return mappers;
    }

}
