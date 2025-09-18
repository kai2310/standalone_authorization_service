package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.translator.ObjectValueConverter;
import com.rubicon.platform.authorization.translator.TranslationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;

public class DataMarketplaceVendorResponseConverter
        implements ObjectValueConverter<List<DataMarketplaceVendor>, RevvAccountResponse>
{
    public RevvAccountResponse convertToPersistentValue(List<DataMarketplaceVendor> clientObject,
                                                                  TranslationContext translationContext)
    {
        RevvAccountResponse response = new RevvAccountResponse();
        if (!CollectionUtils.isEmpty(clientObject))
        {
            for (DataMarketplaceVendor marketplaceVendor : clientObject)
            {
                response.add(new RevvAccount(marketplaceVendor.getId().toString(), marketplaceVendor.getName(),
                        marketplaceVendor.getStatus().toLowerCase(Locale.ROOT)));
            }
        }

        return response;
    }

    // right now this function is not being used
    public List<DataMarketplaceVendor> convertToClientValue(RevvAccountResponse persistentObject,
                                                            TranslationContext translationContext)
    {
        return null;
    }
}
