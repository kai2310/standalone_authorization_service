package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;

public class RoleTypeQueryArgumentConverter extends QueryArgumentConverter
{
    @Override
    public String convertSingleArgument(String apiOperator, String apiArgument)
    {
        String result = "";

        RoleTypeEnum roleType;
        try
        {
            roleType = RoleTypeEnum.valueOf(apiArgument);
        }
        catch (IllegalArgumentException e)
        {
            throw new BadRequestException(String.format("'%s' is not a valid role type", apiArgument));
        }
        if (null != roleType)
        {
            result = roleType.getRoleTypeEnumId().toString();
        }

        return result;
    }
}
