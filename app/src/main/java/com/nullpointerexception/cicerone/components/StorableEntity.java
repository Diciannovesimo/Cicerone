package com.nullpointerexception.cicerone.components;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

/**
 *      Class that defines operations needed to store an object.
 *      Objects to store must extend this class.
 *
 *      @author Luca
 */
public abstract class StorableEntity
{

    /**
     *      Used to identify node of this entity on database.
     *
     *      @return Field used to identify this entity.
     */
    public abstract String getId();

    /**
     *      Gives field names that will be ignored when writing entity on database
     *
     *      @return A list of field names ignored.
     */
    public List<String> getIgnoredFields() { return new Vector<>(); }

    /**
     *      Method to override to provide complex data type conversion from a string value.
     *
     *      @param field    Field to set
     *      @param value    Value to set
     */
    protected void setComplexTypedField(Field field, String value) throws IllegalAccessException { }
}