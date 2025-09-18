package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;
import java.util.Set;

public class AuthorizeRequest extends BaseAuthorizeRequest implements Serializable
{
    private Set<String> subjects;

    public Set<String> getSubjects()
    {
        return subjects;
    }

    public void setSubjects(Set<String> subjects)
    {
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthorizeRequest that = (AuthorizeRequest) o;

        return subjects != null
               ? subjects.equals(that.subjects)
               : that.subjects == null;

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (subjects != null
                                ? subjects.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeRequest{");
        sb.append("subjects=").append(subjects);
        sb.append(", accountContext='").append(getAccountContext()).append('\'');
        sb.append(", service='").append(getService()).append('\'');
        sb.append(", resource='").append(getResource()).append('\'');
        sb.append(", action='").append(getAction()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
