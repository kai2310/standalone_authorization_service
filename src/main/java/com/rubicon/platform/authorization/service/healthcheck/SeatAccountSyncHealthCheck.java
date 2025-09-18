package com.rubicon.platform.authorization.service.healthcheck;

import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import com.rubicon.platform.authorization.service.utils.MetricUtils;

public class SeatAccountSyncHealthCheck extends AccountSyncHealthCheckBase
{
    public SeatAccountSyncHealthCheck(HealthCheckRegistryHolder healthCheckRegistryHolder, MetricUtils metricUtils)
    {
        super(healthCheckRegistryHolder, metricUtils, ACCOUNT_TYPE_SEAT);
    }
}
