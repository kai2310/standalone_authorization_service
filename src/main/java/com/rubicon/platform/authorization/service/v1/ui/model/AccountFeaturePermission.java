package com.rubicon.platform.authorization.service.v1.ui.model;

import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountFeaturePermission extends AbstractPermission
{
    private boolean removeAllowed = false;
    private boolean reactivateAllowed = false;

    public boolean isRemoveAllowed()
    {
        return removeAllowed;
    }

    public void setRemoveAllowed(boolean removeAllowed)
    {
        this.removeAllowed = removeAllowed;
    }

    public boolean isReactivateAllowed()
    {
        return reactivateAllowed;
    }

    public void setReactivateAllowed(boolean reactivateAllowed)
    {
        this.reactivateAllowed = reactivateAllowed;
    }

    public AccountFeaturePermission()
    {
    }

    public AccountFeaturePermission(boolean removeAllowed, boolean reactivateAllowed)
    {
        this.removeAllowed = removeAllowed;
        this.reactivateAllowed = reactivateAllowed;
    }

    @Override
    protected List<OperationRequest> buildOperationRequestList(List<String> operationList)
    {
        // Build out all the operations that are need.
        List<OperationRequest> operations = new ArrayList<>();
        for (String action : operationList)

        {
            OperationRequest operation = new OperationRequest();
            operation.setService(Constants.FINANCE_PUBLISHER_MANAGEMENT_SERVICE_NAME);
            operation.setResource(Constants.FINANCE_PUBLISHER_MANAGEMENT_RESOURCE);
            operation.setAction(action);

            operations.add(operation);
        }

        return operations;
    }


    @Override
    public void setValue(String operationAction, boolean isAuthorized)
    {
        switch (operationAction)
        {
            case Constants.FINANCE_PUBLISHER_MANAGEMENT_ACTION_REACTIVATE:
                this.reactivateAllowed = isAuthorized;
                break;
            case Constants.FINANCE_PUBLISHER_MANAGEMENT_ACTION_REMOVE:
                this.removeAllowed = isAuthorized;
                break;
            default:
                break;
        }
    }

    @Override
    public List<OperationRequest> buildOperationRequestList()
    {
        return buildOperationRequestList(Arrays
                .asList(Constants.FINANCE_PUBLISHER_MANAGEMENT_ACTION_REACTIVATE,
                        Constants.FINANCE_PUBLISHER_MANAGEMENT_ACTION_REMOVE));
    }
}
