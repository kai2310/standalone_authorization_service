package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ErrorDetail;
import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountFeatureLoader;
import com.rubicon.platform.authorization.data.persistence.AccountUniqueCheck;
import com.rubicon.platform.authorization.model.data.acm.Account;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 12/4/12
 */
public class AccountValidator extends BaseAccountValidator<Account,PersistentAccount>
{
	private AccountUniqueCheck accountUniqueCheck;
    private AccountFeatureLoader accountFeatureLoader;

	public void setAccountUniqueCheck(AccountUniqueCheck accountUniqueCheck)
	{
		this.accountUniqueCheck = accountUniqueCheck;
	}

    public void setAccountFeatureLoader(
            AccountFeatureLoader accountFeatureLoader)
    {
        this.accountFeatureLoader = accountFeatureLoader;
    }

    @Override
	public void validateCreate(Account clientObject,PersistenceContext context)
	{
        try
        {
            assertRequired("accountId",clientObject.getAccountId());
            if(containsUnicode(clientObject.getAccountId()))
                throw new ValidationException("The \"accountId\" field may not contain unicode characters.");
            CompoundId id = null;
            try
            {
                id = idParser.parseId(clientObject.getAccountId());
            }
            catch (IllegalArgumentException e)
            {
                throw new BadRequestException(e.getMessage());
            }
            if(!WildcardValidator.noWildcardAllowed.isValid(id))
            {
                throw new ValidationException(WildcardValidator.noWildcardAllowed.getErrorMessage());
            }


            assertRequired("accountName", clientObject.getAccountName());
            validateLength("accountName", clientObject.getAccountName(), 255);
            assertRequired("status", clientObject.getStatus());
            validateLength("status", clientObject.getStatus(), 20);
            if(StringUtils.isNotEmpty(clientObject.getSource()))
                throw new ValidationException("The field \"source\" is read only and can not be specified on a create.");

            if(accountUniqueCheck.exists(id.getIdType(),id.getId()))
                throw new ValidationException(
                        String.format("An account with externalId:\"%s\" already exists.", id));

            validateFeatureIds(clientObject.getAccountFeatureIds());
        }
        catch (HyperionException e)
        {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(e.getMessage());
            errorDetail.setCode("ERROR");
            e.setErrorDetails(Collections.singletonList(errorDetail));
            throw e;
        }
    }

	@Override
	public void validateUpdate(Account clientObject, PersistentAccount persistentObject,PersistenceContext context)
	{
        try
        {
            validateLength("accountName", clientObject.getAccountName(), 255);
            assertNotBlank("accountName", clientObject.getAccountName());
            validateLength("status", clientObject.getStatus(), 20);
            assertNotBlank("status", clientObject.getStatus());

            if(StringUtils.isNotEmpty(clientObject.getAccountId())
               && !StringUtils.equals(clientObject.getAccountId(),persistentObject.getAccountId().asIdString()))
                throw new ValidationException("The field \"accountId\" can not be modified.");

            String accountType = persistentObject.getAccountId().getIdType();
            // allow seat to be modified no matter where seat is from
            if(persistentObject.getSource().equals("revv") && !accountType.equals("seat"))
            {
                if(StringUtils.isNotEmpty(clientObject.getAccountName())
                    && !StringUtils.equals(clientObject.getAccountName(),persistentObject.getAccountName()))
                    throw new ValidationException(
                            "The field \"accountName\" can not be modified because it was synchronized from an external system.");

                if(StringUtils.isNotEmpty(clientObject.getStatus())
                        && !StringUtils.equals(clientObject.getStatus(),persistentObject.getStatus()))
                    throw new ValidationException(
                            "The field \"accountStatus\" can not be modified because it was synchronized from an external system.");
            }

            if(StringUtils.isNotEmpty(clientObject.getSource())
                    && !StringUtils.equals(clientObject.getSource(),persistentObject.getSource()))
                throw new ValidationException("The field \"source\" can not be modified.");

            validateFeatureIds(clientObject.getAccountFeatureIds());
        }
        catch (HyperionException e)
        {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(e.getMessage());
            errorDetail.setCode("ERROR");
            e.setErrorDetails(Collections.singletonList(errorDetail));
            throw e;
        }
    }

	@Override
	public void validateDelete(PersistentAccount persistentObject,PersistenceContext context)
	{
        try
        {
            if(persistentObject.getSource().equals("revv"))
                throw new ValidationException(
                        String.format("Account %d can not be deleted because it was synchronized from an external system.",
                                persistentObject.getId()));
        }
        catch (HyperionException e)
        {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(e.getMessage());
            errorDetail.setCode("ERROR");
            e.setErrorDetails(Collections.singletonList(errorDetail));
            throw e;
        }
    }

    private void validateFeatureIds(Collection<Long> featureIds)
    {
        if(featureIds != null && featureIds.size()>0)
        {
            Set<Long> unique = new HashSet<>(featureIds);
            unique.remove(null);
            List<Long> found = accountFeatureLoader.findIds(unique);
            if(found.size() != unique.size())
            {
                unique.removeAll(found);
                throw new ValidationException(String.format("The following accountFeatureIds can not be found: %s",
                        org.apache.commons.lang3.StringUtils.join(unique, ",")));
            }
        }
    }
}
