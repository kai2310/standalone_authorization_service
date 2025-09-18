package com.rubicon.platform.authorization.data.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 */
@Embeddable
public class CompoundId implements Serializable
{
    // TODO: add a function to split a compound id into parts.
    public static final String WILDCARD = "*";
    public static final String PUBLISHER = "publisher";

    private String idType;
    private String id;

    protected CompoundId()
    {
    }

    public CompoundId(String value)
    {
        String[] split = value.split("/");
        if (split.length != 2)
        {
            throw new IllegalArgumentException(String.format("Invalid CompoundId value %s", value));
        }
        idType = split[0];
        id = split[1];
    }

    public CompoundId(String idType, String id)
    {
        this.idType = idType;
        this.id = id;
    }

    public String getIdType()
    {
        return idType;
    }

    protected void setIdType(String idType)
    {
        this.idType = idType;
    }

    public String getId()
    {
        return id;
    }

    protected void setId(String id)
    {
        this.id = id;
    }

    @Transient
    public boolean isWildcard()
    {
        return WILDCARD.equals(idType) && WILDCARD.equals(id);
    }

    @Transient
    public boolean isWildcardId()
    {
        return WILDCARD.equals(id);
    }

    @Transient
    public boolean isWildcardType()
    {
        return WILDCARD.equals(idType);
    }

    @Transient
    public boolean hasWildcards()
    {
        return isWildcardId() || isWildcardType();
    }

    @Transient
    public boolean isPublisher()
    {
        return PUBLISHER.equals(idType);
    }

    public static CompoundId build(String value)
    {
        if (value == null)
        {
            return null;
        }

        String[] split = value.split("/");
        if (split.length != 2)
        {
            return null;
        }

        return new CompoundId(split[0], split[1]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompoundId that = (CompoundId) o;

        if (id != null
            ? !id.equals(that.id)
            : that.id != null)
        {
            return false;
        }
        if (idType != null
            ? !idType.equals(that.idType)
            : that.idType != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = idType != null
                     ? idType.hashCode()
                     : 0;
        result = 31 * result + (id != null
                                ? id.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return asIdString();
    }

    public String asIdString()
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(idType))
        {
            sb.append(idType).append("/");
        }
        if (StringUtils.isNotEmpty(id))
        {
            sb.append(id);
        }

        return sb.toString();
    }
}
