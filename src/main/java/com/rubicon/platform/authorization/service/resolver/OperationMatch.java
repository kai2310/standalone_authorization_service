package com.rubicon.platform.authorization.service.resolver;

/**
 */
public enum OperationMatch
{
	none(0),
	service(1),
	resource(2),
	action(3);

	private int level;

	private OperationMatch(int level)
	{
		this.level = level;
	}

	/**
	 * Return if this instance has precedence over another instance
	 * @param other The other instance
	 * @return true if this instance has precedence over the supplied instance
	 */
	public boolean hasPrecedence(OperationMatch other)
	{
		return other != null && this.level > other.level;
	}

	public boolean canOverride(OperationMatch other)
	{
		return other == null || this.level >= other.level;
	}
}
