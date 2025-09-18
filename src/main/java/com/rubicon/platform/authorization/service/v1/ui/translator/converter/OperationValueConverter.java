package com.rubicon.platform.authorization.service.v1.ui.translator.converter;

import com.rubicon.platform.authorization.translator.ObjectValueConverter;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.ui.acm.Operation;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class OperationValueConverter
        implements ObjectValueConverter<List<Operation>, List<com.rubicon.platform.authorization.model.data.acm.Operation>>
{
    @Override
    public List<Operation> convertToClientValue(
            List<com.rubicon.platform.authorization.model.data.acm.Operation> persistentValue, TranslationContext context)
    {
        List<Operation> operations = null;
        if (!CollectionUtils.isEmpty(persistentValue))
        {
            operations = new ArrayList<>();
            for (com.rubicon.platform.authorization.model.data.acm.Operation currentOperation : persistentValue)
            {
                Operation operation = new Operation();
                operation.setAction(currentOperation.getAction());
                operation.setService(currentOperation.getService());
                operation.setResource(currentOperation.getResource());
                operation.setProperties(currentOperation.getProperties());

                operations.add(operation);
            }
        }

        return operations;
    }

    @Override
    public List<com.rubicon.platform.authorization.model.data.acm.Operation> convertToPersistentValue(
            List<Operation> clientValue, TranslationContext context)
    {
        List<com.rubicon.platform.authorization.model.data.acm.Operation> operations = null;
        if (!CollectionUtils.isEmpty(clientValue))
        {
            operations = new ArrayList<>();
            for (Operation currentOperation : clientValue)
            {
                com.rubicon.platform.authorization.model.data.acm.Operation operation =
                        new com.rubicon.platform.authorization.model.data.acm.Operation();

                operation.setAction(currentOperation.getAction());
                operation.setService(currentOperation.getService());
                operation.setResource(currentOperation.getResource());
                operation.setProperties(currentOperation.getProperties());

                operations.add(operation);
            }
        }

        return operations;
    }
}

