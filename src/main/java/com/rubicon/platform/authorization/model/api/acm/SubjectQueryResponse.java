package com.rubicon.platform.authorization.model.api.acm;

import java.util.List;
import java.util.Objects;

public class SubjectQueryResponse
{
    private List<String> subjects;

    public List<String> getSubjects()
    {
        return subjects;
    }

    public void setSubjects(List<String> subjects)
    {
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectQueryResponse that = (SubjectQueryResponse) o;
        return Objects.equals(subjects, that.subjects);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(subjects);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("SubjectQueryResponse{");
        sb.append("subjects=").append(subjects);
        sb.append('}');
        return sb.toString();
    }
}
