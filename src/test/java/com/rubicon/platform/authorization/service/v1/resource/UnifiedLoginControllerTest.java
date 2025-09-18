package com.rubicon.platform.authorization.service.v1.resource;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.api.idm.User;
import com.rubicon.platform.authorization.service.client.model.UnifiedLoginUserInfo;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.api.acm.UnifiedLoginAuthorizeOperationRequest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class UnifiedLoginControllerTest extends TestAbstract
{
    private UnifiedLoginController unifiedLoginController = new UnifiedLoginController();

    @DataProvider
    public static Object[][] validateUnifiedLoginAuthorizeOperationRequestDataProvider()
    {
        UnifiedLoginAuthorizeOperationRequest request = new UnifiedLoginAuthorizeOperationRequest();
        request.setToken("123");
        return new Object[][]{
                {null, true},
                {new UnifiedLoginAuthorizeOperationRequest(), true},
                {request, false}
        };
    }

    @Test
    @UseDataProvider("validateUnifiedLoginAuthorizeOperationRequestDataProvider")
    public void validateUnifiedLoginAuthorizeOperationRequestTest(UnifiedLoginAuthorizeOperationRequest request,
                                                                  boolean exceptionThrown)
    {
        if (exceptionThrown)
        {
            expectedException.expect(AuthorizationServiceException.class);
        }

        unifiedLoginController.validateUnifiedLoginAuthorizeOperationRequest(request);
    }

    @DataProvider
    public static Object[][] validateUnifiedLoginUserInfoDataProvider()
    {
        return new Object[][]{
                {USER_EMAIL, Constants.ACCOUNT_TYPE_STREAMING_SEAT, STREAMING_SEAT_ID, false},
                {null, Constants.ACCOUNT_TYPE_STREAMING_SEAT, STREAMING_SEAT_ID, true},
                {USER_EMAIL, null, STREAMING_SEAT_ID, true},
                {USER_EMAIL, Constants.ACCOUNT_TYPE_STREAMING_SEAT, null, true}
        };
    }

    @Test
    @UseDataProvider("validateUnifiedLoginUserInfoDataProvider")
    public void validateUnifiedLoginUserInfoTest(String email, String contextType, String contextId,
                                                 boolean exceptionThrown)
    {
        if (exceptionThrown)
        {
            expectedException.expect(AuthorizationServiceException.class);
        }

        UnifiedLoginUserInfo unifiedLoginUserInfo = new UnifiedLoginUserInfo();
        unifiedLoginUserInfo.setEmail(email);
        unifiedLoginUserInfo.setContextType(contextType);
        unifiedLoginUserInfo.setContextId(contextId);

        unifiedLoginController.validateUnifiedLoginUserInfo(unifiedLoginUserInfo);
    }

    @Test
    public void validateUnifiedLoginUserInfoTest_Null()
    {
        expectedException.expect(AuthorizationServiceException.class);

        unifiedLoginController.validateUnifiedLoginUserInfo(null);
    }

    @DataProvider
    public static Object[][] validateIdmUserDataProvider()
    {
        return new Object[][]{
                {null, true},
                {new User(), false}
        };
    }

    @Test
    @UseDataProvider("validateIdmUserDataProvider")
    public void validateIdmUserTest(User user, boolean exceptionThrown)
    {
        if (exceptionThrown)
        {
            expectedException.expect(AuthorizationServiceException.class);
        }

        unifiedLoginController.validateIdmUser(user);
    }
}
