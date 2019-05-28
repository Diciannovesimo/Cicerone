package com.nullpointerexception.cicerone.components;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Stage implements StorableAsField
{

    private String name;
    private String address;
    private LatLng coordinates;
    private String description;

    public Stage(String name, String address, LatLng coordinates)
    {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
    }

    public Stage() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getFieldId()
    {
        return coordinates.toString();
    }

    @Override
    public Map<String, String> getSubFields()
    {
        Map<String, String> result = new HashMap<>();

        result.put("description", description);
        result.put("address", address);
        result.put("name", name);

        return result;
    }

    @Override
    public void restoreId(String id)
    {
        id = id.replace(" ", "");
        double lat = Double.parseDouble( id.substring(id.indexOf("(")+1, id.indexOf(",")) );
        double lng = Double.parseDouble( id.substring(id.indexOf(",")+1, id.indexOf(")")) );
        this.coordinates = new LatLng(lat, lng);
    }

    @Override
    public void restoreSubFields(Map<String, String> subFields)
    {
        for(String key : subFields.keySet())
        {
            String value = subFields.get(key);

            if(value != null)
            {
                if(key.equals("description"))
                    description = value;

                if(key.equals("address"))
                    address = value;

                if(key.equals("name"))
                    name = value;
            }
        }
    }
}
