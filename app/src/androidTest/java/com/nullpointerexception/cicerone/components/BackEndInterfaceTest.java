package com.nullpointerexception.cicerone.components;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.activities.SplashScreen;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.List;
import java.util.Vector;

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

    @Test
    public void A0_getFieldTest()
    {
        final User user = new User();
        user.setId(TEST_ID);

        BackEndInterface.get().getField(user, "name", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
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
        final User user = new User();
        user.setId(TEST_ID);

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
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
        final User user = new User();
        user.setId("FAKE_USER");
        user.setName(TEST_NAME);

        BackEndInterface.get().storeEntity(user, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                user.setName(null);

                BackEndInterface.get().getEntity(user, new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
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

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void A22_storeEntityItineraryTest()
    {
        Itinerary itinerary = new Itinerary();

        itinerary.setId("Test");
        itinerary.setLanguage("italiano");
        itinerary.setDescription("Descrizione fake.");
        itinerary.setDate(Calendar.getInstance().toString());
        itinerary.setIdCicerone("FAKE_USER");

        List<Stage> stages = new Vector<>();
        stages.add(new Stage("Colosseo", "Via address ...", new LatLng(30.221, 48.65265)));
        stages.add(new Stage("Pianeta terra", "Via Lattea", new LatLng(74.24621, 16.65)));
        stages.add(new Stage("Computer Point", "Via davanti la casa di William", new LatLng(18.235, 52.21985)));
        itinerary.setStages(stages);

        List<User> users = new Vector<>();
        User user1 = new User();
            user1.setId("user1");
            user1.setName("User");  user1.setSurname("1");
            user1.setProfileImageUrl("urlFake//:fwer5w6er51gw5erg16w5er1");
        User user2 = new User();
            user2.setId("user2");
            user2.setName("User");  user2.setSurname("2");
            user2.setProfileImageUrl("urlFake2//:fwer5w6er51gw5erg16w5er1");
        users.add(user1);
        users.add(user2);
        itinerary.setParticipants(users);

        BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                assertTrue(true);
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

        final User user = new User();
        user.setId("FAKE_USER");
        user.setSurname(TEST);

        BackEndInterface.get().storeField(user, "surname", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                user.setSurname(null);

                BackEndInterface.get().getField(user, "surname", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
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
        final User user = new User();
        user.setId("FAKE_USER");
        user.setName(TEST_NAME);

        BackEndInterface.get().removeEntity(user, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                BackEndInterface.get().getField(user, "name", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertNotEquals(TEST_NAME, user.getName());
                    }

                    @Override
                    public void onError()
                    {
                        fail();
                    }
                });
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void D_UserTest ()
    {
        final User TEST1 = new User();
        final User TEST2 = new User();
        TEST1.setId("FAKE_USER_TEST");
        TEST1.setName(TEST_NAME);

        BackEndInterface.get().storeEntity(TEST1, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                TEST2.setId(TEST1.getId());
                TEST2.setName(TEST1.getName());


                BackEndInterface.get().getEntity(TEST1, new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertEquals(TEST2, TEST1);
                    }

                    @Override
                    public void onError()
                    {
                        fail();
                    }
                });
            }

                @Override
                public void onError()
                {
                    fail();
                }
        });
    }


}