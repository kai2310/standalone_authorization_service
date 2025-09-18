package com.rubicon.platform.authorization.model.ui.acm;

public enum RoleTypeEnum
{
    buyer(3L),
    internal(1L),
    marketplace_vendor(8L),
    product(6L),
    seller(2L),
    service(4L),
    streaming_buyer(12L),
    streaming_seat(10L);

    private Long roleTypeId;

    public static RoleTypeEnum getById(Long roleTypeId)
    {
        RoleTypeEnum result = null;
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values())
        {
            if (roleTypeEnum.getRoleTypeEnumId().equals(roleTypeId))
            {
                result = roleTypeEnum;
                break;
            }
        }

        return result;
    }


    public Long getRoleTypeEnumId()
    {
        return roleTypeId;
    }

    private RoleTypeEnum(Long roleTypeId)
    {
        this.roleTypeId = roleTypeId;
    }
}