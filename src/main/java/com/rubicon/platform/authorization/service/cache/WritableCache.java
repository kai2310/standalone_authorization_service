package com.rubicon.platform.authorization.service.cache;


import java.io.Serializable;

/**
 * User: mhellkamp
 * Date: 10/25/12
 */
public interface WritableCache<T extends Serializable,ID extends Serializable>
{
	public void addEntry(T entry);
	public void updateEntry(T entry);
	public void removeEntry(ID id);
    public void clear();
}
