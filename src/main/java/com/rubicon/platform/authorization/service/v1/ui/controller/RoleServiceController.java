package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.resolver.RoleAssignmentServiceResolver;
import com.rubicon.platform.authorization.service.v1.ui.resolver.RoleServiceResolver;
import com.rubicon.platform.authorization.model.ui.acm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

import static com.rubicon.platform.authorization.service.utils.Constants.REMOVE_ROLE_ASSIGNMENT_ACTION;
import static com.rubicon.platform.authorization.service.utils.Constants.UI_AUTHORIZATION_RESOURCE;

@RequestMapping(value = "/v1/authorization/role")
public class RoleServiceController extends BaseUIController
{
    @Autowired
    protected RoleServiceResolver roleServiceResolver;

    @Autowired
    protected RoleAssignmentServiceResolver roleAssignmentServiceResolver;

    private static final String ROLE_HYPERION_ENDPOINT_NAME = "Role";
    private static final String ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME = "RoleAssignment";

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listRoles(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer resultSize,
            @RequestParam(value = "query", required = false) final String query,
            @RequestParam(value = "sort", required = false) final String sort,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "list_role");

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.GET, httpServletRequest);

                PagedResponse<Role> response =
                        roleServiceResolver.getList(pageNumber, resultSize, query, sort, roleTypePermission, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.role.list"), httpServletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
    public DeferredResult<HttpEntity> retrieveById(
            @PathVariable(value = "roleId") final Long roleId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "retrieve_role");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                Role role = roleServiceResolver.getById(roleId, roleTypePermission, context);

                return new HttpEntity<>(role);
            }
        };

        return submit(callable, getTimer("v1.role.retrieve-by-id"), httpServletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public DeferredResult<HttpEntity> create(
            @RequestBody final RoleRequest roleRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "create_role");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.POST,
                                httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                Role role = roleServiceResolver.create(roleRequest, roleTypePermission, context);

                return new HttpEntity<>(role);
            }
        };

        return submit(callable, getTimer("v1.role.create"), httpServletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public DeferredResult<HttpEntity> updateRole(
            @RequestBody final RoleRequest roleRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "update_role");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                Role role = roleServiceResolver.update(roleRequest, roleTypePermission, context);

                return new HttpEntity<>(role);
            }
        };

        return submit(callable, getTimer("v1.role.update"), httpServletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/operation", method = RequestMethod.POST)
    public DeferredResult<HttpEntity> editRoleOperations(
            @RequestBody final EditRoleOperationRequest editRoleOperationRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "role_operation");

                PersistenceContext context = getNoOpPersistenceContextFactory().
                        createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.PUT,
                                httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                Role role = roleServiceResolver.editOperations(
                        editRoleOperationRequest, roleTypePermission, context);

                return new HttpEntity<>(role);
            }
        };

        return submit(callable, getTimer("v1.role.edit-operations"), httpServletRequest);
    }

    @RequestMapping(value = "/remove/{roleId}", method = RequestMethod.DELETE)
    public DeferredResult<HttpEntity> remove(
            @PathVariable(value = "roleId") final Long roleId,
            final HttpServletRequest httpServletRequest)
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "remove_role");

                PersistenceContext persistenceContext =
                        getNoOpPersistenceContextFactory()
                                .createPersistenceContext(ROLE_HYPERION_ENDPOINT_NAME, HttpMethod.DELETE,
                                        httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                roleServiceResolver.remove(roleId, roleTypePermission, persistenceContext);

                return new HttpEntity<>(HttpStatus.OK);
            }
        };

        return submit(callable, getTimer("v1.role.remove"), httpServletRequest);
    }

    @RequestMapping(value = "/assigned-users", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> getAssignedUsers(
            @RequestParam(value = "roleId", required = true) final Long roleId,
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
                assertAuthorized(httpServletRequest, "retrieve_role_assigned_users");

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);
                boolean isEditable =
                        getPermission(httpServletRequest, UI_AUTHORIZATION_RESOURCE, REMOVE_ROLE_ASSIGNMENT_ACTION);


                PagedResponse<AssignedUser> response =
                        roleAssignmentServiceResolver.getAssignedUsers(roleId, pageNumber, resultSize,
                                roleTypePermission, isEditable, context);

                return new HttpEntity<>(response);
            }
        };

        return submit(callable, getTimer("v1.role.get_assigned_users"), httpServletRequest);
    }


}
