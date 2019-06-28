package com.nullpointerexception.cicerone.components;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;
import com.nullpointerexception.cicerone.activities.SplashScreen;

import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ItineraryTest
{

    @Rule
    public ActivityTestRule<SplashScreen> mActivityTestRule = new ActivityTestRule<>(SplashScreen.class);

    /*
        Per luca, se vuoi eseguire i test in uno specifico ordine fai cosi:
        aggiungi sopra public class itinerarytest : @FixMethodOrder(MethodSorters.NAME_ASCENDING)
        e cambia i nomi dei test con delle lettere tipo A,B,C ecc...

        i test sulle liste li aggiungerò in seguito

        stai attento ai campi fieldname in backendinterface variano a seconda di come li chiami
     */
    String ID_CICERONE = "FAKE_USER",
    ID_IT = "TEST",
    CURRENCY = "TEST_CURRENCY",
    DATE = "01/01/20",
    LANGUAGE = "IT",
    LOCATION = "CANOSA",
    MEETING_PLACE = "CATTEDRALE SAN SABINO";

    int MAX_PART = 20,
    PRICE = 100;

    List<User> participants = null;
    List<Stage> stages = null;

    // per il test sull'inserimento dell'utente
    private final String TEST_ID = "TEST";

    /*
        Test che verifica il corretto inserimento di un
        utente nella lista dei partecipanti
     */
    @Test
    public void SetParticipantInDatabase(){
        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);

        final User user = new User();
        user.setId(TEST_ID);

        BackEndInterface.get().getEntity(user, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                participants.add(user);
            }

            @Override
            public void onError()
            {
                fail();
            }
        });

        BackEndInterface.get().storeField(fakeIt, "participants", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                fakeIt.setParticipants(null);

                BackEndInterface.get().getField(fakeIt, "participants", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertEquals(participants, fakeIt.getParticipants());
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

    /*
        Test per controllare se il sistema accetti
        utenti con id non presenti nel DBMS il test
        fallisce se l'utente è inserito nell'itinerario fake
        poichè in teoria è impossibile dato che quell'utente non esiste
        nel sistema
     */
    @Test
    public void SetParticipantNotInDatabase(){
        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);

        final User user = new User();
        user.setId("Casual_ID");

        participants.add(user);

        BackEndInterface.get().storeField(fakeIt, "participants", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                fakeIt.setParticipants(null);

                BackEndInterface.get().getField(fakeIt, "participants", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertNotEquals(participants, fakeIt.getParticipants());
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
    /*
        inserisco un itinerario di prova e subito dopo faccio il getEntity
        su un itinerario vuoto per eseguire un assertEquals sui due itinerari
        e vedere se sono uguali
     */
    @Test
    public void GetEntityItinerary()
    {
        final Itinerary fakeIt = new Itinerary();
        final Itinerary testIt = new Itinerary();

        fakeIt.setId(ID_IT);
        fakeIt.setCurrency(CURRENCY);
        fakeIt.setDate(DATE);
        //fakeIt.setIdUser(ID_CICERONE);
        fakeIt.setLanguage(LANGUAGE);
        fakeIt.setLocation(LOCATION);
        fakeIt.setMaxParticipants(MAX_PART);
        fakeIt.setMeetingPlace(MEETING_PLACE);
        fakeIt.setParticipants(participants);
        fakeIt.setPrice(PRICE);
        fakeIt.setStages(stages);

        BackEndInterface.get().storeEntity(fakeIt, new BackEndInterface.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() { fail(); }
        });

        BackEndInterface.get().getEntity(testIt, new BackEndInterface.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                assertEquals(fakeIt, testIt);
            }

            @Override
            public void onError() {
                fail();
            }
        });
    }

    /*
        Test della restituzione di un campo di un itinerario
        ATTENZIONE: Luca dopo aver implementato controlla il campo fieldName nella
        funzione backendinterface, anche per quello dopo.
     */
    @Test
    public void getItineraryFieldTest()
    {
        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);

        BackEndInterface.get().getField(fakeIt, "language", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                assertEquals(LANGUAGE, fakeIt.getLanguage());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void getItineraryEntityTest()
    {
        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);

        BackEndInterface.get().getEntity(fakeIt, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                assertEquals(LANGUAGE, fakeIt.getLanguage());
            }

            @Override
            public void onError()
            {
                fail();
            }
        });
    }

    @Test
    public void storeItineraryEntityTest()
    {
        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);
        fakeIt.setCurrency(CURRENCY);

        BackEndInterface.get().storeEntity(fakeIt, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                fakeIt.setCurrency(null);

                BackEndInterface.get().getEntity(fakeIt, new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertEquals(CURRENCY, fakeIt.getCurrency());
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
    public void storeItineraryField()
    {
        final String CURRENCY_TEST = "TestCurrency";

        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);
        fakeIt.setCurrency(CURRENCY_TEST);

        BackEndInterface.get().storeField(fakeIt, "surname", new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                fakeIt.setCurrency(null);

                BackEndInterface.get().getField(fakeIt, "surname", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertEquals(CURRENCY_TEST, fakeIt.getCurrency());
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
    public void ItineraryremoveEntity()
    {
        final String TEST_LOCATION = "TestCurrency";

        final Itinerary fakeIt = new Itinerary();
        fakeIt.setId(ID_IT);
        fakeIt.setLocation(TEST_LOCATION);

        BackEndInterface.get().removeEntity(fakeIt, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                BackEndInterface.get().getField(fakeIt, "location", new BackEndInterface.OnOperationCompleteListener()
                {
                    @Override
                    public void onSuccess()
                    {
                        assertNotEquals(TEST_LOCATION, fakeIt.getLocation());
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
    public void generateIdTest()
    {
        Itinerary itinerary = new Itinerary();
        //itinerary.setIdUser("34CCpLlS9Eb6aTUcOXLvt5gh0cu1");
        itinerary.setDate("2019-06-04");
        itinerary.setMeetingTime("20:30");
        itinerary.generateId();

        assertEquals( itinerary.getId(), "34CCpLlS9Eb6aTUcOXLvt5gh0cu12019-06-0420:30");
    }

    @Test
    public void getFieldsFromIdTest()
    {
        Itinerary itinerary = new Itinerary();
        itinerary.setId("34CCpLlS9Eb6aTUcOXLvt5gh0cu12019-06-0420:30");
        itinerary.getFieldsFromId();

        //assertEquals( itinerary.getIdUser(), "34CCpLlS9Eb6aTUcOXLvt5gh0cu1");
        assertEquals( itinerary.getDate(), "2019-06-04" );
        assertEquals( itinerary.getMeetingTime(), "20:30" );
    }

    int count = 0;
    //  TODO: Decommentare test e avviarlo in singolo in modalità debug con breakpoint a storeEntity per generare itinerari.
    //@Test
    public void fillItineraries()
    {
        final int nItineraries = 10;

        List<String> places = Arrays.asList("Milano", "Venezia", "Torino", "Londra", "Roma", "Barcellona", "Bari",
                "Canosa di Puglia", "Bitritto", "Napoli", "New York City", "Berlino");

        final String targetCiceroneId = "fQv6mnTt6BTnuULNhv1DeasBOuL2";
        //  34CCpLlS9Eb6aTUcOXLvt5gh0cu1    (Luca account Google)

        Random random = new Random();
        for(int i = 0; i < nItineraries; i++)
        {
            Itinerary itinerary = new Itinerary();
            itinerary.setCurrency("€");
            Date currentDate = Calendar.getInstance().getTime();
            currentDate.setMonth( currentDate.getMonth() + random.nextInt(3) );
            currentDate.setDate( random.nextInt(20) );
            itinerary.setDate(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentDate));
            //itinerary.setCicerone();
            //itinerary.setIdUser(targetCiceroneId);
            itinerary.setLanguage("Italiano");
            String place = places.get( random.nextInt( places.size()-1 ));
            itinerary.setLocation(place);
            itinerary.setMeetingPlace(place);
            itinerary.setMaxParticipants(random.nextInt(100));
            itinerary.setPrice( (float) random.nextInt(5000) / 100f);
            itinerary.setMeetingTime("23:" + (10 + random.nextInt(49)));
            itinerary.setDescription("A little trip in " + place + ", hope you will like it!");

            //  Participants
            List<User> participants = new Vector<>();
            int bound = random.nextInt(5);

            for(int n = 0; n < bound; n++)
            {
                User user = new User();
                user.setId("FAKE USER");
                user.setName("User #" + n);
                user.setProfileImageUrl("fakeUrl://sdfgwwerbwrgwergwergwebwe");
                participants.add(user);
            }

            itinerary.setParticipants(participants);

            //  Stages
            List<Stage> stages = new Vector<>();
            bound = random.nextInt(5) + 1;

            for(int n = 0; n < bound; n++)
            {
                String loc = places.get( random.nextInt( places.size()-1 ));
                LatLng coords = new LatLng((float) random.nextInt(10000) / 100f,
                        (float) random.nextInt(10000) / 100f);
                stages.add( new Stage(loc, loc, coords));
            }

            itinerary.setStages(stages);
            itinerary.generateId();

            BackEndInterface.get().storeEntity(itinerary, new BackEndInterface.OnOperationCompleteListener()
            {
                @Override
                public void onSuccess()
                {
                    if(count == nItineraries)
                        assertTrue(true);
                    else
                        count++;
                }

                @Override
                public void onError() { fail(); }
            });
        }
    }
}