package com.rubicon.platform.authorization.service.client.idm;

import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.model.api.idm.UserInfo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 *
 */
public class SelfClient extends BaseClient
{

    public SelfClient(String baseUrl)
    {
        super(baseUrl);
    }

    public UserInfo getSelf(String token)
    {
        return getSelf(token, null);
    }

    public UserInfo getSelf(String token, String cid)
    {
        Request request = new Request.Builder()
                .url(baseUrl + "api/v1/self/getSelf?user_token="+token)
                .headers(buildHeaders(cid))
                .build();

        Response response = callService(request);
        if(!response.isSuccessful())
            throw parseErrorResponse(response);

        try
        {
            return objectMapper.readValue(response.body().byteStream(), UserInfo.class);
        }
        catch (IOException e)
        {
            throw new ServiceException(400, "Error reading response.", e);
        }
    }
}
