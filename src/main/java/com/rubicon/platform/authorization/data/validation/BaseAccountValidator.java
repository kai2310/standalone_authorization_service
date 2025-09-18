package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.api.exception.BadRequestException;
import com.dottydingo.hyperion.api.exception.NotFoundException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.translator.IdParser;

/**
 * User: mhellkamp
 * Date: 9/12/12
 */
public abstract class BaseAccountValidator<C extends ApiObject,P extends PersistentObject> extends BaseValidator<C,P>
{
	private AccountLoader accountLoader;
	protected IdParser idParser = IdParser.STANDARD_ID_PARSER;


    public void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    protected CompoundId validateAccount(String account, WildcardValidator wildcardValidator)
	{
		if(account == null)
			return null;

		CompoundId id = null;
		try
		{
			id = idParser.parseId(account);
		}
		catch (IllegalArgumentException e)
		{
			throw new BadRequestException(e.getMessage());
		}
		if(!wildcardValidator.isValid(id))
		{
			throw new ValidationException(wildcardValidator.getErrorMessage());
		}

		if(!id.isWildcard() && !id.isWildcardId())
		{
			PersistentAccount persistentAccount = accountLoader.findByAccountId(id);
			if(persistentAccount == null)
				throw new NotFoundException(
						String.format("No account found for id=\"%s\"",account));
		}

		return id;
	}


}
