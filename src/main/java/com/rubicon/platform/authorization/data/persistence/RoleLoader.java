package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class RoleLoader extends StatusLoader<PersistentRole>
{
    public static final String ROLE_CHECK_QUERY = "select count(*) from role_assignments where role_id = :id and status='ACTIVE'";

    @Override
    protected Class<PersistentRole> getEntityClass()
    {
        return PersistentRole.class;
    }

    public boolean hasReferences(Long id)
    {
        return hasReferences(ROLE_CHECK_QUERY, id) ;

    }
}
