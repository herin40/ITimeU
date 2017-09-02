package com.itti7.itimeu;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerService extends Service {
    public static String strReceiver = "com.TimerService.receiver";
    private String mLeftTime;
    private int runTime;
    private boolean timerSwitch = false;
    private CountDownTimer timer;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNM;
    private final int NOTIFYID=001;

    public TimerService() {

    }

    public String getTime() {
        return timerSwitch ? mLeftTime : "00:00";
    }

    public boolean getRun() {
        return timerSwitch;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("Timer", "------------------------------------------------------->Timer run()");
            timer = new CountDownTimer(runTime * 1000 * 60, 1000) { // minute
                public void onTick(long millisUntilFinished) {
                    if (timerSwitch) {
                        //Log.i("Timer", "------------------------------------------------------>Timer OnTick"); //checked
                        String min = String.format("%02d", (millisUntilFinished) / (1000 * 60));
                        String sec = String.format("%02d", (millisUntilFinished / 1000) % 60);
                        mLeftTime = min + ":" + sec;
                        if (runTime >= 60) {
                            String hour = String.format("%02d", (millisUntilFinished / (1000 * 60 * 60)));
                            mLeftTime = hour + ":" + mLeftTime;
                        }
                    }
                    mBuilder.setContentText(mLeftTime);
                    mNM.notify(NOTIFYID, mBuilder.build());
                }

                public void onFinish() {
                    Log.i("Timer", "------------------------------------------------------->Timer onFinish");
                    if (timerSwitch) {         //send only if it has finished
                        mLeftTime = "00:00";
                        mBuilder.setContentText(mLeftTime);
                        mBuilder.setSubText("FINISHED");
                        mNM.notify(NOTIFYID, mBuilder.build());
                        stopTimer();
                        Log.i("Timer", "------------------------------------------------------->Timer start send Intent");
                        Intent sendIntent = new Intent(strReceiver);  // notice the end of Timer to Fragment
                        sendBroadcast(sendIntent);
                        Log.i("Timer", "------------------------------------------------------->Timer finish send Intent");
                    }
                }
            };
            timer.start();
        }
    };
    public void setTimeName(int time,String name) {
        runTime=time;
        timerSwitch = true;
        handler.post(runnable);
        showNotification(name);
    }
    private void showNotification(String name) {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getService(this, 0,new Intent(this, TimerFragment.class), 0);
        // Set the info for the views that show in the notification panel.
         mBuilder = new NotificationCompat.Builder(TimerService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(name)
                .setContentText(mLeftTime);
         mBuilder.setContentIntent(contentIntent);
        // Send the notification.
        mNM = (NotificationManager)TimerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.notify(NOTIFYID, mBuilder.build());
    }
    public void stopTimer() {
        Log.i("Timer", "------------------------------------------------------->Timer stopTimer");
        timerSwitch = false;
        timer.cancel();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Timer", "------------------------------------------------------->TimeronUnbind");
        stopTimer();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Timer", "------------------------------------------------------->Timer onStartCommand");
        runTime = intent.getIntExtra("RUNTIME", 1);
        Log.i("RUNTIME", "------------------------------------------------------->RUNTIME : "+runTime);
//        timerSwitch = true;
//        handler.post(runnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            Log.i("Timer", "------------------------------------------------------->Timer getService()");
            return TimerService.this;
        }
    }
}
