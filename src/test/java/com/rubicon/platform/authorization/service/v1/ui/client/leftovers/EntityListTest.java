package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EntityListTest
{
	private EntityList<String> entityList = new EntityList<>();

	@Test
	public void fieldTest()
	{
		List<String> content = Arrays.asList("test");
		entityList.setContent(content);

		Assert.assertEquals(content, entityList.getContent());
	}
}
