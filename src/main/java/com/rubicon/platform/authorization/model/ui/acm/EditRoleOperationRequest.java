package com.rubicon.platform.authorization.model.ui.acm;

public class EditRoleOperationRequest extends EditBaseOperationRequest
{
    public EditRoleOperationRequest()
    {

    }

    public EditRoleOperationRequest(Long id, Operation operation, EditActionEnum action)
    {
        super(id, operation, action);
    }

    public EditRoleOperationRequest(Long id, Operation operation, EditActionEnum action, EditOperationEnum operationType)
    {
        super(id, operation, action, operationType);
    }
}
