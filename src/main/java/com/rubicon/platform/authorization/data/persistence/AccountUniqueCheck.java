package com.rubicon.platform.authorization.data.persistence;

/**
 * User: mhellkamp
 * Date: 12/4/12
 */
public interface AccountUniqueCheck
{
	boolean exists(String accountType, String accountId);

}
