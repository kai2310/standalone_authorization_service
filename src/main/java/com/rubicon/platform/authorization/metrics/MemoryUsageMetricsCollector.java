package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;

public class MemoryUsageMetricsCollector
{
    public MemoryUsageMetricsCollector(MetricRegistry registry)
    {
        registry.register("jvm.memory", new MemoryUsageGaugeSet());
        registry.register("jvm.gc", new GarbageCollectorMetricSet());
    }
}
