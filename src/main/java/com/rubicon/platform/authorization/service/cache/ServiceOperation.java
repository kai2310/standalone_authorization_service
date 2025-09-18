package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.Operation;

import java.util.regex.Pattern;

public class ServiceOperation extends Operation
{
    private  Pattern resourcePattern;
    private  Pattern actionPattern;
    private  boolean wildcardResource;
    private  boolean wildcardAction;

    protected ServiceOperation()
    {
    }

    public ServiceOperation(Operation other)
    {
        super(other.getService(), other.getResource(), other.getAction(), other.getProperties());
        resourcePattern = buildPattern(getResource());
        actionPattern = buildPattern(getAction());
        wildcardResource = getResource().equals("*");
        wildcardAction = getAction().equals("*");
    }

    public boolean matchResource(String resource)
    {
        return resourcePattern.matcher(resource).matches();
    }

    public boolean matchAction(String action)
    {
        return actionPattern.matcher(action).matches();
    }

    public boolean isWildcardResource()
    {
        return wildcardResource;
    }

    public boolean isWildcardAction()
    {
        return wildcardAction;
    }

    private Pattern buildPattern(String text)
    {
        if(text.equals("*"))
            return Pattern.compile(".*");
        else if(text.length() > 4 && text.startsWith("||") && text.endsWith("||"))
            return Pattern.compile(createPattern(text),Pattern.CASE_INSENSITIVE);
        else
            return Pattern.compile(Pattern.quote(text),Pattern.CASE_INSENSITIVE);

    }

    private String createPattern(String text)
    {
        String[] parts = text.split("\\|\\|");
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 1; i < parts.length; i++)
        {
            if(i > 1)
                sb.append("|");
            sb.append(Pattern.quote(parts[i]));

        }

        return sb.toString();
    }
}
