package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class AccountGroupLoader extends StatusLoader<PersistentAccountGroup>
{
    private static final String REFERENCED_QUERY = "select count(*) from role_assignments where account_group_id = :id and status='ACTIVE'";

    @Override
    protected Class<PersistentAccountGroup> getEntityClass()
    {
        return PersistentAccountGroup.class;
    }

    public boolean hasReferences(Long id)
    {
        return hasReferences(REFERENCED_QUERY, id) ;

    }
}
