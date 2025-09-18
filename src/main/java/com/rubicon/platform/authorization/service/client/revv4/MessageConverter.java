package com.rubicon.platform.authorization.service.client.revv4;

import java.io.InputStream;
import java.io.OutputStream;

public interface MessageConverter
{
    <T> T read(InputStream is, Class<T> type) throws ClientMarshallingException;
    <T> void write(OutputStream os, T type) throws ClientMarshallingException;
}
