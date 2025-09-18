package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.rubicon.platform.authorization.service.exception.ServiceUnavailableException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@RequestMapping(value = "/v1/authorization/admin")
public class AdminServiceController extends BaseUIController
{

    @RequestMapping(value = "/disable-permissions", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> disablePermissions(final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {

                if (canPermissionsBeDisabled())
                {
                    disableUserPermission(httpServletRequest);
                }
                else
                {
                    throw new ServiceUnavailableException("The service is currently unavailable.");
                }

                return new HttpEntity<>(HttpStatus.OK);
            }
        };

        return submit(callable, getTimer("v1.admin.disable-permissions"), httpServletRequest);
    }

}
