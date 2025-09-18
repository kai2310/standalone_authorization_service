package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;

public class HealthCheckRegistryHolder
{
    private HealthCheckRegistry registry;
    private static HealthCheckRegistryHolder instance;

    public HealthCheckRegistryHolder(HealthCheckRegistry registry)
    {
        this.registry = registry;
        instance = this;
    }

    public HealthCheckRegistry getRegistry()
    {
        return this.registry;
    }

    public static HealthCheckRegistryHolder getInstance()
    {
        return instance;
    }
}
