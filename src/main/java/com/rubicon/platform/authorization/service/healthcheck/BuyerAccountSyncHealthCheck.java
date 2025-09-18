package com.rubicon.platform.authorization.service.healthcheck;

import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import com.rubicon.platform.authorization.service.utils.MetricUtils;

public class BuyerAccountSyncHealthCheck extends AccountSyncHealthCheckBase
{
    public BuyerAccountSyncHealthCheck(HealthCheckRegistryHolder healthCheckRegistryHolder, MetricUtils metricUtils)
    {
        super(healthCheckRegistryHolder, metricUtils, ACCOUNT_TYPE_BUYER);
    }

}
