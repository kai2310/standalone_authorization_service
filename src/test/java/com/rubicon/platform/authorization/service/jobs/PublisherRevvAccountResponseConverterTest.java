package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.data.pmg.Publisher;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class PublisherRevvAccountResponseConverterTest extends TestAbstract
{
	private PublisherRevvAccountResponseConverter converter = new PublisherRevvAccountResponseConverter();
	private TranslationContext context = new TranslationContext();

	@DataProvider
	public static Object[][] convertToPersistentValueDataProvider()
	{
		return new Object[][]{
				{false},
				{true},
		};
	}

	@Test
	@UseDataProvider("convertToPersistentValueDataProvider")
	public void convertToPersistentValueTest(boolean hasPublishers)
	{
		List<Publisher> publisherList = hasPublishers ? Arrays.asList(getPublisher()) : null;

		RevvAccountResponse revvAccounts = converter.convertToPersistentValue(publisherList, context);

		if (hasPublishers)
		{
			Assert.assertEquals(1, revvAccounts.size());
			RevvAccount revvAccount = revvAccounts.get(0);
			Assert.assertEquals(String.valueOf(DATA_SERVICE_ACCOUNT_ID), revvAccount.getId());
			Assert.assertEquals(DATA_SERVICE_ACCOUNT_NAME, revvAccount.getLabel());
			Assert.assertEquals("active", revvAccount.getStatus());
		}
		else
		{
			Assert.assertEquals(0, revvAccounts.size());
		}
	}

	@Test
	public void convertToClientValueTest()
	{
		Assert.assertNull(converter.convertToClientValue(new RevvAccountResponse(), context));
	}
}
