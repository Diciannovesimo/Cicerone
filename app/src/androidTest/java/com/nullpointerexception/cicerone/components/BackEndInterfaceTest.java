package com.nullpointerexception.cicerone.components;

import androidx.test.rule.ActivityTestRule;

import com.nullpointerexception.cicerone.activities.SplashScreen;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BackEndInterfaceTest
{

    @Rule
    public ActivityTestRule<SplashScreen> mActivityTestRule = new ActivityTestRule<>(SplashScreen.class);

    private final String TEST_ID = "TEST";
    private final String TEST_NAME = "TestStoreField";

    private static User user = new User();

    @Test
    public void A0_getFieldTest()
    {
        user = new User();
        user.setId(TEST_ID);

        BackEndInterface.get().getField(user, "name", new BackEndInterface.OnDataReceivedListener()
        {
            @Override
            public void onDataReceived()
            {
                assertEquals(TEST_NAME, user.getName());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void A1_getEntityTest()
    {
        user = new User();
        user.setId(TEST_ID);

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnDataReceivedListener()
        {
            @Override
            public void onDataReceived()
            {
                assertEquals(TEST_NAME, user.getName());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void A2_storeEntityTest()
    {
        user.setId("FAKE_USER");
        user.setName(TEST_NAME);

        BackEndInterface.get().storeEntity(user);

        user = new User();
        user.setId("FAKE_USER");

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnDataReceivedListener()
        {
            @Override
            public void onDataReceived()
            {
                assertEquals(TEST_NAME, user.getName());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }


    @Test
    public void B_storeField()
    {
        final String TEST = "TestStoreField";

        user.setId("FAKE_USER");
        user.setSurname(TEST);

        BackEndInterface.get().storeField(user, "surname");

        user.setSurname(null);

        BackEndInterface.get().getField(user, "surname", new BackEndInterface.OnDataReceivedListener()
        {
            @Override
            public void onDataReceived()
            {
                assertEquals(TEST, user.getSurname());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void C_removeEntity()
    {
        user = new User();
        user.setId("FAKE_USER");
        user.setName(TEST_NAME);

        BackEndInterface.get().removeEntity(user);
        BackEndInterface.get().getField(user, "name", new BackEndInterface.OnDataReceivedListener()
        {
            @Override
            public void onDataReceived()
            {
                assertNotEquals(TEST_NAME, user.getName());
            }

            @Override
            public void onError()
            {
                assertTrue(true);
            }
        });
    }

}