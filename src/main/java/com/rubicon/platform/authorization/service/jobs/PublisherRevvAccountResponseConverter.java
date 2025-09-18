package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.model.data.pmg.Publisher;
import com.rubicon.platform.authorization.translator.ObjectValueConverter;
import com.rubicon.platform.authorization.translator.TranslationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class PublisherRevvAccountResponseConverter implements ObjectValueConverter<List<Publisher>, RevvAccountResponse>
{
	public RevvAccountResponse convertToPersistentValue(List<Publisher> clientObject, TranslationContext translationContext)
	{
		RevvAccountResponse revvAccounts = new RevvAccountResponse();
		if (!CollectionUtils.isEmpty(clientObject))
		{
			for (Publisher publisher : clientObject)
			{
				revvAccounts.add(new RevvAccount(publisher.getId().toString(),
						publisher.getName(), publisher.getStatus().name()));
			}
		}

		return revvAccounts;
	}

	// right now this function is not being used
	public List<Publisher> convertToClientValue(RevvAccountResponse persistentObject, TranslationContext var2)
	{
		return null;
	}
}
