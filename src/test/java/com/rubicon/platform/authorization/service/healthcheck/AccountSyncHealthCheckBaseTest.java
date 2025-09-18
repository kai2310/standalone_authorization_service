package com.rubicon.platform.authorization.service.healthcheck;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import com.rubicon.platform.authorization.service.utils.MetricUtils;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(DataProviderRunner.class)
public class AccountSyncHealthCheckBaseTest extends Assert
{

    protected MetricUtils metricUtils;

    @Mock
    protected MetricRegistry metricRegistry;

    @Mock
    private HealthCheckRegistryHolder healthCheckRegistryHolder;

    @DataProvider
    public static Object[][] getCheckWithHealthyResult()
    {
        return new Object[][]{
                {1, true, "the healthy check has failed"},
                {2000, false, "the unhealthy check failed"}
        };
    }

    @Test
    @UseDataProvider("getCheckWithHealthyResult")
    public void testCheckWithHealthyResult(long counterValue, boolean isHealthy, String errorMessage)
    {
        // Build the metric registry
        Counter counterMetric = new Counter();
        counterMetric.inc(counterValue);

        metricRegistry = spy(new MetricRegistry());
        doReturn(counterMetric).when(metricRegistry).counter(anyString());

        // Build the Metric Utils
        metricUtils = new MetricUtils();
        metricUtils.setMetricRegistry(metricRegistry);

        // Build the Health Registry holder
        HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
        healthCheckRegistryHolder = new HealthCheckRegistryHolder(healthCheckRegistry);

        TestAccountSyncCheck testAccountSyncCheck = new TestAccountSyncCheck(healthCheckRegistryHolder, metricUtils);

        // Test The Health
        HealthCheck.Result result = testAccountSyncCheck.execute();
        assertThat(errorMessage, result.isHealthy(), equalTo(isHealthy));
    }

    protected class TestAccountSyncCheck extends AccountSyncHealthCheckBase
    {
        public TestAccountSyncCheck(HealthCheckRegistryHolder healthCheckRegistryHolder, MetricUtils metricUtils)
        {
            super(healthCheckRegistryHolder, metricUtils, "someTestAccountType");
        }
    }

}
