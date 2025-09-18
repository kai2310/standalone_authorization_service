package com.rubicon.platform.authorization.service.cache;


import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 */
@Component
public class CacheLockHolder
{
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public Lock getReadLock()
    {
        return lock.readLock();
    }

    public Lock getWriteLock()
    {
        return lock.writeLock();
    }
}
