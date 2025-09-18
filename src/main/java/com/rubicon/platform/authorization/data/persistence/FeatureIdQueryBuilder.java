package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.jpa.persistence.query.AbstractEntityJpaQueryBuilder;
import com.dottydingo.hyperion.jpa.persistence.query.ComparisonOperator;
import com.rubicon.platform.authorization.data.model.PersistentAccount;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class FeatureIdQueryBuilder extends AbstractEntityJpaQueryBuilder<PersistentAccount>
{



    @Override
    public Predicate buildPredicate(From root, CriteriaQuery<?> query, CriteriaBuilder cb, ComparisonOperator operator,
                                    List<String> arguments, PersistenceContext context)
    {
        Object parsed = operator.supportsMultipleArguments()
                        ? argumentParser.parse(arguments,Long.class, context)
                        : argumentParser.parse(arguments.get(0),Long.class, context);

        Subquery idQuery = query.subquery(Long.class);
        idQuery.distinct(true);
        Root idRoot = idQuery.from(PersistentAccount.class);
        idQuery.select(idRoot.get("id"));
        SetJoin idFrom = idRoot.joinSet("accountFeatureIds");


        Collection values = null;
        if(parsed instanceof Collection)
            values = (Collection) parsed;
        else
            values = Collections.singletonList(parsed);

        idQuery.where(idFrom.in(values));

        switch (operator)
        {
            case EQUAL:
            case IN:
                return root.get("id").in(idQuery);
            case NOT_EQUAL:
            case NOT_IN:
                return cb.not(root.get("id").in(idQuery));
        }

        throw new BadRequestException("Unsupported operator: " + operator);
    }


}
