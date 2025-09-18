package com.rubicon.platform.authorization.model.ui.acm;

public class EditFeatureOperationRequest extends EditBaseOperationRequest
{
    public EditFeatureOperationRequest()
    {

    }

    public EditFeatureOperationRequest(Long id, Operation operation, EditActionEnum action)
    {
        super(id, operation, action);
    }

    public EditFeatureOperationRequest(Long id, Operation operation, EditActionEnum action, EditOperationEnum operationType)
    {
        super(id, operation, action, operationType);
    }
}
