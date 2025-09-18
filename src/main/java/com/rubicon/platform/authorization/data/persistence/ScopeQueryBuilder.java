package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.jpa.persistence.query.AbstractEntityJpaQueryBuilder;
import com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import static com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator.NOT_EQUAL;
import static com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator.NOT_IN;

public class ScopeQueryBuilder extends AbstractEntityJpaQueryBuilder<PersistentRoleAssignment>
{
    @Override
    public Predicate buildPredicate(From root, CriteriaQuery<?> query, CriteriaBuilder cb, ComparisonOperator operator,
                                    List<String> arguments, PersistenceContext context)
    {
        // Confirm we have a valid operator
        switch (operator)
        {
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LESS_EQUAL:
            case LESS_THAN:
                throw new BadRequestException("Unsupported operator: " + operator);
        }

        // Verify that the operator provided supports multiple arguments
        if (!operator.supportsMultipleArguments() && arguments.size() > 1)
        {
            throw new BadRequestException(operator + " does not support multiple arguments");
        }

        List<Predicate> predicates = new ArrayList<>();

        for (String argument : arguments)
        {
            String accountId = "%" + argument + "%";
            // if we have a Not operator, we want to use not like... otherwise we'll use like
            Predicate likePredicate = (operator.equals(NOT_EQUAL) || operator.equals(NOT_IN))
                                      ? cb.notLike(root.get("scope"), accountId)
                                      : cb.like(root.get("scope"), accountId);

            predicates.add(likePredicate);
        }

        Predicate predicate;
        if (operator.equals(NOT_IN))
        {
            predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
        else
        {
            // For Equals and Not Equals, and/or does not matter as there is only one item in the array
            // For IN, or needs to be provided.
            predicate = cb.or(predicates.toArray(new Predicate[predicates.size()]));
        }

        return predicate;
    }
}
