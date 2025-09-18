package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.client.ClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 */
public class RequestFactoryBuilder implements FactoryBean<ClientHttpRequestFactory>
{
    private int concurrentRequestLimit = 10;
    private int socketTimeout = 0;
    private int connectionTimeout = 0;
    private boolean usePoolingClient = true;

    public void setConcurrentRequestLimit(int concurrentRequestLimit)
    {
        this.concurrentRequestLimit = concurrentRequestLimit;
    }

    public void setSocketTimeout(int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    public void setUsePoolingClient(boolean usePoolingClient)
    {
        this.usePoolingClient = usePoolingClient;
    }

    @Override
    public ClientHttpRequestFactory getObject() throws Exception
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
                    (X509HostnameVerifier)hostnameVerifier);

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", 443,socketFactory));


            if(usePoolingClient)
            {
                PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
                connectionManager.setMaxTotal(concurrentRequestLimit);
                connectionManager.setDefaultMaxPerRoute(concurrentRequestLimit);

                DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
                httpClient.setRedirectStrategy(new LaxRedirectStrategy());
                LoggingHttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                        new LoggingHttpComponentsClientHttpRequestFactory(
                                httpClient);
                clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
                clientHttpRequestFactory.setReadTimeout(socketTimeout);
                return clientHttpRequestFactory;
            }
            else
            {
                NonPoolingClientHttpRequestFactory factory = new NonPoolingClientHttpRequestFactory(schemeRegistry);
                factory.setConnectionTimeout(connectionTimeout);
                factory.setReadTimeout(socketTimeout);
                return factory;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating HttpComponentsClientHttpRequestFactory",e);
        }
    }

    @Override
    public Class<?> getObjectType()
    {
        return ClientHttpRequestFactory.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

}
