package com.rubicon.platform.authorization.hyperion.model;

import com.dottydingo.hyperion.jpa.model.DefaultPersistentHistoryEntry;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="entity_history")
public class PersistentHistoryEntry extends DefaultPersistentHistoryEntry<Long>
{
}
