package com.example.myweather.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.example.myweather.R;
import com.example.myweather.activity.WeatherActivity;
import com.example.myweather.util.Load;
import com.example.myweather.util.ThreadManager;

public class UpdateService extends Service {
    private ThreadManager manager = new ThreadManager();

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        update();
        notification();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int minute = 1000 * 60 * 60 * 6;
        long triggerAtTime = SystemClock.elapsedRealtime() + minute;
        Intent currentIntent = new Intent(this,UpdateService.class);
        PendingIntent updateIntent = PendingIntent.getService(this,0,currentIntent,0);
        try {
            alarmManager.cancel(updateIntent);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,updateIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    void update(){
        SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
        Load load = new Load(this,manager.getExecutorService());
        load.loadOnline(sp.getString("adress","auto_ip"));
    }

    void notification(){
        SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel("1","defaultChannel",NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel1);
        }
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.gg))
                .setContentTitle(sp.getString("temHourly0","")+"℃  "+sp.getString("info0",""))
                .setContentText("别摸了,记得打卡")
                .setSmallIcon(R.mipmap.quin)
                .setContentIntent(pi);
        manager.notify(1,builder.build());

    }
}
