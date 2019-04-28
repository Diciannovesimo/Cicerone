package com.nullpointerexception.cicerone.components;

import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User
{
    /** Email of account */
    private String email,
    /** Formal name */
            displayName,
    /** URL of profile picture */
            profileImageUrl;
    /** Stores the type of access done by user */
    private AccessType accessType;

    /** Construct object from a Firebase user and set fields from it */
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
}
