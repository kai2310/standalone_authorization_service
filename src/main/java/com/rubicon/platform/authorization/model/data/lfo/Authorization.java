package com.rubicon.platform.authorization.model.data.lfo;

import com.dottydingo.hyperion.api.AuditableApiObject;
import com.dottydingo.hyperion.api.BaseApiObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"id", "principleId", "principleType", "resourceId", "resourceType", "created", "createdBy", "modified", "modifiedBy"})
public class Authorization extends BaseApiObject<Long> implements AuditableApiObject<Long> {
    private Long id;
    private Long principleId;
    private PrincipleTypeEnum principleType;
    private String resourceId;
    private ResourceTypeEnum resourceType;
    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "UTC"
    )
    private Date created;
    private String createdBy;
    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "UTC"
    )
    private Date modified;
    private String modifiedBy;

    public Authorization() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrincipleId() {
        return this.principleId;
    }

    public void setPrincipleId(Long principleId) {
        this.principleId = principleId;
    }

    public PrincipleTypeEnum getPrincipleType() {
        return this.principleType;
    }

    public void setPrincipleType(PrincipleTypeEnum principleType) {
        this.principleType = principleType;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceTypeEnum getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}