package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.service.cache.CacheLockHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * User: mhellkamp
 * Date: 10/22/12
 */
public class AccountSyncJob extends BaseSingletonJob
{
	private List<SyncWorker> workers;

    @Autowired
    private CacheLockHolder cacheLockHolder;

	public void setWorkers(List<SyncWorker> workers)
	{
		this.workers = workers;
	}

	@Override
	protected void execute()
	{
        // we want to prevent a refresh from occurring during a sync process
        Lock lock = cacheLockHolder.getReadLock();
        lock.lock();

        try
        {
            for (SyncWorker worker : workers)
            {
                worker.run();
            }
        }
        finally
        {
            lock.unlock();
        }
    }

}
