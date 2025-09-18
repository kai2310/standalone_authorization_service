package com.rubicon.platform.authorization.service.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class CacheHelper
{
    public static <T> T get(Ehcache cache, Object key)
    {
        try
        {
            Element element = cache.get(key);
            if(element != null)
                return (T) element.getObjectValue();

            return null;
        }
        catch (CacheException e)
        {
            Throwable cause = e.getCause();

            if(cause instanceof RuntimeException)
                throw (RuntimeException)cause;

            throw e;
        }
    }

    public static void put(Ehcache cache, Object key, Object value)
    {
        cache.put(new Element(key,value));
    }
}
