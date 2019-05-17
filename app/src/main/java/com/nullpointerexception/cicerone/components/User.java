package com.nullpointerexception.cicerone.components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User extends StorableEntity
{
    /**  Id of account (generally provided by FireBase)   */
    protected String id,
    /** Email of account */
            email,
    /** Formal name */
            displayName,
    /** URL of profile picture */
            profileImageUrl,
    /** Name of user */
            name,
    /** Surname of user */
            surname,
    /** Birth date of user */
            dateBirth,
    /** Phone number of user */
            phoneNumber;

    public User() {}

    /** Construct object from a FireBase user and set fields from it */
    public User(@NonNull FirebaseUser user)
    {
        id = user.getUid();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        displayName = user.getDisplayName();
        if(displayName != null)     //  TODO: Optimize these fields
        {
            if(displayName.contains(" "))
            {
                name = displayName.substring(0, displayName.indexOf(" "));
                surname = displayName.substring(displayName.indexOf(" ")+1);
            }
            else
                name = displayName;
        }

        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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
        return id;
    }


}
