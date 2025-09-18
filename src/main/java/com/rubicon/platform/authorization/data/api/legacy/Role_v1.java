package com.rubicon.platform.authorization.data.api.legacy;

import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rubicon.platform.authorization.model.data.acm.BaseRole;

/**
 */
@Endpoint(value="Role",version = 1)
@JsonPropertyOrder({"id","label","description","realm","ownerAccount","allowedOperations","deniedOperations",
        "created","createdBy","modified","modifiedBy"})
public class Role_v1 extends BaseRole
{

}
