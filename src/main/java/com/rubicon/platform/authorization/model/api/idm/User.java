package com.rubicon.platform.authorization.model.api.idm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rubicon.platform.authorization.model.api.idm.ApiUserType;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"id", "username", "userType", "email", "firstName", "lastName", "phoneNumber", "faxNumber", "timeZone", "currency"})
public class User
{
    private Long id;
    private String username;
    private ApiUserType userType;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String faxNumber;
    private String timeZone;
    private String currency;

    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ApiUserType getUserType() {
        return this.userType;
    }

    public void setUserType(ApiUserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return this.faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
