package com.rubicon.platform.authorization.model.api.acm.operation;

import com.rubicon.platform.authorization.model.data.acm.Operation;

public class OperationRequest
{
    private Long id;

    private Operation operation;

    public OperationRequest()
    {

    }

    public OperationRequest(Long id, Operation operation)
    {
        this.id = id;
        this.operation = operation;
    }

    public Long getId() {
        return this.id;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationRequest that = (OperationRequest) o;

        if (id != null
            ? !id.equals(that.id)
            : that.id != null)
        {
            return false;
        }

        return operation != null
               ? !operation.equals(that.operation)
               : that.operation != null;
    }

    @Override
    public int hashCode() {

        int result = id != null
                     ? id.hashCode()
                     : 0;
        result = 31 * result + (operation != null
                                ? operation.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OperationRequest(id=" + id + ", operation=" + operation + ")";
    }
}
