package com.rubicon.platform.authorization.data.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity()
@Table(name = "account_groups")
@DynamicUpdate
@AttributeOverrides(
        @AttributeOverride(name = "id", column = @Column(name = "account_group_id"))
)
public class PersistentAccountGroup extends BaseStatusPersistentObject
{
    @Column(name = "account_group_type_id")
    private Long accountGroupTypeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "account_group_accounts",joinColumns = {@JoinColumn(name = "account_group_id",nullable = false)})
    @Column(name = "account_id")
    @OrderColumn(name = "idx",nullable = false)
    private List<Long> accountIds;

    @Column(name = "account_type")
    private String accountType;

    public Long getAccountGroupTypeId()
    {
        return accountGroupTypeId;
    }

    public void setAccountGroupTypeId(Long accountGroupTypeId)
    {
        this.accountGroupTypeId = accountGroupTypeId;
    }

    public List<Long> getAccountIds()
    {
        return accountIds;
    }

    public void setAccountIds(List<Long> accountIds)
    {
        this.accountIds = accountIds;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }
}
