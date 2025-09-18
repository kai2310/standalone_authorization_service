package com.rubicon.platform.authorization.service.healthcheck;

import org.junit.Assert;
import org.junit.Test;

public class NetworkAccountSyncHealthCheckTest extends Assert
{
    @Test
    public void testInstantiateObject()
    {
        NetworkAccountSyncHealthCheck healthCheck = new NetworkAccountSyncHealthCheck(null, null);
    }
}
