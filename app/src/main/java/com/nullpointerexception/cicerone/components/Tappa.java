package com.nullpointerexception.cicerone.components;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Tappa implements StorableAsField
{

    private String nome;
    private String indirizzo;
    private LatLng coordinate;
    private String descrizione;

    public Tappa(String nome, String indirizzo, LatLng coordinate)
    {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.coordinate = coordinate;
    }

    public Tappa() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String getFieldId()
    {
        return null;
    }

    @Override
    public Map<String, String> getSubFields()
    {
        return new HashMap<>();
    }

    @Override
    public void restoreSubFields(Map<String, String> subFields)
    {

    }
}
