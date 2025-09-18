package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.model.data.lfo.Authorization;
import com.rubicon.platform.authorization.model.data.lfo.PrincipleTypeEnum;
import com.rubicon.platform.authorization.model.data.lfo.ResourceTypeEnum;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AuthenticationClient;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.client.BaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LeftoverDataClient extends BaseClient
{
    protected OAuth2AuthenticationClient authenticationClient;
    private Logger logger = LoggerFactory.getLogger(LeftoverDataClient.class);

    public LeftoverDataClient(String baseUrl,
                              OAuth2AuthenticationClient authenticationClient)
    {
        super(baseUrl);
        this.authenticationClient = authenticationClient;
    }

    public void addAuthorizationEntry(String userId, String resourceId, String resourceType)
    {
        String requestUrl = baseUrl.concat("v1/authorization");

        // append access token
        requestUrl =
                requestUrl.concat(appendAccessTokenQueryParams(authenticationClient.getAccessToken().getAccessToken()));

        EntityList<Authorization> entityList =
                new EntityList<>(Arrays.asList(constructAuthorization(userId, resourceId, resourceType)));

        createObject(requestUrl, entityList, EntityList.class);
    }

    public void deleteAuthorizationEntry(String userId, String resourceId, String resourceType)
    {
        String authorizationDataUrl = baseUrl.concat("v1/authorization");

        String accessToken = authenticationClient.getAccessToken().getAccessToken();
        // append access token
        String requestUrl = authorizationDataUrl.concat(appendAccessTokenQueryParams(accessToken));

        // append filters in query parameter
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put(Constants.REVV_LEFTOVERS_AUTHORIZATION_PRINCIPLE_TYPE_FIELD,
                Constants.REVV_LEFTOVERS_AUTHORIZATION_USER_PRINCIPLE_TYPE);
        filterMap.put(Constants.REVV_LEFTOVERS_AUTHORIZATION_PRINCIPLE_ID_FIELD, userId);
        filterMap.put(Constants.REVV_LEFTOVERS_AUTHORIZATION_RESOURCE_ID_FIELD, resourceId);
        filterMap.put(Constants.REVV_LEFTOVERS_AUTHORIZATION_RESOURCE_TYPE_FIELD, resourceType);

        requestUrl = requestUrl.concat("&").concat(appendQueryFilterParams(filterMap));

        // get existing authorization entry by userId, resourceId and resourceType
        // if not found, just warn a message
        // if found, delete corresponding authorization record
        EntityResponse<Authorization> response =
                getObject(requestUrl, new TypeReference<EntityResponse<Authorization>>()
                {
                });

        Authorization authorization = findMatchedAuthorization(response, resourceId);

        if (authorization != null)
        {
            deleteObject(authorizationDataUrl.concat("/").concat(authorization.getId().toString())
                    .concat(appendAccessTokenQueryParams(accessToken)));
        }
        else
        {
            logger.warn(
                    "Unable to find matching entry in authorization table for principle type: user, principle id: {}, resource id: {}, resource type: {}",
                    userId, resourceId, resourceType);
        }
    }

    // make it public to be covered by unit tests
    public Authorization findMatchedAuthorization(EntityResponse<Authorization> response, String resourceId)
    {
        Authorization authorization = null;
        if (response != null && !CollectionUtils.isEmpty(response.getContent()))
        {
            // when we query `resourceId==*` in revv leftovers,
            // it actually means resourceId can match anything
            // '*' is a reserved keyword in rsql
            // I don't have a good way to escape it to query plain value of wildcard
            // so add a filter here to get resourceId exactly matched after list of item is returned
            if (Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID.equals(resourceId))
            {
                for (Authorization auth : response.getContent())
                {
                    if (auth.getResourceId().equals(resourceId))
                    {
                        authorization = auth;
                        break;
                    }
                }
            }
            // if we query by a specific value, such as '123'
            // no need to filter again
            else
            {
                authorization = response.getContent().get(0);
            }
        }

        return authorization;
    }

    private Authorization constructAuthorization(String userId, String resourceId, String resourceType)
    {
        Authorization authorization = new Authorization();
        authorization.setPrincipleId(Long.valueOf(userId));
        authorization.setPrincipleType(PrincipleTypeEnum.user);
        authorization.setResourceId(resourceId);
        authorization.setResourceType(ResourceTypeEnum.valueOf(resourceType));

        return authorization;
    }
}
