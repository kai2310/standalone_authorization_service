package com.rubicon.platform.authorization.service.utils;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricUtils
{
    private ConcurrentMap<String, Counter> counterMap = new ConcurrentHashMap<String, Counter>();
    private MetricRegistry metricRegistry;

    public void setMetricRegistry(MetricRegistry metricRegistry)
    {
        this.metricRegistry = metricRegistry;
    }

    // At some point we probably wanna make this more generic so we can use it in multiple places.
    public Counter getCounter(String action, String resourceName)
    {
        String key = String.format("job.worker.%s_%s", resourceName, action.toLowerCase());

        Counter counter = counterMap.get(key);
        if (counter == null)
        {
            counter = metricRegistry.counter(MetricRegistry.name("job", "worker", resourceName, action));
            Counter previous = counterMap.putIfAbsent(key, counter);
            if (previous != null)
            {
                counter = previous;
            }
        }

        return counter;
    }

}
