package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.converter.OperationValueConverter;
import com.rubicon.platform.authorization.service.v1.ui.translator.converter.RoleTypeValueConverter;
import com.rubicon.platform.authorization.translator.DefaultObjectFieldMapper;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.Role;

import java.util.ArrayList;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION;

public class RoleTranslator
        extends DefaultObjectTranslator<Role, com.rubicon.platform.authorization.model.data.acm.Role>
{
    public RoleTranslator()
    {
        super(Role.class, com.rubicon.platform.authorization.model.data.acm.Role.class);
    }

    @Override
    public Role convertPersistent(com.rubicon.platform.authorization.model.data.acm.Role persistent,
                                  TranslationContext translationContext)
    {
        Role role = super.convertPersistent(persistent, translationContext);
        RoleTypePermission roleTypePermission =
                (RoleTypePermission) translationContext.getContextItem(TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION);

        role.setEditable(roleTypePermission.isRoleTypeEditable(role.getType()));

        return role;
    }

    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultObjectFieldMapper("name", "label", null));
        mappers.add(
                new DefaultObjectFieldMapper("allowedOperations", "allowedOperations", new OperationValueConverter()));
        mappers.add(
                new DefaultObjectFieldMapper("deniedOperations", "deniedOperations", new OperationValueConverter()));
        mappers.add(new DefaultObjectFieldMapper("type", "roleTypeId", new RoleTypeValueConverter()));

        return mappers;
    }

}
