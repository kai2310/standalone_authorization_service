package com.rubicon.platform.authorization.service.v1.ui.model;

import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.model.api.acm.OperationRequest;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractPermission
{
    abstract public void setValue(String operationAction, boolean isAuthorized);

    abstract public List<OperationRequest> buildOperationRequestList();


    protected List<OperationRequest> buildOperationRequestList(List<String> operationList)
    {
        // Build out all the operations that are need.
        List<OperationRequest> operations = new ArrayList<>();
        for (String action : operationList)

        {
            OperationRequest operation = new OperationRequest();
            operation.setService(Constants.AUTHORIZATION_SERVICE_NAME);
            operation.setResource(Constants.API_RESTRICTION_RESOURCE);
            operation.setAction(action);

            operations.add(operation);
        }

        return operations;
    }


}
