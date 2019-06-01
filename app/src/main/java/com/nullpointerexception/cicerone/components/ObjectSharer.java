package com.nullpointerexception.cicerone.components;

import java.util.HashMap;
import java.util.Map;

/**
 *      ObjectSharer
 *
 *
 *
 *      @author Luca
 */
public class ObjectSharer
{
    private static final ObjectSharer ourInstance = new ObjectSharer();
    public static ObjectSharer get() { return ourInstance; }
    private ObjectSharer() { }

    private Map<String, Object> objectsShared = new HashMap<>();

    public void shareObject(String key, Object object)
    {
        objectsShared.put(key, object);
    }

    public Object getSharedObject(String key)
    {
        return objectsShared.get(key);
    }

    public void remove(String key)
    {
        objectsShared.remove( objectsShared.get(key) );
    }

}
