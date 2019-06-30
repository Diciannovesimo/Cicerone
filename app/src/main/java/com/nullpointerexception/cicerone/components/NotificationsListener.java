package com.nullpointerexception.cicerone.components;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.activities.SplashScreen;

/**
 *      An {@link IntentService} subclass for handling asynchronous task requests in
 *      a service on a separate handler thread.
 *      It is used to asynchronously check notifications for this user.
 *
 *      @author Luca
 */
public class NotificationsListener extends JobIntentService
{
    /**   Id of job done from this class  */
    private static final int JOB_ID = 3492;
    /**   Counter used to assign an unique id to notifications */
    private static int counterId = 0;
    /**   Context used by this service */
    private static Context context;

    /**
     *      Starts service.
     *
     *      @param ctx  context from where si called.
     *      @param intent   intent from where is called.
     */
    public static void enqueueWork(Context ctx, Intent intent)
    {
        context = ctx;
        enqueueWork(context, NotificationsListener.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        Log.i("NotificationsListener", "Service is running...");

        SharedPreferences sharedPreferences = getSharedPreferences("notificationsListener", MODE_PRIVATE);
        if( ! sharedPreferences.getBoolean("notificationsEnabled", true))
        {
            stopSelf();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(alarmManager == null)
        {
            Log.i("LocalAutoBackup", "OnHandleWork service alarmManager is null.");
            return;
        }

        checkForNotifications();

        AlarmReceiver.setAlarm(context, false);
        stopSelf();
    }

    @Override
    public void onDestroy()
    {
        Log.i("NotificationsListener", "Service stopped.");
        super.onDestroy();
    }

    /**
     *      Checks on database if there are pending notifications to show.
     */
    private void checkForNotifications()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("notificationsListener", MODE_PRIVATE);
        String id = sharedPreferences.getString("idUser", null);

        if(id == null && AuthenticationManager.get().getUserLogged() == null)
            return;

        //  Used to check if app is running
        if(AuthenticationManager.get().getUserLogged() != null)
        {
            id = AuthenticationManager.get().getUserLogged().getId();
            sharedPreferences.edit().putString("idUser", id).apply();
        }

        UserNotification notification = new UserNotification(id);

        BackEndInterface.get().checkNotificationsFor(notification, this, new BackEndInterface.OnOperationCompleteListener()
        {
            @Override
            public void onSuccess()
            {
                Log.i("NotificationsListener", "Check.");

                SharedPreferences sharedPreferences = getSharedPreferences("notificationsListener", MODE_PRIVATE);
                String id = sharedPreferences.getString("idUser", null);

                if(id != null && id.equals(notification.getIdUser()))
                    if( notification.getTitle() != null && ! notification.getTitle().isEmpty())
                    {
                        displayNotification(notification);
                        BackEndInterface.get().removeEntity(notification);
                    }
            }

            @Override
            public void onError() { }
        });
    }

    /**
     *      Show the specified notification.
     *
     *      @param notification notification to show.
     */
    private void displayNotification(UserNotification notification)
    {
        createNotificationChannel();

        Intent intent = new Intent(this, SplashScreen.class);
        if(notification.getIdItinerary() != null && ! notification.getIdItinerary().trim().isEmpty())
        {
            Log.i("Notifiche", "displayNotification() -> putExtra");
            intent.putExtra("notification_info", notification.getIdItinerary());
        }
        else
            Log.i("Notifiche", "displayNotification() -> Error: no id itinerary");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, counterId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf("app_notifications".hashCode()))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(counterId++, builder.build());
    }

    /**
     *      Create notification channel to support notifications system on versions higher than Android Nougat
     */
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "app_notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(name.hashCode()), name, importance);
            channel.setDescription("Notifications of itineraries.");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
