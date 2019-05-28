package com.nullpointerexception.cicerone.components;

/**
 *      ListOfStorables
 *
 *      It allows to store on FireBase Database lists of StorableAsField objects types.
 *
 *      @author Luca
 */
public interface ListOfStorables
{
    /**
     *      Implementation of this method should instance a new object of type used into list,
     *      selecting it from passed name field, and MUST ADD IT to that list.
     *
     *      NOTE:   Don't forget to instantiate list too.
     *
     *      @param fieldName    Name of list as declared in the class
     *      @return             It must return the new added object.
     */
    Object addNewInstanceInto(String fieldName);
}
