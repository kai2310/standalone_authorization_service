package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.service.cache.ServiceOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: mhellkamp
 * Date: 11/29/12
 */
public class OperationMatcher
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	public OperationMatch matchOperation(ServiceOperation operation, String resource, String action)
	{

		OperationMatch match = OperationMatch.service;

        // if resource is a wildcard then we can skip any additional checks
        if (!operation.isWildcardResource())
        {
            if (operation.matchResource(resource))
            {
                match = OperationMatch.resource;
                // if action is a wildcard then we can skip any additional checks
                if (!operation.isWildcardAction())
                {
                    if (operation.matchAction(action))
                    {
                        match = OperationMatch.action;
                    }
                    else
                    {
                        match = OperationMatch.none;
                    }
                }
            }
            else
            {
                match = OperationMatch.none;
            }
        }

        if(logger.isDebugEnabled())
			logger.debug(
					String.format("Match=%s for requested resource=\"%s\", action=\"%s\" against operation resource=\"%s\", action=\"%s\" ",
							match, resource,action, operation.getResource(),operation.getAction()));

		return match;
	}

}
