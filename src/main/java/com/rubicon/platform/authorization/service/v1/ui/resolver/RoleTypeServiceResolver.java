package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.model.ui.acm.PagedResponse;
import com.rubicon.platform.authorization.model.ui.acm.RoleType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.rubicon.platform.authorization.service.utils.Constants.HYPERION_PAGING_KEY_LIMIT;
import static com.rubicon.platform.authorization.service.utils.Constants.HYPERION_PAGING_KEY_START;

public class RoleTypeServiceResolver
        extends BaseServiceResolver<RoleType, com.rubicon.platform.authorization.model.data.acm.RoleType>
{
    public RoleTypeServiceResolver()
    {
    }

    @Transactional
    public PagedResponse<RoleType> getList(Integer currentPage, Integer size, RoleTypePermission roleTypePermission,
                                           PersistenceContext persistenceContext)
    {
        // Translate Page Size into Old Hyperion Values
        Map<String, Integer> hyperionPagingMap = translatePagingToHyperion(currentPage, size);
        Integer start = hyperionPagingMap.get(HYPERION_PAGING_KEY_START);
        Integer limit = hyperionPagingMap.get(HYPERION_PAGING_KEY_LIMIT);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleType> queryResult =
                getPersistenceOperations().query(null, start, limit, null, persistenceContext);


        return buildPagedResponse(queryResult, size, buildRoleTypePermissionTranslationContext(roleTypePermission));
    }

}
