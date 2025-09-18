package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleAssignmentPermission;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.resolver.RoleAssignmentServiceResolver;
import com.rubicon.platform.authorization.model.ui.acm.PagedResponse;
import com.rubicon.platform.authorization.model.ui.acm.RoleAssignmentRequest;
import com.rubicon.platform.authorization.model.ui.acm.UserRoleAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

import static com.rubicon.platform.authorization.service.utils.Constants.REMOVE_ROLE_ASSIGNMENT_ACTION;

@RequestMapping(value = "/v1/authorization/role-assignment")
public class RoleAssignmentServiceController extends BaseUIController
{
    @Autowired
    protected RoleAssignmentServiceResolver roleAssignmentServiceResolver;

    private static final String ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME = "RoleAssignment";


    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> listUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "25") final Integer size,
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
                assertAuthorized(httpServletRequest, "list_user_role_assignment");

                RoleAssignmentPermission roleAssignmentPermission = getRoleAssignmentPermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                PagedResponse<UserRoleAssignment> users = roleAssignmentServiceResolver.listUsers(
                        page, size, query, sort, roleAssignmentPermission.isAssignInitialRoleAssignment(), context);

                return new HttpEntity<>(users);
            }
        };

        return submit(callable, getTimer("v1.role-assignment.list-users"), httpServletRequest);
    }


    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<HttpEntity> findByUserId(
            @PathVariable(value = "userId") final Long userId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "user_role_assignment");

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);
                RoleAssignmentPermission roleAssignmentPermission = getRoleAssignmentPermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME, HttpMethod.GET,
                                httpServletRequest);

                UserRoleAssignment userRoleAssignment =
                        roleAssignmentServiceResolver
                                .getUserRoleAssignments(userId, roleTypePermission,
                                        roleAssignmentPermission.isAssignInitialRoleAssignment(), context);
                return new HttpEntity<>(userRoleAssignment);
            }
        };

        return submit(callable, getTimer("v1.role-assignment.user-retrieve-by-id"), httpServletRequest);
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<HttpEntity> create(
            @RequestBody final RoleAssignmentRequest roleAssignmentRequest,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, "create_role_assignment");

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);
                RoleAssignmentPermission roleAssignmentPermission = getRoleAssignmentPermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME, HttpMethod.POST,
                                httpServletRequest);

                UserRoleAssignment userRoleAssignment = roleAssignmentServiceResolver
                        .createRoleAssignment(roleAssignmentRequest, roleTypePermission, roleAssignmentPermission,
                                context);

                return new HttpEntity<>(userRoleAssignment);
            }
        };

        return submit(callable, getTimer("v1.role-assignment.create"), httpServletRequest);
    }


    @RequestMapping(value = "/remove/{roleAssignmentId}", method = RequestMethod.DELETE)
    @ResponseBody
    public DeferredResult<HttpEntity> removeById(
            @PathVariable(value = "roleAssignmentId") final Long roleAssignmentId,
            final HttpServletRequest httpServletRequest
    )
    {
        Callable<HttpEntity> callable = new Callable<HttpEntity>()
        {
            @Override
            public HttpEntity call() throws Exception
            {
                assertAuthorized(httpServletRequest, REMOVE_ROLE_ASSIGNMENT_ACTION);

                RoleTypePermission roleTypePermission = getRoleTypePermission(httpServletRequest);

                PersistenceContext context = getNoOpPersistenceContextFactory()
                        .createPersistenceContext(ROLE_ASSIGNMENT_HYPERION_ENDPOINT_NAME, HttpMethod.DELETE,
                                httpServletRequest);

                roleAssignmentServiceResolver.removeRoleAssignment(roleAssignmentId, roleTypePermission, context);

                return new HttpEntity<>(HttpStatus.OK);
            }
        };

        return submit(callable, getTimer("v1.role-assignment.remove-by-id"), httpServletRequest);
    }
}
