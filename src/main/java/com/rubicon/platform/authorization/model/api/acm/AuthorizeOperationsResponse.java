package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class AuthorizeOperationsResponse implements Serializable
{
    private List<AuthorizeResponse> authorizeResponses = new ArrayList<>();

    public List<AuthorizeResponse> getAuthorizeResponses()
    {
        return authorizeResponses;
    }

    public void setAuthorizeResponses(List<AuthorizeResponse> authorizeResponses)
    {
        this.authorizeResponses = authorizeResponses;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizeOperationsResponse that = (AuthorizeOperationsResponse) o;

        return authorizeResponses != null
               ? authorizeResponses.equals(that.authorizeResponses)
               : that.authorizeResponses == null;

    }

    @Override
    public int hashCode()
    {
        return authorizeResponses != null
               ? authorizeResponses.hashCode()
               : 0;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeOperationsResponse{");
        sb.append("authorizeResponses=").append(authorizeResponses);
        sb.append('}');
        return sb.toString();
    }
}
