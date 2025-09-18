package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.Account;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AccountTranslatorTest extends TestAbstract
{
    public AccountTranslator accountTranslator;

    @Before
    public void setup()
    {
        accountTranslator = new AccountTranslator();
        accountTranslator.init();
    }

    @Test
    public void convertPersistent()
    {
        com.rubicon.platform.authorization.model.data.acm.Account accountDataService = getDataServiceAccount();

        Account account = accountTranslator.convertPersistent(accountDataService, new TranslationContext());

        String compoundAccountId = account.getContextType().concat("/").concat(account.getContextId().toString());

        assertThat(account.getId(), equalTo(accountDataService.getId()));
        assertThat(account.getName(), equalTo(accountDataService.getAccountName()));
        assertThat(compoundAccountId, equalTo(accountDataService.getAccountId()));
        assertThat(account.getStatus(), equalTo(accountDataService.getStatus()));


    }

}
