package com.rubicon.platform.authorization.service.healthcheck;

import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import com.rubicon.platform.authorization.service.utils.MetricUtils;

public class NetworkAccountSyncHealthCheck extends AccountSyncHealthCheckBase
{
    public NetworkAccountSyncHealthCheck(HealthCheckRegistryHolder healthCheckRegistryHolder, MetricUtils metricUtils)
    {
        super(healthCheckRegistryHolder, metricUtils, ACCOUNT_TYPE_NETWORK);
    }

}
