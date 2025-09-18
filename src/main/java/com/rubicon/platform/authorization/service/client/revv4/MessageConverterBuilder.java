package com.rubicon.platform.authorization.service.client.revv4;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.ClassUtils;

/**
 */
public class MessageConverterBuilder implements FactoryBean<GenericHttpMessageConverter>
{
    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", MessageConverterBuilder.class.getClassLoader()) &&
            ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", MessageConverterBuilder.class.getClassLoader());

    private static final boolean jacksonPresent =
            ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", MessageConverterBuilder.class.getClassLoader()) &&
            ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", MessageConverterBuilder.class.getClassLoader());

    @Override
    public GenericHttpMessageConverter getObject() throws Exception
    {
        if(jackson2Present)
        {
            return buildJackson2();
        }
        else if(jacksonPresent)
        {
            return buildJackson1();
        }
        else
        {
            throw new RuntimeException("Missing required Jackson dependencies. Either Jackson 1.x or 2.x must be available");
        }
    }

    @Override
    public Class<?> getObjectType()
    {
        return GenericHttpMessageConverter.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    protected GenericHttpMessageConverter<Object> buildJackson2()
    {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        return converter;
    }

    protected GenericHttpMessageConverter<Object> buildJackson1()
    {

        org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}
