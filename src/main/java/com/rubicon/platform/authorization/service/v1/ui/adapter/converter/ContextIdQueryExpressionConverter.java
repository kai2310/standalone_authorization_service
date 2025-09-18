package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.v1.ui.resolver.AccountServiceResolver;
import cz.jirutka.rsql.parser.UnknownOperatorException;
import cz.jirutka.rsql.parser.ast.ComparisonOp;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter.LOGICAL_OPERATOR_OR;

public class ContextIdQueryExpressionConverter extends QueryExpressionConverter
{
    private static List<ComparisonOp> SUPPORTED_OPERATIONS = Arrays.asList(ComparisonOp.EQ, ComparisonOp.IN);

    public ContextIdQueryExpressionConverter()
    {
        super(false);
    }

    @Override
    public String convertExpression(String dataField, String apiOperator, List<String> dataArguments)
    {
        try
        {
            if (!SUPPORTED_OPERATIONS.contains(ComparisonOp.parse(apiOperator)))
            {
                throw new BadRequestException("contextId does not support the operator " + apiOperator);
            }
        }
        catch (UnknownOperatorException e)
        {
            throw new BadRequestException(e.getMessage(), e);
        }

        String result;
        List<String> expressions = new ArrayList<>();
        for (String dataArgument : dataArguments)
        {
            for (String contextType : AccountServiceResolver.VALID_CONTEXT_TYPES)
            {
                expressions.add(String.format("%s%s%s/%s", dataField, ComparisonOp.EQ.toString(), contextType, dataArgument));
            }
        }

        result = (expressions.size() > 1)
                 ? String.format("(%s)", StringUtils.join(expressions, LOGICAL_OPERATOR_OR))
                 : expressions.get(0);

        return result;
    }
}
