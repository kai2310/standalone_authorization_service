package com.rubicon.platform.authorization.service.v1.ui.translator;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.ui.acm.Feature;
import com.rubicon.platform.authorization.model.ui.acm.Operation;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rubicon.platform.authorization.service.utils.Constants.TRANSLATE_CONTEXT_IS_EDITABLE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(DataProviderRunner.class)
public class FeatureTranslatorTest extends TestAbstract
{
    FeatureTranslator featureTranslator;

    @Before
    public void setup()
    {
        featureTranslator = new FeatureTranslator();
        featureTranslator.init();
    }


    @DataProvider
    public static Object[][] convertPersistentDataProvider()
    {
        return new Object[][]{
                {true, true},
                {false, false},
                {null, false},
        };
    }

    @Test
    @UseDataProvider("convertPersistentDataProvider")
    public void convertPersistent(Boolean isEditable, Boolean expectedIsEditable)
    {
        TranslationContext context = new TranslationContext();
        context.putContextItem(TRANSLATE_CONTEXT_IS_EDITABLE, isEditable);

        AccountFeature featureDataService = getDataServiceAccountFeature();
        Feature feature = featureTranslator.convertPersistent(featureDataService, context);

        assertThat(feature.getId(), equalTo(DATA_SERVICE_ACCOUNT_FEATURE_ID));
        assertThat(feature.getName(), equalTo(DATA_SERVICE_FEATURE_NAME));
        assertThat(feature.getEditable(), equalTo(expectedIsEditable));

        Operation allowedOperation = feature.getAllowedOperations().get(0);

        assertThat(allowedOperation.getService(), equalTo(OPERATION_SERVICE));
        assertThat(allowedOperation.getResource(), equalTo(OPERATION_RESOURCE));
        assertThat(allowedOperation.getAction(), equalTo(OPERATION_ACTION));

        Operation deniedOperation = feature.getDeniedOperations().get(0);

        assertThat(deniedOperation.getService(), equalTo(OPERATION_SERVICE));
        assertThat(deniedOperation.getResource(), equalTo(OPERATION_RESOURCE));
        assertThat(deniedOperation.getAction(), equalTo(OPERATION_ACTION));

    }

}
