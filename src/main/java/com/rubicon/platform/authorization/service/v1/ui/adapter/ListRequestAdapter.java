package com.rubicon.platform.authorization.service.v1.ui.adapter;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Provides the ability to transform the query and sort parameters in a Spring MVC service to a
 * Hyperion data entity that doesn't precisely match.
 */
public class ListRequestAdapter
{
    protected Logger logger = LoggerFactory.getLogger(ListRequestAdapter.class);

    public static final String EMPTY = "";

    public static final String COMMA = ",";
    public static final String MINUS = "-";
    public static final String QUOTE = "'";
    public static final String LOGICAL_OPERATOR_AND = ";";
    public static final String LOGICAL_OPERATOR_OR = ",";

    private EndpointSpecification endpointSpecification;
    private Boolean suppressExceptions;


    public ListRequestAdapter(EndpointSpecification endpointSpecification)
    {
        this(endpointSpecification, Boolean.FALSE);
    }

    public ListRequestAdapter(EndpointSpecification endpointSpecification, Boolean suppressExceptions)
    {
        this.endpointSpecification = endpointSpecification;
        this.suppressExceptions = suppressExceptions;
    }


    public String adaptQuery(String query)
    {
        String result;

        if (StringUtils.isEmpty(query))
        {
            result = EMPTY;
        }
        else
        {
            try
            {
                ListRequestAdapterQueryBuilder queryBuilder =
                        new ListRequestAdapterQueryBuilder(endpointSpecification, suppressExceptions);

                result = new RSQLParser()
                        .parse(query)
                        .accept(new ListRequestAdapterRsqlVisitor(endpointSpecification, suppressExceptions), queryBuilder)
                        .build();
            }
            catch (RSQLParserException e)
            {
                throw new BadRequestException(String.format("Unable to process the supplied query: %s.", query), e);
            }
            catch (com.dottydingo.hyperion.api.exception.BadRequestException e)
            {
                throw new BadRequestException(e.getMessage(), e);
            }
        }

        logger.debug("Adapted {} query '{}' to '{}'", endpointSpecification.getEndpointName(), query, result);

        return result;
    }


    public String adaptSort(String sort)
    {
        String result;

        if (StringUtils.isEmpty(sort))
        {
            result = EMPTY;
        }
        else
        {
            LinkedHashSet<String> requestedSort = new LinkedHashSet<>(Arrays.asList(sort.split(COMMA)));

            LinkedHashSet<String> requestedSortFields = new LinkedHashSet<>();
            for (String sortField : requestedSort)
            {
                requestedSortFields.add(sortField.replaceFirst(MINUS, EMPTY));
            }

            if (!suppressExceptions)
            {
                LinkedHashSet<String> invalidSort = new LinkedHashSet(requestedSortFields);
                invalidSort.removeAll(endpointSpecification.getValidSort());
                if (invalidSort.size() > 0)
                {
                    String message = String.format("Invalid sort parameter attributes %s",
                            StringUtils.join(invalidSort, COMMA));
                    throw new BadRequestException(message);
                }
            }

            Map<String, Boolean> descendingFieldMap = new HashMap<>();
            for (String sortField : requestedSort)
            {
                descendingFieldMap.put(sortField.replaceFirst(MINUS, EMPTY), sortField.startsWith(MINUS));
            }

            List<String> sortElements = new ArrayList<>();
            for (String sortField : intersection(requestedSortFields, endpointSpecification.getValidSort()))
            {
                sortElements.add(
                        endpointSpecification.getSortFieldFormatter().formatSortField(
                                endpointSpecification.getFieldDefinitions().get(sortField).getDataSortField(),
                                descendingFieldMap.get(sortField)));
            }
            result = StringUtils.join(sortElements, COMMA);
        }

        logger.debug("Adapted {} sort '{}' to '{}'", endpointSpecification.getEndpointName(), sort, result);

        return result;
    }


    private LinkedHashSet<String> intersection(LinkedHashSet<String> set1, LinkedHashSet<String> set2)
    {
        LinkedHashSet<String> result = new LinkedHashSet<>(set1);
        result.retainAll(set2);

        return result;
    }
}
