package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 */
public abstract class AbstractClient
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected MessageConverter messageConverter;
    private SchemeRegistry schemeRegistry;
    private int socketTimeoutSeconds;
    private int connectionTimeoutSeconds;

    protected AbstractClient(int socketTimeoutSeconds,
                             int connectionTimeoutSeconds)
    {
        schemeRegistry = buildSchemeRegistry();
        messageConverter = new MessageConverterFactory().createMessageConverter();
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        this.socketTimeoutSeconds = socketTimeoutSeconds;
    }


    protected SchemeRegistry buildSchemeRegistry()
    {
        try
        {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustStrategy()
            {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                    return true;
                }
            },
                    (X509HostnameVerifier) hostnameVerifier);

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", 443, socketFactory));
            return schemeRegistry;

        }
        catch (Exception e)
        {
            throw new RevvClientException("Error creating client", e);
        }
    }

    protected <T> T readResponse(HttpResponse response, Class<T> type) throws IOException
    {
        InputStream is = response.getEntity().getContent();
        if (logger.isDebugEnabled())
        {
            byte[] bytes = IOUtils.copyToByteArray(is);
            logger.debug("Response body: {}", new String(bytes));
            is = new ByteArrayInputStream(bytes);
        }

        return messageConverter.read(is, type);
    }

    protected HttpResponse executeRequest(HttpUriRequest request) throws IOException
    {
        return executeRequest(request, null);
    }

    protected HttpResponse executeRequest(HttpUriRequest request, HttpContext context) throws IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Sending {} to {}", request.getMethod(), request.getURI());
        }

        if (logger.isDebugEnabled())
        {
            if (request instanceof HttpEntityEnclosingRequest)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ((HttpEntityEnclosingRequest) request).getEntity().writeTo(os);
                logger.debug("Sending request body: {}", os.toString());
            }
        }

        return getHttpClient().execute(request, context);
    }

    protected HttpClient getHttpClient()
    {
        BasicClientConnectionManager connectionManager = new BasicClientConnectionManager(schemeRegistry);

        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
        httpClient.getParams()
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeoutSeconds * 1000);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeoutSeconds * 1000);
        httpClient.setRedirectStrategy(new LaxRedirectStrategy());

        return httpClient;
    }
}
