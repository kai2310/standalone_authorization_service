package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.CompoundId;
import junit.framework.Assert;
import org.junit.Test;

/**
 */
public class IdParserTest
{

	@Test
	public void testParse()
	{
		IdParser parser = IdParser.STANDARD_ID_PARSER;

		CompoundId id = parser.parseId("foo/bar");
		Assert.assertEquals("foo",id.getIdType());
		Assert.assertEquals("bar",id.getId());

		assertParseFail(parser,"foo");
		assertParseFail(parser,"foo/bar/baz");
	}

	@Test
	public void testParseQuery()
	{
		IdParser parser = IdParser.QUERY_ID_PARSER;
		CompoundId id = parser.parseId("foo/bar");
		Assert.assertEquals("foo",id.getIdType());
		Assert.assertEquals("bar",id.getId());

		id = parser.parseId("foo");
		Assert.assertEquals("foo",id.getIdType());
		Assert.assertNull(id.getId());

		assertParseFail(parser,"foo/bar/baz");
	}

	private void assertParseFail(IdParser parser,String input)
	{
		try
		{
			parser.parseId(input);
			Assert.fail("Should have thrown an exception");
		}
		catch (IllegalArgumentException ignore){}
	}

}
