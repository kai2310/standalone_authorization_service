package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.ValueConverter;
import com.rubicon.platform.authorization.data.model.CompoundId;

/**
 * User: mhellkamp
 * Date: 9/12/12
 */
public class CompoundIdValueConverter implements ValueConverter<String,CompoundId>
{
	private IdParser idParser = IdParser.STANDARD_ID_PARSER;

	@Override
	public String convertToClientValue(CompoundId persistentValue, PersistenceContext context)
	{
		return persistentValue.asIdString();
	}

	@Override
	public CompoundId convertToPersistentValue(String clientValue, PersistenceContext context)
	{
		if(clientValue == null)
			return null;

		return idParser.parseId(clientValue);
	}
}
