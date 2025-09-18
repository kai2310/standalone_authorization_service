package com.rubicon.platform.authorization.model.data.acm;

public class UserInfo
{
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;

        UserInfo userInfo = (UserInfo) o;

        if (userId != null
            ? !userId.equals(userInfo.userId)
            : userInfo.userId != null)
        {
            return false;
        }
        if (username != null
            ? !username.equals(userInfo.username)
            : userInfo.username != null)
        {
            return false;
        }
        if (email != null
            ? !email.equals(userInfo.email)
            : userInfo.email != null)
        {
            return false;
        }
        if (firstName != null
            ? !firstName.equals(userInfo.firstName)
            : userInfo.firstName != null)
        {
            return false;
        }
        return !(lastName != null
                 ? !lastName.equals(userInfo.lastName)
                 : userInfo.lastName != null);

    }

    @Override
    public int hashCode()
    {
        int result = userId != null
                     ? userId.hashCode()
                     : 0;
        result = 31 * result + (username != null
                                ? username.hashCode()
                                : 0);
        result = 31 * result + (email != null
                                ? email.hashCode()
                                : 0);
        result = 31 * result + (firstName != null
                                ? firstName.hashCode()
                                : 0);
        result = 31 * result + (lastName != null
                                ? lastName.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "UserInfo{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               '}';
    }
}
