package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "operation", "action", "operationType"})
public class EditBaseOperationRequest
{
    private Long id;
    private Operation operation;
    private EditActionEnum action;
    private EditOperationEnum operationType;

    public EditBaseOperationRequest()
    {

    }

    public EditBaseOperationRequest(Long id, Operation operation, EditActionEnum action)
    {
        this(id, operation, action, EditOperationEnum.allowed);
    }

    public EditBaseOperationRequest(Long id, Operation operation, EditActionEnum action, EditOperationEnum operationType)
    {
        this.id = id;
        this.operation = operation;
        this.action = action;
        this.operationType = operationType;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }

    public EditActionEnum getAction()
    {
        return action;
    }

    public void setAction(EditActionEnum action)
    {
        this.action = action;
    }

    public EditOperationEnum getOperationType()
    {
        return operationType;
    }

    public void setOperationType(EditOperationEnum operationType)
    {
        this.operationType = operationType;
    }
}
