package com.rubicon.platform.authorization.hyperion.marshalling;

import com.dottydingo.hyperion.core.endpoint.marshall.EndpointMarshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public class AfterburnerMarshaller extends EndpointMarshaller
{
    @Override
    protected void configureObjectMapper(ObjectMapper objectMapper)
    {
        objectMapper.registerModule(new AfterburnerModule());
    }
}
