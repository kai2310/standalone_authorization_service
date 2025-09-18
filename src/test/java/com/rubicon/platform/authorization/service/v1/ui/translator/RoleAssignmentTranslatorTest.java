package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.AccountReference;
import com.rubicon.platform.authorization.model.ui.acm.AccountReferenceTypeEnum;
import com.rubicon.platform.authorization.model.ui.acm.Reference;
import com.rubicon.platform.authorization.model.ui.acm.RoleAssignment;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class RoleAssignmentTranslatorTest extends TestAbstract
{
    @DataProvider
    public static Object[][] convertConsistentDataProvider()
    {
        return new Object[][]{
                {true, false, false, false, false, false, false, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, true, false, false, false, false, false, false, true, ACCOUNT_REFERENCE_GROUP},
                {false, false, true, false, false, false, false, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, false, false, true, false, false, false, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, false, false, false, true, false, false, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, false, false, false, false, true, false, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, false, false, false, false, false, true, false, false, ACCOUNT_REFERENCE_GROUP},
                {false, false, false, false, false, false, false, true, false, ACCOUNT_REFERENCE_GROUP},

                {true, false, false, false, false, false, false, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, true, false, false, false, false, false, false, true, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, true, false, false, false, false, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, false, true, false, false, false, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, false, false, true, false, false, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, false, false, false, true, false, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, false, false, false, false, true, false, false, ACCOUNT_REFERENCE_ACCOUNT},
                {false, false, false, false, false, false, false, true, false, ACCOUNT_REFERENCE_ACCOUNT}
        };
    }

    @Test
    @UseDataProvider("convertConsistentDataProvider")
    public void convertPersistent(boolean canEditBuyer, boolean canEditInternal, boolean canEditSeller,
                                  boolean canEditService, boolean canEditProduct,
                                  boolean canEditMarketplaceVendor, boolean canEditStreamingSeat,
                                  boolean canEditStreamingBuyer, boolean expectedEditable,
                                  String accountReferenceType)
    {
        RoleAssignmentTranslator roleAssignmentTranslator =
                new RoleAssignmentTranslator(getRoleObjectCache(), getAccountObjectCache(),
                        getAccountGroupObjectCache(), getDeletedAccountCache());

        roleAssignmentTranslator.init();

        TranslationContext context =
                buildTranslationContext(canEditBuyer, canEditInternal, canEditSeller, canEditService, canEditProduct,
                        canEditMarketplaceVendor, canEditStreamingSeat, canEditStreamingBuyer);

        RoleAssignment roleAssignment =
                roleAssignmentTranslator.convertPersistent(getDataServiceRoleAssignment(accountReferenceType), context);


        // Verify the Role Assignment stuff
        assertThat(roleAssignment.getId(), equalTo(DATA_SERVICE_ROLE_ASSIGNMENT_ID));
        assertThat(roleAssignment.getEditable(), equalTo(expectedEditable));

        AccountReference accountReference = roleAssignment.getAccountReference();
        Long expectedAccountReferenceId = 0L;
        String expectedAccountReferenceString = "";
        AccountReferenceTypeEnum expectedAccountReferenceType = null;

        // Verify the Account Reference Stuff
        assertNotNull(accountReference);
        if (accountReferenceType.equals(ACCOUNT_REFERENCE_GROUP))

        {
            expectedAccountReferenceId = DATA_SERVICE_ACCOUNT_GROUP_ID;
            expectedAccountReferenceString = DATA_SERVICE_ACCOUNT_GROUP_NAME;
            expectedAccountReferenceType = AccountReferenceTypeEnum.group;
        }
        else if (accountReferenceType.equals(ACCOUNT_REFERENCE_ACCOUNT))

        {
            expectedAccountReferenceId = DATA_SERVICE_ACCOUNT_ID;
            expectedAccountReferenceString = DATA_SERVICE_ACCOUNT_NAME;
            expectedAccountReferenceType = AccountReferenceTypeEnum.publisher;
        }

        assertThat(accountReference.getId(), equalTo(expectedAccountReferenceId));
        assertThat(accountReference.getName(), equalTo(expectedAccountReferenceString));
        assertThat(accountReference.getType(), equalTo(expectedAccountReferenceType));

        Reference role = roleAssignment.getRole();
        assertNotNull(role);
        assertThat(role.getId(), equalTo(DATA_SERVICE_ROLE_ID));
        assertThat(role.getName(), equalTo(DATA_SERVICE_ROLE_NAME));

    }

}
