package com.rubicon.platform.authorization.model.data.idm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;
import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@Endpoint("User")
@JsonPropertyOrder({"id", "username", "userType", "email", "password", "firstName", "lastName", "managingAccountIds", "title", "phoneNumber", "faxNumber", "timeZone", "currency", "status", "revision", "created", "createdBy", "modified", "modifiedBy"})
public class User extends BaseAuditableApiObject<Long> {
    private String username;
    private UserType userType;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String title;
    private String phoneNumber;
    private String faxNumber;
    private String timeZone;
    private String currency;
    private Status status;
    private Integer revision;
    private List<String> managingAccountIds;

    public User() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getRevision() {
        return this.revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public List<String> getManagingAccountIds() {
        return this.managingAccountIds;
    }

    public void setManagingAccountIds(List<String> managingAccountIds) {
        this.managingAccountIds = managingAccountIds;
    }
}
