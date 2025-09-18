package com.rubicon.platform.authorization.springmvc;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;

public class CORSInterceptor extends DeferredResultProcessingInterceptorAdapter
{
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception
    {
        HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
        response.setHeader("Access-Control-Allow-Origin", "*");
        super.preProcess(request, deferredResult);
    }
}
