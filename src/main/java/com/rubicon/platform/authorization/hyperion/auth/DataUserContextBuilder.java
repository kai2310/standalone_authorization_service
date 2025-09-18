package com.rubicon.platform.authorization.hyperion.auth;

import com.dottydingo.hyperion.core.endpoint.HyperionContext;
import com.dottydingo.service.endpoint.context.UserContext;
import com.dottydingo.service.endpoint.context.UserContextBuilder;
import com.rubicon.platform.authorization.service.cache.CacheHelper;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import net.sf.ehcache.Ehcache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class DataUserContextBuilder implements UserContextBuilder<UserContext,HyperionContext>
{
    private static final String ACCESS_TOKEN = "access_token";
    private Logger logger = LoggerFactory.getLogger(com.rubicon.platform.authorization.hyperion.auth.DataUserContextBuilder.class);

    private final UserContext noUserContext;
    private Ehcache userCache;

    public void setUserCache(Ehcache userCache)
    {
        this.userCache = userCache;
    }

    public DataUserContextBuilder()
    {
        noUserContext = new UserContext();
        noUserContext.setUserId("");
        noUserContext.setUserName("");
    }


    @Override
    public UserContext buildUserContext(HyperionContext context)
    {
        String accessToken = context.getEndpointRequest().getFirstParameter(ACCESS_TOKEN);
        if(StringUtils.isEmpty(accessToken))
        {
            logger.info("Missing access token in request.");
            return noUserContext;
        }

        UserSelf userSelf = CacheHelper.get(userCache, accessToken);
        if(userSelf == null)
        {
            logger.info("Missing or invalid token in request.");
            return noUserContext;
        }

        return new DataUserContext(userSelf,accessToken,context.getCorrelationId());
    }
}
