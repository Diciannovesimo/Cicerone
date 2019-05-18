package com.nullpointerexception.cicerone.components;

import androidx.test.rule.ActivityTestRule;

import com.nullpointerexception.cicerone.activities.SplashScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackEndInterfaceTest {

    @Rule
    public ActivityTestRule<SplashScreen> mActivityTestRule = new ActivityTestRule<>(SplashScreen.class);

    private static User user = new User();

    @Test
    @Before
    public void storeEntityTest() {
        user.setId("sgh65e4ahehfdabva5");
        user.setName("Will");
        user.setDateBirth("14/03/1998");
        user.setSurname("I am");
        user.setPhoneNumber("3312514186");
        user.setEmail("williamocoluccidiscord@gmail.com");

        BackEndInterface.get().storeEntity(user);
        //TODO : Aggiungere una asserzione adeguata
        assertNotNull(user);
        /*assertEquals("", BackEndInterface.get().getField(user, "", new BackEndInterface.OnDataReceiveListener() {
            @Override
            public void onDataReceived(String data) {

            }

            @Override
            public void onError() {

            }
        })); */
    }


    @Test
    @After
    public void storeField() {
        user.setId("sgh65e4ahehfdabva5");
        user.setName("Will");
        user.setDateBirth("14/03/1998");
        user.setSurname("Collucci");
        user.setPhoneNumber("3312514186");
        user.setEmail("williamocoluccidiscord@gmail.com");

        BackEndInterface.get().storeField(user, "surname");
        assertNotNull(user);
    }

    @Test
    public void removeEntity() {
        user.setId("sgh65e4ahehfdabva5");
        user.setName("Will");
        user.setDateBirth("14/03/1998");
        user.setSurname("I am");
        user.setPhoneNumber("3312514186");
        user.setEmail("williamocoluccidiscord@gmail.com");

        BackEndInterface.get().removeEntity(user);
        assertNotNull(user);
    }

}