package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.model.PersistentOperation;
import com.rubicon.platform.authorization.model.data.acm.Operation;

import java.util.*;


public class AllowedOperationFieldMapper extends BaseOperationFieldMapper
		implements FieldMapper<ApiObject,PersistentObject>
{

	@Override
	public String getClientFieldName()
	{
		return "allowedOperations";
	}


	@Override
	public void convertToClient(ObjectWrapper<PersistentObject> persistentObject,
								ObjectWrapper<ApiObject> clientObject, PersistenceContext context)
	{
		List<Operation> clientOperations = new ArrayList<Operation>();
		List<PersistentOperation> persistentOperations = (List<PersistentOperation>) persistentObject.getValue("operations");

		for (PersistentOperation operation : persistentOperations)
		{
			if( (!operation.isDeny()))
			{
				clientOperations.add(createOperation(operation));
			}
		}

		clientObject.setValue(getClientFieldName(), clientOperations);

	}

	@Override
	public boolean convertToPersistent(ObjectWrapper<ApiObject> clientObject,
									ObjectWrapper<PersistentObject> persistentObject,
                                    PersistenceContext context)
	{
		// we will do both allowed and denied here
		List<Operation> allowed = (List<Operation>) clientObject.getValue("allowedOperations");
		List<Operation> denied = (List<Operation>) clientObject.getValue("deniedOperations");

		// exit if neither are specified
		if(allowed == null && denied == null)
			return false;

		List<PersistentOperation> persistentOperations = (List<PersistentOperation>) persistentObject.getValue("operations");
		List<PersistentOperation> allowedPersistentOperations = new ArrayList<PersistentOperation>();
		List<PersistentOperation> deniedPersistentOperations = new ArrayList<PersistentOperation>();

		for (PersistentOperation operation : persistentOperations)
		{
			if(operation.isDeny())
				deniedPersistentOperations.add(operation);
			else
				allowedPersistentOperations.add(operation);
		}

		persistentOperations.clear();

		if(allowed != null)
		{
			for (Operation operation : allowed)
			{
				persistentOperations.add(createPersistentOperation(operation,false));
			}
		}
		else
		{
			persistentOperations.addAll(allowedPersistentOperations);
		}

		if(denied != null)
		{
			for (Operation operation : denied)
			{
				persistentOperations.add(createPersistentOperation(operation,true));
			}
		}
		else
		{
			persistentOperations.addAll(deniedPersistentOperations);
		}

        return true;
	}

}
