package com.nullpointerexception.cicerone.components;

public enum googleAutocompletationField {
    LUOGO(1),
    PUNTO_DI_INCONTRO(2),
    PLACE(3);

    private final int n;

    private googleAutocompletationField(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }
}
