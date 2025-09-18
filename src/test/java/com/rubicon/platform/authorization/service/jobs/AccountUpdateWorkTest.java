package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class AccountUpdateWorkTest extends TestAbstract
{
    private AccountLoader accountLoader;
    private List<String> autoAddFeatureIds = Arrays.asList("7","11","21","31");


    @DataProvider
    public static Object[][] testProcessProcessDataProvider()
    {
        return new Object[][]{
                {"active", "active", Status.updated},
                {"active", "pending", Status.updated},
                {"active", "deleted", Status.created},
                {"pending", "active", Status.updated},
                {"pending", "pending", Status.updated},
                {"pending", "deleted", Status.created},
                {"deleted", "active", Status.updated},
                {"deleted", "pending", Status.updated},
                {"deleted", "deleted", Status.updated}
        };
    }

    @Test
    @UseDataProvider("testProcessProcessDataProvider")
    public void testProcessWithExistingAccount(String revvStatus, String acmStatus, Status jobStatus)
    {
        RevvAccount revvAccount = new RevvAccount();
        revvAccount.setId("15");
        revvAccount.setStatus(revvStatus);

        PersistentAccount persistentAccount = getPersistentAccount(acmStatus);

        accountLoader = Mockito.mock(AccountLoader.class);
        when(accountLoader.findByAccountId((CompoundId) any())).thenReturn(persistentAccount);
        when(accountLoader.save((PersistentAccount) any())).thenReturn(persistentAccount);


        AccountUpdateWorker accountUpdateWorker = new AccountUpdateWorker();
        accountUpdateWorker.setAccountLoader(accountLoader);
        accountUpdateWorker.setPublisherAutoAddFeatureIds(autoAddFeatureIds);


        AccountUpdateResult result = accountUpdateWorker.process(revvAccount, "publisher");

        assertThat(result.getStatus(), equalTo(jobStatus));
    }

    @DataProvider
    public static Object[][] publisherAutoAddFeaturesDataProvider()
    {
        return new Object[][]{
                {"publisher", true},
                {"network", false}
        };
    }

    @Test
    @UseDataProvider("publisherAutoAddFeaturesDataProvider")
    public void publisherAutoAddFeaturesTest(String accountType, boolean hasAccountFeatures)
    {
        RevvAccount revvAccount = new RevvAccount();
        revvAccount.setId("15");
        revvAccount.setStatus("active");

        accountLoader = Mockito.mock(AccountLoader.class);
        when(accountLoader.findByAccountId(any())).thenReturn(null);
        when(accountLoader.save(any())).thenReturn(getPersistentAccount("active"));


        AccountUpdateWorker accountUpdateWorker = new AccountUpdateWorker();
        accountUpdateWorker.setAccountLoader(accountLoader);
        accountUpdateWorker.setPublisherAutoAddFeatureIds(autoAddFeatureIds);

        ArgumentCaptor<PersistentAccount> accountArgumentCaptor = ArgumentCaptor.forClass(PersistentAccount.class);

        AccountUpdateResult result = accountUpdateWorker.process(revvAccount, accountType);

        assertThat(result.getStatus(), equalTo(Status.created));
        verify(accountLoader).save(accountArgumentCaptor.capture());

        if (hasAccountFeatures)
        {
            Set<Long> accountFeatureIds = accountArgumentCaptor.getValue().getAccountFeatureIds();
            assertEquals(accountFeatureIds.size(), accountFeatureIds.size());
            for (String autoAddFeatureId : autoAddFeatureIds)
            {
                assertTrue(accountFeatureIds.contains(Long.valueOf(autoAddFeatureId)));
            }
        }
        else
        {
            assertEquals(0, accountArgumentCaptor.getValue().getAccountFeatureIds().size());
        }
    }


    protected PersistentAccount getPersistentAccount(String status)
    {
        PersistentAccount account = new PersistentAccount();
        account.setStatus(status);


        return account;
    }
}
