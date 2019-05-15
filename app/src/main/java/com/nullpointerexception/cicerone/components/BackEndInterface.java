package com.nullpointerexception.cicerone.components;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import ru.bullyboo.encoder.Encoder;
import ru.bullyboo.encoder.methods.AES;

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
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(ENCRYPTION_KEY)
                .keySize(AES.Key.SIZE_256)
                .encrypt();
    }

    /**
     *      Decrypt a given string with a given key.
     *
     *      @param string   String to decrypt
     *      @return String decrypted.
     */
    private String decrypt(String string)
    {
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(ENCRYPTION_KEY)
                .keySize(AES.Key.SIZE_256)
                .decrypt();
    }

    /*
            Result types
     */

    /**    Operation successfully completed */
    public final static int RESULT_OK = 0;
    /**    Operation terminated for an error with parameters */
    public final static int RESULT_PARAMETERS_NULL = -1;

    /*    Operation terminated for an error while getting id */
    //public final static int RESULT_ID_ERROR = -2;
    /*    Operation terminated for a problem with connection */
    //public static int RESULT_NO_CONNECTION = -3;

    /**
     *      Stores an object on the FireBase database, encrypting its values.
     *
     *      @param entity   Object to store on the database.
     *      @return The result of operation.
     */
    public int storeEntity(StorableEntity entity)
    {
        //  Check parameter
        if(entity == null)
            return RESULT_PARAMETERS_NULL;

        String id = getIdFrom(entity);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String entityName = entity.getClass().getSimpleName().toLowerCase();

        Map<String, String> fields = entity.getFields();

        for(String fieldName : fields.keySet() )
        {
            String fieldValue = encrypt(fields.get(fieldName));

            DatabaseReference ref = database.getReference(entityName)
                    .child(id)
                    .child(fieldName);
            ref.setValue( fieldValue );
        }

        Log.i("TEST", "Object stored.");
        return RESULT_OK;
    }

    /**
     *      Get data from FireBase database fro the given object, and set it with values obtained.
     *
     *      @param entity                   Entity where will be written data.
     *      @param onDataReceiveListener    Callback methods implementations.
     *      @return The result of operation.
     */
    public int getEntity(final StorableEntity entity, final OnDataReceiveListener onDataReceiveListener)
    {
        //  Check parameter
        if(entity == null)
            return RESULT_PARAMETERS_NULL;

        final String id = getIdFrom(entity);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(entity.getClass().getSimpleName().toLowerCase())
                .child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.i("Test", "dataReceived: " + dataSnapshot.toString());

                Map<String, String> fields = entity.getFields();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    try
                    {
                        String fieldName = ds.getKey();
                        String fieldValue = decrypt(ds.getValue(String.class));

                        if(fieldName != null)
                            fields.put(fieldName, fieldValue);
                    }
                    catch (Exception e)
                    {
                        Log.i("TEST", "getEntity Error: " + e.toString());
                    }
                }

                entity.setFields(fields);

                if(onDataReceiveListener != null)
                    onDataReceiveListener.onDataReceived(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.i("TEST", "Error: " + databaseError.toString());
                if(onDataReceiveListener != null)
                    onDataReceiveListener.onError();
            }
        });

        return RESULT_OK;
    }

    /**
     *      Stores a field on apposite location on database.
     *
     *      @param entity       Object with field to store.
     *      @param fieldName    Name of the field to store.
     */
    public void storeField(StorableEntity entity, String fieldName)
    {
        //  Check parameters
        if(entity == null || fieldName == null)
            return;

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        String fieldValue = entity.getFields().get(fieldName);

        Log.i("TEST", fieldName);

        // NOT CONFIRMED - Get it in some other way
        String id = getIdFrom(entity);

        //  Storing
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(entityName)
                .child(id)
                .child(fieldName);
        ref.setValue( encrypt(fieldValue) );

        Log.i("TEST", "Stored.");
    }

    /**
     *      Get a field from database named as fieldName and set it on object passed with parameters.
     *
     *      @param entity                   Object to be set.
     *      @param fieldName                Name of field.
     *      @param onDataReceiveListener    Provides callback methods after reading database.
     */
    public void getField(final StorableEntity entity, final String fieldName, final OnDataReceiveListener onDataReceiveListener)
    {
        //  Check parameters
        if(entity == null || fieldName == null || onDataReceiveListener == null)
            return;

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        final String id = getIdFrom(entity);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(entityName)
                .child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String value = decrypt(dataSnapshot.child(fieldName).getValue(String.class));

                Log.i("Test", value);
                onDataReceiveListener.onDataReceived(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                onDataReceiveListener.onError();
                Log.i("TEST", "Error: " + databaseError.toString());
            }
        });

    }

    /**
     *      Removes the given entity from FireBase database based on its id.
     *
     *      @param entity   Entity with id of node to remove from database.
     */
    public void removeEntity(StorableEntity entity)
    {
        //  Check parameters
        if(entity == null)
            return;

        String entityName = entity.getClass().getSimpleName().toLowerCase();

        String id = getIdFrom(entity);

        //  Removing
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(entityName)
                .child(id).getRef();
        ref.removeValue();

        Log.i("TEST", "Node removed: " + ref.getKey());
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

        return entity.getId().replace(".", "~");
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

        return id.replace("~", ".");
    }

    /**  Interface which provides callback methods for a data-request to FireBase database.   */
    public interface OnDataReceiveListener
    {
        /**
         * Called when data is correctly received.
         *
         * @param data data received, as String.
         */
        void onDataReceived(String data);

        /**
         * Called when an error has occurred into the request
         */
        void onError();
    }
}
