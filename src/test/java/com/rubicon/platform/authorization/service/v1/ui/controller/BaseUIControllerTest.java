package com.rubicon.platform.authorization.service.v1.ui.controller;

import com.rubicon.platform.authorization.TestAbstract;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


@RunWith(DataProviderRunner.class)
public class BaseUIControllerTest extends TestAbstract
{
    FakeController controller;

    @DataProvider
    public static Object[][] testArePermissionsEnforcedDataProvider()
    {
        return new Object[][]{
                // When In Production, Permissions should always be enforced
                {"prod", null, false, true},
                {"prod", 15L, false, true},
                {"prod", 15L, true, true},
                {"prod", null, true, true},

                // In DEV/QA Permission should always be enforced expect for one situation
                // A User ID is returned from the cache and disablePermission are allowed
                {"qa", null, false, true},
                {"qa", 15L, false, true},
                {"qa", 15L, true, false},
                {"qa", null, true, true},

                // In DEV/QA Permission should always be enforced expect for one situation
                // A User ID is returned from the cache and disablePermission are allowed
                {"dev", null, false, true},
                {"dev", 15L, false, true},
                {"dev", 15L, true, false},
                {"dev", null, true, true},
        };
    }


    @Test
    @UseDataProvider("testArePermissionsEnforcedDataProvider")
    public void testArePermissionsEnforced(String environment, Long cacheReturnValue, boolean disablePermissionAllowed,
                                           boolean expectedResponse)
    {
        controller = spy(new FakeController());

        doReturn(cacheReturnValue).when(controller).getCachedUserId(anyLong());

        controller.setDisablePermissionsAllowed(disablePermissionAllowed);
        controller.setEnvironment(environment);

        assertThat(controller.arePermissionsEnforced(25L), equalTo(expectedResponse));
    }


    class FakeController extends BaseUIController
    {
        @Override
        protected boolean arePermissionsEnforced(Long userId)
        {
            return super.arePermissionsEnforced(userId);
        }

        @Override
        protected Long getCachedUserId(Long userId)
        {
            return super.getCachedUserId(userId);
        }
    }

}
