package com.rubicon.platform.authorization.service.jobs;

import org.jgroups.annotations.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * User: mhellkamp
 * Date: 1/31/13
 */
@ManagedResource
public class SyncJobStatus extends JobStatus
{
    private int lastAdded;
    private int totalAdded;
    private int lastModified;
    private int totalModified;
    private boolean enabled;

    public void added(int added)
    {
        lastAdded = added;
        totalAdded += added;
    }

    public void modified(int modified)
    {
        lastModified = modified;
        totalModified += modified;
    }

	@ManagedAttribute
	public int getLastAdded()
	{
		return lastAdded;
	}

	@ManagedAttribute
	public int getTotalAdded()
	{
		return totalAdded;
	}

	@ManagedAttribute
	public int getLastModified()
	{
		return lastModified;
	}

	@ManagedAttribute
	public int getTotalModified()
	{
		return totalModified;
	}

    @ManagedAttribute
    public boolean isEnabled()
    {
        return enabled;
    }

    @ManagedAttribute
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
