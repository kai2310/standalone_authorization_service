package com.rubicon.platform.authorization.service.v1.ui.client.publishermanagement;

import com.rubicon.platform.authorization.model.api.pmg.PublisherIdsRequest;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.client.BaseClient;
import com.rubicon.platform.authorization.hyperion.auth.DataUserContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PublisherManagementClient extends BaseClient
{
    public PublisherManagementClient(String baseUrl)
    {
        super(baseUrl);
    }

    public void deleteFinancePublisher(Long publisherId, DataUserContext userContext)
    {
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put(Constants.QUERY_PARAMETER_ACCESS_TOKEN, userContext.getAccessToken());
        queryParams.put("publisherId", publisherId);

        String requestUrl = getBaseFinanceUrl().concat("/remove").concat(appendQueryParams(queryParams));
        deleteObject(requestUrl);
    }

    public void reactivateFinancePublisher(Long publisherId, DataUserContext userContext)
    {
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put(Constants.QUERY_PARAMETER_ACCESS_TOKEN, userContext.getAccessToken());

        PublisherIdsRequest publisherIdsRequest = new PublisherIdsRequest();
        publisherIdsRequest.setPublisherIds(Stream.of(publisherId).collect(Collectors.toList()));


        String requestUrl = getBaseFinanceUrl().concat("/reactivate").concat(appendQueryParams(queryParams));
        postObject(requestUrl, publisherIdsRequest);
    }


    protected String getBaseFinanceUrl()
    {
        return this.baseUrl + "api/v1/finance/publisher";
    }

}
