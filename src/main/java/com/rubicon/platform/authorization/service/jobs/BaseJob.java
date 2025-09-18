package com.rubicon.platform.authorization.service.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 */
@ManagedResource
public abstract class BaseJob<S extends JobStatus>
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected S jobStatus;
    protected boolean enabled;

    public void setJobStatus(S jobStatus)
    {
        this.jobStatus = jobStatus;
    }

    @ManagedAttribute
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @ManagedAttribute
    public boolean isEnabled()
    {
        return enabled;
    }

    public void run()
    {
        if (!enabled)
        {
            logger.info("Job is not enabled.");
            return;
        }

        try
        {
            jobStatus.start();
            logger.info("Starting job.");
            execute();
            logger.info("Completing job.");
        }
        catch (Exception e)
        {
            jobStatus.error(e);
            logger.error("Error executing job.", e);
        }
        finally
        {
            jobStatus.stop();
        }
    }

    abstract protected void execute();
}
