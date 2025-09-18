package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.BaseApiObject;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.io.Serializable;

/**
 * User: mhellkamp
 * Date: 10/17/12
 */
public class ObjectCache<T extends BaseApiObject<ID>,ID extends Serializable, CID extends Serializable>
		implements WritableCache<T,ID>, DataCache<T,CID>
{
	protected final Ehcache cache;

	public ObjectCache(Ehcache cache)
	{
		this.cache = cache;
	}

	@Override
	public T getItemById(CID id)
	{
		return unwrapElement(cache.get(id));
	}

	protected Serializable generateKey(T entry)
	{
		return entry.getId();
	}

	@Override
	public void addEntry(T entry)
	{
		cache.put(createElement(generateKey(entry),entry));
	}

	@Override
	public void updateEntry(T entry)
	{
		cache.put(createElement(generateKey(entry),entry));
	}

	@Override
	public void removeEntry(ID id)
	{
		cache.remove(id);
	}

    @Override
    public void clear()
    {
        cache.removeAll();
    }

    protected Element createElement(Serializable key, Serializable entry)
    {
        return new Element(key,new KryoWrapper(entry));
    }

    protected T unwrapElement(Element element)
    {
        if(element != null)
        {
            KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
            return (T) wrapper.getWrapped();
        }
        return null;
    }
}
