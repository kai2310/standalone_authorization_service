package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.graphite.GraphiteSender;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogOnlyGraphite implements GraphiteSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Pattern WHITESPACE = Pattern.compile("[-. ]");
    private String prefix;

    public LogOnlyGraphite(String prefix) {
        this.prefix = prefix;
    }

    public void connect() throws IllegalStateException, IOException {
    }

    public boolean isConnected() {
        return true;
    }

    public void send(String name, String value, long timestamp) throws IOException {
        StringWriter stringWriter = new StringWriter();
        Writer writer = new BufferedWriter(stringWriter);

        try {
            writer.write(this.prefix);
            writer.write(this.sanitize(name));
            writer.write(32);
            writer.write(value);
            writer.write(32);
            writer.write(Long.toString(timestamp));
            writer.write(10);
        } catch (IOException var8) {
            throw var8;
        }

        writer.flush();
        this.logger.info("Graphite Submission: {}", stringWriter.toString());
        writer = null;
    }

    public int getFailures() {
        return 0;
    }

    public void flush() throws IOException {
        this.close();
    }

    public void close() throws IOException {
    }

    protected String sanitize(String s) {
        return WHITESPACE.matcher(s).replaceAll("_");
    }
}
