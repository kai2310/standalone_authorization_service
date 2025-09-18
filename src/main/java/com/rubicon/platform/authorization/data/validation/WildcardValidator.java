package com.rubicon.platform.authorization.data.validation;

import com.rubicon.platform.authorization.data.model.CompoundId;

/**
*/
public class WildcardValidator
{
    public static final String INVALID_ACCOUNT_TYPE_WILDCARD = "INVALID_ACCOUNT_TYPE_WILDCARD";
    public static final String ACCOUNT_TYPE_WILDCARD_NOT_ALLOWED = "ACCOUNT_TYPE_WILDCARD_NOT_ALLOWED";
    public static final String WILDCARD_NOT_ALLOWED = "WILDCARD_NOT_ALLOWED";

	public static final WildcardValidator fullWildcardAllowed = new WildcardValidator(WildcardType.full,
			"accountType only be \"*\" if accountId is also \"*\".",INVALID_ACCOUNT_TYPE_WILDCARD);
	public static final WildcardValidator idWildcardAllowed = new WildcardValidator(WildcardType.id,
                    "accountType can not be a wildcard.",ACCOUNT_TYPE_WILDCARD_NOT_ALLOWED);
	protected static final WildcardValidator noWildcardAllowed = new WildcardValidator(WildcardType.none,
			"Wildcards are not allowed.",WILDCARD_NOT_ALLOWED);
	private WildcardType wildcardType;
    private String errorMessage;
    private String errorCode;

    WildcardValidator(WildcardType wildcardType, String errorMessage, String errorCode)
    {
        this.wildcardType = wildcardType;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public boolean isValid(CompoundId id)
    {
        boolean isFullWildcard = id.isWildcard();
        boolean isIdWildcard = id.isWildcardId();
        boolean isTypeWildcard = id.isWildcardType();

        switch (wildcardType)
        {
            case full:
                return isFullWildcard || ! isTypeWildcard;
            case id:
                return !isFullWildcard && !isTypeWildcard;
            case none:
                return !isTypeWildcard && !isIdWildcard;
        }

        return false;

    }

	enum WildcardType {full,id,none}
}
