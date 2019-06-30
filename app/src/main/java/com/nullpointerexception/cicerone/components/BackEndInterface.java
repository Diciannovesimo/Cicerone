package com.nullpointerexception.cicerone.components;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *      Interfaces components to FireBase Database,
 *      encrypting/decrypting data, giving a clear output.
 *
 *      @author Luca
 */
public class BackEndInterface
{
    /**  Encryption key   */
    private final String ENCRYPTION_KEY = "qergSB65S15s4fb156t.ò876ò,43ò,925y5ADSfgds56gfw75FFB";
    private final String TAG = "BackEndInterface";

    /*
            Singleton setting
     */
    /**  Instance of this class   */
    private static final BackEndInterface ourInstance = new BackEndInterface();
    /**  Used to access to this class   */
    public static BackEndInterface get() { return ourInstance; }
    /**  Private constructor to permit a single instance of this class   */
    private BackEndInterface() { }

    /**
     *      Encrypt a given string with a given key.
     *
     *      @param string   String to encrypt
     *      @return String encrypted.
     */
    private String encrypt(String string)
    {
        if(string == null)
            string = "";

        /*
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(ENCRYPTION_KEY)
                .keySize(AES.Key.SIZE_256)
                .encrypt();*/
        return string;
    }

    /**
     *      Decrypt a given string with a given key.
     *
     *      @param string   String to decrypt
     *      @return String decrypted.
     */
    private String decrypt(String string)
    {
        if(string == null)
            string = "";

        /*
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(ENCRYPTION_KEY)
                .keySize(AES.Key.SIZE_256)
                .decrypt();*/

        return string;
    }

    /**
     *      Calls storeEntity() without callback methods
     */
    public void storeEntity(StorableEntity entity) { storeEntity(entity, null); }

    /**
     *      Stores an object on the FireBase database, encrypting its values.
     *
     *      @param entity   Object to store on the database.
     *      @param onOperationCompleteListener Callback methods handler
     */
    public void storeEntity(final StorableEntity entity, final OnOperationCompleteListener onOperationCompleteListener)
    {
        //  Check parameter
        if(entity == null)
        {
            if(onOperationCompleteListener != null)
                onOperationCompleteListener.onError();
            return;
        }

        final long cTime = System.currentTimeMillis();

        String id = getIdFrom(entity);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String entityName = entity.getClass().getSimpleName().toLowerCase();

        Map<String, FieldValue> fields = getFields(entity);

        Task<Void> resultHandler = null;

        DatabaseReference ref = database.getReference(entityName)
                .child(id);

        for(String fieldName : fields.keySet() )
        {
            FieldValue fieldValue = fields.get(fieldName);

            if(fieldValue == null)
                continue;

            Task<Void> resultHandler2 = storeValue(ref.child(fieldName), fieldValue);

            if(resultHandler2 != null)
                resultHandler = resultHandler2;
        }

        if(resultHandler != null)
        {
            resultHandler.addOnCompleteListener(task ->
            {
                long delay = System.currentTimeMillis() - cTime;
                Log.i(TAG, "Operation storeEntity(" + entity.getClass().getSimpleName() +
                        "): stored a field in " + delay + " ms.");

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();
            })
            .addOnFailureListener(e ->
            {
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            });
        }
    }

    /**
     *      Stores a single storedFieldValue on database
     *
     *      @param ref current root node of database you want to start to store
     *      @param storedFieldValue field to store.
     */
    private Task<Void> storeValue(DatabaseReference ref, FieldValue storedFieldValue)
    {
        Task<Void> result = null;

        if(storedFieldValue == null)
            return result;

        /*
             Check field type
         */
        if(storedFieldValue.isAList())
        {
            for(FieldValue sfv : storedFieldValue.getValuesList())
            {
                Task<Void> resultTemp = storeValue(ref, sfv);

                if(resultTemp != null)
                    result = resultTemp;
            }
        }
        else
        {
            if(storedFieldValue.isSubfield())
            {
                StorableAsField saf = storedFieldValue.getSubField();

                if(saf == null)
                    return result;

                Map<String, String> subFields = saf.getSubFields();
                for(String subFieldName : subFields.keySet())
                {
                    String fieldValue = encrypt( subFields.get(subFieldName) );

                    result = ref.child(saf.getFieldId().replace(".", "~")
                            .replace("/", "-"))
                            .child(subFieldName)
                            .setValue( fieldValue );
                }
            }
            else
            {
                String fieldValue = encrypt(storedFieldValue.getValue());
                result = ref.setValue( fieldValue );
            }
        }

        return result;
    }

    /**
     *      Get data from FireBase database fro the given object, and set it with values obtained.
     *
     *      @param entity                         Entity where will be written data.
     *      @param onOperationCompleteListener    Callback methods implementations.
     *
     *      @return The result of operation.
     */
    public void getEntity(final StorableEntity entity, final OnOperationCompleteListener onOperationCompleteListener)
    {
        //  Check parameter
        if(entity == null)
        {
            if(onOperationCompleteListener != null)
                onOperationCompleteListener.onError();
            return;
        }

        final long cTime = System.currentTimeMillis();

        final String id = getIdFrom(entity);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(entity.getClass().getSimpleName().toLowerCase())
                .child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Map<String, FieldValue> fields = getFields(entity);

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    try
                    {
                        String fieldName = ds.getKey();

                        FieldValue fieldValue = fields.get(fieldName);

                        if(fieldValue == null || fieldName == null)
                            continue;

                        if(fieldValue.isAList())
                        {

                            List<FieldValue> fieldValues = new Vector<>();

                            for(DataSnapshot dsChild1 : ds.getChildren())
                            {
                                Map<String, String> map = new HashMap<>();

                                for(DataSnapshot dsChild2 : dsChild1.getChildren())
                                {
                                    String subFieldName = dsChild2.getKey();

                                    if(subFieldName != null)
                                        map.put(subFieldName, dsChild2.getValue(String.class));
                                }

                                fieldValues.add(new FieldValue(new StorableAsField()
                                {
                                    @Override
                                    public String getFieldId() { return dsChild1.getKey(); }

                                    @Override
                                    public Map<String, String> getSubFields()
                                    {
                                        return map;
                                    }

                                    @Override
                                    public void restoreId(String id) { }

                                    @Override
                                    public void restoreSubFields(Map<String, String> subFields) { }
                                }));
                            }

                            fields.put(fieldName, new FieldValue(fieldValues));
                        }
                        if(fieldValue.isSubfield())
                        {
                            Map<String, String> map = new HashMap<>();

                            StorableAsField saf = fieldValue.getSubField();

                            for(DataSnapshot dsChild : ds.child(saf.getFieldId()).getChildren())
                            {
                                String subFieldName = dsChild.getKey();

                                if(subFieldName != null)
                                    map.put(subFieldName, dsChild.getValue(String.class));
                            }

                            fields.put(fieldName, new FieldValue(new StorableAsField()
                            {
                                @Override
                                public String getFieldId() { return null; }

                                @Override
                                public Map<String, String> getSubFields()
                                {
                                    return map;
                                }

                                @Override
                                public void restoreId(String id) { }

                                @Override
                                public void restoreSubFields(Map<String, String> subFields) { }
                            }));
                        }
                        else
                        {
                            fields.put(fieldName, new FieldValue( getValue(ds, fieldValue)) );
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "getEntity Error: " + e.toString());
                    }
                }

                setFields(entity, fields);

                long delay = System.currentTimeMillis() - cTime;
                int bytes = dataSnapshot.toString().getBytes().length;
                Log.i(TAG, "Operation getEntity(" + entity.getClass().getSimpleName() +
                        "): retrieved " + bytes + " bytes in " + delay + " ms.");

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "Error: " + databaseError.toString());
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            }
        });
    }

    /*
            Used only for developer tools, ignore it.
     */
    public void getAllUsers(List<User> users, OnOperationCompleteListener onOperationCompleteListener)
    {
        long startTime = System.currentTimeMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(User.class.getSimpleName().toLowerCase());
        Query query = ref.orderByKey();
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                users.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User user = new User();
                    user.setId(ds.getKey());
                    getEntityFrom(user, ds);
                    users.add(user);
                }

                Log.i(TAG, "getUsers() -> Query finished retrieving " + dataSnapshot.toString().getBytes().length +
                        " bytes in " + (System.currentTimeMillis() - startTime) + " ms.");

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();

                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            }
        });
    }

    /**
     *      Get itineraries created by a cicerone.
     *
     *      @param idCicerone                   Id of cicerone who has created itineraries.
     *      @param itinerariesList              Itineraries created by specified cicerone.
     *      @param onOperationCompleteListener  Implementation of a callback method.
     */
    public void getItinerariesOf(String idCicerone, List<Itinerary> itinerariesList,
                                 OnOperationCompleteListener onOperationCompleteListener)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("itinerary");
        Query query = ref.orderByKey()
                .startAt(idCicerone)
                .endAt(idCicerone.substring(0, idCicerone.length()-1) +
                        (idCicerone.charAt(idCicerone.length()-1)+1) );

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                itinerariesList.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Itinerary itinerary = new Itinerary();
                    itinerary.setId(ds.getKey());
                    getEntityFrom(itinerary, ds);
                    itinerary.getFieldsFromId();
                    itinerariesList.add(itinerary);
                }

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.e(TAG, "Error: " + databaseError.toString());
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            }
        });
    }

    /**
     *      Launch a query on database to found itineraries programmed for a given
     *      location at the given day.
     *
     *      @param location     City where itinerary will be done.
     *      @param date         When itinerary will be done.
     *      @param itinerariesList  List of itineraries to fill with results
     *      @param onOperationCompleteListener  Implementation of the callback method launched after search ends
     */
    public void searchItinerariesFor(String location, String date, List<Itinerary> itinerariesList,
                                 OnOperationCompleteListener onOperationCompleteListener)
    {
        if(location == null || date == null || itinerariesList == null)
            return;

        long msStart = System.currentTimeMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("itinerary");
        Query query = ref.orderByChild("date")
                .equalTo(date);

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                itinerariesList.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Itinerary itinerary = new Itinerary();
                    itinerary.setId(ds.getKey());

                    try
                    {
                        getEntityFrom(itinerary, ds);
                    }
                    catch (Exception e) { continue; }

                    itinerary.getFieldsFromId();

                    if(itinerary.getLocation().equals(location) &&
                       ! itinerary.getCicerone().getId().equals(AuthenticationManager.get().getUserLogged().getId()))
                        itinerariesList.add(itinerary);
                }

                Log.i(TAG, "Query finished in " + (System.currentTimeMillis() - msStart)
                + "ms with " + itinerariesList.size() + " results.");

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.e(TAG, "Error: " + databaseError.toString());
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            }
        });
    }

    /**
     *      Check and retrieve a notification from a given user.
     *
     *      @param notification
     *      @param onOperationCompleteListener
     */
    public void checkNotificationFor(UserNotification notification, Context context, OnOperationCompleteListener onOperationCompleteListener)
    {
        if(notification == null || onOperationCompleteListener == null)
            return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("usernotification")
                .child(notification.getIdUser());
        Query query = ref.orderByKey().limitToFirst(1);

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //  Check if app is in foreground
                if(AuthenticationManager.get().getUserLogged() != null)
                    query.removeEventListener(this);

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    notification.setTitle(ds.child("title").getValue(String.class));
                    notification.setContent(ds.child("content").getValue(String.class));
                }

                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.e(TAG, "Error: " + databaseError.toString());
                if(onOperationCompleteListener != null)
                    onOperationCompleteListener.onError();
            }
        };

        query.removeEventListener(valueEventListener);
        query.addValueEventListener(valueEventListener);
    }

    /**
     *      Fills a passed entity with fields obtained from DataSnapshot passed.
     *
     *      @param entity       Entity to fill.
     *      @param dataSnapshot DataSnapshot which provides fields of entity
     */
    private void getEntityFrom(StorableEntity entity, DataSnapshot dataSnapshot)
    {
        Map<String, FieldValue> fields = getFields(entity);

        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            try
            {
                String fieldName = ds.getKey();

                FieldValue fieldValue = fields.get(fieldName);

                if(fieldValue == null || fieldName == null)
                    return;

                if(fieldValue.isAList())
                {
                    List<FieldValue> fieldValues = new Vector<>();

                    for(DataSnapshot dsChild1 : ds.getChildren())
                    {
                        Map<String, String> map = new HashMap<>();

                        for(DataSnapshot dsChild2 : dsChild1.getChildren())
                        {
                            String subFieldName = dsChild2.getKey();

                            if(subFieldName != null)
                                map.put(subFieldName, dsChild2.getValue(String.class));
                        }

                        fieldValues.add(new FieldValue(new StorableAsField()
                        {
                            @Override
                            public String getFieldId() { return dsChild1.getKey(); }

                            @Override
                            public Map<String, String> getSubFields()
                            {
                                return map;
                            }

                            @Override
                            public void restoreId(String id) { }

                            @Override
                            public void restoreSubFields(Map<String, String> subFields) { }
                        }));
                    }

                    fields.put(fieldName, new FieldValue(fieldValues));
                }
                if(fieldValue.isSubfield())
                {
                    StorableAsField saf = fieldValue.getSubField();

                    if(saf == null)
                        continue;

                    Map<String, String> map = saf.getSubFields();

                    String id = ds.getChildren().iterator().next().getKey();

                    if(id == null)
                        continue;

                    saf.restoreId(id);

                    for(DataSnapshot dsChild : ds.getChildren().iterator().next().getChildren())
                    {
                        String subFieldName = dsChild.getKey();

                        if(subFieldName != null)
                            map.put(subFieldName, dsChild.getValue(String.class));
                    }

                    saf.restoreSubFields(map);

                    fields.put(fieldName, new FieldValue(saf));
                }
                else
                {
                    fields.put(fieldName, new FieldValue( getValue(ds, fieldValue)) );
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "getEntity Error: " + e.toString());
            }
        }

        setFields(entity, fields);
    }


    /**
     *      Get value as string of a FieldValue from a DataSnapshot
     *
     *      @param ds                   DataSnapshot
     *      @param storedFieldValue     FieldValue
     *      @return     Value, as string, of a FieldValue from a DataSnapshot.
     */
    private String getValue(DataSnapshot ds, FieldValue storedFieldValue)
    {
        String result = "";

        if(storedFieldValue.isSubfield())
        {
            StorableAsField saf = storedFieldValue.getSubField();

            if(saf == null)
                return "";

            for(String key : saf.getSubFields().keySet())
            {
                String fieldValue = decrypt(ds.child(key).getValue(String.class));

                if(fieldValue != null)
                    return fieldValue;
            }
        }
        else
        {
            String fieldValue = decrypt(ds.getValue(String.class));

            if(fieldValue != null)
                return fieldValue;
        }

        return result;
    }

    /**
     *      Calls storeField() without callback methods
     */
    public void storeField(StorableEntity entity, final String fieldName)
    {
        storeField(entity, fieldName, null);
    }

    /**
     *      Stores a field on apposite location on database.
     *
     *      @param entity       Object with field to store.
     *      @param fieldName    Name of the field to store.
     *      @param onOperationCompleteListener Callback methods handler
     */
    public void storeField(StorableEntity entity, final String fieldName,  final OnOperationCompleteListener onOperationCompleteListener)
    {
        //  Check parameters
        if(entity == null || fieldName == null)
        {
            if(onOperationCompleteListener != null)
                onOperationCompleteListener.onError();
            return;
        }

        final long cTime = System.currentTimeMillis();

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        FieldValue storedFieldValue = getFields(entity).get(fieldName);

        if(storedFieldValue.isSubfield())
        {
            // Store sub-field
        }
        else
        {
            String fieldValue = storedFieldValue.getValue();

            // NOT CONFIRMED - Get it in some other way
            String id = getIdFrom(entity);

            final String encryptedValue = encrypt(fieldValue);

            //  Storing
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(entityName)
                    .child(id)
                    .child(fieldName);
            ref.setValue( encryptedValue )
                    .addOnCompleteListener(task ->
                    {
                        long delay = System.currentTimeMillis() - cTime;
                        int bytes = encryptedValue.getBytes().length;
                        Log.i(TAG, "Operation storeField(" + fieldName +
                                "): uploaded " + bytes + " bytes in " + delay + " ms.");
                        if(onOperationCompleteListener != null)
                            onOperationCompleteListener.onSuccess();
                    })
                    .addOnFailureListener(e ->
                    {
                        if(onOperationCompleteListener != null)
                            onOperationCompleteListener.onError();
                    });
        }
    }

    /**
     *      Get a field from database named as fieldName and set it on object passed with parameters.
     *
     *      @param entity                   Object to be set.
     *      @param fieldName                Name of field.
     *      @param onOperationCompleteListener    Provides callback methods after reading database.
     */
    public void getField(final StorableEntity entity, final String fieldName, final OnOperationCompleteListener onOperationCompleteListener)
    {
        //  Check parameters
        if(entity == null || fieldName == null)
            return;

        final long cTime = System.currentTimeMillis();

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        final String id = getIdFrom(entity);

        FieldValue fieldValue = getFields(entity).get(fieldName);

        if(fieldValue == null)
            return;

        if(fieldValue.isSubfield())
        {
            // TODO: Restore sub-field
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(entityName)
                    .child(id);
            ref.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    long delay = System.currentTimeMillis() - cTime;
                    int bytes = dataSnapshot.toString().getBytes().length;
                    Log.i(TAG, "Operation getEntity(" + fieldName +
                            "): retrieved " + bytes + " bytes in " + delay + " ms.");

                    String value = decrypt(dataSnapshot.child(fieldName).getValue(String.class));

                    try
                    {
                        Field field = entity.getClass().getDeclaredField(fieldName);
                        field.set(entity, value);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                        if(onOperationCompleteListener != null)
                            onOperationCompleteListener.onError();
                    }

                    if(onOperationCompleteListener != null)
                        onOperationCompleteListener.onSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    if(onOperationCompleteListener != null)
                        onOperationCompleteListener.onError();
                    Log.e(TAG, "Error: " + databaseError.toString());
                }
            });
        }
    }

    /**
     *      Calls removeEntity() without callback methods
     */
    public void removeEntity(final StorableEntity entity) { removeEntity(entity, null); }

    /**
     *      Removes the given entity from FireBase database based on its id.
     *
     *      @param entity   Entity with id of node to remove from database.
     *      @param onOperationCompleteListener Callback methods handler
     */
    public void removeEntity(final StorableEntity entity, final OnOperationCompleteListener onOperationCompleteListener)
    {
        //  Check parameters
        if(entity == null)
        {
            if(onOperationCompleteListener != null)
                onOperationCompleteListener.onError();
            return;
        }

        final long cTime = System.currentTimeMillis();

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        String id = getIdFrom(entity);

        //  Removing
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference(entityName)
                .child(id).getRef();
        ref.removeValue()
                .addOnCompleteListener(task ->
                {
                    long delay = System.currentTimeMillis() - cTime;
                    Log.i(TAG, "Operation removeEntity(" + entity.getClass().getSimpleName() +
                            ") terminated in " + delay + " ms.");

                    if(onOperationCompleteListener != null)
                        onOperationCompleteListener.onSuccess();
                })
                .addOnFailureListener(e ->
                {
                    Log.e(TAG, "Error: " + e.toString());
                    if(onOperationCompleteListener != null)
                        onOperationCompleteListener.onError();
                });
    }

    /**
     *      Get id from a entity and convert it into an encrypted legal value for FireBase database.
     *
     *      @param entity   Entity that gives its id.
     *      @return         An encrypted and legal value for FireBase database.
     */
    private String getIdFrom(StorableEntity entity)
    {
        if(entity == null)
            return null;

        // Firebase Database paths must not contain '.', '#', '$', '[', or ']'

        /*
        String id;
        id = encrypt(entity.getId());*/

        return entity.getId();//.replace(".", "~");
    }

    /**
     *      Get decrypted id from the respective encrypted used as index on the database.
     *
     *      @param id   Id to be decrypted.
     *      @return     A decrypted id used as index on the database.
     */
    private String getFromId(String id)
    {
        if(id == null)
            return null;

        return id;//.replace("~", ".");
    }

    /**
     *      Creates a map with:
     *          keys:       declared fields name, as String
     *          values:     runtime value of field in the current instance of object
     *
     *      NOTE: Every field not implementing 'StorableAsField' interface
     *            value is converted into string calling its toString() method.
     *
     *      @return Tha map created containing fields and their values.
     */
    private Map<String, FieldValue> getFields(StorableEntity entity)
    {
        Map<String, FieldValue> result = new HashMap<>();

        try
        {
            for(Field field : entity.getClass().getDeclaredFields())
            {
                Object value = field.get(entity);

                boolean ignore = false;
                for(String name : entity.getIgnoredFields())
                    if(name.equalsIgnoreCase(field.getName()))
                    {
                        ignore = true;
                        break;
                    }

                if(ignore)
                    continue;

                if( value instanceof StorableAsField )
                {
                    StorableAsField saf = (StorableAsField) value;
                    result.put(field.getName(), new FieldValue(saf));
                    continue;
                }

                if(field.getType().isAssignableFrom(List.class) )
                {
                    //Object probe = field.get(entity);

                    List<?> values = (List<?>) field.get(entity);

                    if( values != null && ! values.isEmpty())
                    {
                        if(values.get(0) instanceof StorableAsField)
                        {
                            List<FieldValue> fieldValues = new Vector<>();

                            for(Object saf : values)
                                fieldValues.add( new FieldValue((StorableAsField) saf) );

                            result.put(field.getName(), new FieldValue(fieldValues) );
                        }
                        else
                        {
                            List<FieldValue> fieldValues = new Vector<>();

                            for(Object obj : values)
                                fieldValues.add( new FieldValue( obj.toString() ) );

                            result.put(field.getName(), new FieldValue(fieldValues) );
                        }
                    }
                    else
                    {
                        //  used to let see this field to getEntity()
                        result.put(field.getName(), new FieldValue(new Vector<>()) );
                    }

                    continue;
                }

                if( ! field.isSynthetic() && ((entity instanceof Serializable) ||
                        ( ! field.getName().equalsIgnoreCase("serialversionUID")) ))
                {
                    String val = value != null ? value.toString() : "";
                    result.put(field.getName(), new FieldValue(val) );
                }
            }
        }
        catch (IllegalAccessException e)
        {
            Log.e("Error", e.toString());
        }

        return result;
    }

    /**
     *      Set declared fields of object using a map passed as parameter.
     *
     *      NOTE: If your class have fields of a not-primitive or String type, you must override
     *      setComplexTypedField() method with an implementation which provides that conversion.
     *
     *      @param fields  Map used to set fields. it is formatted as:
     *          keys:       declared fields name, as String
     *          values:     runtime value of field in the current instance of object
     */
    private void setFields(StorableEntity entity, Map<String, FieldValue> fields)
    {
        if(fields == null)
            return;

        for(String fieldName : fields.keySet())
        {
            FieldValue fieldValue = fields.get(fieldName);

            if(fieldValue == null)
                continue;

            try
            {
                Field field = entity.getClass().getDeclaredField(fieldName);

                if(fieldValue.isAList())
                {

                    if(fieldValue.getValuesList() != null)
                    {
                        for(FieldValue fv : fieldValue.getValuesList())
                        {
                            if(fv.isSubfield())
                            {
                                StorableAsField saf = fv.getSubField();

                                if(saf == null)
                                    continue;

                                Object newObject = ((ListOfStorables) entity).addNewInstanceInto(fieldName);

                                if(newObject instanceof StorableAsField)
                                {
                                    ((StorableAsField) newObject).restoreSubFields(saf.getSubFields());
                                    ((StorableAsField) newObject).restoreId(
                                            saf.getFieldId()
                                                    .replace("~", ".")
                                                    .replace("-", "/"));
                                }
                            }
                            else
                            {
                                ((List<String>) field.get(entity)).add(fv.getValue());
                            }
                        }
                    }

                    continue;
                }

                if(fieldValue.isSubfield())
                {

                    StorableAsField saf = fieldValue.getSubField();

                    if(saf == null)
                        continue;

                    ((StorableAsField) field.get(entity)).restoreSubFields( saf.getSubFields() );

                    continue;
                }

                /*
                      Convert string into correct type
                 */

                if(field.getType().equals(int.class))
                {
                    String value = fieldValue.getValue();
                    if(value != null)
                        field.set(entity, Integer.parseInt(value));
                }

                if(field.getType().equals(float.class))
                {
                    String value = fieldValue.getValue();
                    if(value != null)
                        field.set(entity, Float.parseFloat(value));
                }

                if(field.getType().equals(double.class))
                {
                    String value = fieldValue.getValue();
                    if(value != null)
                        field.set(entity, Double.parseDouble(value));
                }

                if(field.getType().equals(boolean.class))
                {
                    String value = fieldValue.getValue();
                    if(value != null)
                        field.set(entity, Boolean.parseBoolean(value));
                }

                if(field.getType().equals(String.class))
                {
                    String value = fieldValue.getValue();
                    if(value != null)
                        field.set(entity, value);
                }

                entity.setComplexTypedField(field, fieldValue.getValue());
            }
            catch (Exception e)
            {
                Log.e("Error", e.toString());
            }
        }
    }

    /**
     *      Represents a field value, which can be of two different types:
     *          -   A String value
     *          -   A sub field, with its own fields.
     */
    public class FieldValue
    {
        /**   Simple string value  */
        private String value;
        /**   SubField value  */
        private StorableAsField subField;
        /**   List of subfields  */
        private List<FieldValue> valuesList;
        /**   Used to determinate type of field value  */
        private boolean isSubfield = false;
        /**   Used to determinate if this field value is a list  */
        private boolean isAList = false;

        public FieldValue(@NonNull String value)
        {
            this.value = value;
        }

        public FieldValue(@NonNull StorableAsField subField)
        {
            isSubfield = true;
            this.subField = subField;
        }

        public FieldValue(@NonNull List<FieldValue> valuesList)
        {
            this.valuesList = valuesList;
            isAList = true;
            if( ! valuesList.isEmpty())
                isSubfield = valuesList.get(0).isSubfield;
        }

        public String getValue() {
            return value;
        }

        public StorableAsField getSubField() {
            return subField;
        }

        public boolean isSubfield() {
            return isSubfield;
        }

        public List<FieldValue> getValuesList() {
            return valuesList;
        }

        public boolean isAList() {
            return isAList;
        }

        @Override
        public String toString()
        {
            return "FieldValue{" +
                    "value='" + value + '\'' +
                    ", subField=" + subField +
                    ", valuesList=" + (valuesList != null? Arrays.toString(valuesList.toArray()) : "") +
                    ", isSubfield=" + isSubfield +
                    ", isAList=" + isAList +
                    '}';
        }
    }

    /**  Interface which provides callback methods for a data-request to FireBase database.   */
    public interface OnOperationCompleteListener
    {
        /**
         *      Called when data is correctly received.
         */
        void onSuccess();

        /**
         *      Called when an error has occurred into the request
         */
        void onError();
    }
}