package com.rubicon.platform.authorization.service.v1.ui.model;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItems;

@RunWith(DataProviderRunner.class)
public class RoleTypePermissionTest extends TestAbstract
{
    @DataProvider
    public static Object[][] setValuesDataProvider()
    {
        return new Object[][]{
                {ROLE_TYPE_ACTION_EDIT_BUYER, true, true},
                {ROLE_TYPE_ACTION_EDIT_BUYER, false, false},
                {ROLE_TYPE_ACTION_EDIT_INTERNAL, true, true},
                {ROLE_TYPE_ACTION_EDIT_INTERNAL, false, false},
                {ROLE_TYPE_ACTION_EDIT_SELLER, true, true},
                {ROLE_TYPE_ACTION_EDIT_SELLER, false, false},
                {ROLE_TYPE_ACTION_EDIT_SERVICE, true, true},
                {ROLE_TYPE_ACTION_EDIT_SERVICE, false, false},
                {ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR, true, true},
                {ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR, false, false},
                {ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT, true, true},
                {ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT, false, false},
                {ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER, true, true},
                {ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER, false, false},
                {ROLE_TYPE_ACTION_VIEW_BUYER, true, true},
                {ROLE_TYPE_ACTION_VIEW_BUYER, false, false},
                {ROLE_TYPE_ACTION_VIEW_INTERNAL, true, true},
                {ROLE_TYPE_ACTION_VIEW_INTERNAL, false, false},
                {ROLE_TYPE_ACTION_VIEW_SELLER, true, true},
                {ROLE_TYPE_ACTION_VIEW_SELLER, false, false},
                {ROLE_TYPE_ACTION_VIEW_SERVICE, true, true},
                {ROLE_TYPE_ACTION_VIEW_SERVICE, false, false},
                {ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR, true, true},
                {ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR, false, false},
                {ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT, true, true},
                {ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT, false, false},
                {ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER, true, true},
                {ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER, false, false},
                {"Some Random String", true, true},
        };
    }


    @Test
    @UseDataProvider("setValuesDataProvider")
    public void setValues(String action, boolean isAuthorized, boolean expected)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        roleTypePermission.setValue(action, isAuthorized);

        switch (action)
        {
            case ROLE_TYPE_ACTION_EDIT_BUYER:
                assertEquals(roleTypePermission.isEditBuyer(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_INTERNAL:
                assertEquals(roleTypePermission.isEditInternal(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_SELLER:
                assertEquals(roleTypePermission.isEditSeller(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_SERVICE:
                assertEquals(roleTypePermission.isEditService(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR:
                assertEquals(roleTypePermission.isEditMarketplaceVendor(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT:
                assertEquals(roleTypePermission.isEditStreamingSeat(), expected);
                break;
            case ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER:
                assertEquals(roleTypePermission.isEditStreamingBuyer(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_BUYER:
                assertEquals(roleTypePermission.isViewBuyer(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_INTERNAL:
                assertEquals(roleTypePermission.isViewInternal(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_SELLER:
                assertEquals(roleTypePermission.isViewSeller(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_SERVICE:
                assertEquals(roleTypePermission.isViewService(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR:
                assertEquals(roleTypePermission.isViewMarketplaceVendor(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT:
                assertEquals(roleTypePermission.isViewStreamingSeat(), expected);
                break;
            case ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER:
                assertEquals(roleTypePermission.isViewStreamingBuyer(), expected);
                break;
            default:
                assertFalse(roleTypePermission.isEditBuyer());
                assertFalse(roleTypePermission.isEditInternal());
                assertFalse(roleTypePermission.isEditSeller());
                assertFalse(roleTypePermission.isEditService());
                assertFalse(roleTypePermission.isEditMarketplaceVendor());
                assertFalse(roleTypePermission.isEditStreamingSeat());
                assertFalse(roleTypePermission.isEditStreamingBuyer());
                assertFalse(roleTypePermission.isViewBuyer());
                assertFalse(roleTypePermission.isViewInternal());
                assertFalse(roleTypePermission.isViewSeller());
                assertFalse(roleTypePermission.isViewService());
                assertFalse(roleTypePermission.isViewMarketplaceVendor());
                assertFalse(roleTypePermission.isViewStreamingSeat());
                assertFalse(roleTypePermission.isViewStreamingBuyer());
        }
    }

    @Test
    public void testBuildOperationRequestList()
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        List<OperationRequest> operationRequestList = roleTypePermission.buildOperationRequestList();

        for (OperationRequest operationRequest : operationRequestList)
        {
            assertThat(operationRequest.getService(), equalTo(Constants.AUTHORIZATION_SERVICE_NAME));
            assertThat(operationRequest.getResource(), equalTo(Constants.API_RESTRICTION_RESOURCE));
            assertThat(ROLE_TYPE_ACTION_LIST, hasItems(operationRequest.getAction()));
        }
    }


    @DataProvider
    public static Object[][] buildFilterStringDataProvider()
    {
        return new Object[][]{
                {true, false, false, false, false, false, false, false,
                        Collections.singletonList(RoleTypeEnum.buyer.getRoleTypeEnumId())},
                {false, true, false, false, false, false, false, false,
                        Collections.singletonList(RoleTypeEnum.internal.getRoleTypeEnumId())},
                {false, false, true, false, false, false, false, false,
                        Collections.singletonList(RoleTypeEnum.seller.getRoleTypeEnumId())},
                {false, false, false, true, false, false, false, false,
                        Collections.singletonList(RoleTypeEnum.service.getRoleTypeEnumId())},
                {false, false, false, false, true, false, false, false,
                        Collections.singletonList(RoleTypeEnum.product.getRoleTypeEnumId())},
                {false, false, false, false, false, true, false, false,
                        Collections.singletonList(RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId())},
                {false, false, false, false, false, false, true, false,
                        Collections.singletonList(RoleTypeEnum.streaming_seat.getRoleTypeEnumId())},
                {false, false, false, false, false, false, false, true,
                        Collections.singletonList(RoleTypeEnum.streaming_buyer.getRoleTypeEnumId())},
                {true, true, false, false, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.buyer.getRoleTypeEnumId(),
                                RoleTypeEnum.internal.getRoleTypeEnumId())},
                {false, true, true, false, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.internal.getRoleTypeEnumId(),
                                RoleTypeEnum.seller.getRoleTypeEnumId())},
                {false, false, true, true, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.service.getRoleTypeEnumId())},
                {true, true, true, false, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.buyer.getRoleTypeEnumId(), RoleTypeEnum.internal.getRoleTypeEnumId(),
                                RoleTypeEnum.seller.getRoleTypeEnumId())},
                {false, true, true, true, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.internal.getRoleTypeEnumId(),
                                RoleTypeEnum.seller.getRoleTypeEnumId(), RoleTypeEnum.service.getRoleTypeEnumId())},
                {true, true, true, true, false, false, false, false,
                        Arrays.asList(RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.internal.getRoleTypeEnumId(), RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.service.getRoleTypeEnumId())},
                {true, true, true, true, true, false, false, false,
                        Arrays.asList(RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.internal.getRoleTypeEnumId(), RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.service.getRoleTypeEnumId(), RoleTypeEnum.product.getRoleTypeEnumId())},
                {true, true, true, true, true, true, false, false,
                        Arrays.asList(RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.internal.getRoleTypeEnumId(), RoleTypeEnum.seller.getRoleTypeEnumId(),
                                RoleTypeEnum.service.getRoleTypeEnumId(), RoleTypeEnum.product.getRoleTypeEnumId(),
                                RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId())},
                {false, false, false, false, false, true, true, true,
                        Arrays.asList(RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId(),
                                RoleTypeEnum.streaming_seat.getRoleTypeEnumId(),
                                RoleTypeEnum.streaming_buyer.getRoleTypeEnumId())}
        };
    }

    @Test
    @UseDataProvider("buildFilterStringDataProvider")
    public void testBuildFilterString(boolean canViewBuyer, boolean canViewInternal, boolean canViewSeller,
                                      boolean canViewService, boolean canViewProduct,
                                      boolean canViewMarketplaceVendor, boolean canViewStreamingSeat,
                                      boolean canViewStreamingBuyer, List<Long> expectedIds)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setViewBuyer(canViewBuyer);
        roleTypePermission.setViewInternal(canViewInternal);
        roleTypePermission.setViewSeller(canViewSeller);
        roleTypePermission.setViewService(canViewService);
        roleTypePermission.setViewProduct(canViewProduct);
        roleTypePermission.setViewMarketplaceVendor(canViewMarketplaceVendor);
        roleTypePermission.setViewStreamingSeat(canViewStreamingSeat);
        roleTypePermission.setViewStreamingBuyer(canViewStreamingBuyer);

        String filterString = roleTypePermission.buildFilterString();

        assertThat(filterString, containsString("roleTypeId=IN=("));
        assertThat(filterString, containsString(")"));
        for (Long roleTypeId : expectedIds)
        {
            assertThat(filterString, containsString(roleTypeId.toString()));
        }

    }

    @Test
    public void testBuildFilterString_withNoValueSet()
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();

        String filterString = roleTypePermission.buildFilterString();

        assertThat(filterString, equalTo("roleTypeId==0"));

    }

    @DataProvider
    public static Object[][] isRoleTypeAccessibleDataProvider()
    {
        return new Object[][]{
                {true, false, false, false, false, false, false, false, RoleTypeEnum.buyer, true},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.buyer, false},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.buyer, false},
                {false, false, true, false, false, false, false, false, RoleTypeEnum.internal, false},
                {false, true, false, true, false, false, false, false, RoleTypeEnum.internal, true},
                {false, false, false, true, false, false, false, false, RoleTypeEnum.internal, false},
                {true, true, false, false, false, false, false, false, RoleTypeEnum.seller, false},
                {false, true, true, false, false, false, false, false, RoleTypeEnum.seller, true},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.seller, false},
                {false, false, true, true, false, false, false, false, RoleTypeEnum.service, true},
                {true, true, true, false, false, false, false, false, RoleTypeEnum.service, false},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.service, false},
                {false, false, false, false, true, false, false, false, RoleTypeEnum.product, true},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.product, false},
                {false, false, false, false, false, true, false, false, RoleTypeEnum.marketplace_vendor, true},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.marketplace_vendor, false},
                {false, false, false, false, false, false, true, false, RoleTypeEnum.streaming_seat, true},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.streaming_seat, false},
                {false, false, false, false, false, false, false, true, RoleTypeEnum.streaming_buyer, true},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.streaming_buyer, false}
        };
    }


    @Test
    @UseDataProvider("isRoleTypeAccessibleDataProvider")
    public void testIsRoleTypeEditable(boolean canEditBuyer, boolean canEditInternal, boolean canEditSeller,
                                       boolean canEditService, boolean canEditProduct,
                                       boolean canEditMarketplaceVendor, boolean canEditStreamingSeat,
                                       boolean canEditStreamingBuyer,
                                       RoleTypeEnum roleType, boolean isEditable)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setEditBuyer(canEditBuyer);
        roleTypePermission.setEditInternal(canEditInternal);
        roleTypePermission.setEditSeller(canEditSeller);
        roleTypePermission.setEditService(canEditService);
        roleTypePermission.setEditProduct(canEditProduct);
        roleTypePermission.setEditMarketplaceVendor(canEditMarketplaceVendor);
        roleTypePermission.setEditStreamingSeat(canEditStreamingSeat);
        roleTypePermission.setEditStreamingBuyer(canEditStreamingBuyer);

        assertThat(roleTypePermission.isRoleTypeEditable(roleType), equalTo(isEditable));
    }

    @Test
    @UseDataProvider("isRoleTypeAccessibleDataProvider")
    public void testIsRoleTypeViewable(boolean canViewBuyer, boolean canViewInternal, boolean canViewSeller,
                                       boolean canViewService, boolean canViewProduct,
                                       boolean canViewMarketplaceVendor, boolean canViewStreamingSeat,
                                       boolean canViewStreamingBuyer,
                                       RoleTypeEnum roleType, boolean isViewable)
    {
        RoleTypePermission roleTypePermission = new RoleTypePermission();
        roleTypePermission.setViewBuyer(canViewBuyer);
        roleTypePermission.setViewInternal(canViewInternal);
        roleTypePermission.setViewSeller(canViewSeller);
        roleTypePermission.setViewService(canViewService);
        roleTypePermission.setViewProduct(canViewProduct);
        roleTypePermission.setViewMarketplaceVendor(canViewMarketplaceVendor);
        roleTypePermission.setViewStreamingSeat(canViewStreamingSeat);
        roleTypePermission.setViewStreamingBuyer(canViewStreamingBuyer);

        assertThat(roleTypePermission.isRoleTypeViewable(roleType), equalTo(isViewable));
    }
}
