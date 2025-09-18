package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.data.persistence.AccountGroupLoader;
import com.rubicon.platform.authorization.data.persistence.AccountGroupTypeLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class AccountGroupValidator extends BaseFrameworkValidator<AccountGroup,PersistentAccountGroup>
{
    public static final String ACCOUNT_GROUP_ACCOUNT_REQUIRED = "ACCOUNT_GROUP_ACCOUNT_REQUIRED";
    public static final String ACCOUNT_GROUP_ACCOUNT_EXCLUSIVE = "ACCOUNT_GROUP_ACCOUNT_EXCLUSIVE";
    public static final String DYNAMIC_ACCOUNT_GROUP_TO_ACCOUNTS = "DYNAMIC_ACCOUNT_GROUP_TO_ACCOUNTS";
    public static final String ACCOUNTS_TO_DYNAMIC_ACCOUNT_GROUP = "ACCOUNTS_TO_DYNAMIC_ACCOUNT_GROUP";

    @Autowired
    private AccountGroupLoader accountGroupLoader;

    @Autowired
    private AccountGroupTypeLoader accountGroupTypeLoader;

    @Autowired
    private AccountLoader accountLoader;

    protected void setAccountGroupTypeLoader(AccountGroupTypeLoader accountGroupTypeLoader)
    {
        this.accountGroupTypeLoader = accountGroupTypeLoader;
    }

    protected void setAccountGroupLoader(AccountGroupLoader accountGroupLoader)
    {
        this.accountGroupLoader = accountGroupLoader;
    }

    protected void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    @Override
    protected void validateCreate(AccountGroup clientObject, ValidationErrorContext errorContext,
                                  PersistenceContext persistenceContext)
    {
        validateRequired(errorContext,"label",clientObject.getLabel());
        validateNotBlank(errorContext, "label", clientObject.getLabel());
        validateLength(errorContext, "label", clientObject.getLabel(), 64);
        validateLength(errorContext, "accountType", clientObject.getAccountType(), 64);
        validateNotBlank(errorContext, "accountType", clientObject.getAccountType());
        validateRequired(errorContext, "accountGroupTypeId", clientObject.getAccountGroupTypeId());
        validateNotEmpty(errorContext, "accountIds", clientObject.getAccountIds());
        if(clientObject.getAccountGroupTypeId() != null && !accountGroupTypeLoader.exists(clientObject.getAccountGroupTypeId()))
            errorContext.addValidationError(NOT_FOUND,"accountGroupTypeId","AccountGroupType",clientObject.getAccountGroupTypeId());

        if(clientObject.getAccountIds() == null && StringUtils.isBlank(clientObject.getAccountType()))
            errorContext.addValidationError(ACCOUNT_GROUP_ACCOUNT_REQUIRED,"AccountGroup");

        if(clientObject.getAccountIds() != null && StringUtils.isNotBlank(clientObject.getAccountType()))
            errorContext.addValidationError(ACCOUNT_GROUP_ACCOUNT_EXCLUSIVE, "AccountGroup");

        validateAccountIds(clientObject.getAccountIds(),errorContext);
        validateReadOnly(errorContext,"status",clientObject.getStatus());

    }

    @Override
    protected void validateUpdate(AccountGroup clientObject, PersistentAccountGroup persistentObject,
                                  ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        validateNotBlank(errorContext,"label",clientObject.getLabel());
        validateNotChanged(errorContext,"status",clientObject.getStatus(),persistentObject.getStatus());
        validateLength(errorContext,"label",clientObject.getLabel(),64);
        validateLength(errorContext, "accountType", clientObject.getAccountType(), 64);
        validateNotBlank(errorContext, "accountType", clientObject.getAccountType());
        validateNotEmpty(errorContext,"accountIds",clientObject.getAccountIds());
        if(valueChanged(clientObject.getAccountGroupTypeId(),persistentObject.getAccountGroupTypeId())
                && !accountGroupTypeLoader.exists(clientObject.getAccountGroupTypeId()))
            errorContext.addValidationError(NOT_FOUND,"accountGroupTypeId","AccountGroupType",clientObject.getAccountGroupTypeId());

        boolean isDynamicGroup = StringUtils.isNotEmpty(persistentObject.getAccountType());

        if(isDynamicGroup)
        {
            if(clientObject.getAccountIds() != null)
                errorContext.addValidationError(DYNAMIC_ACCOUNT_GROUP_TO_ACCOUNTS,"AccountGroup");
        }
        else
        {
            if(StringUtils.isNotEmpty(clientObject.getAccountType()))
                errorContext.addValidationError(ACCOUNTS_TO_DYNAMIC_ACCOUNT_GROUP,"AccountGroup");
        }

        if(valueChanged(clientObject.getAccountIds(),persistentObject.getAccountIds()))
            validateAccountIds(clientObject.getAccountIds(),errorContext);

    }

    @Override
    protected void validateCreateConflict(AccountGroup clientObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(StringUtils.isNotBlank(clientObject.getLabel()) && !accountGroupLoader.isLabelUnique(clientObject.getLabel()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateUpdateConflict(AccountGroup clientObject, PersistentAccountGroup persistentObject,
                                          ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        if(valueChanged(clientObject.getLabel(),persistentObject.getLabel())
           && StringUtils.isNotBlank(clientObject.getLabel())
           && !accountGroupLoader.isLabelUnique(clientObject.getLabel(),persistentObject.getId()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateDeleteConflict(PersistentAccountGroup persistentObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(accountGroupLoader.hasReferences(persistentObject.getId()))
            errorContext.addValidationError(DELETE_CONFLICT,"id","AccountGroup",persistentObject.getId().toString());
    }

    private void validateAccountIds(List<Long> accountIds,ValidationErrorContext errorContext)
    {
        if(accountIds != null && accountIds.size()>0)
        {
            Set<Long> unique = new HashSet<>(accountIds);
            unique.remove(null);
            List<Long> found = accountLoader.findIds(unique);
            if(found.size() != unique.size())
            {
                unique.removeAll(found);
                errorContext.addValidationError(NOT_FOUND_IDS, "accountIds", "Account", StringUtils.join(unique,","));
            }
        }
    }

}
