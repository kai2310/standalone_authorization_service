package com.rubicon.platform.authorization.service.v1.resource;

import com.rubicon.platform.authorization.service.cache.BaseRoleObjectCache;
import com.rubicon.platform.authorization.service.cache.ServiceOperation;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsHolder;
import com.rubicon.platform.authorization.service.resolver.OperationMatch;
import com.rubicon.platform.authorization.service.resolver.OperationMatcher;
import com.rubicon.platform.authorization.model.data.acm.BaseRoleApiObject;
import com.rubicon.platform.authorization.model.data.acm.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 */
@Component
public class OperationQueryResolver
{
    private OperationMatcher operationMatcher = new OperationMatcher();

    @Autowired
    @Qualifier("roleObjectCache")
    private BaseRoleObjectCache<Role> roleCache;

    @Autowired
    @Qualifier("accountFeatureObjectCache")
    private BaseRoleObjectCache<Role> accountFeatureCache;

    public List<Long> getMatchingRoles(String service, String resource, String action)
    {
        return checkOperations(service, resource, action, roleCache.loadAll());
    }

    public List<Long> getMatchingAccountFeatures(String service, String resource, String action)
    {
        return checkOperations(service, resource, action, accountFeatureCache.loadAll());
    }

    private <T extends BaseRoleApiObject> List<Long> checkOperations(String service, String resource, String action,
                                       List<ServiceOperationsHolder<T>> roles)
    {
        List<Long> results = new LinkedList<>();
        for (ServiceOperationsHolder<T> role : roles)
        {
            if(checkOperations(resource, action, role.getAllowedOperations(service)))
                results.add(role.getWrapped().getId());
        }

        return results;
    }

    private boolean checkOperations(String resource, String action, List<ServiceOperation> allowedOperations)
    {
        for (ServiceOperation allowedOperation : allowedOperations)
        {
            if(operationMatcher.matchOperation(allowedOperation,resource,action) != OperationMatch.none)
                return true;
        }

        return false;
    }
}
