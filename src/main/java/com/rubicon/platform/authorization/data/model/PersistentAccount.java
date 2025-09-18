package com.rubicon.platform.authorization.data.model;

import com.dottydingo.hyperion.jpa.model.BasePersistentObject;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.*;

/**
 */
@Entity
@Table(name = "accounts")
@DynamicUpdate
public class PersistentAccount extends BasePersistentObject<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "account_id")
	private Long id;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="idType",column = @Column(name = "imp_account_type")),
		@AttributeOverride(name="id",column = @Column(name = "imp_account_id"))
	})
	private CompoundId accountId;

	@Formula("imp_account_id*1")
	@Basic(fetch = FetchType.LAZY)
	@Column(insertable = false,updatable = false)
	private Integer accountIdNumeric;

	@Column(name = "account_name")
	private String accountName;

	@Column(name = "status")
	private String status;

	@Column(name = "source")
	private String source;

	@ElementCollection(fetch = FetchType.LAZY)
	@JoinTable(name = "account_feature_roles",
			joinColumns = @JoinColumn(name = "account_id"))
	@BatchSize(size=100)
    @Column(name = "feature_role_id",nullable = false)
    @OrderBy
	@Fetch(FetchMode.SELECT)
	private Set<Long> accountFeatureIds = new HashSet<>();

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getModified()
	{
		return modified;
	}

	public void setModified(Date modified)
	{
		this.modified = modified;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public Set<Long> getAccountFeatureIds()
	{
		return accountFeatureIds;
	}

	public void setAccountFeatureIds(Set<Long> accountFeatureIds)
	{
		this.accountFeatureIds = accountFeatureIds;
	}

	public CompoundId getAccountId()
	{
		return accountId;
	}

	public void setAccountId(CompoundId accountId)
	{
		this.accountId = accountId;
	}
}
