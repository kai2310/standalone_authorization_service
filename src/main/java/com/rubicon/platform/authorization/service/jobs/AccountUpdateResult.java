package com.rubicon.platform.authorization.service.jobs;

/**
 * User: mhellkamp
 * Date: 10/31/12
 */
public class AccountUpdateResult
{
	private Long id;
	private Status status;

	public AccountUpdateResult(Long id, Status status)
	{
		this.id = id;
		this.status = status;
	}

	public Long getId()
	{
		return id;
	}

	public Status getStatus()
	{
		return status;
	}
}
