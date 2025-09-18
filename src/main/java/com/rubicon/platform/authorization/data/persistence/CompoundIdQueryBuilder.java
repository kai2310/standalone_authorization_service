package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator;
import com.dottydingo.hyperion.jpa.persistence.query.JpaEntityQueryBuilder;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.translator.IdParser;
import org.apache.commons.lang.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.List;

import static com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator.NOT_EQUAL;
import static com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator.NOT_IN;

/**
 */
public class CompoundIdQueryBuilder implements JpaEntityQueryBuilder
{
    private IdParser idParser = IdParser.QUERY_ID_PARSER;
    private String propertyName;

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    @Override
    public Predicate buildPredicate(From root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb,
                                    ComparisonOperator comparisonOperator, List<String> arguments,
                                    PersistenceContext persistenceContext)
    {

        if(arguments.size() != 1)
            throw new BadRequestException("Multiple arguments detected.");

        CompoundId id = idParser.parseId(arguments.get(0));

        Predicate idPredicate = null;
        Predicate typePredicate = null;

        if(StringUtils.isNotEmpty(id.getIdType()))
        {
            idPredicate = determinePredicate(root, cb, comparisonOperator, "idType", id.getIdType());
        }

        if(StringUtils.isNotEmpty(id.getId()))
        {
            typePredicate = determinePredicate(root, cb, comparisonOperator, "id", id.getId());
        }

        Predicate predicate = null;
        if(idPredicate != null && typePredicate != null)
        {
            predicate = cb.and(idPredicate,typePredicate);
        }
        else if(typePredicate != null)
            predicate = typePredicate;
        else
            predicate = idPredicate;

        return predicate;
    }


    protected Predicate determinePredicate(From root, CriteriaBuilder cb, ComparisonOperator comparisonOperator,
                                           String key, String value)
    {
        Predicate predicate = null;
        if (comparisonOperator.equals(NOT_EQUAL) || comparisonOperator.equals(NOT_IN))
        {
            predicate = cb.notEqual(root.get(propertyName).get(key), value);
        }
        else
        {
            predicate = cb.equal(root.get(propertyName).get(key), value);
        }

        return predicate;
    }
}
