package com.rubicon.platform.authorization.service;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.endpoint.pipeline.auth.NoOpAuthorizationContext;
import com.dottydingo.hyperion.core.message.HyperionMessageSource;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.registry.ApiVersionPlugin;
import com.dottydingo.hyperion.core.registry.ApiVersionRegistry;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.dottydingo.hyperion.core.registry.ServiceRegistry;
import com.dottydingo.service.endpoint.context.UserContext;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class NoOpPersistenceContextFactory
{
    private final String ENTITY_PLUGIN = "entityPlugin";
    private final String API_VERSION = "apiVersion";
    private final String API_PLUGIN_VERSION = "apiVersionPlugin";

    @Autowired
    @Qualifier("hyperionServiceRegistry")
    private ServiceRegistry serviceRegistry;

    @Autowired
    private HyperionMessageSource hyperionMessageSource;

    public PersistenceContext createPersistenceContext(String hyperionEndpointName, HttpMethod httpMethod, HttpServletRequest httpServletRequest)
    {
        PersistenceContext persistenceContext = new PersistenceContext();

        // in event change listener, different action may be performed based on different http method, such as cacheChangeListener
        // pass in the correct http method, instead of getting from http servlet request because they may be different
        // to make sure entity change events are processed correctly.
        persistenceContext.setHttpMethod(httpMethod);
        persistenceContext.setEntity(hyperionEndpointName);

        Map<String, Object> entityPluginData = getEntityPluginData(hyperionEndpointName);

        persistenceContext.setEntityPlugin((EntityPlugin) entityPluginData.get(ENTITY_PLUGIN));
        persistenceContext.setApiVersionPlugin((ApiVersionPlugin) entityPluginData.get(API_PLUGIN_VERSION));
        persistenceContext.setMessageSource(hyperionMessageSource);

        UserSelf userSelf = (UserSelf) httpServletRequest.getAttribute(AuthorizationInterceptor.USER_INFO);

        // since we are using NoOpAuthorizationContext, which returns true all the time
        // it won't matter if there is access token or not
        UserContext userContext = new DataUserContext(userSelf, httpServletRequest.getParameter(AuthorizationInterceptor.ACCESS_TOKEN), MDC.get(RequestLogFilter.CID));

        NoOpAuthorizationContext authContext = new NoOpAuthorizationContext();

        persistenceContext.setAuthorizationContext(authContext);
        persistenceContext.setUserContext(userContext);
        return persistenceContext;
    }

    private Map<String, Object> getEntityPluginData(String hyperionEndpointName)
    {
        Map<String, Object> entityPluginData = new HashMap<>();

        EntityPlugin entityPlugin = serviceRegistry.getPluginForName(hyperionEndpointName);
        ApiVersionRegistry apiVersionRegistry = entityPlugin.getApiVersionRegistry();
        Integer apiVersion = apiVersionRegistry.getLatestVersion();
        ApiVersionPlugin apiVersionPlugin = apiVersionRegistry.getPluginForVersion(apiVersion);

        entityPluginData.put(ENTITY_PLUGIN, entityPlugin);
        entityPluginData.put(API_VERSION, apiVersion);
        entityPluginData.put(API_PLUGIN_VERSION, apiVersionPlugin);

        return entityPluginData;
    }
}
