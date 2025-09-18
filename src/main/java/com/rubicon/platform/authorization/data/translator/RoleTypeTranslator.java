package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.PersistentRoleType;
import com.rubicon.platform.authorization.model.data.acm.RoleType;

/**
 */
public class RoleTypeTranslator extends BaseStatusTranslator<RoleType,PersistentRoleType>
{
    public RoleTypeTranslator()
    {
        super(RoleType.class, PersistentRoleType.class);
    }

}
