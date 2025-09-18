package com.rubicon.platform.authorization.data.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 */
@Entity()
@Table(name = "account_group_types")
@DynamicUpdate
@AttributeOverrides(
        @AttributeOverride(name = "id", column = @Column(name = "account_group_type_id"))
)
public class PersistentAccountGroupType extends BaseStatusPersistentObject
{
}
