package com.nullpointerexception.cicerone.components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User extends StorableEntity implements StorableAsField
{
    /**  Id of account (generally provided by FireBase)   */
    protected String id,
    /** Email of account */
    email,
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
        if(user.getDisplayName() != null)
        {
            if(user.getDisplayName().contains(" "))
            {
                name = user.getDisplayName().substring(0, user.getDisplayName().indexOf(" "));
                surname = user.getDisplayName().substring(user.getDisplayName().indexOf(" ")+1);
            }
            else
            {
                name = user.getDisplayName();
                surname = "";
            }
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

    /**
     *      @return Return name and surname concatenated
     */
    public String getDisplayName()
    {
        return name + " " + surname;
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

    public void setId(String id) {
        this.id = id;
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

    /**
     *      Implementation of its superclass method.
     *
     *      @return A list of ignored fields
     */
    @Override
    public List<String> getIgnoredFields()
    {
        return Collections.singletonList("id");
    }

    /**
     *      Implementation of its superclass method.
     *      Provides an id to indexing storage of this object type as sub-field.
     *
     *      @return     Identifier of this object type as sub-field on database.
     */
    @Override
    public String getFieldId()
    {
        return id;
    }

    /**
     *      Implementation of its superclass method.
     *
     *      @return The map with field values needed.
     */
    @Override
    public Map<String, String> getSubFields()
    {
        Map<String, String> map = new HashMap<>();

        String displayName = getDisplayName();
        String imageUrl = getProfileImageUrl();

        if(displayName != null)
            map.put("displayName", displayName);

        if(imageUrl != null)
            map.put("profileImageUrl", imageUrl);

        return map;
    }

    /**
     *      Implementation of its superclass method.
     *      Restores fields stored before on database.
     */
    @Override
    public void restoreSubFields(Map<String, String> subFields)
    {
        for(String key : subFields.keySet())
        {
            String value = subFields.get(key);

            if(value != null)
            {
                if(key.equals("displayName"))
                {
                    if(value.contains(" "))
                    {
                        name = value.substring(0, value.indexOf(" "));
                        surname = value.substring(value.indexOf(" ")+1);
                    }
                    else
                    {
                        name = value;
                        surname = "";
                    }
                }

                if(key.equals("profileImageUrl"))
                {
                    profileImageUrl = value;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(profileImageUrl, user.profileImageUrl) &&
                Objects.equals(name, user.name) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(dateBirth, user.dateBirth) &&
                Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, profileImageUrl, name, surname, dateBirth, phoneNumber);
    }
}