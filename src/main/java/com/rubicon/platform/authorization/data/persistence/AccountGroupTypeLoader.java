package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class AccountGroupTypeLoader extends StatusLoader<PersistentAccountGroupType>
{
    private static final String REFERENCED_QUERY = "select count(*) from account_groups where account_group_type_id = :id and status='ACTIVE'";

    @Override
    protected Class<PersistentAccountGroupType> getEntityClass()
    {
        return PersistentAccountGroupType.class;
    }

    public boolean hasReferences(Long id)
    {
        return hasReferences(REFERENCED_QUERY, id) ;

    }
}
