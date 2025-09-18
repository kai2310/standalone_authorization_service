package com.rubicon.platform.authorization.service.cache.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: mhellkamp
 * Date: 10/26/12
 */
public class MessageParser
{
	protected static final String DELIMITER = ":::";
	private Logger logger = LoggerFactory.getLogger(MessageParser.class);

	public String createMessage(String name, Long id,Operation operation)
	{
		StringBuilder sb = new StringBuilder(50);
		sb.append(name);
		sb.append(DELIMITER);
		sb.append(operation.name());
		sb.append(DELIMITER);
		sb.append(id);
		return sb.toString();
	}

	public CacheOperationMessage parseMessage(String message)
	{
		if(message == null || message.length() == 0)
		{
			logger.warn("Empty message received");
			return null;
		}

		logger.debug("Received message: {}",message);

		String[] split = message.split(DELIMITER);
		if(split.length != 3)
		{
			logger.warn("Invalid message received: {}",message);
			return null;
		}

		Long id = null;
		try
		{
			id = Long.parseLong(split[2]);
		}
		catch(NumberFormatException ignore){}

		if(id == null)
		{
			logger.warn("Invalid id:{}",split[2]);
			return null;
		}

		Operation operation = null;
		try
		{
			operation = Operation.valueOf(split[1]);
		}
		catch (Exception ignore){}
		if(operation == null)
		{
			logger.warn("Invalid operation:{}",split[1]);
			return null;
		}

		return new CacheOperationMessage(split[0],operation,id);
	}
}
