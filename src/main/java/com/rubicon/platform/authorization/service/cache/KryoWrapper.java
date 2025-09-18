package com.rubicon.platform.authorization.service.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.RegexSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Adapted from http://stackoverflow.com/questions/17431500/custom-serialization-with-ehcache
 */
public final class KryoWrapper implements Serializable
{
    // Setup ThreadLocal of Kryo instances
    private static ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        protected Kryo initialValue()
        {
            Kryo kryo = new Kryo();
            kryo.setAsmEnabled(true);

            kryo.register(Pattern.class,new RegexSerializer());

            return kryo;
        }; };


    private Object wrapped;

    public KryoWrapper(Object wrapped)
    {
        this.wrapped = wrapped;
    }

    public Object getWrapped()
    {
        return wrapped;
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        Input input = new Input(is);
        wrapped = kryos.get().readClassAndObject(input);
        input.close();
    }

    private void writeObject(ObjectOutputStream os) throws IOException
    {
        Output output = new Output(os);
        kryos.get().writeClassAndObject(output, wrapped);
        output.close();
    }
}
