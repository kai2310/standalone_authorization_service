package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import javax.management.MBeanServer;

public class JmxMetricsReporter
{
    private MetricRegistry metricRegistry;
    private MBeanServer mBeanServer;

    public JmxMetricsReporter() {
    }

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void setmBeanServer(MBeanServer mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    public void init() {
        JmxReporter reporter = JmxReporter.forRegistry(this.metricRegistry).registerWith(this.mBeanServer).build();
        reporter.start();
    }
}
