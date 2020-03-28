package com.example.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

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
//        Toast.makeText(this,"Updating...",Toast.LENGTH_SHORT).show();
    }
}
