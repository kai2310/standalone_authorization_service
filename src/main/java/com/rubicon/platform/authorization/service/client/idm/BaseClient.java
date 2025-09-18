package com.rubicon.platform.authorization.service.client.idm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubicon.platform.authorization.service.exception.ErrorBean;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.squareup.okhttp.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;

public class BaseClient
{
    public static final com.squareup.okhttp.MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String userAgent = "IdentityManagementClient";
    private static final ObjectMapper DEFAULT_OBJECTMAPPER = new ObjectMapper();
    protected ObjectMapper objectMapper = DEFAULT_OBJECTMAPPER;
    protected OkHttpClient httpClient;
    protected String baseUrl;
    private boolean trustAllCerts = false;

    protected BaseClient(String baseUrl)
    {
        this.baseUrl = baseUrl;
        if(!this.baseUrl.endsWith("/"))
            this.baseUrl += "/";

        // OPA-842 - Simple version tolerance here for any deserialization
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        httpClient = buildOkHttpClient();
    }

    protected com.squareup.okhttp.Response callService(Request request)
    {
        com.squareup.okhttp.Response response;
        try
        {
            response = httpClient.newCall(request).execute();
        }
        catch (IOException e)
        {
            throw new ServiceException(400, "Error calling service.", e);
        }
        return response;
    }

    protected RuntimeException parseErrorResponse(Response response)
    {
        ErrorBean errorBean = null;
        try
        {
            errorBean = objectMapper.readValue(response.body().byteStream(),ErrorBean.class);
        }
        catch (Exception ignore){}
        if(errorBean == null)
            return new ServiceException(response.code(),"An unknown exception has occurred.");

        return new ServiceException(response.code(),errorBean.getMessage());
    }

    protected Headers buildHeaders(String cid)
    {
        Headers.Builder b = new Headers.Builder();
        if (null != cid)
        {
            b.add("RP-CID", cid);
            b.add("RubiconProject-RequestId", cid);
        }
        b.add("user-agent", userAgent);
        return b.build();
    }

    public void setTrustAllCerts(boolean trustAllCerts)
    {
        this.trustAllCerts = trustAllCerts;
        httpClient = buildOkHttpClient();
    }

    private OkHttpClient buildOkHttpClient()
    {
        if(trustAllCerts)
            return getTrustAllClient();
        return new OkHttpClient();
    }

    private OkHttpClient getTrustAllClient()
    {
        try
        {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager()
                    {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws
                                CertificateException
                        {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException
                        {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });

            return okHttpClient;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
