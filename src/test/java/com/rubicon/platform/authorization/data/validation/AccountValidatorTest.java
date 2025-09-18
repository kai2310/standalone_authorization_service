package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.exception.ValidationException;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.model.data.acm.Account;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AccountValidatorTest extends BaseValidatorFixture
{
    private AccountValidator validator = new AccountValidator();
    private final static String REVV_SOURCE = "revv";
    private final static String ENDPOINT_SOURCE = "endpoint";
    private final static String ACCOUNT_ID = "publisher/2763";
    private final static String ACCOUNT_NAME = "QA 1 Test";
    private final static String ACTIVE_STATUS = "active";
    private final static String DELETED_STATUS = "deleted";

    @DataProvider
    public static Object[][] AccountIdUpdateDataProvider()
    {
        return new Object[][]{
                {ACCOUNT_ID, false},
                {"publisher/1001", true}
        };
    }

    @Test
    @UseDataProvider("AccountIdUpdateDataProvider")
    public void AccountIdUpdateTest(String accountId, boolean hasError)
    {
        Account account = getAccount();
        account.setAccountId(accountId);

        if (hasError)
        {
            assertFailsUpdate(ValidationException.class, "The field \"accountId\" can not be modified.",
                    validator, account, getPersistentAccount());
        }
    }

    @DataProvider
    public static Object[][] sourceUpdateDataProvider()
    {
        return new Object[][]{
                {REVV_SOURCE, false},
                {ENDPOINT_SOURCE, true}
        };
    }

    @Test
    @UseDataProvider("sourceUpdateDataProvider")
    public void sourceUpdateTest(String source, boolean hasError)
    {
        Account account = new Account();
        account.setSource(source);

        if (hasError)
        {
            assertFailsUpdate(ValidationException.class, "The field \"source\" can not be modified.",
                    validator, account, getPersistentAccount());
        }
    }

    @DataProvider
    public static Object[][] nameAndStatusUpdateDataProvider()
    {
        String publisherType = "publisher";
        String seatType = "seat";
        return new Object[][]{
                {publisherType, ENDPOINT_SOURCE, ACCOUNT_NAME, ACTIVE_STATUS, false, null},
                {seatType, ENDPOINT_SOURCE, ACCOUNT_NAME, ACTIVE_STATUS, false, null},
                {publisherType, ENDPOINT_SOURCE, ACCOUNT_NAME, DELETED_STATUS, false, null},
                {seatType, ENDPOINT_SOURCE, ACCOUNT_NAME, DELETED_STATUS, false, null},
                {publisherType, ENDPOINT_SOURCE, ACCOUNT_NAME.concat("123"), ACTIVE_STATUS, false, null},
                {seatType, ENDPOINT_SOURCE, ACCOUNT_NAME.concat("123"), ACTIVE_STATUS, false, null},
                {publisherType, REVV_SOURCE, ACCOUNT_NAME, ACTIVE_STATUS, false, null},
                {seatType, REVV_SOURCE, ACCOUNT_NAME, ACTIVE_STATUS, false, null},
                {publisherType, REVV_SOURCE, ACCOUNT_NAME.concat("123"), ACTIVE_STATUS, true,
                        "The field \"accountName\" can not be modified because it was synchronized from an external system."},
                {seatType, REVV_SOURCE, ACCOUNT_NAME.concat("123"), ACTIVE_STATUS, false, null},
                {publisherType, REVV_SOURCE, ACCOUNT_NAME, DELETED_STATUS, true,
                        "The field \"accountStatus\" can not be modified because it was synchronized from an external system."},
                {seatType, REVV_SOURCE, ACCOUNT_NAME, DELETED_STATUS, false, null}
        };
    }

    @Test
    @UseDataProvider("nameAndStatusUpdateDataProvider")
    public void nameAndStatusUpdateTest(String accountType, String source, String accountName,
                                        String status, boolean hasError, String errorMsg)
    {
        PersistentAccount persistentAccount = getPersistentAccount();
        persistentAccount.setAccountId(new CompoundId(accountType, "2763"));
        persistentAccount.setSource(source);

        Account account = getAccount();
        account.setAccountId(accountType.concat("/2763"));
        account.setAccountName(accountName);
        account.setStatus(status);
        account.setSource(source);

        if (hasError)
        {
            assertFailsUpdate(ValidationException.class, errorMsg, validator,
                    account, persistentAccount);
        }
    }

    private PersistentAccount getPersistentAccount()
    {
        PersistentAccount persistentAccount = new PersistentAccount();
        persistentAccount.setAccountName(ACCOUNT_NAME);
        persistentAccount.setAccountId(new CompoundId("publisher", "2763"));
        persistentAccount.setSource(REVV_SOURCE);
        persistentAccount.setStatus(ACTIVE_STATUS);

        return persistentAccount;
    }

    private Account getAccount()
    {
        Account account = new Account();
        account.setAccountName(ACCOUNT_NAME);
        account.setAccountId(ACCOUNT_ID);
        account.setSource(REVV_SOURCE);
        account.setStatus(ACTIVE_STATUS);

        return account;
    }
}
