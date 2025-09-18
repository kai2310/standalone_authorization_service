package com.rubicon.platform.authorization.model.api.idm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;

@JsonPropertyOrder({"userId", "userName", "email", "firstName", "lastName", "managingAccountIds"})
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> managingAccountIds;

    public UserInfo() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<String> getManagingAccountIds() {
        return this.managingAccountIds;
    }

    public void setManagingAccountIds(List<String> managingAccountIds) {
        this.managingAccountIds = managingAccountIds;
    }
}
