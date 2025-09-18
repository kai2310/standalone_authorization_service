package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import com.rubicon.platform.authorization.model.data.lfo.Authorization;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class EntityResponseTest
{
	private EntityResponse<Authorization> entityResponse = new EntityResponse<>();

	@Test
	public void fieldTest()
	{
		entityResponse.setPage(new Page());
		entityResponse.setContent(Arrays.asList(new Authorization()));

		Assert.assertNotNull(entityResponse.getPage());
		Assert.assertNotNull(entityResponse.getContent());
	}
}
