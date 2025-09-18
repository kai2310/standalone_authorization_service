package com.rubicon.platform.authorization.service.jobs;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * User: mhellkamp
 * Date: 1/31/13
 */
public abstract class BaseSingletonJob<S extends JobStatus> extends BaseJob<S>
{
    private String jobKey;
	private Lock jobLock;
	private Map<String,Long> sharedMap;

    public void setJobLock(Lock jobLock)
	{
		this.jobLock = jobLock;
	}

	public void setSharedMap(Map<String, Long> sharedMap)
	{
		this.sharedMap = sharedMap;
	}

    private long getLastMappedStartTime()
	{
		Long last = sharedMap.get(jobKey);
		if(last == null) return 0;
		return last;
	}

	public void setJobKey(String jobKey)
	{
		this.jobKey = jobKey;
	}

    @Override
    public void run()
    {
        if (!enabled)
        {
            logger.info("Job is not enabled.");
            return;
        }

        if (!jobLock.tryLock())
        {
            logger.info("No lock acquired. Skipping execution.");
            return;
        }

        long now = System.currentTimeMillis();
        try
        {
            long last = getLastMappedStartTime();
            if (Math.abs(now - last) < 10000)
            {
                logger.info("This job has already been started within the last 10 seconds, skipping execution.");
                return;
            }

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
            sharedMap.put(jobKey, now);
            jobLock.unlock();
        }
    }
}
