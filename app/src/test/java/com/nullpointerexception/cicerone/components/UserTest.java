package com.nullpointerexception.cicerone.components;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {


    public User user = new User("william","willy", User.AccessType.DEFAULT);

    /*
        Test on get function
     */
    @Test
    public void getEmailTest(){
        assertEquals("william",user.getEmail());
    }

    @Test
    public void getDisplayNameTest()
    {
        assertEquals("willy",user.getDisplayName());
    }

    @Test
    public void getAccessType()
    {
        assertEquals(User.AccessType.DEFAULT,user.getAccessType());
    }

    /*
        Test on set function
     */
    @Test
    public void setEmailTest(){
        user.setEmail("william2");
        assertEquals("william2",user.getEmail());
    }

    @Test
    public void setDisplayNameTest()
    {
        user.setDisplayName("willy2");
        assertEquals("willy2",user.getDisplayName());
    }

    @Test
    public void setAccessType(){
        user.setAccessType(User.AccessType.GOOGLE);
        assertEquals(User.AccessType.GOOGLE,user.getAccessType());
    }
}