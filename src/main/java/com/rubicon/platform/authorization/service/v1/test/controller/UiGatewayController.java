package com.rubicon.platform.authorization.service.v1.test.controller;

import com.rubicon.platform.authorization.service.AuthorizationInterceptor;
import com.rubicon.platform.authorization.service.BaseController;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.exception.UnauthorizedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@RequestMapping(value = "/v1/ui-gateway")
public class UiGatewayController extends BaseController
{
    @Override
    protected String getEndpointName()
    {
        return null; // not used
    }

    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<HttpEntity> testGet(final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthenticated(httpServletRequest);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        };
        return submit(callable, null, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<HttpEntity> testPost(final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthenticated(httpServletRequest);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        };
        return submit(callable, null, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public DeferredResult<HttpEntity> testPut(final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthenticated(httpServletRequest);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        };
        return submit(callable, null, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public DeferredResult<HttpEntity> testDelete(final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthenticated(httpServletRequest);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        };
        return submit(callable, null, httpServletRequest);
    }

    private void assertAuthenticated(HttpServletRequest httpServletRequest)
    {
        Object userInfo = httpServletRequest.getAttribute(AuthorizationInterceptor.USER_INFO);
        if (!(userInfo instanceof UserSelf))
        {
            throw new UnauthorizedException("Missing or invalid access/user token.");
        }
    }
}

