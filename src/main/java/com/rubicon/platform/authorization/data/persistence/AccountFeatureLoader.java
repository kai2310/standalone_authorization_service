package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class AccountFeatureLoader extends AbstractLoader<PersistentAccountFeature,Long>
{
    public static final String ACCOUNT_FEATURE_QUERY = "select count(*) from account_feature_roles where feature_role_id = :id";

    @Override
    protected Class<PersistentAccountFeature> getEntityClass()
    {
        return PersistentAccountFeature.class;
    }

    public boolean hasReferences(Long id)
    {
        return hasReferences(ACCOUNT_FEATURE_QUERY, id) ;

    }
}
