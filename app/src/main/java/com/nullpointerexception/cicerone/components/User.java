package com.nullpointerexception.cicerone.components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User extends StorableEntity implements StorableAsField, ListOfStorables
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

    /**   List of itineraries which this user have or is participating.  */
    protected List<Itinerary> itineraries = new ArrayList<>();

    /**   Average vote into user feedbacks  */
    protected float averageFeedback;

    /**   Number of feedbacks given to this user.  */
    protected int feedbacksCount;

    /**   List of feedback user received from other users.  */
    protected List<Feedback> feedbacks = new Vector<>();

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
        {
            profileImageUrl = user.getPhotoUrl().toString();

            if(profileImageUrl.contains("facebook"))
                profileImageUrl += "?type=large";
        }
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

    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    public void addItinerary(Itinerary itinerary)
    {
        itineraries.add(0, itinerary);
    }

    public void removeItinerary(int index) {
        itineraries.remove(index);
    }

    public List<Feedback> getFeedbacks()
    {
        return new Vector<>(feedbacks);
    }

    public float getAverageFeedback() {
        return averageFeedback;
    }

    public void setAverageFeedback(float averageFeedback) {
        this.averageFeedback = averageFeedback;
    }

    public int getFeedbacksCount() {
        return feedbacksCount;
    }

    public void setFeedbacksCount(int feedbacksCount) {
        this.feedbacksCount = feedbacksCount;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    /**
     *      Add a feedback into user feedbacks list and calculate new average rating.
     *
     *      @param feedback Feedback to add.
     */
    public void addFeedback(Feedback feedback)
    {
        int sum = (int) (averageFeedback * feedbacksCount);

        sum += feedback.getVote();
        feedbacks.add(feedback);
        feedbacksCount++;

        averageFeedback = (float) sum / feedbacksCount;
    }

    /**
     *      Edit a previous shared feedback and calculate new average rating.
     *
     *      @param feedback Feedback to edit.
     */
    public void editFeedback(Feedback feedback)
    {
        int sum = (int) (averageFeedback * feedbacksCount);

        for(Feedback fd : feedbacks)
            if(fd.getIdUser().equals(feedback.getIdUser()))
            {
                sum = sum - fd.getVote() + feedback.getVote();

                fd.setVote(feedback.getVote());
                fd.setComment(feedback.getComment());

                break;
            }

        averageFeedback = (float) sum / feedbacksCount;
    }

    /**
     *      Remove a feedback from user feedbacks list and calculate new average rating.
     *
     *      NOTE: This method require a manual delete of the node on the database.
     *
     *      @param feedback Feedback to remove.
     */
    public void removeFeedback(Feedback feedback)
    {
        int sum = (int) (averageFeedback * feedbacksCount);

        sum -= feedback.getVote();
        feedbacks.remove(feedback);
        feedbacksCount--;

        if(feedbacksCount > 0)
            averageFeedback = (float) sum / feedbacksCount;
        else
            averageFeedback = 0;
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
     *      Implementation of its superclass method
     */
    @Override
    public void restoreId(String id)
    {
        this.id = id;
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
    public Object addNewInstanceInto(String fieldName)
    {

        if(fieldName.equals("itineraries"))
        {
            Itinerary itinerary = new Itinerary();

            if(itineraries == null)
                itineraries = new Vector<>();

            itineraries.add(0, itinerary);
            return itinerary;
        }

        if(fieldName.equals("feedbacks"))
        {
            Feedback feedback = new Feedback(id);

            if(feedbacks == null)
                feedbacks = new Vector<>();

            feedbacks.add(feedback);
            return feedback;
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Float.compare(user.getAverageFeedback(), getAverageFeedback()) == 0 &&
                getFeedbacksCount() == user.getFeedbacksCount() &&
                Objects.equals(getId(), user.getId()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getProfileImageUrl(), user.getProfileImageUrl()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getSurname(), user.getSurname()) &&
                Objects.equals(getDateBirth(), user.getDateBirth()) &&
                Objects.equals(getPhoneNumber(), user.getPhoneNumber()) &&
                Objects.equals(getItineraries(), user.getItineraries()) &&
                Objects.equals(getFeedbacks(), user.getFeedbacks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getProfileImageUrl(), getName(), getSurname(), getDateBirth(), getPhoneNumber(), getItineraries(), getAverageFeedback(), getFeedbacksCount(), getFeedbacks());
    }
}