package com.rubicon.platform.authorization.service.v1.ui.translator.converter;

import com.rubicon.platform.authorization.service.cache.BaseRoleObjectCache;
import com.rubicon.platform.authorization.translator.ObjectValueConverter;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.Reference;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FeatureValueConverter implements ObjectValueConverter<List<Reference>, Set<Long>>
{
    private BaseRoleObjectCache<AccountFeature> accountFeatureCache;

    public FeatureValueConverter(BaseRoleObjectCache<AccountFeature> accountFeatureCache)
    {
        this.accountFeatureCache = accountFeatureCache;
    }

    @Override
    public List<Reference> convertToClientValue(Set<Long> persistentValue, TranslationContext context)
    {
        List<Reference> references = new ArrayList<>();
        if (!CollectionUtils.isEmpty(persistentValue))
        {
            for (Long featureId : persistentValue)
            {
                AccountFeature feature = accountFeatureCache.getItemById(featureId);
                if (null != feature)
                {
                    Reference reference = new Reference();
                    reference.setId(featureId);
                    reference.setName(feature.getLabel());
                    references.add(reference);
                }
            }
        }
        return references;
    }

    @Override
    public Set<Long> convertToPersistentValue(List<Reference> clientValue, TranslationContext context)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
