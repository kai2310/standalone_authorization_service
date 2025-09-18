package com.rubicon.platform.authorization.service.v1.ui.translator.converter;

import com.rubicon.platform.authorization.translator.ObjectValueConverter;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;

public class RoleTypeValueConverter implements ObjectValueConverter<RoleTypeEnum, Long>
{
    @Override
    public RoleTypeEnum convertToClientValue(Long persistentValue, TranslationContext context)
    {
        return RoleTypeEnum.getById(persistentValue);
    }

    @Override
    public Long convertToPersistentValue(RoleTypeEnum clientValue, TranslationContext context)
    {
        return clientValue.getRoleTypeEnumId();
    }
}

