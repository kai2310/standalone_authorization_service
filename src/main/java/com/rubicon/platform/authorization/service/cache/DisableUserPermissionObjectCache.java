package com.rubicon.platform.authorization.service.cache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.io.Serializable;

public class DisableUserPermissionObjectCache implements WritableCache<Long, Long>
{
    protected final Ehcache cache;

    public DisableUserPermissionObjectCache(Ehcache cache)
    {
        this.cache = cache;
    }

    protected Serializable generateKey(Long entry)
    {
        return "disabled-user-permission-" + entry.toString();
    }

    public Long getItemById(Long id)
    {
        return unwrapElement(cache.get(generateKey(id)));
    }

    @Override
    public void addEntry(Long entry)
    {
        cache.put(createElement(generateKey(entry), entry));
    }

    @Override
    public void updateEntry(Long entry)
    {
        cache.put(createElement(generateKey(entry), entry));
    }

    @Override
    public void removeEntry(Long id)
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
        return new Element(key, new KryoWrapper(entry));
    }

    protected Long unwrapElement(Element element)
    {
        Long unwrappedElement = null;
        if (element != null)
        {
            KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
            unwrappedElement = (Long) wrapper.getWrapped();
        }

        return unwrappedElement;
    }
}
