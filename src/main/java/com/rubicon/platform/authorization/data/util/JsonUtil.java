package com.rubicon.platform.authorization.data.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 9/11/12
 */
public class JsonUtil
{
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static List<String> toList(String value)
	{
		if(value == null || value.length() == 0)
			return null;

		List<String> list = null;
		try
		{
			list = objectMapper.readValue(value,List.class);
		}
		catch (IOException e)
		{
			logger.warn("Error converting stored value to list.",e);
		}

		return list;

	}

	public static Set<String> toSet(String value)
	{
		if(value == null || value.length() == 0)
			return null;

		Set<String> list = null;
		try
		{
			list = objectMapper.readValue(value,Set.class);
		}
		catch (IOException e)
		{
			logger.warn("Error converting stored value to list.",e);
		}

		return list;

	}

	public static String toString(List<String> value)
	{
		if(value == null )
			return null;

		String string = null;
		try
		{
			string = objectMapper.writeValueAsString(value);
		}
		catch (IOException e)
		{
			logger.warn("Error converting list to a string.",e);
		}

		return string;

	}
}
