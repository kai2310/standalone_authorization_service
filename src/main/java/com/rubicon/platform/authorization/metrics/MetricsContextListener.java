package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class MetricsContextListener extends MetricsServlet.ContextListener {
    public MetricsContextListener() {
    }

    protected MetricRegistry getMetricRegistry() {
        return MetricRegistryHolder.getInstance().getRegistry();
    }

    protected HealthCheckRegistry getHealthCheckRegistry() {
        return HealthCheckRegistryHolder.getInstance().getRegistry();
    }

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ServletContext context = event.getServletContext();
        context.setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, this.getHealthCheckRegistry());
    }
}
