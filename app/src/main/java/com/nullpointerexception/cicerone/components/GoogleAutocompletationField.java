package com.nullpointerexception.cicerone.components;

public enum GoogleAutocompletationField {
    LUOGO(1),
    PUNTO_DI_INCONTRO(2),
    PLACE(3);

    private final int n;

    private GoogleAutocompletationField(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }
}
