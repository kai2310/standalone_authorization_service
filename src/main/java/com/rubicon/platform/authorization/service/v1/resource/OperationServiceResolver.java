package com.rubicon.platform.authorization.service.v1.resource;

import com.dottydingo.hyperion.core.persistence.ExceptionMappingDecorator;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeEvent;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeListener;
import com.rubicon.platform.authorization.service.exception.ServiceException;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import com.rubicon.platform.authorization.model.data.acm.Role;
import com.rubicon.platform.authorization.model.api.acm.operation.OperationRequest;
import org.springframework.util.CollectionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class OperationServiceResolver
{
    private Logger logger = LoggerFactory.getLogger(OperationServiceResolver.class);

    @Autowired
    @Qualifier("rolePersistenceOperations")
    private ExceptionMappingDecorator rolePersistenceOperations;

    @Autowired
    @Qualifier("accountFeaturePersistenceOperations")
    private ExceptionMappingDecorator accountFeaturePersistenceOperations;

    private Role getRequestedRole(Long roleId, PersistenceContext context)
    {
        Role role = null;

        if (roleId == null)
        {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Invalid ID for Role.");
        } else {

            List<Role> roles = rolePersistenceOperations.findByIds(Arrays.asList(roleId), context);

            if (roles == null || roles.size() == 0) {
                throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Invalid ID for Role.");
            } else {

                if (roles.size() > 1)
                {
                    logger.warn("Expected to find only one role, but found " + roles.size());
                }
                role = roles.get(0);
            }
        }

        return role;
    }

    private AccountFeature getRequestedAccountFeature(Long accountFeatureId, PersistenceContext context)
    {
        AccountFeature accountFeature = null;

        if (accountFeatureId == null)
        {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Invalid ID for Account Feature.");
        } else {
            List<AccountFeature> accountFeatures = accountFeaturePersistenceOperations.findByIds(Arrays.asList(accountFeatureId), context);

            if (accountFeatures == null || accountFeatures.size() == 0)
            {
                throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Invalid ID for Account Feature.");
            } else {
                if (accountFeatures.size() > 1)
                {
                    logger.warn("Expected to find only one account feature, but found " + accountFeatures.size());
                }

                accountFeature = accountFeatures.get(0);
            }
        }

        return accountFeature;
    }

    // validate operation to make sure all fields populated, and properties must have one property
    private void validateRequestedOperation(Operation requested)
    {
        boolean isValid = (requested != null) &&
                (!StringUtils.isEmpty(requested.getService()) &&
                (!StringUtils.isEmpty(requested.getResource())) &&
                (!StringUtils.isEmpty(requested.getAction())) &&
                (areRequestedPropertiesValid(requested.getProperties())));

        if (!isValid)
        {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "All fields must be populated for this operation, and properties must not be empty or contain invalid value(s).");
        }
    }

    private boolean areRequestedPropertiesValid(List<String> properties)
    {
        if (CollectionUtils.isEmpty(properties))
        {
            return false;
        }

        for(String property : properties)
        {
            if (property == null || StringUtils.isEmpty(property.trim()))
            {
                return false;
            }
        }

        return true;
    }

    private boolean areTwoOperationsEqual(Operation operation1, Operation operation2)
    {
        return (operation1.getService().toLowerCase().equals(operation2.getService().toLowerCase()))
                && (operation1.getResource().toLowerCase().equals(operation2.getResource().toLowerCase()))
                && (operation1.getAction().toLowerCase().equals(operation2.getAction().toLowerCase()));
    }


    // validate matched operation if role/account feature has one
    private void validateMatchedOperation(Operation matched)
    {
        if (matched.getProperties() == null || matched.getProperties().size() == 0)
        {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "The matched operation must have at least one property.");
        }
    }

    private List<Operation> updateAllowedOperationList(List<Operation> allowedOperations, Operation requestedOperation)
    {
        validateRequestedOperation(requestedOperation);

        if (allowedOperations == null) {
            allowedOperations = new ArrayList<>();
        }

        // in case there are multiple operations having same service, resource and action as requested one
        // we store indexes of matched ones in the list
        List<Integer> found = new ArrayList<>();
        for (int index = 0; index < allowedOperations.size(); index++)
        {
            Operation allowed = allowedOperations.get(index);
            if (areTwoOperationsEqual(allowed, requestedOperation))
            {
                validateMatchedOperation(allowed);
                found.add(index);
            }
        }

        if (CollectionUtils.isEmpty(found))
        {
            allowedOperations.add(requestedOperation);
        } else if (found.size() == 1)
        {
            allowedOperations.get(found.get(0)).setProperties(requestedOperation.getProperties());
        } else {

            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "The operation provided appears multiple times in the role/account feature. Please clean up corresponding role/account feature.");
        }

        return allowedOperations;
    }

    public Role upsertOperationToRole(OperationRequest operationRequest, PersistenceContext context)
    {
        Long roleId = operationRequest.getId();
        Role role = getRequestedRole(roleId, context);
        List<Operation> operations = role.getAllowedOperations();
        role.setAllowedOperations(updateAllowedOperationList(operations, operationRequest.getOperation()));

        Role updatedRole = (Role) rolePersistenceOperations.updateItem(Arrays.asList(roleId), role, context);

        // update cache for role
        processEntityChange(context);

        return updatedRole;
    }

    public AccountFeature upsertOperationToAccountFeature(OperationRequest operationRequest, PersistenceContext context)
    {
        Long accountFeatureId = operationRequest.getId();
        AccountFeature accountFeature = getRequestedAccountFeature(accountFeatureId, context);
        List<Operation> operations = accountFeature.getAllowedOperations();
        accountFeature.setAllowedOperations(updateAllowedOperationList(operations, operationRequest.getOperation()));

        AccountFeature updatedAccountFeature = (AccountFeature) accountFeaturePersistenceOperations.updateItem(Arrays.asList(accountFeatureId), accountFeature, context);

        //update cache for account feature
        processEntityChange(context);

        return updatedAccountFeature;
    }

    // The following code is not triggered when api endpoints are using persistence operation only
    // which cause cache not updated.
    private void processEntityChange(PersistenceContext context)
    {
        if (context.getEntityPlugin().hasEntityChangeListeners())
        {
            // get cache change listener to update cache in this case
            List entityChangeListeners = context.getEntityPlugin().getEntityChangeListeners();
            Iterator listenerIterator = entityChangeListeners.iterator();

            while(listenerIterator.hasNext()) {
                EntityChangeListener entityChangeListener = (EntityChangeListener)listenerIterator.next();
                Iterator eventIterator = context.getEntityChangeEvents().iterator();

                while(eventIterator.hasNext()) {
                    EntityChangeEvent event = (EntityChangeEvent)eventIterator.next();
                    entityChangeListener.processEntityChange(event);
                }
            }
        }
    }

    // used in unit tests
    public void setRolePersistenceOperations(ExceptionMappingDecorator rolePersistenceOperations) {
        this.rolePersistenceOperations = rolePersistenceOperations;
    }

    public void setAccountFeaturePersistenceOperations(ExceptionMappingDecorator accountFeaturePersistenceOperations) {
        this.accountFeaturePersistenceOperations = accountFeaturePersistenceOperations;
    }
}
