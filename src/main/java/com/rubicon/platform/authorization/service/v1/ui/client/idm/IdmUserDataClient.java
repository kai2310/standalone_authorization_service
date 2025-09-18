package com.rubicon.platform.authorization.service.v1.ui.client.idm;

import com.dottydingo.hyperion.api.ApiObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.model.data.idm.User;
import com.rubicon.platform.authorization.service.client.BaseClient;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class IdmUserDataClient extends BaseClient
{
    public IdmUserDataClient(String baseUrl)
    {
        super(baseUrl);
    }

    public User getUserById(Long userId, DataUserContext userContext)
    {
        EntityResponse<User> entityResponse = performIdmDataUserList(1, 1, "id==" + userId, null, userContext);

        return getSingleItemFromEntityResponse(entityResponse);
    }


    public EntityResponse<User> getUsers(
            Integer start, Integer limit, String query, String sort, DataUserContext userContext)
    {
        return performIdmDataUserList(start, limit, query, sort, userContext);
    }


    private EntityResponse<User> performIdmDataUserList(
            Integer start, Integer limit, String query, String sort, DataUserContext userContext)
    {
        String requestUrl = baseUrl.concat("User/");

        Map<String, Object> queryParams = new HashMap<>();
        try
        {
            queryParams.put("query", URLEncoder.encode(query, StandardCharsets.UTF_8.name()));
        }
        catch (Exception e)
        {
            // in case we are unable to encode the query, we log a warning and use the original query
            logger.warn("Error encoding query parameter: {} due to {}", query, e.getMessage());
            queryParams.put("query", query);
        }

        if(StringUtils.isNotEmpty(sort))
        {
            queryParams.put("sort", sort);
        }
        queryParams.put("version", "1");
        queryParams.put("fields", "id,username,status,userType");
        queryParams.put("access_token", userContext.getAccessToken());
        queryParams.put("cid", userContext.getCorrelationId());
        queryParams.put("start", start);
        queryParams.put("limit", limit);

        requestUrl += appendQueryParams(queryParams);

        return getObject(requestUrl, new TypeReference<EntityResponse<User>>() {});
    }

    protected <T extends ApiObject> T getSingleItemFromEntityResponse(EntityResponse<T> entityResponse)
    {
        return (entityResponse.getEntries() != null && entityResponse.getEntries().size() == 1)
               ?
               entityResponse.getEntries().get(0)
               : null;
    }
}
