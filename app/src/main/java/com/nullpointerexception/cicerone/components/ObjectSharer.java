package com.nullpointerexception.cicerone.components;

import java.util.HashMap;
import java.util.Map;

/**
 *      ObjectSharer
 *
 *      Let accessible objects with environment.
 *
 *      @author Luca
 */
public class ObjectSharer
{
    private static final ObjectSharer ourInstance = new ObjectSharer();
    public static ObjectSharer get() { return ourInstance; }
    private ObjectSharer() { }

    /**   Map with objects to share, tagged by a string  */
    private Map<String, Object> sharedObjects = new HashMap<>();

    /**
     *      Put an object into the map of shared objects.
     *
     *      @param key      Key to access to the shared object
     *      @param object   Object to share
     */
    public void shareObject(String key, Object object)
    {
        sharedObjects.put(key, object);
    }

    /**
     *      Retrieve an object shared before.
     *
     *      @param key  Key of object you want to access
     *      @return     Object with the corrisponding key.
     */
    public Object getSharedObject(String key)
    {
        return sharedObjects.get(key);
    }

    /**
     *      Remove an object from map of shared objects.
     *
     *      @param key  Key of object you want to remove
     */
    public void remove(String key)
    {
        sharedObjects.remove( sharedObjects.get(key) );
    }

}
