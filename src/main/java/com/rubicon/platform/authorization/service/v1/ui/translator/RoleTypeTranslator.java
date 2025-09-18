package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.translator.DefaultObjectFieldMapper;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.RoleType;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION;

public class RoleTypeTranslator
        extends DefaultObjectTranslator<RoleType, com.rubicon.platform.authorization.model.data.acm.RoleType>
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    public RoleTypeTranslator()
    {
        super(RoleType.class, com.rubicon.platform.authorization.model.data.acm.RoleType.class);
    }


    @Override
    public List<RoleType> convertPersistent(List<com.rubicon.platform.authorization.model.data.acm.RoleType> persistentList,
                                            TranslationContext translationContext)
    {
        List<RoleType> list = new LinkedList<>();
        for (com.rubicon.platform.authorization.model.data.acm.RoleType persistent : persistentList)
        {
            RoleType roleType = convertPersistent(persistent, translationContext);

            if (roleType != null)
            {
                list.add(convertPersistent(persistent, translationContext));
            }
        }

        return list;
    }

    @Override
    public RoleType convertPersistent(com.rubicon.platform.authorization.model.data.acm.RoleType persistent,
                                      TranslationContext translationContext)
    {
        RoleType roleType = super.convertPersistent(persistent, translationContext);
        boolean validRoleType = false;
        RoleTypePermission roleTypePermission =
                (RoleTypePermission) translationContext.getContextItem(TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION);

        RoleTypeEnum roleTypeEnum = RoleTypeEnum.getById(persistent.getId());
        if (null == roleTypeEnum)
        {
            logger.warn(String.format("A Role Type Id %d, that can not be translated was found", persistent.getId()));
        }
        else
        {
            validRoleType = true;
            roleType.setId(roleTypeEnum);
        }

        // If the user can't view the role type, set the value to null.
        if (!validRoleType || !roleTypePermission.isRoleTypeViewable(roleType.getId()))
        {
            roleType = null;
        }

        return roleType;
    }

    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        
        mappers.add(new DefaultObjectFieldMapper("name", "label", null));

        return mappers;
    }

}
