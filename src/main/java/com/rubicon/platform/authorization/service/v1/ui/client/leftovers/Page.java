package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"size", "totalElements", "totalPages", "number"})
public class Page
{
    private Integer size;
    private Long totalElements;
    private Long totalPages;
    private Integer number;

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public Long getTotalElements()
    {
        return totalElements;
    }

    public void setTotalElements(Long totalElements)
    {
        this.totalElements = totalElements;
    }

    public Long getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(Long totalPages)
    {
        this.totalPages = totalPages;
    }

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(Integer number)
    {
        this.number = number;
    }
}
