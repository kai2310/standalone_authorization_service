package com.rubicon.platform.authorization.service.client.model;

import com.rubicon.platform.authorization.service.utils.Constants;

import static com.rubicon.platform.authorization.service.utils.Constants.ACCOUNT_TYPE_BUYER;

public class UnifiedLoginUserInfo
{
    private String email;
    private String contextType;
    private String contextId;

    public UnifiedLoginUserInfo()
    {

    }

    public UnifiedLoginUserInfo(String platform, String email, String contextType, String contextId)
    {
        this.email = email;
        this.contextType = mapContextType(platform, contextType);
        this.contextId = contextId;
    }

    // map the context type from streaming platform or dv+
    // if it is `Seat`/'Buyer' in streaming platform, map it to `streaming-seat`/`streaming-buyer`
    // if it is buyer in the dv+ platform, map it to seat
    protected String mapContextType(String platform, String contextType)
    {
        String mappedContextType = contextType;
        if (contextType != null)
        {
            if (Constants.MAGNITE_STREAMING_PLATFORM.equals(platform))
            {
                mappedContextType =
                        Constants.MAGNITE_STREAMING_PLATFORM.toLowerCase().concat("-")
                                .concat(contextType.toLowerCase());
            }
            else if (Constants.MAGNITE_DV_PLUS.equals(platform) && ACCOUNT_TYPE_BUYER.equals(contextType))
            {
                mappedContextType = Constants.ACCOUNT_TYPE_SEAT;
            }
        }

        return mappedContextType;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setContextType(String contextType)
    {
        this.contextType = contextType;
    }

    public void setContextId(String contextId)
    {
        this.contextId = contextId;
    }

    public String getEmail()
    {
        return email;
    }

    public String getContextType()
    {
        return contextType;
    }

    public String getContextId()
    {
        return contextId;
    }
}
