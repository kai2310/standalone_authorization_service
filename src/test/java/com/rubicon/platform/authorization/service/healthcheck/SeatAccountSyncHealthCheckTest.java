package com.rubicon.platform.authorization.service.healthcheck;

import org.junit.Assert;
import org.junit.Test;

public class SeatAccountSyncHealthCheckTest extends Assert
{
    @Test
    public void testInstantiateObject()
    {
        SeatAccountSyncHealthCheck healthCheck = new SeatAccountSyncHealthCheck(null, null);
    }
}
