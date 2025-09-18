package com.rubicon.platform.authorization.service.cache;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 10/25/12
 */
public class SubjectIdMap
{
	private Map<Long,String> idToSubjectMap = new HashMap<Long, String>();
	private Map<String,Set<Long>> subjectToIdsMap = new HashMap<String, Set<Long>>();

	public synchronized void mapIdToSubject(Long id, String subject)
	{
		String previous = idToSubjectMap.put(id,subject);

		Set<Long> ids = subjectToIdsMap.get(subject);
		if(ids == null)
		{
			ids = new HashSet<Long>();
			subjectToIdsMap.put(subject,ids);
		}
		ids.add(id);

		// did the mapping change?
		if(previous != null && !previous.equals(subject))
		{
			Set<Long> previousIds = subjectToIdsMap.get(previous);
			if(previousIds != null)
			{
				previousIds.remove(id);
				if(previousIds.isEmpty())
					subjectToIdsMap.remove(previous);
			}
		}
	}

	public synchronized Collection<Long> getIdsForSubject(String subject)
	{
		List<Long> returnList = Collections.emptyList();

		Set<Long> ids = subjectToIdsMap.get(subject);

		if(ids != null)
			returnList = Collections.unmodifiableList(new ArrayList<Long>(ids));

		return returnList;
	}

	public synchronized void removeId(Long id)
	{
		String subject = idToSubjectMap.remove(id);
		if(subject != null)
		{
			Set<Long> ids = subjectToIdsMap.get(subject);
			if(ids != null)
			{
				ids.remove(id);
				if(ids.isEmpty())
					subjectToIdsMap.remove(subject);
			}
		}
	}

    public synchronized void clear()
    {
        idToSubjectMap.clear();
        subjectToIdsMap.clear();
    }
}
