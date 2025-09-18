package com.rubicon.platform.authorization.service.jobs;

import com.codahale.metrics.Counter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubicon.platform.authorization.model.data.pmg.Publisher;
import com.rubicon.platform.authorization.translator.TranslationContext;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PublisherRetriever extends RevvRetriever<RevvAccountResponse>
{
    @Override
    public RevvAccountResponse retrieve(long since, String status)
    {
        List<String> queryParams = new ArrayList<>();
        if (since > 0)
        {
            // convert date format to be same as format in publisher api model
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            queryParams.add(String.format("modified>=%s", dateFormatter.format(new Date(since))));
        }

        if (status != null)
        {
            queryParams.add(String.format("status=in=(%s)", status));
        }

        // set show_deleted flag and add page param in case there more way more accounts to sync
        StringBuilder query = new StringBuilder("show_deleted=true&page=%s");
        if (properties != null)
        {
            query.append(String.format("&fields=%s", properties));
        }

        if (queryParams.size() > 0)
        {
            // append query parameters
            query.append("&query=".concat(StringUtils.join(queryParams, ";")));
        }

        String requestUrl = baseUrl + "?" + query.toString();

        RevvAccountResponse publisherData = null;
        Counter counter = metricUtils.getCounter(accountType, "accountsync");
        ObjectMapper objectMapper = new ObjectMapper();
        int page = 1;
        long totalElements;
        List<Publisher> publishers = new ArrayList<>();
        try
        {
            do
            {
                // set page filter
                JsonNode jsonNode =
                        restTemplate.getForObject(String.format(requestUrl, String.valueOf(page)), JsonNode.class);
                JsonNode pageNode = jsonNode.get("page");
                totalElements = pageNode.get("totalElements").asLong();

                // convert into list of publishers
                publishers.addAll((List<Publisher>) objectMapper.reader(new TypeReference<List<Publisher>>()
                {
                }).readValue(jsonNode.get("content")));
                page++;
            }
            while (publishers.size() < totalElements);

            // convert publishers into revv account response
            PublisherRevvAccountResponseConverter converter = new PublisherRevvAccountResponseConverter();
            publisherData = converter.convertToPersistentValue(publishers, new TranslationContext());

            // Reset the counter to zero after a successful run
            counter.dec(counter.getCount());
        }
        catch (Exception e)
        {
            counter.inc();
            logger.warn(String.format("Error calling Publisher Management API for %s", baseUrl), e);
        }

        return publisherData;
    }
}
