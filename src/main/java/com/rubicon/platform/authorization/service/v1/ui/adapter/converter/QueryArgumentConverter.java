package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Override to convert the argument values of a
 */
public class QueryArgumentConverter
{
    public final List<String> convertArguments(String apiOperator, List<String> apiArguments)
    {
        List<String> result = new ArrayList<>();

        for (String apiArgument : apiArguments)
        {
            result.add(convertSingleArgument(apiOperator, apiArgument));
        }

        return result;
    }


    public String convertSingleArgument(String apiComparator, String apiArgument)
    {
        return apiArgument;
    }
}
