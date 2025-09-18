package com.rubicon.platform.authorization.data.model;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Entity
@Table(name = "roles")
@DynamicUpdate
@AttributeOverrides(
		@AttributeOverride(name = "id", column = @Column(name = "role_id"))
)
public class PersistentRole extends BaseStatusPersistentObject
{
    @Column(name = "realm")
    private String realm;

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

	@ElementCollection()
	@CollectionTable(
			name="role_operations",
			joinColumns=@JoinColumn(name="role_id"))
	@IndexColumn(name = "idx",nullable = false)
	@BatchSize(size = 50)
	private List<PersistentOperation> operations = new ArrayList<PersistentOperation>();

    @Column(name = "role_type_id")
    private Long roleTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type_id", updatable = false, insertable = false)
    private PersistentRoleType roleType;

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public List<PersistentOperation> getOperations()
	{
		return operations;
	}

	public void setOperations(List<PersistentOperation> operations)
	{
		this.operations = operations;
	}

	public CompoundId getOwnerAccount()
	{
		return ownerAccount;
	}

	public void setOwnerAccount(CompoundId ownerAccount)
	{
		this.ownerAccount = ownerAccount;
	}

    public Long getRoleTypeId()
    {
        return roleTypeId;
    }

    public void setRoleTypeId(Long roleTypeId)
    {
        this.roleTypeId = roleTypeId;
    }

    public PersistentRoleType getRoleType()
    {
        return roleType;
    }
}
