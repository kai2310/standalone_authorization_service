package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphiteMetricsReporter
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private MetricRegistry metricRegistry;
    private boolean enable;
    private String host;
    private int port;
    private boolean logOnly = false;

    public GraphiteMetricsReporter() {
    }

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLogOnly(boolean logOnly) {
        this.logOnly = logOnly;
    }

    public void init() throws Exception {
        if (this.enable) {
            InetAddress local = InetAddress.getLocalHost();
            String hostname = local.getHostName();
            this.logger.info("Host Name = \"{}\"", hostname);
            String prefix = this.buildPrefix(hostname);
            this.logger.info("Calculated prefix = \"{}\"", prefix);
            this.logger.info("logOnly={}", this.logOnly);
            prefix = prefix + ".";
            Object graphite;
            if (this.logOnly) {
                this.logger.info("Debug mode, only logging entries.");
                graphite = new LogOnlyGraphite(prefix);
            } else {
                graphite = new CustomGraphite(this.host, this.port, prefix);
            }

            GraphiteReporter
                    reporter = GraphiteReporter.forRegistry(this.metricRegistry).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(
                    MetricFilter.ALL).build((GraphiteSender)graphite);
            reporter.start(1L, TimeUnit.MINUTES);
        } else {
            this.logger.info("Graphite reporting disabled.");
        }

    }

    private String buildPrefix(String host) {
        if (host == null) {
            host = "unknown.rubicorp.com";
        }

        host = host.toLowerCase();
        Matcher m = Pattern.compile("^(\\w{4}-\\w{3}\\d)\\d{3}\\.(\\w+)\\.").matcher(host.toUpperCase());
        String hostgroup;
        if (m.lookingAt()) {
            hostgroup = String.format("%s-%s", m.group(1), m.group(2));
        } else if (host.endsWith("rubicorp.com")) {
            hostgroup = "FRPL-DEV0-SM1";
        } else {
            hostgroup = "UNKN-UNK0-UNK";
        }

        return String.format("metrics.%s.%s", hostgroup, host.replace('.', '_'));
    }
}
