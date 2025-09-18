package com.rubicon.platform.authorization.service.healthcheck;

import com.codahale.metrics.Counter;
import com.codahale.metrics.health.HealthCheck;
import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import com.rubicon.platform.authorization.service.utils.MetricUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;

abstract class AccountSyncHealthCheckBase extends HealthCheck
{
    protected static final long CONSECUTIVE_ERROR_COUNT = 5;
    protected static final String ACCOUNT_TYPE_PUBLISHER = "publisher";
    protected static final String ACCOUNT_TYPE_NETWORK = "network";
    protected static final String ACCOUNT_TYPE_SEAT = "seat";
    protected static final String ACCOUNT_TYPE_BUYER = "buyer";
    protected static final String JOBTYPE = "accountsync";

    private MetricUtils metricUtils;
    private HealthCheckRegistryHolder healthCheckRegistryHolder;
    private String accountType;


    public MetricUtils getMetricUtils()
    {
        return metricUtils;
    }

    public void setMetricUtils(MetricUtils metricUtils)
    {
        this.metricUtils = metricUtils;
    }

    public HealthCheckRegistryHolder getHealthCheckRegistryHolder()
    {
        return healthCheckRegistryHolder;
    }

    public void setHealthCheckRegistryHolder(
            HealthCheckRegistryHolder healthCheckRegistryHolder)
    {
        this.healthCheckRegistryHolder = healthCheckRegistryHolder;
    }

    public AccountSyncHealthCheckBase(HealthCheckRegistryHolder healthCheckRegistryHolder, MetricUtils metricUtils,
                                      String accountType)
    {
        super();
        this.healthCheckRegistryHolder = healthCheckRegistryHolder;
        this.metricUtils = metricUtils;
        this.accountType = accountType;
    }

    @Override
    protected Result check() throws Exception
    {
        Counter counter = getMetricUtils().getCounter(accountType, JOBTYPE);
        HealthCheck.Result healthCheck = null;
        if (counter.getCount() >= CONSECUTIVE_ERROR_COUNT)
        {
            String unhealthyString =
                    StringUtils.capitalize(accountType) + " account sync has failed at least " +
                    CONSECUTIVE_ERROR_COUNT + " consecutive attempts";

            healthCheck =
                    HealthCheck.Result.unhealthy(unhealthyString);
        }
        else
        {
            healthCheck = HealthCheck.Result.healthy();
        }

        return healthCheck;
    }


    @PostConstruct
    public void addToRegistry()
    {
        getHealthCheckRegistryHolder().getInstance().getRegistry().register(this.getClass().getSimpleName(), this);
    }

}
