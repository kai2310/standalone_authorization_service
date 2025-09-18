package com.rubicon.platform.authorization.service.utils;

import com.rubicon.platform.authorization.model.data.lfo.ResourceTypeEnum;

import java.util.Arrays;
import java.util.List;

public class Constants
{
    public static final String ENV_PROD = "prod";

    public static final String MAGNITE_INTERNAL_CONTEXT = "publisher/100";
    public static final String AUTHORIZATION_SERVICE_NAME = "AccessManagement";

    public static final String HYPERION_PAGING_KEY_LIMIT = "limit";
    public static final String HYPERION_PAGING_KEY_START = "start";

    public static final String TRANSLATE_CONTEXT_IS_EDITABLE = "is_editable";
    public static final String TRANSLATE_CONTEXT_ROLE_TYPE_PERMISSION = "role_type_permission";
    public static final String TRANSLATE_CONTEXT_ACCOUNT_FEATURE_ACTION_ENUM = "AccountFeatureActionEnum";

    public static final String API_RESTRICTION_RESOURCE = "api_restrictions";
    public static final String UI_AUTHORIZATION_RESOURCE = "ui_authorization";

    public static final String FEATURE_RESTRICTION_ACTION = "edit_feature";
    public static final String EDIT_ACCOUNT_FEATURES_ACTION = "edit_account_features";
    public static final String REMOVE_ROLE_ASSIGNMENT_ACTION = "remove_role_assignment";


    public static final String ROLE_TYPE_ACTION_EDIT_BUYER = "edit_buyer_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_INTERNAL = "edit_internal_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_SELLER = "edit_seller_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_SERVICE = "edit_service_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_PRODUCT = "edit_product_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR = "edit_marketplace_vendor_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT = "edit_streaming_seat_role_type";
    public static final String ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER = "edit_streaming_buyer_role_type";

    public static final String ROLE_TYPE_ACTION_VIEW_BUYER = "view_buyer_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_INTERNAL = "view_internal_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_SELLER = "view_seller_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_SERVICE = "view_service_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_PRODUCT = "view_product_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR = "view_marketplace_vendor_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT = "view_streaming_seat_role_type";
    public static final String ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER = "view_streaming_buyer_role_type";

    public static final String ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_PUBLISHER = "assign_all_publisher";
    public static final String ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_SEAT = "assign_all_seat";
    public static final String ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_MARKETPLACE_VENDOR = "assign_all_marketplace_vendor";
    public static final String ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_SEAT = "assign_all_streaming_seat";
    public static final String ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_BUYER = "assign_all_streaming_buyer";

    public static final String ROLE_ASSIGNMENT_ACTION_ADD_INITIAL_ROLE_ASSIGNMENT = "add_initial_role_assignment";

    public static final List<String> ROLE_TYPE_ACTION_LIST =
            Arrays.asList(ROLE_TYPE_ACTION_EDIT_BUYER, ROLE_TYPE_ACTION_EDIT_INTERNAL, ROLE_TYPE_ACTION_EDIT_SELLER,
                    ROLE_TYPE_ACTION_EDIT_SERVICE, ROLE_TYPE_ACTION_EDIT_PRODUCT,
                    ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR, ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT,
                    ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER, ROLE_TYPE_ACTION_VIEW_BUYER,
                    ROLE_TYPE_ACTION_VIEW_INTERNAL, ROLE_TYPE_ACTION_VIEW_SELLER, ROLE_TYPE_ACTION_VIEW_SERVICE,
                    ROLE_TYPE_ACTION_VIEW_PRODUCT, ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR,
                    ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT, ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER);

    public static final List<String> ROLE_ASSIGNMENT_ACTION_LIST =
            Arrays.asList(ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_PUBLISHER, ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_SEAT,
                    ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_MARKETPLACE_VENDOR,
                    ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_SEAT, ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_BUYER,
                    ROLE_ASSIGNMENT_ACTION_ADD_INITIAL_ROLE_ASSIGNMENT);

    public static final String REALM_NAME = "REVVPlatform";

    public static final String QUERY_PARAMETER_ACCESS_TOKEN = "access_token";

    public static final String REVV_LEFTOVERS_AUTHORIZATION_ACCOUNT_RESOURCE_TYPE = ResourceTypeEnum.account.name();
    public static final String REVV_LEFTOVERS_AUTHORIZATION_SEAT_RESOURCE_TYPE = ResourceTypeEnum.seat.name();
    public static final String REVV_LEFTOVERS_AUTHORIZATION_MARKETPLACE_VENDOR_RESOURCE_TYPE =
            ResourceTypeEnum.marketplace_vendor.name();

    public static final String REVV_LEFTOVERS_AUTHORIZATION_USER_PRINCIPLE_TYPE = "user";
    public static final String REVV_LEFTOVERS_AUTHORIZATION_PRINCIPLE_TYPE_FIELD = "principleType";
    public static final String REVV_LEFTOVERS_AUTHORIZATION_PRINCIPLE_ID_FIELD = "principleId";
    public static final String REVV_LEFTOVERS_AUTHORIZATION_RESOURCE_TYPE_FIELD = "resourceType";
    public static final String REVV_LEFTOVERS_AUTHORIZATION_RESOURCE_ID_FIELD = "resourceId";
    public static final String REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID = "*";

    public static final Long ACCOUNT_GROUP_ALL_PUBLISHER_ID = 1L;
    public static final Long ACCOUNT_GROUP_ALL_SEAT_ID = 2L;
    public static final Long ACCOUNT_GROUP_ALL_MARKETPLACE_VENDOR_ID = 32L;
    public static final String ACCOUNT_TYPE_PUBLISHER = "publisher";
    public static final String ACCOUNT_TYPE_SEAT = "seat";
    public static final String ACCOUNT_TYPE_PARTNER = "partner";
    public static final String ACCOUNT_TYPE_MARKETPLACE_VENDOR = "mp-vendor";
    public static final String ACCOUNT_TYPE_STREAMING_SEAT = "streaming-seat";
    public static final String ACCOUNT_TYPE_STREAMING_BUYER = "streaming-buyer";
    // Buyer is currently being sent from the login server, but our code only understand seat
    public static final String ACCOUNT_TYPE_BUYER = "buyer";


    /*
        These publisher finance service operations are created to support deleting and reactivating accounts in via
        the Access Management UI API. I felt having an extra api property permission to control this was overboard, and
        lead to issues with keeping things in sync. If we use the same permissions, we should be okay, even though it
        violates the rules of operations, plus this API needs to delete as the user pushing the button. We can't use
        a service user in this case.
     */
    public static final String FINANCE_PUBLISHER_MANAGEMENT_SERVICE_NAME = "PubFinanceService";
    public static final String FINANCE_PUBLISHER_MANAGEMENT_RESOURCE = "publisher_service";
    public static final String FINANCE_PUBLISHER_MANAGEMENT_ACTION_REACTIVATE = "reactivate";
    public static final String FINANCE_PUBLISHER_MANAGEMENT_ACTION_REMOVE = "remove";

    public static final String MAGNITE_DV_PLUS = "dvplus";
    public static final String MAGNITE_STREAMING_PLATFORM = "streaming";
}

