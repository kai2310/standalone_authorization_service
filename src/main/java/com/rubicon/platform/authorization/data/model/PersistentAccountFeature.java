package com.rubicon.platform.authorization.data.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Entity()
@Table(name = "feature_roles")
@DynamicUpdate
@AttributeOverrides(
		@AttributeOverride(name = "id", column = @Column(name = "feature_role_id"))
)
public class PersistentAccountFeature extends BaseStatusPersistentObject
{
    @Column(name = "realm")
    private String realm;

	@ElementCollection()
	@CollectionTable(
			name="feature_role_operations",
			joinColumns=@JoinColumn(name="feature_role_id"))
	@IndexColumn(name = "idx",nullable = false)
	@BatchSize(size=50)
	private List<PersistentOperation> operations = new ArrayList<PersistentOperation>();

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
}
