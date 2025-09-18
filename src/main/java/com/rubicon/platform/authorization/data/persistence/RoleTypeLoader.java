package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.PersistentRoleType;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class RoleTypeLoader extends StatusLoader<PersistentRoleType>
{
    public static final String ROLE_TYPE_CHECK_QUERY = "select count(*) from roles where role_type_id = :id and status='ACTIVE'";

    @Override
    protected Class<PersistentRoleType> getEntityClass()
    {
        return PersistentRoleType.class;
    }

    public boolean hasReferences(Long id)
    {
        return hasReferences(ROLE_TYPE_CHECK_QUERY, id) ;

    }
}
