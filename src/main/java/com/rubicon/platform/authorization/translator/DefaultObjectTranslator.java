package com.rubicon.platform.authorization.translator;

import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DefaultObjectTranslator<C, P> implements ObjectTranslator<C, P>
{
    protected TypeMapper clientTypeMapper;
    protected TypeMapper persistentTypeMapper;
    private Map<String, ObjectFieldMapper> fieldMapperMap = new HashMap<>();
    private FastClass clientClass;
    private FastClass persistentClass;

    public DefaultObjectTranslator(Class<C> clientClass, Class<P> persistentClass)
    {
        this.clientClass = FastClass.create(clientClass);
        this.persistentClass = FastClass.create(persistentClass);
    }

    protected C createClientInstance()
    {
        try
        {
            return (C) clientClass.newInstance();
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected P createPersistentInstance()
    {
        try
        {
            return (P) persistentClass.newInstance();
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void init()
    {
        clientTypeMapper = new TypeMapper(createClientInstance().getClass());
        persistentTypeMapper = new TypeMapper(createPersistentInstance().getClass());
        initializeDefaultFieldMappers();
        initializeCustomFieldMappers();
    }

    protected void beforeConvert(ObjectWrapper<C> clientObjectWrapper, ObjectWrapper<P> persistentObjectWrapper,
                                 TranslationContext translationContext)
    {
    }

    public List<P> convertClient(List<C> clientList, TranslationContext translationContext)
    {
        List<P> list = new LinkedList<>();
        for (C client : clientList)
        {
            list.add(convertClient(client, translationContext));
        }

        return list;
    }

    @Override
    public P convertClient(C client, TranslationContext translationContext)
    {
        P persistentObject = createPersistentInstance();
        ObjectWrapper<P> persistentObjectWrapper = createPersistentObjectWrapper(persistentObject);
        ObjectWrapper<C> clientObjectWrapper = createClientObjectWrapper(client);

        beforeConvert(clientObjectWrapper, persistentObjectWrapper, translationContext);

        for (ObjectFieldMapper mapper : fieldMapperMap.values())
        {
            mapper.convertToPersistent(clientObjectWrapper, persistentObjectWrapper, translationContext);
        }

        afterConvert(clientObjectWrapper, persistentObjectWrapper, translationContext);

        return persistentObject;
    }

    protected void afterConvert(ObjectWrapper<C> clientObjectWrapper, ObjectWrapper<P> persistentObjectWrapper,
                                TranslationContext translationContext)
    {
    }

    protected void beforeCopyClient(ObjectWrapper<C> clientObjectWrapper, ObjectWrapper<P> persistentObjectWrapper,
                                    TranslationContext translationContext)
    {
    }

    @Override
    public boolean copyClient(C client, P persistent, TranslationContext translationContext)
    {
        boolean dirty = false;
        ObjectWrapper<P> persistentObjectWrapper = createPersistentObjectWrapper(persistent);
        ObjectWrapper<C> clientObjectWrapper = createClientObjectWrapper(client);

        beforeCopyClient(clientObjectWrapper, persistentObjectWrapper, translationContext);
        for (ObjectFieldMapper mapper : fieldMapperMap.values())
        {
            if (mapper.convertToPersistent(clientObjectWrapper, persistentObjectWrapper, translationContext))
            {
                dirty = true;
            }
        }

        afterCopyClient(clientObjectWrapper, persistentObjectWrapper, translationContext);

        return dirty;
    }

    protected void afterCopyClient(ObjectWrapper<C> clientObjectWrapper, ObjectWrapper<P> persistentObjectWrapper,
                                   TranslationContext translationContext)
    {
    }

    public List<C> convertPersistent(List<P> persistentList, TranslationContext translationContext)
    {
        List<C> list = new LinkedList<>();
        for (P persistent : persistentList)
        {
            list.add(convertPersistent(persistent, translationContext));
        }

        return list;
    }

    @Override
    public C convertPersistent(P persistent, TranslationContext translationContext)
    {
        C clientObject = createClientInstance();

        ObjectWrapper<P> persistentObjectWrapper = createPersistentObjectWrapper(persistent);
        ObjectWrapper<C> clientObjectWrapper = createClientObjectWrapper(clientObject);

        for (Map.Entry<String, ObjectFieldMapper> entry : fieldMapperMap.entrySet())
        {
            entry.getValue().convertToClient(persistentObjectWrapper, clientObjectWrapper, translationContext);
        }

        convertPersistent(clientObject, persistent, translationContext);

        return clientObject;
    }

    protected void convertPersistent(C client, P persistent, TranslationContext translationContext)
    {
    }

    protected void beforeCopyPersistent(ObjectWrapper<P> persistentObjectWrapper, ObjectWrapper<C> clientObjectWrapper,
                                        TranslationContext translationContext)
    {
    }

    @Override
    public void copyPersistent(P persistent, C client, TranslationContext translationContext)
    {
        boolean dirty = false;
        ObjectWrapper<P> persistentObjectWrapper = createPersistentObjectWrapper(persistent);
        ObjectWrapper<C> clientObjectWrapper = createClientObjectWrapper(client);

        beforeCopyPersistent(persistentObjectWrapper, clientObjectWrapper, translationContext);
        for (ObjectFieldMapper mapper : fieldMapperMap.values())
        {
            mapper.convertToClient(persistentObjectWrapper, clientObjectWrapper, translationContext);
        }
        afterCopyPersistent(persistentObjectWrapper, clientObjectWrapper, translationContext);

    }

    protected void afterCopyPersistent(ObjectWrapper<P> persistentObjectWrapper, ObjectWrapper<C> clientObjectWrapper,
                                       TranslationContext translationContext)
    {
    }

    protected void initializeDefaultFieldMappers()
    {
        Set<String> clientFields = clientTypeMapper.getFieldNames();
        for (String clientField : clientFields)
        {
            Class clientType = clientTypeMapper.getFieldType(clientField);
            Class persistentType = persistentTypeMapper.getFieldType(clientField);
            if (persistentType != null && clientType.equals(persistentType))
            {
                fieldMapperMap.put(clientField, new DefaultObjectFieldMapper(clientField));
            }
        }
    }

    protected void initializeCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = getCustomFieldMappers();
        for (ObjectFieldMapper mapper : mappers)
        {
            fieldMapperMap.put(mapper.getClientFieldName(), mapper);
        }
    }

    protected List<ObjectFieldMapper> getCustomFieldMappers()
    {
        List<ObjectFieldMapper> mappers = new ArrayList<ObjectFieldMapper>();
        return mappers;
    }

    protected ObjectWrapper<C> createClientObjectWrapper(C client)
    {
        return new ObjectWrapper<C>(client, clientTypeMapper);
    }

    protected ObjectWrapper<P> createPersistentObjectWrapper(P persistent)
    {
        return new ObjectWrapper<P>(persistent, persistentTypeMapper);
    }

}
