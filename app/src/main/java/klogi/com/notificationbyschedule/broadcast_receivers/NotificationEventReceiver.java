package klogi.com.notificationbyschedule.broadcast_receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.notifications.NotificationIntentService;

/**
 * Created by Alaeddine amama
 *
 * WakefulBroadcastReceiver used to receive intents fired from the AlarmManager for showing notifications
 * and from the notification itself if it is deleted.
 */
public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
   public static DBHandler  db = null;

    private static   int NOTIFICATIONS_INTERVAL =1;

    public static void setupAlarm(Context context) {
        db=new DBHandler(context);




        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long min = AlarmManager.INTERVAL_FIFTEEN_MINUTES/15;
        int freqPosition= Integer.parseInt(db.getUser().getFreq());
        if((freqPosition>=0)&&(freqPosition<=2)){

            NOTIFICATIONS_INTERVAL=freqPosition+1;
        }else if(freqPosition==3){
            NOTIFICATIONS_INTERVAL=15;
        }else if(freqPosition==4){
            NOTIFICATIONS_INTERVAL=30;
        }
         else if (freqPosition>=5&&freqPosition<=15){
            NOTIFICATIONS_INTERVAL=(freqPosition-2)*60;
        }
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTriggerAt(new Date()),
                NOTIFICATIONS_INTERVAL *min,
                alarmIntent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.cancel(alarmIntent);
    }

    private static long getTriggerAt(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        return calendar.getTimeInMillis();
    }

    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
        } else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");
            serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
        }

        if (serviceIntent != null) {
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, serviceIntent);
        }
    }
}
