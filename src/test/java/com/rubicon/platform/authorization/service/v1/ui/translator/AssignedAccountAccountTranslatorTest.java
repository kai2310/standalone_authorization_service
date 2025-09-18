package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.ui.acm.Account;
import com.rubicon.platform.authorization.model.ui.acm.AssignedAccount;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_IS_EDITABLE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class AssignedAccountAccountTranslatorTest extends TestAbstract
{
    public AssignedAccountAccountTranslator translator;

    @Before
    public void setup()
    {
        translator = new AssignedAccountAccountTranslator();
        translator.init();
    }

    @DataProvider
    public static Object[][] convertPersistentDataProvider()
    {
        return new Object[][] {
                {Boolean.TRUE},
                {Boolean.FALSE}
        };
    }

    @Test
    @UseDataProvider("convertPersistentDataProvider")
    public void convertPersistentTest(Boolean isEditable)
    {
        Account apiAccount = getUiAccount();

        TranslationContext context = new TranslationContext();
        context.putContextItem(TRANSLATE_CONTEXT_IS_EDITABLE, isEditable);

        AssignedAccount assignedAccount = translator.convertPersistent(apiAccount, context);

        assertThat(assignedAccount.getId(), equalTo(DATA_SERVICE_ACCOUNT_ID));
        assertThat(assignedAccount.getName(), equalTo(DATA_SERVICE_ACCOUNT_NAME));
        assertThat(assignedAccount.getEditable(), equalTo(isEditable));
    }
}
