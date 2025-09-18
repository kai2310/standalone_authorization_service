package com.rubicon.platform.authorization.service.v1.ui.adapter.converter;

public class SortFieldV2Formatter implements SortFieldFormatter
{
    public static final String DESCENDING = "-";

    @Override
    public String formatSortField(String dataField, boolean descending)
    {
        return String.format("%s%s", descending ? DESCENDING : "", dataField);
    }
}
