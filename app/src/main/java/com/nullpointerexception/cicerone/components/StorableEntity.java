package com.nullpointerexception.cicerone.components;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
     *      Creates a map with:
     *          keys:       declared fields name, as String
     *          values:     runtime value of field in the current instance of object
     *
     *      NOTE: Fields in the class that extends this one, must have a visibility different from
     *      'private' (is preferred as 'protected'), or them cannot be seen from this method.
     *      Moreover every value is converted into string calling its toString() method.
     *
     *      @return Tha map created containing fields and their values of the current instance of object.
     */
    public final Map<String, String> getFields()
    {
        Map<String, String> result = new HashMap<>();

        try
        {
            for(Field field : this.getClass().getDeclaredFields())
            {
                Object value = field.get(this);
                result.put(field.getName(), value != null ? value.toString() : "");
            }
        }
        catch (IllegalAccessException e)
        {
            Log.e("Error", e.toString());
        }

        return result;
    }

    /**
     *      Set declared fields of object using a map passed as parameter.
     *
     *      NOTE: Fields in the class that extends this one, must have a visibility different from
     *      'private' (is preferred as 'protected'), or them cannot be seen from this method.
     *      Moreover if your class have fields of a not-primitive or String type, you must override
     *      setComplexTypedField() method with an implementation which provides that conversion.
     *
     *      @param fields  Map used to set fields. it is formatted as:
     *          keys:       declared fields name, as String
     *          values:     runtime value of field in the current instance of object
     */
    public final void setFields(Map<String, String> fields)
    {
        if(fields == null)
            return;

        for(String fieldName : fields.keySet())
        {
            try
            {
                Field field = this.getClass().getDeclaredField(fieldName);

                if(field.getClass().isPrimitive())
                {

                    /*
                            Convert string into correct type
                     */

                    if(field.getType().equals(int.class))
                    {
                        String value = fields.get(fieldName);
                        if(value != null)
                            field.set(this, Integer.parseInt(value));
                    }

                    if(field.getType().equals(float.class))
                    {
                        String value = fields.get(fieldName);
                        if(value != null)
                            field.set(this, Float.parseFloat(value));
                    }

                    if(field.getType().equals(double.class))
                    {
                        String value = fields.get(fieldName);
                        if(value != null)
                            field.set(this, Double.parseDouble(value));
                    }

                    if(field.getType().equals(boolean.class))
                    {
                        String value = fields.get(fieldName);
                        if(value != null)
                            field.set(this, Boolean.parseBoolean(value));
                    }
                }

                if(field.getType().equals(String.class))
                {
                    String value = fields.get(fieldName);
                    if(value != null)
                        field.set(this, value);
                }

                setComplexTypedField(field, fields.get(fieldName));
            }
            catch (Exception e)
            {
                Log.e("Error", e.toString());
            }
        }
    }

    /**
     *      Method to override to provide complex data type conversion from a string value.
     *
     *      @param field    Field to set
     *      @param value    Value to set
     */
    protected void setComplexTypedField(Field field, String value) throws IllegalAccessException { }
}
