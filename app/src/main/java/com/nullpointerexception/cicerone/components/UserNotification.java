package com.nullpointerexception.cicerone.components;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *      UserNotification
 *
 *      Represents a notification to show to a specific user.
 *      It is stored into database until that user can retrieve it.
 *
 *      @author Luca
 */
public class UserNotification extends StorableEntity
{

    /**    Id of user that will receive notification*/
    protected String idUser;
    /**    Text to show as notification title */
    protected String title,
    /**    Text to show as notification content */
                     content,
    /**    Id of itinerary to show when this notification get clicked */
                     idItinerary;

    public UserNotification(String userTarget)
    {
        idUser = userTarget;
    }

    public UserNotification(String userTarget, String title, String content)
    {
        idUser = userTarget;
        this.title = title;
        this.content = content;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdItinerary() {
        return idItinerary;
    }

    public void setIdItinerary(String idItinerary) {
        this.idItinerary = idItinerary;
    }

    @Override
    public String getId()
    {
        return idUser + "/" + hashCode();
    }

    @Override
    public List<String> getIgnoredFields()
    {
        return Arrays.asList("idUser");
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotification that = (UserNotification) o;
        return Objects.equals(getIdUser(), that.getIdUser()) &&
                Objects.equals(getTitle(), that.getTitle()) &&
                Objects.equals(getContent(), that.getContent()) &&
                Objects.equals(getIdItinerary(), that.getIdItinerary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUser(), getTitle(), getContent(), getIdItinerary());
    }
}
