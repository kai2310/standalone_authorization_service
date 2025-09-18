package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.cache.AccountGroupObjectCache;
import com.rubicon.platform.authorization.service.cache.AccountObjectCache;
import com.rubicon.platform.authorization.service.cache.RoleAssignmentObjectCache;
import com.rubicon.platform.authorization.service.cache.ServiceRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
@Component
public class RoleAssignmentResolver
{
    @Autowired
    private RoleAssignmentObjectCache roleAssignmentCache;

    @Autowired
    private AccountGroupObjectCache accountGroupCache;

    @Autowired
    private AccountObjectCache accountCache;

    List<ServiceRoleAssignment> resolveRoleAssignments(List<CompoundId> subjectIds, CompoundId accountId)
    {

        Set<Long> accountGroupIds = new HashSet<>();
        if(accountId != null)
        {
            Account account = accountCache.getByAccountId(accountId);
            if(account != null)
            {
                accountGroupIds.addAll(accountGroupCache.getAccountGroupIdsForAccount(account.getId()));
                accountGroupIds.addAll(accountGroupCache.getAccountGroupsForType(accountId.getIdType()));
            }
        }
        return roleAssignmentCache.getPermissions(subjectIds, accountId,accountGroupIds);
    }
}
