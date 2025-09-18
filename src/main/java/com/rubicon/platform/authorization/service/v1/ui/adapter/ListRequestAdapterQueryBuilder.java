package com.rubicon.platform.authorization.service.v1.ui.adapter;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter.LOGICAL_OPERATOR_AND;
import static com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter.LOGICAL_OPERATOR_OR;

class ListRequestAdapterQueryBuilder
{
    enum NodeType
    {
        logicalAnd, logicalOr, comparison;
    }

    private EndpointSpecification endpointSpecification;
    private boolean suppressExceptions;

    private NodeType nodeType;
    private String comparison;
    private List<ListRequestAdapterQueryBuilder> logicalQueryBuilders = new ArrayList<>();

    ListRequestAdapterQueryBuilder(
            EndpointSpecification endpointSpecification, boolean suppressExceptions)
    {
        this.endpointSpecification = endpointSpecification;
        this.suppressExceptions = suppressExceptions;
    }


    ListRequestAdapterQueryBuilder addLogicalNode(
            NodeType logicalNodeType, ListRequestAdapterQueryBuilder queryBuilder)
    {
        nodeType = logicalNodeType;
        logicalQueryBuilders.add(queryBuilder);
        return this;
    }


    ListRequestAdapterQueryBuilder setComparisonNode(ComparisonNode node)
    {
        nodeType = NodeType.comparison;
        boolean foundMatchingField = false;

        if (endpointSpecification.getValidQuery().contains(node.getSelector()))
        {
            foundMatchingField = true;

            EndpointSpecification.FieldDefinition fieldDefinition =
                    endpointSpecification.getFieldDefinitions().get(node.getSelector());

            List<String> dataArguments = fieldDefinition.getQueryArgumentConverter().convertArguments(
                    node.getOperator(), node.getArguments());

            comparison = fieldDefinition.getQueryExpressionConverter().convertExpression(
                    fieldDefinition.getDataQueryField(), node.getOperator(), dataArguments);
        }

        if (!suppressExceptions && !foundMatchingField)
        {
            throw new BadRequestException(String.format("query cannot contain field %s", node.getSelector()));
        }

        return this;
    }


    String build()
    {
        String result = null;

        switch (nodeType)
        {
            case logicalAnd:
                result = buildLogicalQuery(LOGICAL_OPERATOR_AND);
                break;
            case logicalOr:
                result = buildLogicalQuery(LOGICAL_OPERATOR_OR);
                break;
            case comparison:
                result = comparison;
                break;
        }

        return result;
    }


    private String buildLogicalQuery(String logicalOperator)
    {
        List<String> queryList = new ArrayList<>();
        for (ListRequestAdapterQueryBuilder logicalQueryBuilder : logicalQueryBuilders)
        {
            queryList.add(logicalQueryBuilder.build());
        }

        return String.format("(%s)", StringUtils.join(queryList, logicalOperator));
    }
}
