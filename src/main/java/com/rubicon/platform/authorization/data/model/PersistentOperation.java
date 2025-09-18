package com.rubicon.platform.authorization.data.model;


import javax.persistence.*;

/**
 */
@Embeddable
public class PersistentOperation
{
	private enum denyEnum {yes,no}

	@Column(name = "deny")
	@Enumerated(EnumType.STRING)
	private denyEnum deny = denyEnum.no;

	@Column(name = "service")
	private String service;

	@Column(name = "resource")
	private String resource;

	@Column(name = "action_name")
	private String action;

	@Column(name = "properties")
	private String properties;

	public boolean isDeny()
	{
		return deny == denyEnum.yes;
	}

	public void setDeny(boolean deny)
	{
		if(deny)
			this.deny = denyEnum.yes;
		else
			this.deny = denyEnum.no;
	}


	public String getService()
	{
		return service;
	}

	public void setService(String service)
	{
		this.service = service;
	}

	public String getResource()
	{
		return resource;
	}

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getProperties()
	{
		return properties;
	}

	public void setProperties(String properties)
	{
		this.properties = properties;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PersistentOperation that = (PersistentOperation) o;

		if (action != null
			? !action.equals(that.action)
			: that.action != null) return false;
		if (deny != that.deny) return false;
		if (properties != null
			? !properties.equals(that.properties)
			: that.properties != null) return false;
		if (resource != null
			? !resource.equals(that.resource)
			: that.resource != null) return false;
		if (service != null
			? !service.equals(that.service)
			: that.service != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = deny != null
					 ? deny.hashCode()
					 : 0;
		result = 31 * result + (service != null
								? service.hashCode()
								: 0);
		result = 31 * result + (resource != null
								? resource.hashCode()
								: 0);
		result = 31 * result + (action != null
								? action.hashCode()
								: 0);
		result = 31 * result + (properties != null
								? properties.hashCode()
								: 0);
		return result;
	}
}
