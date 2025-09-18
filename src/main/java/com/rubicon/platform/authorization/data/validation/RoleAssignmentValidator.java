package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.persistence.AccountGroupLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentLoader;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class RoleAssignmentValidator extends BaseFrameworkValidator<RoleAssignment,PersistentRoleAssignment>
{
    public static final String INVALID_ID_FORMAT = "INVALID_ID_FORMAT";
    public static final String SUBJECT_WILDCARD_NOT_ALLOWED = "SUBJECT_WILDCARD_NOT_ALLOWED";
    public static final String DUPLICATE_ENTRIES = "DUPLICATE_ENTRIES";
    public static final String REALM_MISMATCH = "REALM_MISMATCH";
    public static final String DUPLICATE_ROLE_ASSIGNMENT = "DUPLICATE_ROLE_ASSIGNMENT";
    public static final String DELETED_ITEM = "DELETED_ITEM";
    public static final String ACCOUNT_REQUIRED = "ACCOUNT_REQUIRED";
    public static final String ACCOUNT_EXCLUSIVE = "ACCOUNT_EXCLUSIVE";

    protected IdParser idParser = IdParser.STANDARD_ID_PARSER;

    @Autowired
    private AccountLoader accountLoader;

    @Autowired
    private RoleAssignmentLoader roleAssignmentLoader;

    @Autowired
    private AccountGroupLoader accountGroupLoader;

    @Autowired
    private RoleLoader roleLoader;

    protected void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    protected void setRoleAssignmentLoader(RoleAssignmentLoader roleAssignmentLoader)
    {
        this.roleAssignmentLoader = roleAssignmentLoader;
    }

    protected void setRoleLoader(RoleLoader roleLoader)
    {
        this.roleLoader = roleLoader;
    }

    protected void setAccountGroupLoader(AccountGroupLoader accountGroupLoader)
    {
        this.accountGroupLoader = accountGroupLoader;
    }

    @Override
    protected void validateCreate(RoleAssignment clientObject, ValidationErrorContext errorContext,
                                  PersistenceContext persistenceContext)
    {

        validateRequired(errorContext,"ownerAccount", clientObject.getOwnerAccount());
        validateAccountRequired(errorContext,clientObject.getAccount(),clientObject.getAccountGroupId());
        validateRequired(errorContext,"subject", clientObject.getSubject());
        validateRequired(errorContext,"roleId", clientObject.getRoleId());
        validateLength(errorContext,"realm",clientObject.getRealm(),64);
        validateReadOnly(errorContext,"status",clientObject.getStatus());
        if(!isUnique(clientObject.getScope()))
            errorContext.addValidationError(DUPLICATE_ENTRIES,"scope","scope");

        CompoundId ownerAccount = null;
        CompoundId subject = null;
        CompoundId accountContext = null;

        if(clientObject.getOwnerAccount() != null)
            ownerAccount = validateAccount(errorContext,"ownerAccount",clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);

        if(ownerAccount != null && !ownerAccount.hasWildcards() && !accountLoader.exists(ownerAccount))
            errorContext.addValidationError(NOT_FOUND,"ownerAccount","Account",ownerAccount);


        if(clientObject.getAccount() != null)
            accountContext = validateAccount(errorContext,"account",clientObject.getAccount(), WildcardValidator.noWildcardAllowed);

        if(accountContext != null  && !accountContext.hasWildcards() && !accountLoader.exists(accountContext))
            errorContext.addValidationError(NOT_FOUND,"account","Account",accountContext);


        if(clientObject.getSubject() != null)
        {
            subject = parseId(clientObject.getSubject());
            if(subject == null)
                errorContext.addValidationError(INVALID_ID_FORMAT,"subject",clientObject.getSubject(),"subject");
            else
            {
                if(!WildcardValidator.noWildcardAllowed.isValid(subject))
                    errorContext.addValidationError(SUBJECT_WILDCARD_NOT_ALLOWED,"subject",clientObject.getSubject(),"subject");
            }
        }

        if(clientObject.getRoleId() != null)
        {
            PersistentRole role = roleLoader.find(clientObject.getRoleId());
            if(role == null)
                // by casting roleId to string to avoid having comma in the number, such as 1,000 in error message
                errorContext.addValidationError(NOT_FOUND,"roleId","Role",clientObject.getRoleId().toString());
            else
            {
                if(StringUtils.isNotEmpty(clientObject.getRealm())
                   && StringUtils.isNotEmpty(role.getRealm()))
                {
                    if(!clientObject.getRealm().equals(role.getRealm()))
                        errorContext.addValidationError(REALM_MISMATCH,"realm",clientObject.getRealm(),role.getRealm());
                }
            }
        }

        if(clientObject.getAccountGroupId() != null && !accountGroupLoader.exists(clientObject.getAccountGroupId()))
            // by casting accountGroupId to string to avoid having comma in the number, such as 1,000 in error message
            errorContext.addValidationError(NOT_FOUND,"accountGroupId","AccountGroup", clientObject.getAccountGroupId().toString());
    }

    @Override
    protected void validateCreateConflict(RoleAssignment clientObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(StringUtils.isNotBlank(clientObject.getSubject()) && clientObject.getRoleId() != null &&
           (StringUtils.isNotBlank(clientObject.getAccount()) || clientObject.getAccountGroupId() != null)
           && !containsUnicode(clientObject.getAccount()))
        {
            CompoundId subject = CompoundId.build(clientObject.getSubject());
            CompoundId accountContext = CompoundId.build(clientObject.getAccount());

            if(subject != null && (accountContext!=null || clientObject.getAccountGroupId() != null) &&
                roleAssignmentLoader.exists(subject,accountContext,clientObject.getRoleId(), clientObject.getAccountGroupId()))
            {
                String accountField = clientObject.getAccount() == null ? "accountGroupId" : "account";
                String accountValue = clientObject.getAccount() == null ? asString(clientObject.getAccountGroupId()) : clientObject.getAccount();
                errorContext.addValidationError(DUPLICATE_ROLE_ASSIGNMENT, "RoleAssignment", subject, clientObject.getRoleId(),
                        accountField,accountValue);
            }
        }
    }

    @Override
    protected void validateUpdate(RoleAssignment clientObject, PersistentRoleAssignment persistentObject,
                                  ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        // can't edit a deleted item
        if(persistentObject.getStatus() == Status.DELETED)
        {
            errorContext.addValidationError(DELETED_ITEM,"RoleAssignment","RoleAssignment");
            return;
        }


        validateNotChanged(errorContext, "subject", clientObject.getSubject(), getIdString(persistentObject.getSubject()));
        validateNotChanged(errorContext, "account", clientObject.getAccount(), getIdString(persistentObject.getAccount()));
        validateNotChanged(errorContext, "roleId", clientObject.getRoleId(), persistentObject.getRoleId());
        validateNotChanged(errorContext, "status", clientObject.getStatus(), persistentObject.getStatus());
        validateNotChanged(errorContext, "accountGroupId", clientObject.getAccountGroupId(), persistentObject.getAccountGroupId());

        CompoundId ownerAccount = null;
        if(clientObject.getOwnerAccount() != null)
            ownerAccount = validateAccount(errorContext,"ownerAccount",clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);

        if(valueChanged(ownerAccount,persistentObject.getOwnerAccount()) && !ownerAccount.hasWildcards() && !accountLoader.exists(ownerAccount) )
            errorContext.addValidationError(NOT_FOUND,"ownerAccount","Account",ownerAccount);

        if(!isUnique(clientObject.getScope()))
            errorContext.addValidationError(DUPLICATE_ENTRIES,"scope","scope");

        validateNotChanged(errorContext, "realm", clientObject.getRealm(), persistentObject.getRealm());


    }

    protected void validateAccountRequired(ValidationErrorContext errorContext,String account,Long accountGroupId)
    {
        boolean accountMissing = StringUtils.isBlank(account);
        boolean accountGroupMissing = accountGroupId == null;
        if(accountMissing && accountGroupMissing)
            errorContext.addValidationError(ACCOUNT_REQUIRED,"RoleAssignment");
        else if(!accountMissing && !accountGroupMissing)
            errorContext.addValidationError(ACCOUNT_EXCLUSIVE,"RoleAssignment");
            
    }

    private String getIdString(CompoundId id)
    {
        if(id==null)
            return null;
        return id.asIdString();
    }

    protected <T> boolean isUnique(Collection<T> collection)
    {
        if(collection == null || collection.size() == 0)
            return true;

        Set<T> set = new HashSet<T>(collection);
        return collection.size() == set.size();
    }

    protected CompoundId parseId(String account)
    {
        CompoundId id = null;
        try
        {
            return idParser.parseId(account);
        }
        catch (IllegalArgumentException ignore){}
        return null;
    }

    protected CompoundId validateAccount(ValidationErrorContext errorContext, String field, String value,
                                       WildcardValidator fullWildcardAllowed)
    {
        if(containsUnicode(value) )
        {
            // if this contains unicode then we don't what to resolve it.
            errorContext.addValidationError(UNICODE_NOT_ALLOWED,field,field);
            return null;
        }
        CompoundId ownerAccount = null;
        ownerAccount = parseId(value);
        if(ownerAccount == null)
            errorContext.addValidationError(INVALID_ID_FORMAT,field, value,field);
        else
        {

            if(!fullWildcardAllowed.isValid(ownerAccount))
                errorContext.addValidationError(fullWildcardAllowed.getErrorCode(),field,ownerAccount,field);
        }

        return ownerAccount;
    }
}
