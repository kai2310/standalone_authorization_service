package com.rubicon.platform.authorization.model.data.pmg;

import com.dottydingo.hyperion.api.BaseApiObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "level", "salesDirectorId", "accountDirectorId", "revenueOpsLeadId",
        "accountManagerId", "podTeam", "currencyId", "salesCountryId", "salesRegionId", "financeRegionId",
        "financeCountryId", "financeCategoryId", "financeInfoId", "sfAccountId", "sfContactId", "termedDate",
        "termedReason", "notes", "externalId", "exchangeApiIntegration", "useClearingPrice",
        "exchangeApiDeferImpressions", "openBiddingAllowed", "thirdPartyDataUsageAllowed", "ccpaCompliant",
        "ccpaComplianceRequested", "status", "created", "modified"})
public class Publisher extends BaseApiObject<Long>
{
    private String name;
    private Long level;
    private Long salesDirectorId;
    private Long accountDirectorId;
    private Long revenueOpsLeadId;
    private Long accountManagerId;
    private String podTeam;
    private String currencyId;
    private Long salesCountryId;
    private Long salesRegionId;
    private Long financeRegionId;
    private Long financeCountryId;
    private Long financeCategoryId;
    private Long financeInfoId;
    private String sfAccountId;
    private String sfContactId;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd",
            timezone = "UTC"
    )
    private Date termedDate;
    private String termedReason;
    private String notes;
    private Long externalId;
    private Boolean exchangeApiIntegration;
    private Boolean useClearingPrice;
    private Boolean exchangeApiDeferImpressions;
    private Boolean openBiddingAllowed;
    private Boolean thirdPartyDataUsageAllowed;
    private Boolean ccpaCompliant;
    private Boolean ccpaComplianceRequested;
    private PublisherStatusEnum status;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "UTC"
    )
    private Date created;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "UTC"
    )
    private Date modified;

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getExternalId()
    {
        return this.externalId;
    }

    public void setExternalId(Long externalId)
    {
        this.externalId = externalId;
    }

    public Boolean getCcpaCompliant()
    {
        return this.ccpaCompliant;
    }

    public void setCcpaCompliant(Boolean ccpaCompliant)
    {
        this.ccpaCompliant = ccpaCompliant;
    }

    public Boolean getCcpaComplianceRequested()
    {
        return this.ccpaComplianceRequested;
    }

    public void setCcpaComplianceRequested(Boolean ccpaComplianceRequested)
    {
        this.ccpaComplianceRequested = ccpaComplianceRequested;
    }

    public PublisherStatusEnum getStatus()
    {
        return this.status;
    }

    public void setStatus(PublisherStatusEnum status)
    {
        this.status = status;
    }

    public Date getModified()
    {
        return this.modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public Date getCreated()
    {
        return this.created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCurrencyId()
    {
        return this.currencyId;
    }

    public void setCurrencyId(String currencyId)
    {
        this.currencyId = currencyId;
    }

    public Long getFinanceRegionId()
    {
        return this.financeRegionId;
    }

    public void setFinanceRegionId(Long financeRegionId)
    {
        this.financeRegionId = financeRegionId;
    }

    public Long getFinanceCountryId()
    {
        return this.financeCountryId;
    }

    public void setFinanceCountryId(Long financeCountryId)
    {
        this.financeCountryId = financeCountryId;
    }

    public Long getFinanceCategoryId()
    {
        return this.financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId)
    {
        this.financeCategoryId = financeCategoryId;
    }

    public Long getFinanceInfoId()
    {
        return this.financeInfoId;
    }

    public void setFinanceInfoId(Long financeInfoId)
    {
        this.financeInfoId = financeInfoId;
    }

    public Date getTermedDate()
    {
        return this.termedDate;
    }

    public void setTermedDate(Date termedDate)
    {
        this.termedDate = termedDate;
    }

    public String getTermedReason()
    {
        return this.termedReason;
    }

    public void setTermedReason(String termedReason)
    {
        this.termedReason = termedReason;
    }

    public String getNotes()
    {
        return this.notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Boolean getExchangeApiIntegration()
    {
        return this.exchangeApiIntegration;
    }

    public void setExchangeApiIntegration(Boolean exchangeApiIntegration)
    {
        this.exchangeApiIntegration = exchangeApiIntegration;
    }

    public Boolean getUseClearingPrice()
    {
        return this.useClearingPrice;
    }

    public void setUseClearingPrice(Boolean useClearingPrice)
    {
        this.useClearingPrice = useClearingPrice;
    }

    public Boolean getExchangeApiDeferImpressions()
    {
        return this.exchangeApiDeferImpressions;
    }

    public void setExchangeApiDeferImpressions(Boolean exchangeApiDeferImpressions)
    {
        this.exchangeApiDeferImpressions = exchangeApiDeferImpressions;
    }

    public Long getLevel()
    {
        return this.level;
    }

    public void setLevel(Long level)
    {
        this.level = level;
    }

    public Long getSalesDirectorId()
    {
        return this.salesDirectorId;
    }

    public void setSalesDirectorId(Long salesDirectorId)
    {
        this.salesDirectorId = salesDirectorId;
    }

    public Long getAccountDirectorId()
    {
        return this.accountDirectorId;
    }

    public void setAccountDirectorId(Long accountDirectorId)
    {
        this.accountDirectorId = accountDirectorId;
    }

    public Long getRevenueOpsLeadId()
    {
        return this.revenueOpsLeadId;
    }

    public void setRevenueOpsLeadId(Long revenueOpsLeadId)
    {
        this.revenueOpsLeadId = revenueOpsLeadId;
    }

    public Long getAccountManagerId()
    {
        return this.accountManagerId;
    }

    public void setAccountManagerId(Long accountManagerId)
    {
        this.accountManagerId = accountManagerId;
    }

    public String getPodTeam()
    {
        return this.podTeam;
    }

    public void setPodTeam(String podTeam)
    {
        this.podTeam = podTeam;
    }

    public Long getSalesCountryId()
    {
        return this.salesCountryId;
    }

    public void setSalesCountryId(Long salesCountryId)
    {
        this.salesCountryId = salesCountryId;
    }

    public Long getSalesRegionId()
    {
        return this.salesRegionId;
    }

    public void setSalesRegionId(Long salesRegionId)
    {
        this.salesRegionId = salesRegionId;
    }

    public String getSfAccountId()
    {
        return this.sfAccountId;
    }

    public void setSfAccountId(String sfAccountId)
    {
        this.sfAccountId = sfAccountId;
    }

    public String getSfContactId()
    {
        return this.sfContactId;
    }

    public void setSfContactId(String sfContactId)
    {
        this.sfContactId = sfContactId;
    }

    public Boolean getOpenBiddingAllowed()
    {
        return this.openBiddingAllowed;
    }

    public void setOpenBiddingAllowed(Boolean openBiddingAllowed)
    {
        this.openBiddingAllowed = openBiddingAllowed;
    }

    public Boolean getThirdPartyDataUsageAllowed()
    {
        return this.thirdPartyDataUsageAllowed;
    }

    public void setThirdPartyDataUsageAllowed(Boolean thirdPartyDataUsageAllowed)
    {
        this.thirdPartyDataUsageAllowed = thirdPartyDataUsageAllowed;
    }
}