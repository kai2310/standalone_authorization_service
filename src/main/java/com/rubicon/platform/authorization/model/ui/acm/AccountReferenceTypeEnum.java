package com.rubicon.platform.authorization.model.ui.acm;

public enum AccountReferenceTypeEnum
{
    group("group"),
    publisher("publisher"),
    seat("seat"),
    partner("partner"),
    marketplace_vendor("mp-vendor"),
    streaming_seat("streaming-seat"),
    streaming_buyer("streaming-buyer");

    private String idType;

    AccountReferenceTypeEnum(String idType)
    {
        this.idType = idType;
    }

    public String getIdType()
    {
        return idType;
    }

    public static AccountReferenceTypeEnum getByIdType(String idType)
    {
        AccountReferenceTypeEnum accountReferenceType = null;
        for (AccountReferenceTypeEnum type : AccountReferenceTypeEnum.values())
        {
            if (type.getIdType().equals(idType))
            {
                accountReferenceType = type;
            }
        }

        return accountReferenceType;
    }
}
