package com.rubicon.platform.authorization.service.cache;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * User: mhellkamp
 * Date: 10/25/12
 */
public class SubjectIdMapTest
{
	private SubjectIdMap idMap;

	@Before
	public void setup()
	{
		idMap = new SubjectIdMap();
	}

	@Test
	public void testBasicOperations()
	{
		idMap.mapIdToSubject(1l,"subject/1");
		idMap.mapIdToSubject(2l,"subject/1");
		idMap.mapIdToSubject(3l,"subject/2");
		idMap.mapIdToSubject(4l,"subject/5");

		Collection<Long> ids = idMap.getIdsForSubject("subject/1");
		Assert.assertNotNull(ids);
		Assert.assertEquals(2,ids.size());
		Assert.assertTrue(ids.contains(1L));
		Assert.assertTrue(ids.contains(2L));

		ids = idMap.getIdsForSubject("subject/2");
		Assert.assertNotNull(ids);
		Assert.assertEquals(1,ids.size());
		Assert.assertTrue(ids.contains(3L));

		ids = idMap.getIdsForSubject("subject/5");
		Assert.assertNotNull(ids);
		Assert.assertEquals(1,ids.size());
		Assert.assertTrue(ids.contains(4L));

		ids = idMap.getIdsForSubject("subject/99");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0,ids.size());

		idMap.removeId(2L);
		ids = idMap.getIdsForSubject("subject/1");
		Assert.assertEquals(1,ids.size());
		Assert.assertTrue(ids.contains(1L));

		idMap.removeId(4L);
		ids = idMap.getIdsForSubject("subject/5");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0,ids.size());

	}

	@Test
	public void testRemapSubject()
	{
		idMap.mapIdToSubject(1l,"subject/1");
		idMap.mapIdToSubject(2l,"subject/1");
		idMap.mapIdToSubject(3l,"subject/1");
		idMap.mapIdToSubject(4l,"subject/1");
		idMap.mapIdToSubject(5l,"subject/2");

		Collection<Long> ids = idMap.getIdsForSubject("subject/1");
		Assert.assertEquals(4,ids.size());

		idMap.mapIdToSubject(1L,"subject/99");
		ids = idMap.getIdsForSubject("subject/1");
		Assert.assertEquals(3,ids.size());
		Assert.assertTrue(ids.contains(2L));
		Assert.assertTrue(ids.contains(3L));
		Assert.assertTrue(ids.contains(4L));

		ids = idMap.getIdsForSubject("subject/99");
		Assert.assertEquals(1,ids.size());
		Assert.assertTrue(ids.contains(1L));

		ids = idMap.getIdsForSubject("subject/2");
		Assert.assertEquals(1,ids.size());
		Assert.assertTrue(ids.contains(5L));

		idMap.mapIdToSubject(5L,"subject/99");
		ids = idMap.getIdsForSubject("subject/99");
		Assert.assertEquals(2,ids.size());
		Assert.assertTrue(ids.contains(1L));
		Assert.assertTrue(ids.contains(5L));

		ids = idMap.getIdsForSubject("subject/2");
		Assert.assertEquals(0,ids.size());

	}
}
