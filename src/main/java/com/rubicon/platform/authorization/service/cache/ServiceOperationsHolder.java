package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.BaseRoleApiObject;
import com.rubicon.platform.authorization.model.data.acm.Operation;

import java.io.Serializable;
import java.util.*;

public class ServiceOperationsHolder<T extends BaseRoleApiObject> implements Serializable
{
    private T wrapped;
    private Map<String,List<ServiceOperation>> allowedServiceOperationsMap;
    private Map<String,List<ServiceOperation>> deniedOperationsMap;

    protected ServiceOperationsHolder()
    {
    }

    public ServiceOperationsHolder(T wrapped)
    {
        this.wrapped = wrapped;
        allowedServiceOperationsMap = processOperations(wrapped.getAllowedOperations());
        deniedOperationsMap = processOperations(wrapped.getDeniedOperations());
    }

    public T getWrapped()
    {
        return wrapped;
    }

    public List<ServiceOperation> getAllowedOperations(String service)
    {
        List<ServiceOperation> operations = allowedServiceOperationsMap.get(service.toLowerCase());

        if(operations != null)
            return operations;

        return Collections.emptyList();
    }

    public List<ServiceOperation> getDeniedOperations(String service)
    {
        List<ServiceOperation> operations = deniedOperationsMap.get(service.toLowerCase());

        if(operations != null)
            return operations;

        return Collections.emptyList();
    }

    private Map<String,List<ServiceOperation>> processOperations(List<Operation> operations)
    {
        if(operations == null || operations.isEmpty())
            return Collections.emptyMap();

        Map<String,List<ServiceOperation>> map = new HashMap<String, List<ServiceOperation>>();
        for (Operation operation : operations)
        {
            String serviceLower = operation.getService().toLowerCase();
            List<ServiceOperation> existing = map.get(serviceLower);
            if(existing == null)
            {
                existing = new ArrayList<ServiceOperation>();
                map.put(serviceLower,existing);
            }
            existing.add(new ServiceOperation(operation));
        }

        return map;
    }
}
