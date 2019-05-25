package com.nullpointerexception.cicerone.components;

import java.util.Map;

/**
 *      Storable As Field
 *
 *      Interface used to specify how store on database sub-entity fields.
 *
 *      @author Luca
 */
public interface StorableAsField
{
    /**   Id of a single field of this class-type, located under label node.  */
    String getFieldId();

    /**   Generate a map with couples of (fieldName, fieldValue) that you want to get stored  */
    Map<String, String> getSubFields();

    /**   It should restore fields with fields map passed as parameters, which have fields in the format
     *    specified with implementation of getSubFields() method. */
    void restoreSubFields(Map<String, String> subFields);
}
