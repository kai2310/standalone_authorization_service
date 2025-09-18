package com.rubicon.platform.authorization.data.model;


import com.dottydingo.hyperion.jpa.model.BaseAuditablePersistentObject;

import javax.persistence.*;

/**
 */
@MappedSuperclass
public abstract class BaseLabeledPersistentObject extends BaseAuditablePersistentObject<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	private Long id;

	@Column(name = "label")
	private String label = "";

	@Column(name = "description")
	private String description = "";

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
