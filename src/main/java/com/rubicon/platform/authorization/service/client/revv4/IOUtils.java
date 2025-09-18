package com.rubicon.platform.authorization.service.client.revv4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class IOUtils
{
    private static final int BUFFER_SIZE = 4096;

    public static byte[] copyToByteArray(InputStream is) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(is,os);
        return os.toByteArray();
    }

    public static void copy(InputStream is, OutputStream os) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = is.read(buffer)) != -1)
        {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
    }
}
