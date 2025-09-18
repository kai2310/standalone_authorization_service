package com.rubicon.platform.authorization.data.model;

import com.dottydingo.hyperion.jpa.model.BaseAuditablePersistentObject;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 */
@Entity
@Table(name = "role_assignments")
@DynamicUpdate
public class PersistentRoleAssignment extends BaseAuditablePersistentObject<Long> implements StatusEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name ="role_assignment_id")
	private Long id;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="idType",column = @Column(name = "owner_account_type")),
			@AttributeOverride(name="id",column = @Column(name = "owner_account_id"))
	})
	private CompoundId ownerAccount;

	@Formula("owner_account_id*1")
	@Basic(fetch = FetchType.LAZY)
	@Column(insertable = false,updatable = false)
	private Integer ownerAccountNumeric;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="idType",column = @Column(name = "subject_type")),
			@AttributeOverride(name="id",column = @Column(name = "subject_id"))
	})
	private CompoundId subject;

	@Formula("subject_id*1")
	@Basic(fetch = FetchType.LAZY)
	@Column(insertable = false,updatable = false)
	private Integer subjectNumeric;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="idType",column = @Column(name = "context_type")),
			@AttributeOverride(name="id",column = @Column(name = "context_id"))
	})
	private CompoundId account;

	@Formula("context_id*1")
	@Basic(fetch = FetchType.LAZY)
	@Column(insertable = false,updatable = false)
	private Integer accountNumeric;

    @Column(name = "realm")
    private String realm;


	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "scope")
	private String scope;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "account_group_id")
	private Long accountGroupId;

	@Column(name = "active")
	private Integer active;

	@ManyToOne(fetch = FetchType.LAZY,optional = true)
	@JoinColumn(name = "account_group_id",insertable = false,updatable = false)
	@BatchSize(size = 100)
	private PersistentAccountGroup accountGroup;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}


    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }


	public Long getRoleId()
	{
		return roleId;
	}

	public void setRoleId(Long roleId)
	{
		this.roleId = roleId;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public CompoundId getOwnerAccount()
	{
		return ownerAccount;
	}

	public void setOwnerAccount(CompoundId ownerAccount)
	{
		this.ownerAccount = ownerAccount;
	}

	public CompoundId getSubject()
	{
		return subject;
	}

	public void setSubject(CompoundId subject)
	{
		this.subject = subject;
	}

	public CompoundId getAccount()
	{
		return account;
	}

	public void setAccount(CompoundId account)
	{
		this.account = account;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Long getAccountGroupId()
	{
		return accountGroupId;
	}

	public void setAccountGroupId(Long accountGroupId)
	{
		this.accountGroupId = accountGroupId;
	}

	// only for v1
	public PersistentAccountGroup getAccountGroup()
	{
		return accountGroup;
	}

	// hibernate only
	public void setAccountGroup(PersistentAccountGroup accountGroup)
	{
		this.accountGroup = accountGroup;
	}

	@PrePersist
	@PreUpdate
	public void preWrite()
	{
		active = status == Status.ACTIVE ? 1 : null;
	}
}
