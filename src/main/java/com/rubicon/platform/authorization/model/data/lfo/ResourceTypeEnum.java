package com.rubicon.platform.authorization.model.data.lfo;
public enum ResourceTypeEnum
{
    account("account"),
    seat("seat"),
    marketplace_vendor("mp-vendor");

    private String dbValue;

    ResourceTypeEnum(String dbValue)
    {
        this.dbValue = dbValue;
    }

    public static ResourceTypeEnum getByDbValue(String dbValue)
    {
        ResourceTypeEnum resourceType = null;
        for (ResourceTypeEnum resourceTypeEnum : ResourceTypeEnum.values())
        {
            if (resourceTypeEnum.getDbValue().equals(dbValue))
            {
                resourceType = resourceTypeEnum;
            }
        }

        return resourceType;
    }

    public String getDbValue()
    {
        return dbValue;
    }
}
