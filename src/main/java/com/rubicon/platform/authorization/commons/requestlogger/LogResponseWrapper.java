package com.rubicon.platform.authorization.commons.requestlogger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class LogResponseWrapper extends HttpServletResponseWrapper
{
    private int status = 0;
    private CountingOutputStream os;
    private PrintWriter printWriter;

    public LogResponseWrapper(HttpServletResponse response) throws IOException
    {
        super(response);
        os = new CountingOutputStream(response.getOutputStream());
    }

    public int getStatus()
    {
        return status;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException
    {
        if(status > 0) return;
        super.sendError(sc, msg);
        this.status = sc;
    }

    @Override
    public void sendError(int sc) throws IOException
    {
        if(status > 0) return;
        super.sendError(sc);
        this.status = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException
    {
        super.sendRedirect(location);
        this.status = 302;
    }

    @Override
    public void setStatus(int sc)
    {
        if(status > 0) return;
        super.setStatus(sc);
        this.status = sc;
    }

    @Override
    public void setStatus(int sc, String sm)
    {
        if(status > 0) return;
        super.setStatus(sc, sm);
        this.status = sc;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        return os;
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        if (printWriter == null)
            printWriter = new ResponsePrintWriter(os);

        return printWriter;
    }

    public long getResponseSize()
    {
        return os.getSize();
    }

    private class CountingOutputStream extends ServletOutputStream
    {
        private long size;
        private ServletOutputStream os;
        private  boolean isClosed = false;

        private CountingOutputStream(ServletOutputStream os)
        {
            this.os = os;
        }

        @Override
        public void write(int b) throws IOException
        {
            os.write(b);
            size++;
        }

        public long getSize()
        {
            return size;
        }

        @Override
        public void flush() throws IOException
        {
            os.flush();
        }

        @Override
        public void close() throws IOException
        {
            if(!isClosed)
            {
                os.close();
                isClosed = true;
            }
        }
    }

    private class ResponsePrintWriter extends PrintWriter
    {

        private ResponsePrintWriter(OutputStream os)
        {
            super(os);
        }

        @Override
        public void write(char buf[], int off, int len)
        {
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len)
        {
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c)
        {
            super.write(c);
            super.flush();
        }

    }
}
