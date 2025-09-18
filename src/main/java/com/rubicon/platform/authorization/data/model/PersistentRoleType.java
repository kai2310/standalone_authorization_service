package com.rubicon.platform.authorization.data.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 */
@Entity
@Table(name = "role_types")
@DynamicUpdate
@AttributeOverrides(
		@AttributeOverride(name = "id", column = @Column(name = "role_type_id"))
)
public class PersistentRoleType extends BaseStatusPersistentObject
{
}
