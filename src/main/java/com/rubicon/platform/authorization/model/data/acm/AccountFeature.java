package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Endpoint("AccountFeature")
@JsonPropertyOrder({"id","label","realm","description","allowedOperations","deniedOperations","created","createdBy","modified","modifiedBy"})
public class AccountFeature extends BaseRoleApiObject
{
    @Override
    public String toString()
    {
        return "AccountFeature" +
               "\n" + "{" +
               "\n" + "id=" + getId() +
               "\n" + "label='" + getLabel() + '\'' + "," +
               "\n" + "realm='" + getRealm() + '\'' + "," +
               "\n" + "description='" + getDescription() + '\'' + "," +
               "\n" + "allowedOperations=" + getAllowedOperations() + "," +
               "\n" + "deniedOperations=" + getDeniedOperations() + "," +
               "\n" + "created=" + getCreated() + "," +
               "\n" + "createdBy=" + getCreatedBy() + "," +
               "\n" + "}";
    }
}
