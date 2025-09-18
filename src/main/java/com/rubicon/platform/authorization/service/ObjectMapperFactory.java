package com.rubicon.platform.authorization.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 */
@Component("serviceObjectMapper")
public class ObjectMapperFactory implements FactoryBean<ObjectMapper>
{
    @Override
    public ObjectMapper getObject() throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new AfterburnerModule());
        return objectMapper;
    }

    @Override
    public Class<?> getObjectType()
    {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
