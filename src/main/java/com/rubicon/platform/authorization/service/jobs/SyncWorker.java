package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.config.ConfigurationManager;
import com.rubicon.platform.authorization.service.cache.cluster.DistributedInvalidationBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/22/12
 */
public class SyncWorker
{
    private static Logger logger = LoggerFactory.getLogger(SyncWorker.class);

    private AccountUpdateWorker accountUpdateWorker;
    private ConfigurationManager configurationManager;
    private String jobDataKey;
    private String initialStatusFilter;
    private String updateStatusFilter;
    private String accountType;
    private RevvRetriever<RevvAccountResponse> retriever;
    private SyncJobStatus jobStatus;

	public void setAccountUpdateWorker(AccountUpdateWorker accountUpdateWorker)
	{
		this.accountUpdateWorker = accountUpdateWorker;
	}

	public void setConfigurationManager(ConfigurationManager configurationManager)
	{
		this.configurationManager = configurationManager;
	}

	public void setJobDataKey(String jobDataKey)
	{
		this.jobDataKey = jobDataKey;
	}

	public void setInitialStatusFilter(String initialStatusFilter)
	{
		this.initialStatusFilter = initialStatusFilter;
	}

    public void setUpdateStatusFilter(String updateStatusFilter)
    {
        this.updateStatusFilter = updateStatusFilter;
    }

    public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public void setRetriever(RevvRetriever<RevvAccountResponse> retriever)
	{
		this.retriever = retriever;
	}

	public void setJobStatus(SyncJobStatus jobStatus)
	{
		this.jobStatus = jobStatus;
	}

    public void run()
    {
        if (jobStatus.isEnabled())
        {

            logger.info("Starting {} sync", accountType);
            jobStatus.start();
            try
            {
                long lastUpdate = 0;
                String value = configurationManager.getValue(jobDataKey);
                if (value != null)
                {
                    logger.info("Found {} of {}", jobDataKey, value);
                    try
                    {
                        lastUpdate = Long.parseLong(value);
                    }
                    catch (NumberFormatException ignore)
                    {
                    }
                }

                logger.info("Using last update = {}", lastUpdate);

                String status = lastUpdate == 0
                                ? initialStatusFilter
                                : updateStatusFilter;

                long start = System.currentTimeMillis();

                List<RevvAccount> accounts = retriever.retrieve(lastUpdate, status);

                // If the accounts is null, note it
                if (accounts == null)
                {
                    logger.warn("There was an error in the {} account sync.", accountType);
                }
                else
                {
                    logger.info("Found {} items to update.", accounts.size());

                    int newCount = 0;
                    int updatedCount = 0;

                    for (RevvAccount account : accounts)
                    {
                        try
                        {
                            AccountUpdateResult result = accountUpdateWorker.process(account, accountType);
                            if (result.getStatus() == Status.created)
                            {
                                newCount++;
                                DistributedInvalidationBroadcaster.getInstance()
                                        .processCreate("Account", result.getId());
                            }
                            else
                            {
                                updatedCount++;
                                DistributedInvalidationBroadcaster.getInstance()
                                        .processUpdate("Account", result.getId());
                            }

                        }
                        catch (Exception e)
                        {
                            logger.warn(
                                    String.format("Error processing account update for accountId: %s", account.getId()),
                                    e);
                        }


                    }

                    long total = System.currentTimeMillis() - start;

                    if (logger.isInfoEnabled())
                    {
                        logger.info(
                                String.format("Completed %d new and %d updated in %dms", newCount, updatedCount, total)
                        );
                    }

                    jobStatus.added(newCount);
                    jobStatus.modified(updatedCount);

                    configurationManager.setValue(jobDataKey, Long.toString(start));

                }
            }
            catch (Exception e)
            {
                jobStatus.error(e);
                logger.error("Error in account sync", e);
            }

            jobStatus.stop();
        }
        else
        {
            logger.info("The {} job sync worker is currently disabled.", accountType);
        }
    }

}
