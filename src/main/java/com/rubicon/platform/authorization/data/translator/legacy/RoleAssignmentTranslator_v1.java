package com.rubicon.platform.authorization.data.translator.legacy;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultFieldMapper;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.rubicon.platform.authorization.data.api.legacy.RoleAssignment_v1;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.translator.BaseRoleAssignmentTranslator;
import com.rubicon.platform.authorization.data.translator.CompoundIdValueConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class RoleAssignmentTranslator_v1 extends BaseRoleAssignmentTranslator<RoleAssignment_v1>
{
    public RoleAssignmentTranslator_v1()
    {
        super(RoleAssignment_v1.class);
    }

    @Autowired
    private LegacyAccountFieldMapper accountFieldMapper;

    protected void setAccountFieldMapper(LegacyAccountFieldMapper accountFieldMapper)
    {
        this.accountFieldMapper = accountFieldMapper;
    }

    @Override
    protected List<FieldMapper> getCustomFieldMappers()
    {
        List<FieldMapper> mappers = new ArrayList<FieldMapper>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(accountFieldMapper);

        return mappers;
    }

    @Override
    protected void convertPersistent(RoleAssignment_v1 client, PersistentRoleAssignment persistent,
                                     PersistenceContext context)
    {
        super.convertPersistent(client, persistent, context);
        if(persistent.getAccountGroupId().equals(EMPTY_ACCOUNT_GROUP))
            client.setAccountGroupId(null);

    }
}
