package com.rubicon.platform.authorization.service.client.revv4.model;


public class Account extends BaseLabeledModel
{
    private Reference dataProvider;
    private String currency;

    public Reference getDataProvider()
    {
        return dataProvider;
    }

    public void setDataProvider(Reference dataProvider)
    {
        this.dataProvider = dataProvider;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }
}