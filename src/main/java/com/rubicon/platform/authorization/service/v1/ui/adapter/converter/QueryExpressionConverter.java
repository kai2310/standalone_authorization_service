package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter.*;

public class QueryExpressionConverter
{
    private boolean argumentsQuoted;

    public QueryExpressionConverter()
    {
        this(true);
    }

    public QueryExpressionConverter(boolean argumentsQuoted)
    {
        this.argumentsQuoted = argumentsQuoted;
    }


    public String convertExpression(String dataField, String apiOperator, List<String> dataArguments)
    {
        String quote = quote();
        String dataArgumentString;
        if (dataArguments.size() > 1)
        {
            dataArgumentString = String.format("(%s%s%s)",
                    quote, StringUtils.join(dataArguments, quote + COMMA + quote), quote);
        }
        else
        {
            dataArgumentString = String.format("%s%s%s", quote, dataArguments.get(0), quote);
        }

        return String.format("%s%s%s", dataField, apiOperator, dataArgumentString);
    }


    protected final String quote()
    {
        return argumentsQuoted ? QUOTE : EMPTY;
    }
}
