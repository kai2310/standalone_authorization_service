package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

public class SortFieldV1Formatter implements SortFieldFormatter
{
    public static final String DESCENDING = ":desc";

    @Override
    public String formatSortField(String dataField, boolean descending)
    {
        return String.format("%s%s", dataField, descending ? DESCENDING : "");
    }
}
