package com.rubicon.platform.authorization.service.jobs;

import com.codahale.metrics.Counter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DataMarketplaceVendorRetriever extends RevvRetriever<RevvAccountResponse>
{
    @Override
    public RevvAccountResponse retrieve(long since, String status)
    {
        List<String> queryParams = new ArrayList<>();
        if (since > 0)
        {
            // convert date format to be same as format in publisher api model
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            queryParams.add(String.format("updated=gt=%s", dateFormatter.format(new Date(since))));
        }

        if (status != null)
        {
            queryParams.add(String.format("status=in=(%s)", status));
        }

        // set show_deleted flag and add page param in case there more way more accounts to sync
        StringBuilder query = new StringBuilder("");
        if (queryParams.size() > 0)
        {
            // append query parameters
            query.append("query=".concat(StringUtils.join(queryParams, ";")));
        }

        String requestUrl = baseUrl + "?" + query.toString();

        RevvAccountResponse response = null;
        Counter counter = metricUtils.getCounter(accountType, "accountsync");
        ObjectMapper objectMapper = new ObjectMapper();
        List<DataMarketplaceVendor> marketplaceVendors = new ArrayList<>();
        try
        {
            // set page filter
            JsonNode jsonNode = restTemplate.getForObject(requestUrl, JsonNode.class);

            // convert into list of publishers
            marketplaceVendors.addAll((List<DataMarketplaceVendor>) objectMapper.reader(new TypeReference<List<DataMarketplaceVendor>>()
            {
            }).readValue(jsonNode.get("content")));

            // convert publishers into revv account response
            DataMarketplaceVendorResponseConverter converter = new DataMarketplaceVendorResponseConverter();
            response = converter.convertToPersistentValue(marketplaceVendors, new TranslationContext());

            // Reset the counter to zero after a successful run
            counter.dec(counter.getCount());
        }
        catch (Exception e)
        {
            counter.inc();
            logger.warn(String.format("Error calling Marketplace Data API for %s", baseUrl), e);
        }

        return response;
    }
}
