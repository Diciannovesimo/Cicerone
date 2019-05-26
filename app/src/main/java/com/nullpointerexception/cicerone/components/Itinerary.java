package com.nullpointerexception.cicerone.components;

import java.util.List;

/**
 *      Itinerary
 *
 *      Represents a path that a Cicerone want to offer to Globetrotters.
 *
 *      @author Luca
 */
public class Itinerary extends StorableEntity
{
    /**   Id of itinerary  */
    protected String id,
    /**   Id of cicerone which have created this itinerary  */
            idCicerone,
    /**   City of itinerary  */
            location,
    /**   Date when itinerary will be done  */
            date,
    /**   Location where Cicerone will meet Globetrotters to start itinerary  */
            meetingPlace,
    /**   Time of meeting */
            meetingTime,
    /**   Language spoken during itinerary by Cicerone who is guiding it  */
            language,
    /**   Currency of price of itinerary  */
            currency,
    /**   Description of itinerary  */
            description;

    /**   Price to pay to Cicerone to participate to this itinerary  */
    protected float price;

    /**   Max numbers of participants to this itinerary  */
    protected int maxParticipants;

    /**   List of stages that will be visited into this itinerary  */
    protected List<Stage> stages;

    /**   List of users that will participate to this itinerary  */
    protected List<User> participants;

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCicerone() {
        return idCicerone;
    }

    public void setIdCicerone(String idCicerone) {
        this.idCicerone = idCicerone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getId()
    {
        return id;
    }
}
