package com.rubicon.platform.authorization.translator;

public class DefaultObjectFieldMapper<C, P> implements ObjectFieldMapper<C, P>
{
    private String clientFieldName;
    private String persistentFieldName;
    protected ObjectValueConverter valueConverter;
    protected PropertyChangeEvaluator propertyChangeEvaluator = new DefaultPropertyChangeEvaluator();

    public DefaultObjectFieldMapper(String name)
    {
        this(name, name, null);
    }

    public DefaultObjectFieldMapper(String clientFieldName, String persistentFieldName,
                                    ObjectValueConverter valueConverter)
    {
        this.clientFieldName = clientFieldName;
        this.persistentFieldName = persistentFieldName;
        this.valueConverter = valueConverter;
    }

    @Override
    public String getClientFieldName()
    {
        return clientFieldName;
    }

    public String getPersistentFieldName()
    {
        return persistentFieldName;
    }

    public void setPropertyChangeEvaluator(PropertyChangeEvaluator propertyChangeEvaluator)
    {
        this.propertyChangeEvaluator = propertyChangeEvaluator;
    }

    @Override
    public boolean convertToClient(ObjectWrapper<P> persistentObjectWrapper, ObjectWrapper<C> clientObjectWrapper,
                                   TranslationContext context)
    {
        Object persistentValue = persistentObjectWrapper.getValue(getPersistentFieldName());

        if (valueConverter != null)
        {
            persistentValue = valueConverter.convertToClientValue(persistentValue, context);
        }

        clientObjectWrapper.setValue(getClientFieldName(), persistentValue);

        return true;
    }

    @Override
    public boolean convertToPersistent(ObjectWrapper<C> clientObjectWrapper,
                                       ObjectWrapper<P> persistentObjectWrapper, TranslationContext context)
    {
        Object clientValue = clientObjectWrapper.getValue(getClientFieldName());
        if (valueConverter != null)
        {
            clientValue = valueConverter.convertToPersistentValue(clientValue, context);
        }

        boolean dirty = false;

        if (clientValue != null)
        {
            dirty = propertyChangeEvaluator.hasChanged(persistentObjectWrapper.getValue(persistentFieldName),
                    clientValue);
            if (dirty)
            {
                persistentObjectWrapper.setValue(getPersistentFieldName(), clientValue);
            }
        }

        return dirty;
    }
}
