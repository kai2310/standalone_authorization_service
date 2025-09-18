package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.MetricRegistry;

public class MetricRegistryHolder
{
    private MetricRegistry registry;
    private static MetricRegistryHolder instance;

    public MetricRegistryHolder(MetricRegistry registry)
    {
        this.registry = registry;
        instance = this;
    }

    public static MetricRegistryHolder getInstance()
    {
        return instance;
    }

    public MetricRegistry getRegistry()
    {
        return this.registry;
    }
}
