package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.AccountFeatureActionEnum;
import org.junit.Before;
import org.junit.Test;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AccountFeatureTranslatorTest extends TestAbstract
{
    public AccountFeatureTranslator accountFeatureTranslator;

    @Before
    public void setup()
    {
        accountFeatureTranslator = new AccountFeatureTranslator(getMockAccountFeatureCache());
        accountFeatureTranslator.init();
    }

    @Test
    public void convertPersistent()
    {
        com.rubicon.platform.authorization.model.data.acm.Account accountDataService = getDataServiceAccount();

        TranslationContext context = new TranslationContext();
        context.putContextItem(TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM, AccountFeatureActionEnum.none);

        AccountFeature accountFeature =
                accountFeatureTranslator.convertPersistent(accountDataService, context);

        CompoundId expectedCompoundId = new CompoundId(DATA_SERVICE_ACCOUNT_COMBO_ID);

        assertThat(accountFeature.getId(), equalTo(accountDataService.getId()));
        assertThat(accountFeature.getName(), equalTo(accountDataService.getAccountName()));
        assertThat(accountFeature.getContextType(), equalTo(expectedCompoundId.getIdType()));
        assertThat(accountFeature.getContextId().toString(), equalTo(expectedCompoundId.getId()));
        assertThat(accountFeature.getAllowedAction(), equalTo(AccountFeatureActionEnum.none));
        assertThat(accountFeature.getStatus(), equalTo(DATA_SERVICE_STATUS));

        assertThat(accountFeature.getFeatures().size(), equalTo(1));
        assertThat(accountFeature.getFeatures().get(0).getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        assertThat(accountFeature.getFeatures().get(0).getName(), equalTo(DATA_SERVICE_FEATURE_NAME));

    }
}
