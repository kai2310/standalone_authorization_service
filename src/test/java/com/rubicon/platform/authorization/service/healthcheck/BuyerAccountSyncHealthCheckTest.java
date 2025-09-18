package com.rubicon.platform.authorization.service.healthcheck;

import org.junit.Assert;
import org.junit.Test;

public class BuyerAccountSyncHealthCheckTest extends Assert
{
    @Test
    public void testInstantiateObject()
    {
        BuyerAccountSyncHealthCheck healthCheck = new BuyerAccountSyncHealthCheck(null, null);
    }
}
