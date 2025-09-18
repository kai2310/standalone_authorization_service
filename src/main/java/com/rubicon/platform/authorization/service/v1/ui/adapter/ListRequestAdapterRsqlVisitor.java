package com.rubicon.platform.authorization.service.v1.ui.adapter;

import cz.jirutka.rsql.parser.ast.*;

class ListRequestAdapterRsqlVisitor implements RSQLVisitor<ListRequestAdapterQueryBuilder, ListRequestAdapterQueryBuilder>
{
    private EndpointSpecification endpointSpecification;
    private boolean suppressExceptions;

    ListRequestAdapterRsqlVisitor(EndpointSpecification endpointSpecification, boolean suppressExceptions)
    {
        this.endpointSpecification = endpointSpecification;
        this.suppressExceptions = suppressExceptions;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(AndNode node, ListRequestAdapterQueryBuilder builder)
    {
        for (Node child : node)
        {
            builder.addLogicalNode(ListRequestAdapterQueryBuilder.NodeType.logicalAnd,
                    child.accept(this, new ListRequestAdapterQueryBuilder(endpointSpecification, suppressExceptions)));
        }
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(OrNode node, ListRequestAdapterQueryBuilder builder)
    {
        for (Node child : node)
        {
            builder.addLogicalNode(ListRequestAdapterQueryBuilder.NodeType.logicalOr,
                    child.accept(this, new ListRequestAdapterQueryBuilder(endpointSpecification, suppressExceptions)));
        }
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(EqualNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(InNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(GreaterThanOrEqualNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(GreaterThanNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(LessThanOrEqualNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(LessThanNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(NotEqualNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }

    @Override
    public ListRequestAdapterQueryBuilder visit(NotInNode node, ListRequestAdapterQueryBuilder builder)
    {
        builder.setComparisonNode(node);
        return builder;
    }
}
