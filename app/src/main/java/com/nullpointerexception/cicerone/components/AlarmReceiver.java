package com.nullpointerexception.cicerone.components;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 *      AlarmReceiver
 *
 *      Is used to start the background service {@link NotificationsListener} when app get closed.
 *
 *      @author Luca
 */
public class AlarmReceiver extends BroadcastReceiver
{
    /**   Custom intent string used to identify broadcast intent sent from pending intent created by this class  */
    public static final String CUSTOM_INTENT = "com.nullpointerexception.cicerone.ALARM";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("NotificationsListener", "Broadcast message received.");
        NotificationsListener.enqueueWork(context, intent);
    }

    public static void cancelAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager == null)
        {
            Log.i("NotificationsListener", "cancelAlarm() alarmManager is null.");
            return;
        }

        /* cancel any pending alarm */
        alarmManager.cancel(getPendingIntent(context));

        Log.i("NotificationsListener", "Current intent canceled.");
    }

    public static void setAlarm(Context context, boolean force)
    {
        cancelAlarm(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager == null)
        {
            Log.i("NotificationsListener", "setAlarm() alarmManager is null.");
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 10000, AlarmReceiver.getPendingIntent(context));
        else
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 10000, AlarmReceiver.getPendingIntent(context));

        Log.i("NotificationsListener", "Next alarm set.");
    }

    public static PendingIntent getPendingIntent(Context context)
    {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(context,
                0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
