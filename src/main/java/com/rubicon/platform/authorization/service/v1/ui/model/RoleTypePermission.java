package com.rubicon.platform.authorization.service.v1.ui.model;

import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;
import com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.rubicon.platform.authorization.model.ui.acm.RoleTypeEnum.*;

public class RoleTypePermission extends AbstractPermission
{
    private boolean editBuyer = false;
    private boolean editInternal = false;
    private boolean editSeller = false;
    private boolean editService = false;
    private boolean editProduct = false;
    private boolean editMarketplaceVendor = false;
    private boolean editStreamingSeat = false;
    private boolean editStreamingBuyer = false;

    private boolean viewBuyer = false;
    private boolean viewInternal = false;
    private boolean viewSeller = false;
    private boolean viewService = false;
    private boolean viewProduct = false;
    private boolean viewMarketplaceVendor = false;
    private boolean viewStreamingSeat = false;
    private boolean viewStreamingBuyer = false;

    public boolean isEditBuyer()
    {
        return editBuyer;
    }

    public void setEditBuyer(boolean editBuyer)
    {
        this.editBuyer = editBuyer;
    }

    public boolean isEditInternal()
    {
        return editInternal;
    }

    public void setEditInternal(boolean editInternal)
    {
        this.editInternal = editInternal;
    }

    public boolean isEditSeller()
    {
        return editSeller;
    }

    public void setEditSeller(boolean editSeller)
    {
        this.editSeller = editSeller;
    }

    public boolean isEditService()
    {
        return editService;
    }

    public void setEditService(boolean editService)
    {
        this.editService = editService;
    }

    public boolean isViewBuyer()
    {
        return viewBuyer;
    }

    public void setViewBuyer(boolean viewBuyer)
    {
        this.viewBuyer = viewBuyer;
    }

    public boolean isViewInternal()
    {
        return viewInternal;
    }

    public void setViewInternal(boolean viewInternal)
    {
        this.viewInternal = viewInternal;
    }

    public boolean isViewSeller()
    {
        return viewSeller;
    }

    public void setViewSeller(boolean viewSeller)
    {
        this.viewSeller = viewSeller;
    }

    public boolean isViewService()
    {
        return viewService;
    }

    public void setViewService(boolean viewService)
    {
        this.viewService = viewService;
    }

    public boolean isEditProduct()
    {
        return editProduct;
    }

    public void setEditProduct(boolean editProduct)
    {
        this.editProduct = editProduct;
    }

    public boolean isViewProduct()
    {
        return viewProduct;
    }

    public void setViewProduct(boolean viewProduct)
    {
        this.viewProduct = viewProduct;
    }

    public boolean isEditMarketplaceVendor()
    {
        return editMarketplaceVendor;
    }

    public void setEditMarketplaceVendor(boolean editMarketplaceVendor)
    {
        this.editMarketplaceVendor = editMarketplaceVendor;
    }

    public boolean isViewMarketplaceVendor()
    {
        return viewMarketplaceVendor;
    }

    public void setViewMarketplaceVendor(boolean viewMarketplaceVendor)
    {
        this.viewMarketplaceVendor = viewMarketplaceVendor;
    }

    public boolean isEditStreamingSeat()
    {
        return editStreamingSeat;
    }

    public void setEditStreamingSeat(boolean editStreamingSeat)
    {
        this.editStreamingSeat = editStreamingSeat;
    }

    public boolean isViewStreamingSeat()
    {
        return viewStreamingSeat;
    }

    public void setViewStreamingSeat(boolean viewStreamingSeat)
    {
        this.viewStreamingSeat = viewStreamingSeat;
    }

    public boolean isEditStreamingBuyer()
    {
        return editStreamingBuyer;
    }

    public void setEditStreamingBuyer(boolean editStreamingBuyer)
    {
        this.editStreamingBuyer = editStreamingBuyer;
    }

    public boolean isViewStreamingBuyer()
    {
        return viewStreamingBuyer;
    }

    public void setViewStreamingBuyer(boolean viewStreamingBuyer)
    {
        this.viewStreamingBuyer = viewStreamingBuyer;
    }

    public void setValue(String operationAction, boolean isAuthorized)
    {
        switch (operationAction)
        {
            case Constants.ROLE_TYPE_ACTION_EDIT_BUYER:
                this.editBuyer = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_INTERNAL:
                this.editInternal = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_SELLER:
                this.editSeller = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_SERVICE:
                this.editService = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_PRODUCT:
                this.editProduct = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_MARKETPLACE_VENDOR:
                this.editMarketplaceVendor = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_STREAMING_SEAT:
                this.editStreamingSeat = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_EDIT_STREAMING_BUYER:
                this.editStreamingBuyer = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_BUYER:
                this.viewBuyer = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_INTERNAL:
                this.viewInternal = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_SELLER:
                this.viewSeller = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_SERVICE:
                this.viewService = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_PRODUCT:
                this.viewProduct = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_MARKETPLACE_VENDOR:
                this.viewMarketplaceVendor = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_STREAMING_SEAT:
                this.viewStreamingSeat = isAuthorized;
                break;
            case Constants.ROLE_TYPE_ACTION_VIEW_STREAMING_BUYER:
                this.viewStreamingBuyer = isAuthorized;
                break;
            default:
                break;
        }
    }

    @Override
    public List<OperationRequest> buildOperationRequestList()
    {
        return buildOperationRequestList(Constants.ROLE_TYPE_ACTION_LIST);
    }

    public String buildFilterString()
    {
        // Prevent anything from being returned by default;
        String filter = "roleTypeId==0";
        List<String> filterList = new ArrayList<>();

        populateFilterList(filterList, this.viewBuyer, buyer.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewInternal, internal.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewSeller, seller.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewService, service.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewProduct, product.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewMarketplaceVendor, marketplace_vendor.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewStreamingSeat, streaming_seat.getRoleTypeEnumId());
        populateFilterList(filterList, this.viewStreamingBuyer, streaming_buyer.getRoleTypeEnumId());

        StringBuilder inStringBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty((filterList)))
        {
            for (String roleTypeId : filterList)
            {
                inStringBuilder.append(roleTypeId);
                inStringBuilder.append(",");
            }
            String inString = inStringBuilder.toString();

            if (inString.endsWith(","))
            {
                inString = inString.substring(0, inString.length() - 1);
            }

            StringBuilder filterBuilder = new StringBuilder();
            filterBuilder.append("roleTypeId=IN=(");
            filterBuilder.append(inString);
            filterBuilder.append(")");

            filter = filterBuilder.toString();
        }

        return filter;
    }


    public boolean isRoleTypeEditable(RoleTypeEnum roleType)
    {
        boolean isEditable;

        switch (roleType)
        {
            case buyer:
                isEditable = editBuyer;
                break;
            case internal:
                isEditable = editInternal;
                break;
            case seller:
                isEditable = editSeller;
                break;
            case service:
                isEditable = editService;
                break;
            case product:
                isEditable = editProduct;
                break;
            case marketplace_vendor:
                isEditable = editMarketplaceVendor;
                break;
            case streaming_seat:
                isEditable = editStreamingSeat;
                break;
            case streaming_buyer:
                isEditable = editStreamingBuyer;
                break;
            default:
                isEditable = false;
        }

        return isEditable;
    }

    public boolean isRoleTypeViewable(RoleTypeEnum roleType)
    {
        boolean isViewable;
        switch (roleType)
        {
            case buyer:
                isViewable = viewBuyer;
                break;
            case internal:
                isViewable = viewInternal;
                break;
            case seller:
                isViewable = viewSeller;
                break;
            case service:
                isViewable = viewService;
                break;
            case product:
                isViewable = viewProduct;
                break;
            case marketplace_vendor:
                isViewable = viewMarketplaceVendor;
                break;
            case streaming_seat:
                isViewable = viewStreamingSeat;
                break;
            case streaming_buyer:
                isViewable = viewStreamingBuyer;
                break;
            default:
                isViewable = false;
        }

        return isViewable;
    }

    protected void populateFilterList(List<String> filterList, Boolean isAllowed, Long roleTypeId)
    {
        if (isAllowed)
        {
            filterList.add(roleTypeId.toString());
        }
    }

}
