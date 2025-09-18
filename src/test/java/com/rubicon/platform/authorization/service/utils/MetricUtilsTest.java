package com.rubicon.platform.authorization.service.utils;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class MetricUtilsTest extends Assert
{
    protected static final long COUNTER = 8561;

    @Mock
    MetricRegistry metricRegistry;

    @Test
    public void testGetCounter()
    {
        Counter counter = new Counter();
        counter.inc(COUNTER);

        metricRegistry = spy(new MetricRegistry());
        doReturn(counter).when(metricRegistry).counter(anyString());

        MetricUtils metricUtils = new MetricUtils();
        metricUtils.setMetricRegistry(metricRegistry);

        assertThat(metricUtils.getCounter("blah", "blah").getCount(), equalTo(COUNTER));
    }

}
