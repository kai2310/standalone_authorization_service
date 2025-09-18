package com.rubicon.platform.authorization.service.cache.cluster;

/**
 * User: mhellkamp
 * Date: 10/26/12
 */
public class CacheOperationMessage
{
	private String className;
	private Operation operation;
	private Long id;

	public CacheOperationMessage(String className, Operation operation, Long id)
	{
		this.className = className;
		this.operation = operation;
		this.id = id;
	}

	public String getClassName()
	{
		return className;
	}

	public Operation getOperation()
	{
		return operation;
	}

	public Long getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("CacheOperationMessage");
		sb.append("{className='").append(className).append('\'');
		sb.append(", operation=").append(operation);
		sb.append(", id=").append(id);
		sb.append('}');
		return sb.toString();
	}
}
