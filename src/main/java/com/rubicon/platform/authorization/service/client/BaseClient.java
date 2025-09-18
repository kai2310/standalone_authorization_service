package com.rubicon.platform.authorization.service.client;

import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.service.exception.ServiceUnavailableException;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO abstract this class
public class BaseClient extends com.rubicon.platform.authorization.service.client.idm.BaseClient
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Create a new client with the supplied base URL
     *
     * @param baseUrl The base URL
     */
    public BaseClient(String baseUrl)
    {
        super(baseUrl);
    }

    public Response getObject(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(null))
                .build();

        Response response = callService(request);
        if (!response.isSuccessful())
        {
            handleUnsuccessfulError(response);
        }

        return response;
    }

    public Response postObject(String url, Object body)
    {
        String requestBody = null;
        try
        {
            requestBody = objectMapper.writeValueAsString(body);
        }
        catch (JsonProcessingException e)
        {
            throw new BadRequestException("Error reading request body.", e);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .headers(buildHeaders(null))
                .build();


        Response response = callService(request);
        if (!response.isSuccessful())
        {
            handleUnsuccessfulError(response);
        }

        return response;
    }


    public Response postObject(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .headers(buildHeaders(null))
                .build();


        Response response = callService(request);
        if (!response.isSuccessful())
        {
            handleUnsuccessfulError(response);
        }

        return response;
    }

    public <O> O getObject(String url, Class<O> clientClass)
    {
        Response response = getObject(url);

        O jsonObject;
        try
        {
            jsonObject = objectMapper.readValue(response.body().byteStream(), clientClass);
            logger.debug("Body from GET: " + objectMapper.writeValueAsString(jsonObject));

        }
        catch (IOException e)
        {
            throw new BadRequestException("Error reading response.", e);
        }

        if (jsonObject == null)
        {
            throw new ServiceUnavailableException("No response returned from service.");
        }

        return jsonObject;
    }

    public <T> T getObject(String url, TypeReference<T> clientClass)
    {
        Response response = getObject(url);

        T jsonObject;
        try
        {
            jsonObject = objectMapper.readValue(response.body().byteStream(), clientClass);
            logger.debug("Body from GET: " + objectMapper.writeValueAsString(jsonObject));

        }
        catch (IOException e)
        {
            throw new BadRequestException("Error reading response.", e);
        }

        if (jsonObject == null)
        {
            throw new ServiceUnavailableException("No response returned from service.");
        }

        return jsonObject;
    }

    public <O, B> O createObject(String url, B requestBody, Class<O> clientClass)
    {
        RequestBody body;
        try
        {
            body = RequestBody.create(JSON, objectMapper.writeValueAsBytes(requestBody));
            logger.debug("Body for POST: " + objectMapper.writeValueAsString(requestBody));
        }
        catch (JsonProcessingException e)
        {
            // if this happens something is terribly wrong...
            throw new RuntimeException("An unexpected error occurred", e);
        }

        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(null))
                .post(body)
                .build();

        Response response = callService(request);
        if (!response.isSuccessful())
        {
            handleUnsuccessfulError(response);
        }

        try
        {
            O jsonObject = objectMapper.readValue(response.body().byteStream(), clientClass);
            logger.debug("Body from POST: " + objectMapper.writeValueAsString(jsonObject));
            return jsonObject;
        }
        catch (IOException e)
        {
            throw new BadRequestException("Error reading response.", e);
        }

    }

    public Response deleteObject(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(null))
                .delete()
                .build();

        Response response = callService(request);
        if (!response.isSuccessful())
        {
            handleUnsuccessfulError(response);
        }

        return response;
    }


    protected void handleUnsuccessfulError(Response response)
    {
        throw new ServiceException(response.code(), response.message());
    }

    public String appendAccessTokenQueryParams(String accessToken)
    {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(Constants.QUERY_PARAMETER_ACCESS_TOKEN, accessToken);

        return appendQueryParams(queryParams);
    }

    // append query parameters
    public String appendQueryParams(Map<String, Object> queryParamMap)
    {
        String result = "";
        if (queryParamMap != null && queryParamMap.size() > 0)
        {
            result = "?";
            List<String> queryParamElements = new ArrayList<>();
            for (Map.Entry<String, Object> entry : queryParamMap.entrySet())
            {
                queryParamElements.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
            result = result + StringUtils.join(queryParamElements, "&");
        }
        return result;
    }

    // append filter parameter inside "query" parameter
    public String appendQueryFilterParams(Map<String, Object> filterMap)
    {
        String result = "";
        if (filterMap != null && filterMap.size() > 0)
        {
            result = "query=";
            List<String> queryParamElements = new ArrayList<>();
            for (Map.Entry<String, Object> entry : filterMap.entrySet())
            {
                queryParamElements.add(String.format("%s==%s", entry.getKey(), entry.getValue()));
            }
            result = result + StringUtils.join(queryParamElements, ";");
        }

        return result;
    }
}
