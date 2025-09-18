package com.rubicon.platform.authorization.service.cache.cluster;

import com.hazelcast.core.ITopic;

/**
 * User: mhellkamp
 * Date: 10/18/12
 */
public class DistributedInvalidationBroadcaster
{
	private static DistributedInvalidationBroadcaster INSTANCE;
	private MessageParser messageParser;
	private ITopic<String> topic;

	public static DistributedInvalidationBroadcaster getInstance()
	{
		return INSTANCE;
	}

	public DistributedInvalidationBroadcaster()
	{
		// hack to allow static access for the JPA listener
		INSTANCE = this;
	}

	public void setMessageParser(MessageParser messageParser)
	{
		this.messageParser = messageParser;
	}

	public void setTopic(ITopic<String> topic)
	{
		this.topic = topic;
	}

	public <P> void processCreate(String entity,Long id)
	{
		topic.publish(messageParser.createMessage(entity,id,Operation.create));
	}

	public <P> void processUpdate(String entity,Long id)
	{
		topic.publish(messageParser.createMessage(entity,id,Operation.update));
	}

	public <P> void processDelete(String entity,Long id)
	{
		topic.publish(messageParser.createMessage(entity,id,Operation.delete));
	}

}
