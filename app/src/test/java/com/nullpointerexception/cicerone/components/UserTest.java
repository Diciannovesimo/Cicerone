package com.nullpointerexception.cicerone.components;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {


    public User user = new User();

    /*
        Test on set and get function
     */
    @Test
    public void setEmailTest(){
        user.setEmail("william2");
        assertEquals("william2",user.getEmail());
    }

    @Test
    public void setDisplayNameTest()
    {
        user.setName("willy2");
        assertEquals("willy2",user.getName());
    }

}