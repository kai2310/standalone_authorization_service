package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.endpoint.pipeline.auth.NoOpAuthorizationContext;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.service.endpoint.context.UserContext;

/**
 */
public class BaseTranslatorFixture
{
    protected PersistenceContext context;

    public void setUp() throws Exception
    {
        context = new PersistenceContext();
        UserContext userContext = new UserContext();
        userContext.setUserId("1");
        userContext.setUserName("name");
        context.setUserContext(userContext);
        context.setAuthorizationContext(new NoOpAuthorizationContext());
    }
}
