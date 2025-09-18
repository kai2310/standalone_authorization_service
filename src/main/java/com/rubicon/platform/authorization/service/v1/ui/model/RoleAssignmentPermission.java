package com.rubicon.platform.authorization.service.v1.ui.model;

import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;
import com.rubicon.platform.authorization.model.ui.acm.AccountGroupEnum;

import java.util.List;

public class RoleAssignmentPermission extends AbstractPermission
{
    private boolean assignAllPublisher = false;
    private boolean assignAllSeat = false;
    private boolean assignAllMarketplaceVendor = false;
    private boolean assignAllStreamingSeat = false;
    private boolean assignAllStreamingBuyer = false;
    private boolean assignInitialRoleAssignment = false;

    public boolean isAssignAllPublisher()
    {
        return assignAllPublisher;
    }

    public void setAssignAllPublisher(boolean assignAllPublisher)
    {
        this.assignAllPublisher = assignAllPublisher;
    }

    public boolean isAssignAllSeat()
    {
        return assignAllSeat;
    }

    public void setAssignAllSeat(boolean assignAllSeat)
    {
        this.assignAllSeat = assignAllSeat;
    }

    public boolean isAssignInitialRoleAssignment()
    {
        return assignInitialRoleAssignment;
    }

    public void setAssignInitialRoleAssignment(boolean assignInitialRoleAssignment)
    {
        this.assignInitialRoleAssignment = assignInitialRoleAssignment;
    }

    public boolean isAssignAllMarketplaceVendor()
    {
        return assignAllMarketplaceVendor;
    }

    public void setAssignAllMarketplaceVendor(boolean assignAllMarketplaceVendor)
    {
        this.assignAllMarketplaceVendor = assignAllMarketplaceVendor;
    }

    public boolean isAssignAllStreamingSeat()
    {
        return assignAllStreamingSeat;
    }

    public void setAssignAllStreamingSeat(boolean assignAllStreamingSeat)
    {
        this.assignAllStreamingSeat = assignAllStreamingSeat;
    }

    public boolean isAssignAllStreamingBuyer()
    {
        return assignAllStreamingBuyer;
    }

    public void setAssignAllStreamingBuyer(boolean assignAllStreamingBuyer)
    {
        this.assignAllStreamingBuyer = assignAllStreamingBuyer;
    }

    @Override
    public void setValue(String operationAction, boolean isAuthorized)
    {
        switch (operationAction)
        {
            case Constants.ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_PUBLISHER:
                this.assignAllPublisher = isAuthorized;
                break;
            case Constants.ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_SEAT:
                this.assignAllSeat = isAuthorized;
                break;
            case Constants.ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_MARKETPLACE_VENDOR:
                this.assignAllMarketplaceVendor = isAuthorized;
                break;
            case Constants.ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_SEAT:
                this.assignAllStreamingSeat = isAuthorized;
                break;
            case Constants.ROLE_ASSIGNMENT_ACTION_ASSIGN_ALL_STREAMING_BUYER:
                this.assignAllStreamingBuyer = isAuthorized;
                break;
            case Constants.ROLE_ASSIGNMENT_ACTION_ADD_INITIAL_ROLE_ASSIGNMENT:
                this.assignInitialRoleAssignment = isAuthorized;
            default:
                break;
        }
    }

    @Override
    public List<OperationRequest> buildOperationRequestList()
    {
        return super.buildOperationRequestList(Constants.ROLE_ASSIGNMENT_ACTION_LIST);
    }

    public boolean isAuthorizedForAccountGroup(AccountGroupEnum accountGroup)
    {
        boolean isAuthorized;
        switch (accountGroup)
        {
            case ALL_PUBLISHERS:
                isAuthorized = this.assignAllPublisher;
                break;
            case ALL_SEATS:
                isAuthorized = this.assignAllSeat;
                break;
            case ALL_MARKETPLACE_VENDORS:
                isAuthorized = this.assignAllMarketplaceVendor;
                break;
            case ALL_STREAMING_SEATS:
                isAuthorized = this.assignAllStreamingSeat;
                break;
            case ALL_STREAMING_BUYERS:
                isAuthorized = this.assignAllStreamingBuyer;
                break;
            default:
                isAuthorized = false;
        }

        return isAuthorized;
    }
}
