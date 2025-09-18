package com.rubicon.platform.authorization.service;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.service.resolver.*;
import com.rubicon.platform.authorization.service.v1.api.AuthorizationServiceException;
import com.rubicon.platform.authorization.model.api.acm.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jlukas on 10/13/15.
 */
public abstract class BaseAuthorizeController extends BaseController
{
    private Logger logger = LoggerFactory.getLogger(BaseAuthorizeController.class);

    private IdParser idParser = IdParser.STANDARD_ID_PARSER;

    @Autowired
    private RoleResolver roleResolver;

    @Autowired
    SubjectAccountMatchResolver subjectAccountMatchResolver;

    @Autowired
    private AccountFeatureResolver accountFeatureResolver;

    protected AuthorizeResponse baseAuthorizeOperation(List<CompoundId> subjectIds, CompoundId accountId,
                                                           String service,
                                                           String resource, String action,
                                                           boolean returnAuthorizedAccounts)
    {
        ResolvedSubjectAccounts roleMatches =
                roleResolver.resolveOperation(subjectIds, accountId, service, resource, action);
        SubjectAccountMatches subjectAccountMatches = subjectAccountMatchResolver.resolve(accountId, roleMatches);

        AuthorizeResponse response = new AuthorizeResponse();
        response.setService(service);
        response.setResource(resource);
        response.setAction(action);

        if (!subjectAccountMatches.isAuthorized())
        {
            logger.debug("Subject(s) do not have permission for this action.");
            response.setAuthorized(false);
            response.setReason("Subject(s) do not have permissions for this action.");
            return response;
        }

        ResolvedOperationAccounts accountMatches = accountFeatureResolver.resolveOperation(service, resource, action,
                subjectAccountMatches
        );

        List<CompoundId> authorizedAccountIds = accountMatches.getAuthorizedAccounts();

        if (authorizedAccountIds.size() == 0)
        {
            logger.debug("Feature is not allowed by any accounts.");
            response.setAuthorized(false);
            response.setReason("Feature is not allowed by any accounts.");
            return response;
        }

        response.setAuthorized(true);
        response.setScope(new ArrayList<String>(subjectAccountMatches.getScope()));

        List<AuthorizedAccount> authorizedAccounts = new ArrayList<AuthorizedAccount>();
        for (CompoundId authorizedAccountId : authorizedAccountIds)
        {
            logger.debug("Resolving properties for accountId={}", authorizedAccountId);
            AuthorizedAccount account = new AuthorizedAccount();
            account.setAccountId(authorizedAccountId.asIdString());

            AssignedAccountOperation operation = accountMatches.getAccountAssignedOperation(authorizedAccountId);
            account.setAllowedProperties(operation.resolveAllowedProperties());
            account.setDeniedProperties(operation.resolveDeniedProperties());

            authorizedAccounts.add(account);
        }

        if (returnAuthorizedAccounts)
        {
            response.setAuthorizedAccounts(authorizedAccounts);
        }

        return response;
    }

    protected List<AuthorizeResponse> baseAuthorizeOperations(Set<String> subjects,
                                                                  BaseAuthorizeOperationsRequest authorizeOperationsRequest,
                                                                  boolean returnAuthorizedAccounts)
    {
        List<AuthorizeResponse> responses = new ArrayList<>();

        String accountContext = authorizeOperationsRequest.getAccountContext();
        List<OperationRequest> operations = authorizeOperationsRequest.getOperations();

        if (operations == null || operations.isEmpty())
        {
            throw new AuthorizationServiceException(422, "Missing or empty parameter \"service\".");
        }

        for (OperationRequest operation : operations)
        {
            validateOperation(operation);
        }

        CompoundId accountId = null;
        try
        {
            accountId = idParser.parseId(accountContext);
        }
        catch (IllegalArgumentException e)
        {
            throw new AuthorizationServiceException(422, e.getMessage());
        }
        if (accountId != null && accountId.hasWildcards())
        {
            throw new AuthorizationServiceException(422, "AccountContext may not contain wildcards.");
        }

        Set<String> subjectTypes = new HashSet<String>();
        List<CompoundId> subjectIds = new ArrayList<CompoundId>();
        for (String s : subjects)
        {
            CompoundId id = null;
            try
            {
                id = idParser.parseId(s);
            }
            catch (IllegalArgumentException e)
            {
                throw new AuthorizationServiceException(422, e.getMessage());
            }

            if (id == null)
            {
                throw new AuthorizationServiceException(422, "Subject can not be blank");
            }
            if (id.hasWildcards())
            {
                throw new AuthorizationServiceException(422, "Subjects may not contain wildcards.");
            }

            subjectTypes.add(id.getIdType());
            subjectIds.add(id);
        }

        for (OperationRequest operation : operations)
        {
            responses.add(baseAuthorizeOperation(subjectIds, accountId, operation.getService(), operation.getResource(),
                    operation.getAction(), returnAuthorizedAccounts));
        }

        return responses;
    }


    protected void validateAuthorizeRequest(BaseAuthorizeRequest authorizeRequest)
    {
        String service = authorizeRequest.getService();
        String resource = authorizeRequest.getResource();
        String action = authorizeRequest.getAction();

        if (StringUtils.isBlank(service))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"service\".");
        }

        if (service.equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"service\" can not be a wildcard.");
        }

        if (StringUtils.isBlank(resource))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"resource\".");
        }

        if (resource.equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"resource\" can not be a wildcard.");
        }

        if (StringUtils.isBlank(action))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"action\".");
        }

        if (action.equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"action\" can not be a wildcard.");
        }
    }

    protected void validateOperation(OperationRequest operationRequest)
    {
        if (StringUtils.isBlank(operationRequest.getService()))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"service\".");
        }

        if (operationRequest.getService().equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"service\" can not be a wildcard.");
        }

        if (StringUtils.isBlank(operationRequest.getResource()))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"resource\".");
        }

        if (operationRequest.getResource().equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"resource\" can not be a wildcard.");
        }

        if (StringUtils.isBlank(operationRequest.getAction()))
        {
            throw new AuthorizationServiceException(422, "Missing parameter \"action\".");
        }

        if (operationRequest.getAction().equals("*"))
        {
            throw new AuthorizationServiceException(422, "Parameter \"action\" can not be a wildcard.");
        }
    }
}
