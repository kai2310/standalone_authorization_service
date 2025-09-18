package com.rubicon.platform.authorization.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User: mhellkamp
 * Date: 1/31/13
 */
@Entity
@Table(name = "configuration")
public class Configuration
{
	@Id
	@Column(name = "config_key")
	private String key;

	@Column(name = "config_value")
	private String value;

	public Configuration()
	{
	}

	public Configuration(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
