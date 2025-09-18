package com.rubicon.platform.authorization.service.cache.cluster;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: mhellkamp
 * Date: 10/26/12
 */
public class MessageParserTest
{
	private MessageParser messageParser = new MessageParser();

	@Test
	public void testCreateMessage()
	{
		String message = messageParser.createMessage("String",42L,Operation.delete);
		Assert.assertNotNull(message);

		String expected = "String" + MessageParser.DELIMITER + "delete" + MessageParser.DELIMITER + "42";
		Assert.assertEquals(expected,message);
	}

	@Test
	public void testParseMessage()
	{
		String message =  "String" + MessageParser.DELIMITER + "update" + MessageParser.DELIMITER + "42";
		CacheOperationMessage operationMessage = messageParser.parseMessage(message);
		Assert.assertNotNull(operationMessage);

		Assert.assertEquals("String",operationMessage.getClassName());
		Assert.assertEquals(Operation.update,operationMessage.getOperation());
		Assert.assertEquals(new Long(42L),operationMessage.getId());
	}

	@Test
	public void testParseInvalid()
	{
		CacheOperationMessage operationMessage = messageParser.parseMessage(null);
		Assert.assertNull(operationMessage);

		operationMessage = messageParser.parseMessage("");
		Assert.assertNull(operationMessage);

		operationMessage = messageParser.parseMessage("foobar:::");
		Assert.assertNull(operationMessage);

		operationMessage = messageParser.parseMessage("foobar:::delete:::x");
		Assert.assertNull(operationMessage);

		operationMessage = messageParser.parseMessage("foobar:::something:::99");
		Assert.assertNull(operationMessage);
	}
}
