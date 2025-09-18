package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;
import java.util.Set;

public class AuthorizeOperationsRequest extends BaseAuthorizeOperationsRequest implements Serializable
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

        AuthorizeOperationsRequest that = (AuthorizeOperationsRequest) o;

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
        final StringBuilder sb = new StringBuilder("AuthorizeOperationsRequest{");
        sb.append("subjects=").append(subjects);
        sb.append(", accountContext='").append(getAccountContext()).append('\'');
        sb.append(", operations=").append(getOperations());
        sb.append('}');
        return sb.toString();
    }
}
