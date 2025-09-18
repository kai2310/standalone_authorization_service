package com.rubicon.platform.authorization.service.v1.ui.adapter;

import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryArgumentConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.SortFieldFormatter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.SortFieldV1Formatter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Use to build an endpoint specification
 */
public class EndpointSpecificationBuilder
{
    private String endpointName;
    private SortFieldFormatter sortFieldFormatter;
    private LinkedHashSet<String> validQuery = null;
    private LinkedHashSet<String> validSort = null;
    private LinkedHashMap<String, EndpointSpecification.FieldDefinition> fieldDefinitions =
            new LinkedHashMap<>();

    public EndpointSpecificationBuilder(String endpointName)
    {
        this(endpointName, new SortFieldV1Formatter());
    }

    public EndpointSpecificationBuilder(String endpointName, SortFieldFormatter sortFieldFormatter)
    {
        this.endpointName = endpointName;
        this.sortFieldFormatter = sortFieldFormatter;
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName)
    {
        return addFieldMapping(apiFieldName, dataQueryFieldName, dataQueryFieldName,
                new QueryArgumentConverter(), new QueryExpressionConverter());
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName,
                                                        QueryArgumentConverter queryArgumentConverter)
    {
        return addFieldMapping(apiFieldName, dataQueryFieldName, dataQueryFieldName,
                queryArgumentConverter, new QueryExpressionConverter());
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName,
                                                        QueryExpressionConverter queryExpressionConverter)
    {
        return addFieldMapping(apiFieldName, dataQueryFieldName, dataQueryFieldName,
                new QueryArgumentConverter(), queryExpressionConverter);
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName,
                                                        String dataSortFieldName,
                                                        QueryExpressionConverter queryExpressionConverter)
    {
        return addFieldMapping(apiFieldName, dataQueryFieldName, dataSortFieldName,
                new QueryArgumentConverter(), queryExpressionConverter);
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName,
                                                        QueryArgumentConverter queryArgumentConverter,
                                                        QueryExpressionConverter queryExpressionConverter)
    {
        return addFieldMapping(apiFieldName, dataQueryFieldName, dataQueryFieldName,
                queryArgumentConverter, queryExpressionConverter);
    }

    public EndpointSpecificationBuilder addFieldMapping(String apiFieldName, String dataQueryFieldName,
                                                        String dataSortFieldName,
                                                        QueryArgumentConverter queryArgumentConverter,
                                                        QueryExpressionConverter queryExpressionConverter)
    {
        fieldDefinitions.put(apiFieldName, new EndpointSpecification.FieldDefinition(
                dataQueryFieldName, dataSortFieldName, queryArgumentConverter, queryExpressionConverter));

        return this;
    }

    public EndpointSpecificationBuilder setValidQuery(String... fields)
    {
        validQuery = new LinkedHashSet<>(Arrays.asList(fields));

        return this;
    }

    public EndpointSpecificationBuilder setValidSort(String... fields)
    {
        validSort = new LinkedHashSet<>(Arrays.asList(fields));

        return this;
    }

    public EndpointSpecification build()
    {
        EndpointSpecification spec = new EndpointSpecification();

        spec.setEndpointName(endpointName);
        spec.setSortFieldFormatter(sortFieldFormatter);
        spec.setFieldDefinitions(fieldDefinitions);
        spec.setValidQuery(validQuery);
        spec.setValidSort(validSort);

        return spec;
    }
}
