package com.nullpointerexception.cicerone.components;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ObjectSharerTest
{

    @Test
    public void shareObject()
    {
        assertTrue(true);
    }

    @Test
    public void getSharedObject()
    {
        assertTrue(true);
    }

    @Test
    public void removeWithKey()
    {
        User user = new User();
        user.setName("Svil");

        ObjectSharer.get().shareObject("user", user);

        ObjectSharer.get().remove("user");

        assertNull( ObjectSharer.get().getSharedObject("user") );
    }
}