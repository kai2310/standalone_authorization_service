package com.rubicon.platform.authorization.model.ui.acm;

public enum AccountGroupEnum
{
    ALL_PUBLISHERS(1L),
    ALL_SEATS(2L),
    ALL_MARKETPLACE_VENDORS(32L),
    ALL_STREAMING_SEATS(34L),
    ALL_STREAMING_BUYERS(36L);
    private Long accountGroupId;


    public Long getAccountGroupEnumId()
    {
        return accountGroupId;
    }

    private AccountGroupEnum(Long accountGroupId)
    {
        this.accountGroupId = accountGroupId;
    }
}
