package com.rubicon.platform.authorization.service;

import com.rubicon.platform.authorization.model.api.idm.UserInfo;
import com.rubicon.platform.authorization.service.client.idm.SelfClient;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import com.rubicon.platform.authorization.hyperion.cache.CacheHelper;
import net.sf.ehcache.Ehcache;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter
{
    public static final String USER_INFO = "USER_INFO";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_TOKEN = "user_token";

    @Autowired
    @Qualifier("userCache")
    private Ehcache userCache;

    @Autowired
    private SelfClient selfClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String accessToken = request.getParameter(ACCESS_TOKEN);
        String userToken = request.getParameter(USER_TOKEN);

        UserSelf self = null;
        if(!StringUtils.isEmpty(accessToken))
        {
            self = CacheHelper.get(userCache, accessToken);
        } else if (!StringUtils.isEmpty(userToken))
        {
            UserInfo user = selfClient.getSelf(userToken, MDC.get(RequestLogFilter.CID));
            if (user != null)
            {
                self = new UserSelf();
                self.setId(user.getUserId());
                self.setEmail(user.getEmail());
                self.setUsername(user.getUserName());
                self.setFirstName(user.getFirstName());
                self.setLastName(user.getLastName());
            }
        }

        if (self != null)
        {
            request.setAttribute(USER_INFO, self);
        }

        return true;
    }
}
