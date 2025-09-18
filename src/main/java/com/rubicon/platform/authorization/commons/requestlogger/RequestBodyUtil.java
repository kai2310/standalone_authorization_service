package com.rubicon.platform.authorization.commons.requestlogger;

import java.util.regex.Pattern;

public class RequestBodyUtil
{
    private final Pattern WHITESPACE = Pattern.compile("[\\s]+",Pattern.MULTILINE);
    private final Pattern QUOTE = Pattern.compile("\"",Pattern.MULTILINE);

    public String filterWhitespace(String input)
    {
        if(input == null || input.length()==0)
            return input;

        return WHITESPACE.matcher(input).replaceAll(" ");
    }

    public String escapeQuotes(String input)
    {
        if(input == null || input.length()==0)
            return input;

        return QUOTE.matcher(input).replaceAll("\\\\\"");
    }
}