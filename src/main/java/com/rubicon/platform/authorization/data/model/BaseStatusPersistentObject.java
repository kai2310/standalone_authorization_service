package com.rubicon.platform.authorization.data.model;

import com.rubicon.platform.authorization.model.data.acm.Status;

import javax.persistence.*;

/**
 */
@MappedSuperclass
public class BaseStatusPersistentObject extends BaseLabeledPersistentObject implements StatusEntity
{
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "active")
    private Integer active;

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    @PrePersist
    @PreUpdate
    public void preWrite()
    {
        active = status == Status.ACTIVE ? 1 : null;
    }
}
