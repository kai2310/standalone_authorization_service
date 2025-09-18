package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.service.cache.BaseRoleObjectCache;
import com.rubicon.platform.authorization.service.v1.ui.translator.converter.FeatureValueConverter;
import com.rubicon.platform.authorization.translator.DefaultObjectFieldMapper;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.data.acm.Account;
import com.rubicon.platform.authorization.model.ui.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.AccountFeatureActionEnum;

import java.util.ArrayList;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM;

public class AccountFeatureTranslator
        extends DefaultObjectTranslator<AccountFeature, Account>
{
    private BaseRoleObjectCache<com.rubicon.platform.authorization.model.data.acm.AccountFeature> accountFeatureCache;

    public AccountFeatureTranslator(
            BaseRoleObjectCache<com.rubicon.platform.authorization.model.data.acm.AccountFeature> accountFeatureCache)
    {
        super(AccountFeature.class, Account.class);
        this.accountFeatureCache = accountFeatureCache;
    }

    @Override
    public AccountFeature convertPersistent(Account persistent, TranslationContext translationContext)
    {
        AccountFeature accountFeature = super.convertPersistent(persistent, translationContext);

        // Convert the compound id to multiple fields
        String[] contextParts = persistent.getAccountId().split("/");
        accountFeature.setContextType(contextParts[0]);
        accountFeature.setContextId(Long.parseLong(contextParts[1]));

        AccountFeatureActionEnum allowedAction = (AccountFeatureActionEnum) translationContext
                .getContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM);

        accountFeature.setAllowedAction(allowedAction);


        return accountFeature;
    }



    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultObjectFieldMapper("name", "accountName", null));
        mappers.add(new DefaultObjectFieldMapper("features", "accountFeatureIds",
                new FeatureValueConverter(accountFeatureCache)));
        mappers.add(new DefaultObjectFieldMapper("status", "status", null));

        return mappers;
    }
}
