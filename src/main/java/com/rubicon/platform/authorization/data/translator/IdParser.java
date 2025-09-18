package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.CompoundId;
import org.apache.commons.lang3.StringUtils;

/**
 */
public class IdParser
{
	public static IdParser STANDARD_ID_PARSER = new IdParser();
	public static IdParser QUERY_ID_PARSER = new IdParser(true);


	private boolean query = false;

	private IdParser()
	{
	}

	private IdParser(boolean query)
	{
		this.query = query;
	}

	public CompoundId parseId(String input)
	{
		if(StringUtils.isBlank(input))
			return null;

		String errMessage = query ?
				String.format("Invalid id string \"%s\". Must be in the form of <idType> or <idType>/<id>.",input) :
				String.format("Invalid id string \"%s\". Must be in the form of <idType>/<id>.",input);
		String[] split = input.split("/");
		if(!query && split.length != 2)
			throw new IllegalArgumentException(errMessage);

		if(query && split.length > 2)
			throw new IllegalArgumentException(errMessage);

		String type = split[0];
		String id = split.length ==2 ? split[1] : null;

        if(StringUtils.isBlank(type) || (id != null && StringUtils.isBlank(id)))
            throw new IllegalArgumentException(errMessage);

		return new CompoundId(type,id);
	}
}
