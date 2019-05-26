package com.nullpointerexception.cicerone.components;

import androidx.test.rule.ActivityTestRule;

import com.nullpointerexception.cicerone.activities.SplashScreen;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ItineraryTest {

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
    List<Tappa> stages = null;

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
    public void GetEntityItinerary(){
        final Itinerary fakeIt = new Itinerary();
        final Itinerary testIt = new Itinerary();

        fakeIt.setId(ID_IT);
        fakeIt.setCurrency(CURRENCY);
        fakeIt.setDate(DATE);
        fakeIt.setIdCicerone(ID_CICERONE);
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
            public void onError() {fail();}
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
}