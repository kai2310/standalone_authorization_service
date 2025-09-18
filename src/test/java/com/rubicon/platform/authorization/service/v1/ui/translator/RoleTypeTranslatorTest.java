package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.ui.acm.RoleType;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class RoleTypeTranslatorTest extends TestAbstract
{
    RoleTypeTranslator roleTypeTranslator;

    @Before
    public void setup()
    {
        roleTypeTranslator = new RoleTypeTranslator();
        roleTypeTranslator.init();
    }

    @DataProvider
    public static Object[][] convertPersistentDataProvider()
    {
        return new Object[][]{
                {RoleTypeEnum.buyer.getRoleTypeEnumId(), RoleTypeEnum.buyer, "A Random buyer Role Type", true},
                {RoleTypeEnum.seller.getRoleTypeEnumId(), RoleTypeEnum.seller, "A Random Seller Role Type", true},
                {RoleTypeEnum.internal.getRoleTypeEnumId(), RoleTypeEnum.internal, "A Random internal Role Type", true},
                {RoleTypeEnum.service.getRoleTypeEnumId(), RoleTypeEnum.service, "A Random Service Role Type", true},
                {RoleTypeEnum.marketplace_vendor.getRoleTypeEnumId(), RoleTypeEnum.marketplace_vendor,
                        "A Random Marketplace Vendor Role Type", true},
                {15L, RoleTypeEnum.seller, "It does not matter", false}
        };
    }


    @Test
    @UseDataProvider("convertPersistentDataProvider")
    public void convertPersistent(Long roleTypeId, RoleTypeEnum roleTypeEnum, String name, boolean validConversion)
    {
        com.rubicon.platform.authorization.model.data.acm.RoleType roleTypeData = getDataServiceRoleType(roleTypeId, name);

        RoleType roleType =
                roleTypeTranslator.convertPersistent(roleTypeData,
                        buildViewTranslationContext(true, true, true, true, true, true, true, true));

        if (validConversion)
        {
            assertThat(roleType.getId(), equalTo(roleTypeEnum));
            assertThat(roleType.getName(), equalTo(name));
        }
        else
        {
            assertNull(roleType);
        }
    }

    @Test
    public void convertPersistentList()
    {
        String expectedName = "Role Type One name";
        RoleTypeEnum roleTypeEnum = RoleTypeEnum.seller;
        com.rubicon.platform.authorization.model.data.acm.RoleType roleTypeDataOne =
                getDataServiceRoleType(roleTypeEnum, expectedName);

        com.rubicon.platform.authorization.model.data.acm.RoleType roleTypeDataTwo =
                getDataServiceRoleType(RoleTypeEnum.buyer, "Role Type Two name");

        List<RoleType> roleTypes =
                roleTypeTranslator.convertPersistent(Arrays.asList(roleTypeDataOne, roleTypeDataTwo),
                        buildViewTranslationContext(false, false, true, false, false, false, false, false));


        assertThat(roleTypes, notNullValue());
        assertThat(roleTypes.size(), equalTo(1));

        RoleType roleType = roleTypes.get(0);

        assertThat(roleType.getId(), equalTo(roleTypeEnum));
        assertThat(roleType.getName(), equalTo(expectedName));
    }

}
