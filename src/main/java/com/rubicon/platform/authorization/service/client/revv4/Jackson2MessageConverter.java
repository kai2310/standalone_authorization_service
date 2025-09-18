package com.rubicon.platform.authorization.service.client.revv4;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 */
public class Jackson2MessageConverter implements MessageConverter
{
    private ObjectMapper objectMapper;

    public Jackson2MessageConverter()
    {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
