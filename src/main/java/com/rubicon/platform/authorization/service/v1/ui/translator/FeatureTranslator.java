package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.service.v1.ui.translator.converter.OperationValueConverter;
import com.rubicon.platform.authorization.translator.DefaultObjectFieldMapper;
import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;
import com.rubicon.platform.authorization.translator.ObjectFieldMapper;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.Feature;

import java.util.ArrayList;
import java.util.List;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_IS_EDITABLE;

public class FeatureTranslator
        extends DefaultObjectTranslator<Feature, AccountFeature>
{
    public FeatureTranslator()
    {
        super(Feature.class, com.rubicon.platform.authorization.model.data.acm.AccountFeature.class);
    }

    @Override
    public Feature convertPersistent(AccountFeature persistent, TranslationContext translationContext)
    {
        Feature feature = super.convertPersistent(persistent, translationContext);

        Boolean isEditable = (Boolean) translationContext.getContextItem(TRANSLATE_CONTEXT_IS_EDITABLE);
        feature.setEditable((isEditable != null)
                            ? isEditable
                            : false);

        return feature;
    }

    @Override
    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<>();
        mappers.addAll(super.getCustomFieldMappers());
        mappers.add(new DefaultObjectFieldMapper("name", "label", null));
        mappers.add(
                new DefaultObjectFieldMapper("allowedOperations", "allowedOperations", new OperationValueConverter()));
        mappers.add(
                new DefaultObjectFieldMapper("deniedOperations", "deniedOperations", new OperationValueConverter()));

        return mappers;
    }

}
