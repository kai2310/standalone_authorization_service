package com.rubicon.platform.authorization.service.cache.cluster;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.rubicon.platform.authorization.service.cache.CacheLoadException;
import com.rubicon.platform.authorization.service.cache.CacheNotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: mhellkamp
 * Date: 10/25/12
 */
public class DistributedInvalidationListener implements MessageListener<String>
{
	private static final Executor messageExecutor = Executors.newSingleThreadExecutor();
	private Logger logger = LoggerFactory.getLogger(DistributedInvalidationListener.class);

	private MessageParser messageParser;
	private Map<String,CacheNotificationListener> listenerMap;

	public void setMessageParser(MessageParser messageParser)
	{
		this.messageParser = messageParser;
	}

	public void setListenerMap(Map<String, CacheNotificationListener> listenerMap)
	{
		this.listenerMap = listenerMap;
	}

	@Override
	public void onMessage(final Message<String> message)
	{
		final DistributedInvalidationListener listener = this;

		messageExecutor.execute(new Runnable() {
			@Override
			public void run()
			{
				listener.processMessage(message.getMessageObject());
			}
		});
	}

	private void processMessage(String message)
	{
		CacheOperationMessage cacheOperationMessage = messageParser.parseMessage(message);
		if(cacheOperationMessage != null)
		{
			CacheNotificationListener listener = listenerMap.get(cacheOperationMessage.getClassName());
			if(listener != null)
			{
				logger.debug("Processing {}",cacheOperationMessage);

                boolean retry = true;
                while (retry)
                {
                    retry = false;
                    try
                    {
                        switch (cacheOperationMessage.getOperation())
                        {
                            case create:
                                listener.onCreate(cacheOperationMessage.getId());
                                break;
                            case update:
                                listener.onUpdate(cacheOperationMessage.getId());
                                break;
                            case delete:
                                listener.onDelete(cacheOperationMessage.getId());
                                break;
                            default:
                                logger.warn("Unrecognized operation for message: {}", message);
                        }
                    }
                    catch (CacheLoadException | TransactionException e)
                    {
                        retry = true;
                        logger.error("Error updating cache. Will retry.",e);
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException ignore){}
                    }

                }
			}
			else
				logger.warn("No listener found for {}",cacheOperationMessage.getClassName());
		}
	}
}
