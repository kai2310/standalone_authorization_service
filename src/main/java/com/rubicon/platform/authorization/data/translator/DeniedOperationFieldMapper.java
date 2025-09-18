package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.FieldMapper;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.model.PersistentOperation;
import com.rubicon.platform.authorization.model.data.acm.Operation;

import java.util.ArrayList;
import java.util.List;


public class DeniedOperationFieldMapper extends BaseOperationFieldMapper
		implements FieldMapper<ApiObject,PersistentObject>
{

	@Override
	public String getClientFieldName()
	{
		return "deniedOperations";
	}


	@Override
	public void convertToClient(ObjectWrapper<PersistentObject> persistentObject,
								ObjectWrapper<ApiObject> clientObject, PersistenceContext context)
	{
		List<Operation> clientOperations = new ArrayList<Operation>();
		List<PersistentOperation> persistentOperations = (List<PersistentOperation>) persistentObject.getValue("operations");

		for (PersistentOperation operation : persistentOperations)
		{
			if( (operation.isDeny()))
			{
				clientOperations.add(createOperation(operation));
			}
		}

		clientObject.setValue(getClientFieldName(),clientOperations);

	}

	@Override
	public boolean convertToPersistent(ObjectWrapper<ApiObject> clientObject,
									ObjectWrapper<PersistentObject> persistentObject, PersistenceContext context)
	{
		return false;
	}


}
