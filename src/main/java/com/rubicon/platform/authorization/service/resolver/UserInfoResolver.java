package com.rubicon.platform.authorization.service.resolver;

import com.hazelcast.util.StringUtil;
import com.rubicon.platform.authorization.service.client.idm.SelfClient;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.model.data.acm.UserInfo;
import com.rubicon.platform.authorization.hyperion.cache.CacheHelper;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by jlukas on 10/8/15.
 */
@Component
public class UserInfoResolver
{

    @Autowired
    @Qualifier("userCache")
    private Ehcache userCache;

    @Autowired
    private SelfClient selfClient;

    public UserInfo getUserInfo(String userToken, String accessToken, String cid)
    {
        UserInfo userInfo = null;

        if(!StringUtil.isNullOrEmpty(userToken)){
            com.rubicon.platform.authorization.model.api.idm.UserInfo user = selfClient.getSelf(userToken, cid);

            if(user != null)
            {
                userInfo = new UserInfo();
                userInfo.setUserId(user.getUserId());
                userInfo.setEmail(user.getEmail());
                userInfo.setUsername(user.getUserName());
                userInfo.setFirstName(user.getFirstName());
                userInfo.setLastName(user.getLastName());
            }
        }

        if(!StringUtil.isNullOrEmpty(accessToken) && userInfo == null){
            UserSelf self = CacheHelper.get(userCache, accessToken);

            userInfo = setUserInfo(self);
        }

        return userInfo;
    }

    private UserInfo setUserInfo(UserSelf userSelf){
        UserInfo userInfo = new UserInfo();

        if(userSelf != null){
            userInfo.setUserId(userSelf.getId());
            userInfo.setUsername(userSelf.getUsername());
            userInfo.setEmail(userSelf.getEmail());
            userInfo.setFirstName(userSelf.getFirstName());
            userInfo.setLastName(userSelf.getLastName());

            return userInfo;
        }

        return null;
    }
}
