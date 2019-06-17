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
    public void Z1_getItineraryTest()
    {
        Itinerary test = new Itinerary();
        test.setId("TestGet");

        BackEndInterface.get().getEntity(test, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                boolean success = test.getParticipants().get(0).getId().equals("user1")
                        && test.getMaxParticipants() == 50
                        && test.price == 10f;
                assertTrue(success);
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void Z2_storeEntityItineraryTest()
    {
        Itinerary itinerary = new Itinerary();

        itinerary.setId("TestStore");
        itinerary.setLanguage("italiano");
        itinerary.setDescription("Descrizione fake.");
        itinerary.setDate(Calendar.getInstance().toString());
        //itinerary.setIdCicerone("FAKE_USER");
        itinerary.setMaxParticipants(50);
        itinerary.setPrice(10f);

        List<Stage> stages = new Vector<>();
        stages.add(new Stage("Colosseo", "Via address ...", new LatLng(30.221, 48.65265)));
        stages.add(new Stage("Pianeta terra", "Via Lattea", new LatLng(74.24621, 16.65)));
        stages.add(new Stage("Computer Point", "Via davanti la casa di William", new LatLng(18.235, 52.21985)));
        itinerary.setStages(stages);
        itinerary.setProposedStages(stages);

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
                Itinerary it2 = new Itinerary();
                it2.setId("TestStore");

                BackEndInterface.get().getEntity(it2, new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertEquals(itinerary, it2);
                    }

                    @Override
                    public void onError()
                    {
                        fail("Get entity failed.");
                    }
                });
            }

            @Override
            public void onError()
            {
                fail("Store entity failed.");
            }
        });
    }

    @Test
    public void Z3_removeItineraryTest()
    {
        Itinerary test = new Itinerary();
        test.setId("TestStore");

        BackEndInterface.get().removeEntity(test, new BackEndInterface.OnOperationCompleteListener()
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


}