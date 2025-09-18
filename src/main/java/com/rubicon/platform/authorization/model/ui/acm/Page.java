package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"size", "totalElements", "totalPages", "number"})
public class Page
{
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Integer number;

    public Page()
    {
    }

    public Page(Page page)
    {
        this.size = page.size;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.number = page.number;
    }

    public Page(Integer start, Integer limit, Long totalCount, Integer responseCount)
    {
        size = limit.intValue();
        totalElements = totalCount;
        totalPages = Long.valueOf(totalCount / limit + (totalCount % limit > 0
                                                        ? 1
                                                        : 0)).intValue();
        number = Long.valueOf(start / limit).intValue() + 1;
    }

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

    public Integer getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages)
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
