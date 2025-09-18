package com.rubicon.platform.authorization.commons.requestlogger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogRequestWrapper extends HttpServletRequestWrapper
{
    // the default max request body size to cache is 1M
    private final static int DEFAULT_MAX_REQUEST_BODY_SIZE = 1024 * 1000;
    private CachingInputStream is;
    private BufferedReader reader;

    public LogRequestWrapper(HttpServletRequest request) throws IOException
    {
        this(request,DEFAULT_MAX_REQUEST_BODY_SIZE);
    }

    public LogRequestWrapper(HttpServletRequest request, int maxRequestBodySize) throws IOException
    {
        super(request);
        is = new CachingInputStream(request.getInputStream(),maxRequestBodySize);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return is;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        if(reader == null)
            reader = new BufferedReader(new InputStreamReader(is));

        return reader;
    }

    public String getRequestBody()
    {
        return new String(is.getBufferContents());
    }

    private class CachingInputStream extends ServletInputStream
    {
        private int currentSize = 0;
        private boolean bufferOverflow = false;
        private int maxBufferSize;
        private ServletInputStream is;
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);

        private CachingInputStream(ServletInputStream is,int maxBufferSize)
        {
            this.is = is;
            this.maxBufferSize = maxBufferSize;
        }

        @Override
        public int read() throws IOException
        {
            int value = is.read();
            if(value != -1 && ! bufferOverflow)
            {
                if(currentSize <= maxBufferSize)
                {
                    currentSize++;
                    buffer.write(value);
                }
                else
                {
                    bufferOverflow = true;
                }
            }
            return value;
        }

        public byte[] getBufferContents()
        {
            if(bufferOverflow)
                return new byte[0];

            return buffer.toByteArray();
        }
    }
}
