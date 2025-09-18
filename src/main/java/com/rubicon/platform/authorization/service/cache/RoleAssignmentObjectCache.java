package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 10/17/12
 */
public class RoleAssignmentObjectCache implements WritableCache<RoleAssignment, Long>
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private SubjectIdMap idMap = new SubjectIdMap();

	private final Ehcache cache;

	public RoleAssignmentObjectCache(Ehcache cache)
	{
		this.cache = cache;
	}

	public List<ServiceRoleAssignment> getPermissions(Collection<CompoundId> subjects, CompoundId accountContext,
													  Set<Long> accountGroupIds)
	{
		List<ServiceRoleAssignment> roleAssignments = new ArrayList<>();

		logger.debug("Checking for roleAssignments.");
		for (CompoundId subject : subjects)
		{
			Collection<Long> ids = idMap.getIdsForSubject(subject.asIdString());
			logger.debug("Found {} roleAssignments for subject {}",ids.size(),subject);

			for (Long id : ids)
			{
                ServiceRoleAssignment roleAssignment = unwrapElement(cache.get(id));
				if(roleAssignment != null)
				{
					if(allowAccount(roleAssignment,accountContext) || allowAccountGroup(roleAssignment,accountGroupIds))
					{
						logger.debug("Added {}", roleAssignment);
						roleAssignments.add(roleAssignment);
					}
				}
			}
		}

		return roleAssignments;
	}

	private boolean allowAccount(ServiceRoleAssignment roleAssignment,CompoundId accountContext)
	{
		if(accountContext == null)
		{
			logger.debug("No account context specified, allowing roleId={} for account={}.",
					roleAssignment.getRoleId(),
					roleAssignment.getAccount());
			return true;
		}

		if(roleAssignment.getAccountId() == null)
		{
			// this is an account group assignment
			return false;
		}

		CompoundId roleContext = roleAssignment.getAccountId();

		if(accountContext.getIdType().equals(roleContext.getIdType()) &&
				(accountContext.getId().equals(roleContext.getId()) || roleContext.isWildcardId()))
		{
			logger.debug("RoleAssignment.id={} matches account={}", roleAssignment.getId(),roleContext.asIdString());
			return true;
		}

		logger.debug("RoleAssignment.id={} does not match account={}", roleAssignment.getId(),roleContext.asIdString());
		return false;
	}

	private boolean allowAccountGroup(ServiceRoleAssignment roleAssignment,Set<Long> accountGroupIds)
	{
		if(roleAssignment.getAccountGroupId()== null || accountGroupIds.isEmpty())
		{
			return false;
		}

		if(accountGroupIds.contains(roleAssignment.getAccountGroupId()))
		{
			logger.debug("RoleAssignment.id={} matches for accountGroupId={}",
					roleAssignment.getId(),
					roleAssignment.getAccountGroupId());
			return true;
		}

		logger.debug("RoleAssignment.id={} does not match for accountGroupId={}",
				roleAssignment.getId(),
				roleAssignment.getAccountGroupId());
		return false;
	}

	public void addEntry(RoleAssignment roleAssignment)
	{
		synchronized (cache)
		{
			idMap.mapIdToSubject(roleAssignment.getId(), roleAssignment.getSubject());
			cache.put(createElement(roleAssignment.getId(), roleAssignment));
		}
	}

	public void updateEntry(RoleAssignment roleAssignment)
	{
		synchronized (cache)
		{
			idMap.mapIdToSubject(roleAssignment.getId(), roleAssignment.getSubject());
			cache.put(createElement(roleAssignment.getId(), roleAssignment));
		}
	}

	public void removeEntry(Long id)
	{
		synchronized (cache)
		{
			idMap.removeId(id);
			cache.remove(id);
		}
	}

    @Override
    public void clear()
    {
        synchronized (cache)
        {
            idMap.clear();
            cache.removeAll();
        }
    }

    protected Element createElement(Serializable key, RoleAssignment entry)
    {
        return new Element(key,new KryoWrapper(new ServiceRoleAssignment(entry)));
    }

    protected ServiceRoleAssignment unwrapElement(Element element)
    {
        if(element != null)
        {
            KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
            return (ServiceRoleAssignment) wrapper.getWrapped();
        }
        return null;
    }

    // Exposing this Information So we can actually get the data from outside of this class
    public SubjectIdMap getIdMap()
    {
        return idMap;
    }
}
