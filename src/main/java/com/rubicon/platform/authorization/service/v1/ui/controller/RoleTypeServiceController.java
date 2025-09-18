package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.resolver.RoleTypeServiceResolver;
import com.rubicon.platform.authorization.model.ui.acm.PagedResponse;
import com.rubicon.platform.authorization.model.ui.acm.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@RequestMapping(value = "/v1/authorization/role-type")
public class RoleTypeServiceController extends BaseUIController
{
    @Autowired
    protected RoleTypeServiceResolver roleTypeServiceResolver;

    private static final String ROLE_TYPE_HYPERION_ENDPOINT_NAME = "RoleType";

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listRoles(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer resultSize,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "list_role_type");

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_TYPE_HYPERION_ENDPOINT_NAME, HttpMethod.GET, httpServletRequest);

                PagedResponse<RoleType> response =
                        roleTypeServiceResolver.getList(pageNumber, resultSize, roleTypePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.role-type.list"), httpServletRequest);
    }


}
