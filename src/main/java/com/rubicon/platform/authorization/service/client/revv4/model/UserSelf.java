package com.rubicon.platform.authorization.service.client.revv4.model;

/**
 */
public class UserSelf extends BaseModel
{
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String contextType;
    private Account account;
    private Reference seat;
    private Reference network;
    private String status;

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

    public String getContextType()
    {
        return contextType;
    }

    public void setContextType(String contextType)
    {
        this.contextType = contextType;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public Reference getSeat()
    {
        return seat;
    }

    public void setSeat(Reference seat)
    {
        this.seat = seat;
    }

    public Reference getNetwork()
    {
        return network;
    }

    public void setNetwork(Reference network)
    {
        this.network = network;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
