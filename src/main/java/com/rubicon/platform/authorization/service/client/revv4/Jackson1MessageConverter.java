package com.rubicon.platform.authorization.service.client.revv4;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class Jackson1MessageConverter implements MessageConverter
{
    private ObjectMapper objectMapper;

    public Jackson1MessageConverter()
    {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    @Override
    public <T> T read(InputStream is, Class<T> type) throws ClientMarshallingException
    {
        try
        {
            return objectMapper.readValue(is,type);
        }
        catch (IOException e)
        {
            throw new ClientMarshallingException("Error unmarshalling data.",e);
        }
    }

    @Override
    public <T> void write(OutputStream os, T type) throws ClientMarshallingException
    {
        try
        {
            objectMapper.writeValue(os,type);
        }
        catch (IOException e)
        {
            throw new ClientMarshallingException("Error marshalling data.",e);
        }
    }
}
