package com.rubicon.platform.authorization.metrics;

import com.codahale.metrics.graphite.GraphiteSender;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import javax.net.SocketFactory;

public class CustomGraphite implements GraphiteSender {
    private static final Pattern WHITESPACE = Pattern.compile("[-. ]");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private String hostname;
    private int port;
    private InetSocketAddress address;
    private SocketFactory socketFactory;
    private Charset charset;
    private Socket socket;
    private Writer writer;
    private int failures;
    private String prefix;

    public CustomGraphite(String hostname, int port, String prefix) {
        this.hostname = hostname;
        this.port = port;
        this.address = null;
        this.socketFactory = SocketFactory.getDefault();
        this.charset = UTF_8;
        this.prefix = prefix;
    }

    public void connect() throws IllegalStateException, IOException {
        if (this.socket != null) {
            throw new IllegalStateException("Already connected");
        } else {
            InetSocketAddress address = this.address;
            if (address == null) {
                address = new InetSocketAddress(this.hostname, this.port);
            }

            if (address.getAddress() == null) {
                throw new UnknownHostException(address.getHostName());
            } else {
                this.socket = this.socketFactory.createSocket(address.getAddress(), address.getPort());
                this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), this.charset));
            }
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
    }

    public void send(String name, String value, long timestamp) throws IOException {
        try {
            this.writer.write(this.prefix);
            this.writer.write(this.sanitize(name));
            this.writer.write(32);
            this.writer.write(value);
            this.writer.write(32);
            this.writer.write(Long.toString(timestamp));
            this.writer.write(10);
            this.failures = 0;
        } catch (IOException var6) {
            ++this.failures;
            throw var6;
        }
    }

    public int getFailures() {
        return this.failures;
    }

    public void flush() throws IOException {
        this.close();
    }

    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        }

        if (this.socket != null) {
            this.socket.close();
        }

        this.socket = null;
        this.writer = null;
    }

    protected String sanitize(String s) {
        return WHITESPACE.matcher(s).replaceAll("_");
    }
}
