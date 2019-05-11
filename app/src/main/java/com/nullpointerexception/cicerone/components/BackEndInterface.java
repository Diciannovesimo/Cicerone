package com.nullpointerexception.cicerone.components;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

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
    /*
            Singleton setting
     */
    private static final BackEndInterface ourInstance = new BackEndInterface();
    static BackEndInterface get() { return ourInstance; }
    private BackEndInterface() { }

    //  TODO: Change encryption method

    private String encrypt(String key, String string)
    {
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(key)
                .keySize(AES.Key.SIZE_128)
                .encrypt();
    }

    private String decrypt(String key, String string)
    {
        return Encoder.BuilderAES()
                .message(string)
                .method(AES.Method.AES_CBC_ISO10126PADDING)
                .key(key)
                .keySize(AES.Key.SIZE_128)
                .decrypt();
    }

    public void storeField(Entities entity, EntityFields field, String fieldValue)
    {
        //  Check parameters
        if(entity == null || field == null || fieldValue == null)
            return;

        // NOT CONFIRMED - Get it in some other way
        String id = FirebaseInstanceId.getInstance().getToken();
        if(id == null)
            return;

        String fieldName = field.name().replace(entity.name() + "_", "");

        //  Storing
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(id)
                .child(entity.name()).child(fieldName);
        ref.setValue( encrypt(id, fieldValue) );

        Log.i("TEST", "Stored.");
    }

    public void getField(Entities entity, final EntityFields field, final OnDataReceiveListener onDataReceiveListener)
    {
        //  Check parameters
        if(entity == null || field == null || onDataReceiveListener == null)
            return;

        final String fieldName = field.name().replace(entity.name() + "_", "");

        // NOT CONFIRMED - Get it in some other way
        final String id = FirebaseInstanceId.getInstance().getToken();
        if(id == null)
            return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(id)
                .child(entity.name());
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.i("Test", dataSnapshot.child(fieldName).getValue(String.class));
                onDataReceiveListener.onDataReceived(
                        decrypt(id, dataSnapshot.child(fieldName).getValue(String.class)) );
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                onDataReceiveListener.onError();
                Log.i("TEST", "Error: " + databaseError.toString());
            }
        });

    }

    public interface OnDataReceiveListener { void onDataReceived(String data); void onError(); }

    enum Entities
    {
        user
    }

    enum EntityFields
    {
        user_name,
        user_surname,
        user_dateBirth,
        user_email,
        user_displayName,
        user_profileImageUrl,
        user_accessType
    }
}
