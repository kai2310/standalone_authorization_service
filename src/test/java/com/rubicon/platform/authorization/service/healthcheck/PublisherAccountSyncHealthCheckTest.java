package com.rubicon.platform.authorization.service.healthcheck;

import org.junit.Assert;
import org.junit.Test;

public class PublisherAccountSyncHealthCheckTest extends Assert
{
    @Test
    public void testInstantiateObject()
    {
        PublisherAccountSyncHealthCheck healthCheck = new PublisherAccountSyncHealthCheck(null, null);
    }
}
