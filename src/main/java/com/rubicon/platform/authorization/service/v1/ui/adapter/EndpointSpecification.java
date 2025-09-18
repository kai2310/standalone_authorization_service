package com.rubicon.platform.authorization.service.v1.ui.adapter;

import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryArgumentConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryExpressionConverter;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.SortFieldFormatter;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Defines valid field names for the API Standard query params: query, and sort and various field conversions
 */
public class EndpointSpecification
{
    static class FieldDefinition
    {
        private String dataQueryField;
        private String dataSortField;
        private QueryArgumentConverter queryArgumentConverter;
        private QueryExpressionConverter queryExpressionConverter;

        public FieldDefinition(String dataQueryField,
                               String dataSortField,
                               QueryArgumentConverter queryArgumentConverter,
                               QueryExpressionConverter queryExpressionConverter)
        {
            this.dataQueryField = dataQueryField;
            this.dataSortField = dataSortField;
            this.queryArgumentConverter = queryArgumentConverter;
            this.queryExpressionConverter = queryExpressionConverter;
        }

        public String getDataQueryField()
        {
            return dataQueryField;
        }

        public String getDataSortField()
        {
            return dataSortField;
        }

        public QueryArgumentConverter getQueryArgumentConverter()
        {
            return queryArgumentConverter;
        }

        public QueryExpressionConverter getQueryExpressionConverter()
        {
            return queryExpressionConverter;
        }
    }

    private String endpointName = null;
    private SortFieldFormatter sortFieldFormatter = null;
    private LinkedHashMap<String, FieldDefinition> fieldDefinitions = null;
    private LinkedHashSet<String> validQuery = null;
    private LinkedHashSet<String> validSort = null;

    /**
     * A name to describe the endpoint in diagnostics
     */
    public String getEndpointName()
    {
        return endpointName;
    }

    /**
     * Provides ability for sort fields to be formatted differently per endpoint
     */
    public SortFieldFormatter getSortFieldFormatter()
    {
        return sortFieldFormatter;
    }

    /**
     * A mapping from exposed API field names to underlying data field names
     */
    public LinkedHashMap<String, FieldDefinition> getFieldDefinitions()
    {
        return fieldDefinitions;
    }

    /**
     * The names of the valid values to pass to "query"
     */
    public LinkedHashSet<String> getValidQuery()
    {
        return validQuery;
    }

    /**
     * The names of the valid values to pass to "sort"
     */
    public LinkedHashSet<String> getValidSort()
    {
        return validSort;
    }

    void setEndpointName(String name)
    {
        this.endpointName = name;
    }

    public void setSortFieldFormatter(
            SortFieldFormatter sortFieldFormatter)
    {
        this.sortFieldFormatter = sortFieldFormatter;
    }

    void setFieldDefinitions(LinkedHashMap<String, FieldDefinition> fieldDefinitions)
    {
        this.fieldDefinitions = fieldDefinitions;
    }

    void setValidQuery(LinkedHashSet<String> validQuery)
    {
        this.validQuery = validQuery;
    }

    void setValidSort(LinkedHashSet<String> validSort)
    {
        this.validSort = validSort;
    }
}
