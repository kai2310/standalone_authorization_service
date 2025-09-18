package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.BaseRoleApiObject;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.LinkedList;
import java.util.List;

public class BaseRoleObjectCache<T extends BaseRoleApiObject> extends ObjectCache<T,Long,Long>
    implements ServiceOperationsCache<T>
{
    public BaseRoleObjectCache(Ehcache cache)
    {
        super(cache);
    }


    @Override
    public void addEntry(T entry)
    {
        cache.put(createElement(generateKey(entry), new ServiceOperationsHolder<T>(entry)));
    }

    @Override
    public void updateEntry(T entry)
    {
        cache.put(createElement(generateKey(entry), new ServiceOperationsHolder<T>(entry)));
    }

    @Override
    public ServiceOperationsHolder<T> getServiceOperationsHolder(Long id)
    {
        Element element = cache.get(id);
        if(element != null)
        {
            KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
            return (ServiceOperationsHolder) wrapper.getWrapped();
        }
        return null;
    }

    @Override
    public List<ServiceOperationsHolder<T>> loadAll()
    {
        List<ServiceOperationsHolder<T>> list = new LinkedList<>();
        List keys = cache.getKeys();
        for (Object key : keys)
        {
            Element element = cache.get(key);
            if(element != null)
            {
                KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
                list.add((ServiceOperationsHolder) wrapper.getWrapped());
            }
        }
        return list;
    }


    @Override
    protected T unwrapElement(Element element)
    {
        if(element != null)
        {
            KryoWrapper wrapper = (KryoWrapper) element.getObjectValue();
            return (T) ((ServiceOperationsHolder)wrapper.getWrapped()).getWrapped();
        }
        return null;
    }
}
