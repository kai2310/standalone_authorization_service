package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.Operation;
import com.rubicon.platform.authorization.model.ui.acm.Role;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class RoleTranslatorTest extends TestAbstract
{
    RoleTranslator roleTranslator;

    @Before
    public void setup()
    {
        roleTranslator = new RoleTranslator();
        roleTranslator.init();
    }

    @DataProvider
    public static Object[][] convertConsistentDataProvider()
    {
        return new Object[][]{
                {true, false, false, false, false, false, false, false, RoleTypeEnum.buyer.getRoleTypeEnumId(), true,
                        RoleTypeEnum.buyer},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.buyer.getRoleTypeEnumId(), false,
                        RoleTypeEnum.buyer},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.internal.getRoleTypeEnumId(), true,
                        RoleTypeEnum.internal},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.internal.getRoleTypeEnumId(),
                        false,
                        RoleTypeEnum.internal},
                {false, false, true, false, false, false, false, false, RoleTypeEnum.seller.getRoleTypeEnumId(), true,
                        RoleTypeEnum.seller},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.seller.getRoleTypeEnumId(), false,
                        RoleTypeEnum.seller},
                {false, false, false, true, false, false, false, false, RoleTypeEnum.service.getRoleTypeEnumId(), true,
                        RoleTypeEnum.service},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.service.getRoleTypeEnumId(),
                        false,
                        RoleTypeEnum.service},
                {false, false, false, false, false, false, false, false, RoleTypeEnum.product.getRoleTypeEnumId(),
                        false,
                        RoleTypeEnum.product},
                {false, false, false, false, true, false, false, false, RoleTypeEnum.product.getRoleTypeEnumId(), true,
                        RoleTypeEnum.product},
                {false, false, false, false, false, false, false, false,
                        RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId(), false,
                        RoleTypeEnum.marketplace_vendor},
                {false, false, false, false, false, true, false, false,
                        RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId(), true,
                        RoleTypeEnum.marketplace_vendor},
                {false, false, false, false, false, false, false, false,
                        RoleTypeEnum.streaming_seat.getRoleTypeEnumId(), false,
                        RoleTypeEnum.streaming_seat},
                {false, false, false, false, false, false, true, false, RoleTypeEnum.streaming_seat.getRoleTypeEnumId(),
                        true,
                        RoleTypeEnum.streaming_seat},
                {false, false, false, false, false, false, false, false,
                        RoleTypeEnum.streaming_buyer.getRoleTypeEnumId(), false,
                        RoleTypeEnum.streaming_buyer},
                {false, false, false, false, false, false, false, true,
                        RoleTypeEnum.streaming_buyer.getRoleTypeEnumId(), true,
                        RoleTypeEnum.streaming_buyer},

                {true, false, false, false, false, false, false, false, RoleTypeEnum.internal.getRoleTypeEnumId(),
                        false,
                        RoleTypeEnum.internal},
                {false, true, false, false, false, false, false, false, RoleTypeEnum.seller.getRoleTypeEnumId(), false,
                        RoleTypeEnum.seller},
                {false, false, true, false, false, false, false, false, RoleTypeEnum.service.getRoleTypeEnumId(), false,
                        RoleTypeEnum.service},
                {false, false, false, true, false, false, false, false, RoleTypeEnum.product.getRoleTypeEnumId(), false,
                        RoleTypeEnum.product},
                {false, false, false, false, true, false, false, false, RoleTypeEnum.buyer.getRoleTypeEnumId(), false,
                        RoleTypeEnum.buyer},
                {false, false, false, false, true, false, false, false,
                        RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId(), false,
                        RoleTypeEnum.marketplace_vendor},
                {false, false, false, false, true, false, false, false, RoleTypeEnum.streaming_seat.getRoleTypeEnumId(),
                        false,
                        RoleTypeEnum.streaming_seat},
                {false, false, false, false, true, false, false, false,
                        RoleTypeEnum.streaming_buyer.getRoleTypeEnumId(), false,
                        RoleTypeEnum.streaming_buyer}
        };
    }


    @Test
    @UseDataProvider("convertConsistentDataProvider")
    public void convertPersistent(boolean canEditBuyer, boolean canEditInternal, boolean canEditSeller,
                                  boolean canEditService, boolean canEditProduct,
                                  boolean canEditMarketplaceVendor, boolean canEditStreamingSeat,
                                  boolean canEditStreamingBuyer, Long roleTypeId,
                                  boolean expectedIsEditable, RoleTypeEnum expectedRoleTypeEnum)
    {
        TranslationContext context =
                buildTranslationContext(canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                        canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer);

        com.rubicon.platform.authorization.model.data.acm.Role dataServiceRole = getDataServiceRole(roleTypeId);
        Role role = roleTranslator.convertPersistent(dataServiceRole, context);

        assertThat(role.getId(), equalTo(DATA_SERVICE_ROLE_ID));
        assertThat(role.getName(), equalTo(DATA_SERVICE_ROLE_NAME));
        assertThat(role.getType(), equalTo(expectedRoleTypeEnum));
        assertThat(role.getEditable(), equalTo(expectedIsEditable));


        Operation allowedOperation = role.getAllowedOperations().get(0);

        assertThat(allowedOperation.getService(), equalTo(OPERATION_SERVICE));
        assertThat(allowedOperation.getResource(), equalTo(OPERATION_RESOURCE));
        assertThat(allowedOperation.getAction(), equalTo(OPERATION_ACTION));

        Operation deniedOperation = role.getDeniedOperations().get(0);

        assertThat(deniedOperation.getService(), equalTo(OPERATION_SERVICE));
        assertThat(deniedOperation.getResource(), equalTo(OPERATION_RESOURCE));
        assertThat(deniedOperation.getAction(), equalTo(OPERATION_ACTION));
    }
}
