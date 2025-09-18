package com.rubicon.platform.authorization.service.client.revv4;

public class MessageConverterFactory
{
    private static final boolean jackson2Present =
            isPresent("com.fasterxml.jackson.databind.ObjectMapper") &&
            isPresent("com.fasterxml.jackson.core.JsonGenerator");

    private static final boolean jacksonPresent =
            isPresent("org.codehaus.jackson.map.ObjectMapper") &&
            isPresent("org.codehaus.jackson.JsonGenerator");


    public MessageConverter createMessageConverter()
    {
        if(jackson2Present)
            return new Jackson2MessageConverter();
        else if(jacksonPresent)
            return new Jackson1MessageConverter();

        throw new RuntimeException("Missing required Jackson dependencies. Either Jackson 1.x or 2.x must be available");
    }

    private static boolean isPresent(String clazz)
    {
        try
        {
            MessageConverterFactory.class.getClassLoader().loadClass(clazz);
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

}
