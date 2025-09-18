package com.rubicon.platform.authorization.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.client.model.UnifiedLoginUserInfo;
import com.squareup.okhttp.*;

public class Auth0Client extends BaseClient
{
    public Auth0Client(String baseUrl)
    {
        super(baseUrl);
    }

    public UnifiedLoginUserInfo getUserInfo(String token)
    {
        logger.info("sending a request to Auth0 to get user info");
        // to override how we populate headers
        Request request = new Request.Builder()
                .url(this.baseUrl.concat("userinfo"))
                .post(RequestBody
                        .create(MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE), ""))
                .headers(buildRequestHeaders(token))
                .build();


        Response response = callService(request);
        if (!response.isSuccessful())
        {
            // in base client, it calls handleUnsuccessfulError(response)
            // which also throws a service exception
            // override here to have more information on error
            throw new ServiceException(response.code(),
                    "unable to get user info from Auth0 for given token due to: " + response.message());
        }

        UnifiedLoginUserInfo unifiedLoginUserInfo = null;
        try
        {
            // convert response into UnifiedLoginUserInfo model
            JsonNode jsonNode = objectMapper.readValue(response.body().byteStream(), JsonNode.class);
            JsonNode userMetadata = jsonNode.get("user_metadata");
            unifiedLoginUserInfo =
                    new UnifiedLoginUserInfo(userMetadata.get("platform_id").asText(), jsonNode.get("email").asText(),
                            userMetadata.get("context_type").asText(),
                            userMetadata.get("context_id").asText());
        }
        catch (Exception e)
        {
            logger.warn("unable to parse Auth0 response due to {}", e.getMessage());
        }

        return unifiedLoginUserInfo;
    }

    private Headers buildRequestHeaders(String token)
    {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Authorization", "Bearer ".concat(token));
        builder.add("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        return builder.build();
    }
}
