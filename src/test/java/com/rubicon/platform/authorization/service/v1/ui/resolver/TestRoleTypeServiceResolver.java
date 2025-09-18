package com.rubicon.platform.authorization.service.v1.ui.resolver;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.QueryResult;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.v1.ui.model.RoleTypePermission;
import com.rubicon.platform.authorization.service.v1.ui.translator.RoleTypeTranslator;
import com.rubicon.platform.authorization.model.ui.acm.Page;
import com.rubicon.platform.authorization.model.ui.acm.PagedResponse;
import com.rubicon.platform.authorization.model.ui.acm.RoleType;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class TestRoleTypeServiceResolver extends TestAbstract
{
    public static Integer DATA_SERVICE_TOTAL_COUNT = 20;
    public static int TOTAL_RECORDS_IN_RESULTS = 1;
    public static final String NOT_AUTHORIZED_FOR_ROLE_TYPE_MSG =
            "You are attempting to create/modify a role for a role type you do not have access to.";
    protected PersistenceContext context = new PersistenceContext();
    protected RoleTypeServiceResolver resolver;
    protected RoleTypeTranslator roleTypeTranslator;

    @Before
    public void setup()
    {
        roleTypeTranslator = new RoleTypeTranslator();
        roleTypeTranslator.init();

        context.setEntityPlugin(new EntityPlugin());

        resolver = new RoleTypeServiceResolver();
        resolver.setPersistenceOperations(setupUiServicePersistenceOperations(getQueryResults(2), getRoleTypeList(1)));
        resolver.setTranslator(roleTypeTranslator);
    }

    @Test
    public void testGetList()
    {
        int resultCount = 2;
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setViewBuyer(true);

        PersistenceContext context = new PersistenceContext();
        PagedResponse<RoleType> pagedResponse =
                resolver.getList(1, DATA_SERVICE_TOTAL_COUNT, roleTypePermission, context);

        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getPage());
        assertNotNull(pagedResponse.getContent());

        Page page = pagedResponse.getPage();
        assertThat(DATA_SERVICE_TOTAL_COUNT, equalTo(page.getSize()));
        assertThat(1, equalTo(page.getTotalPages()));
        assertThat(1, equalTo(page.getNumber()));

        // We are getting one cause the default
        List<RoleType> roleTypeList = pagedResponse.getContent();
        assertThat(resultCount, equalTo(roleTypeList.size()));
        RoleType role = roleTypeList.get(0);

        assertThat(DATA_SERVICE_ROLE_TYPE, equalTo(role.getId()));
        assertThat(DATA_SERVICE_ROLE_TYPE_NAME, equalTo(role.getName()));
    }

    protected List<com.rubicon.platform.authorization.model.data.acm.RoleType> getRoleTypeList(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.RoleType> roleList = new ArrayList<>();
        for (int index = 0; index < resultCount; index++)
        {
            roleList.add(getDataServiceRoleType());
        }

        return roleList;
    }


    protected QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleType> getQueryResults(int resultCount)
    {
        List<com.rubicon.platform.authorization.model.data.acm.RoleType> roleTypeList = getRoleTypeList(resultCount);

        QueryResult<com.rubicon.platform.authorization.model.data.acm.RoleType> queryResult = new QueryResult<>();
        queryResult.setItems(roleTypeList);
        queryResult.setResponseCount(roleTypeList.size());
        queryResult.setStart(1);
        queryResult.setTotalCount(resultCount);

        return queryResult;
    }


}
