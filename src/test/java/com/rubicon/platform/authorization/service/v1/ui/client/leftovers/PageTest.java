package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import org.junit.Assert;
import org.junit.Test;

public class PageTest
{
	private Page page = new Page();

	@Test
	public void fieldTest()
	{
		Integer size = 1;
		Long totalElements = 1L;
		Long totalPages = 1L;
		Integer number = 1;

		page.setSize(size);
		page.setNumber(number);
		page.setTotalElements(totalElements);
		page.setTotalPages(totalPages);

		Assert.assertEquals(size, page.getSize());
		Assert.assertEquals(number, page.getNumber());
		Assert.assertEquals(totalElements, page.getTotalElements());
		Assert.assertEquals(totalPages, page.getTotalPages());
	}
}
