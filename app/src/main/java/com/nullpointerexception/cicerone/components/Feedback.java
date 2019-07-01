package com.nullpointerexception.cicerone.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the feedback given from every user to a Cicerone
 *
 * TODO: continua
 */
public class Feedback implements StorableAsField
{
    private String idUser,
                   displayNameUser,
                   profileImageUrlUser,
                   comment;
    private int vote;

    public Feedback(String idUser)
    {
        this.idUser = idUser;
    }

    public Feedback(User user)
    {
        this.idUser = user.getId();
        this.displayNameUser = user.getDisplayName();
        this.profileImageUrlUser = user.getProfileImageUrl();
    }

    public String getIdUser()
    {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getProfileImageUrlUser() {
        return profileImageUrlUser;
    }

    public void setProfileImageUrlUser(String profileImageUrlUser) {
        this.profileImageUrlUser = profileImageUrlUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getDisplayNameUser() {
        return displayNameUser;
    }

    public void setDisplayNameUser(String displayNameUser) {
        this.displayNameUser = displayNameUser;
    }

    @Override
    public String getFieldId()
    {
        return idUser;
    }

    @Override
    public Map<String, String> getSubFields()
    {
        Map<String, String> fields = new HashMap<>();

        fields.put("profileImageUrlUser", profileImageUrlUser);
        fields.put("displayNameUser", displayNameUser);
        fields.put("comment", comment);
        fields.put("vote", String.valueOf(vote));

        return fields;
    }

    @Override
    public void restoreId(String id)
    {
        this.idUser = id;
    }

    @Override
    public void restoreSubFields(Map<String, String> subFields)
    {
        profileImageUrlUser = subFields.get("profileImageUrlUser");
        displayNameUser = subFields.get("displayNameUser");
        comment = subFields.get("comment");
        if( subFields.get("vote") != null)
            vote = Integer.parseInt( subFields.get("vote"));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return getIdUser().equals(feedback.getIdUser());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getIdUser(), getDisplayNameUser(), getProfileImageUrlUser(), getComment(), getVote());
    }
}
