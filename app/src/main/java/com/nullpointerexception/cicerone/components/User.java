package com.nullpointerexception.cicerone.components;

import androidx.annotation.NonNull;

import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User extends StorableEntity
{
    /** Email of account */
    protected String email,
    /** Formal name */
            displayName,
    /** URL of profile picture */
            profileImageUrl;
    /** Stores the type of access done by user */
    protected AccessType accessType;

    //  TODO: Add other fields

    public User() {}

    /** Construct object from a FireBase user and set fields from it */
    public User(@NonNull FirebaseUser user)
    {
        email = user.getEmail();
        displayName = user.getDisplayName();
        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
        accessType = AccessType.DEFAULT;
    }

    /** Construct object from a Google account and set fields from it */
    public User(@NonNull GoogleSignInAccount user)
    {
        email = user.getEmail();
        displayName = user.getDisplayName();
        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
        accessType = AccessType.GOOGLE;
    }

    /** Construct object from a Facebook account and set fields from it */
    public User(@NonNull Profile user)
    {
        displayName = user.getName();
        if(user.getProfilePictureUri(64, 64) != null)
            profileImageUrl = user.getProfilePictureUri(64, 64).toString();
        accessType = AccessType.FACEBOOK;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public AccessType getAccessType()
    {
        return accessType;
    }

    public void setAccessType(AccessType type)
    {
        this.accessType = type;
    }

    /**
     *      Describes the type of access user done
     */
    public enum AccessType
    {
        /** Access with an email and password */
        DEFAULT,
        /** Access with google sign-in */
        GOOGLE,
        /** Access with facebook account */
        FACEBOOK
    }

    /**
     *      Implementation of its superclass method.
     *      Provides an id to indexing storage of this object type.
     *
     *      @return     Identifier of this object type on database.
     */
    @Override
    public String getId()
    {
        return email;
    }

    /**
     *      Implementation customized to convert field 'accessType'.
     *
     *      @param field    Field to set
     *      @param value    Value to set
     */
    @Override
    protected void setComplexTypedField(Field field, String value) throws IllegalAccessException
    {
        if(field.getType().equals(AccessType.class))
        {
            if(value != null)
                for(AccessType accessType : AccessType.values())
                    if(accessType.name().equalsIgnoreCase(value))
                    {
                        field.set(this, accessType);
                        break;
                    }
        }
    }


}
