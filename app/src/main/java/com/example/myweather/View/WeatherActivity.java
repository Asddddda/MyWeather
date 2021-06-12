package com.example.myweather.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myweather.R;

import com.example.myweather.util.Load;
import com.example.myweather.util.ThreadManager;
import com.example.myweather.service.UpdateService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;


import static com.example.myweather.util.Load.makeStatusBarTransparent;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    private ThreadManager manager = new ThreadManager();

    private ExecutorService executorService = manager.getExecutorService();

    private FrameLayout weatherLayoutAll;

    private ScrollView weatherLayout;

    private TextView titleLoc;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout hourlyLayout;

    private LinearLayout forecastLayout;//预报可调整长度

    private TextView aqiText;

    private  TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    public static final int UPDATE_TEXT = 1;

    private static final int ALBUM_REQUEST_CODE = 2;

    private SwipeRefreshLayout swipeRefreshLayout;

    private DrawerLayout drawerLayout;

    private ImageButton menuButton;

    private TextView nowText;

    private String adress;

    private String path;

    private Boolean isLoading = false;

    private Boolean isFirst = true;

    @SuppressLint("HandlerLeak")
    //借助Handler加载数据
    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
            if(msg.what == UPDATE_TEXT) {
                swipeRefreshLayout.setRefreshing(false);
                showWeatherInfo();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initListener();
        final SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
        adress = sp.getString("adress","");
        //无数据则提示加载
        if(adress.equals(""))
            Toast.makeText(WeatherActivity.this, "请刷新", Toast.LENGTH_SHORT).show();
        //加载框架
        showWeatherInfo();
        //加载自定义背景
        path = sp.getString("path","");
        if(!path.equals("")){
            try{
                FileInputStream fis = new FileInputStream(path);
                Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                weatherLayoutAll.setBackground(new BitmapDrawable(getResources(),bitmap));
            }catch (FileNotFoundException e){
                e.printStackTrace();
                weatherLayoutAll.setBackground(getDrawable(R.drawable.weather_dark));
            }
        }else { weatherLayoutAll.setBackground(getDrawable(R.drawable.weather_dark)); }
        //搜索地址后的重新加载
        boolean reload =getIntent().getBooleanExtra("reload",false);
        if(reload){
            adress = sp.getString("adress","");
        loadMain(adress);}
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(sp.getString("adress","").equals("")){
                    SharedPreferences.Editor editor = getSharedPreferences("Weather", MODE_PRIVATE).edit();
                    editor.putString("adress", "auto_ip");
                    editor.apply();
                }
                adress = sp.getString("adress","");
                loadMain(adress);
            }
        });
    }

    public void initView() {
        makeStatusBarTransparent(WeatherActivity.this);
        //初始化各控件
        weatherLayoutAll = findViewById(R.id.weather_layout_all);
        weatherLayout = findViewById(R.id.weather_layout);
        titleLoc = findViewById(R.id.title_loc);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        hourlyLayout = findViewById(R.id.hourly_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.menu_button);
        swipeRefreshLayout = findViewById(R.id.swipe_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        nowText = findViewById(R.id.now_text);
    }

    public void initListener() {
        menuButton.setOnClickListener(this);
        findViewById(R.id.change_view).setOnClickListener(this);
        findViewById(R.id.bac_update_view).setOnClickListener(this);
        findViewById(R.id.locate_view).setOnClickListener(this);
        findViewById(R.id.search_view).setOnClickListener(this);
        findViewById(R.id.now_view).setOnClickListener(this);
        findViewById(R.id.media_demo).setOnClickListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(!isLoading)
            finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
        if (sp.getBoolean("backgroundUpdate",false)){
            Intent startUpdateIntent = new Intent(this, UpdateService.class);
            if(Build.VERSION.SDK_INT>=26) {//仅在26及以上有效
                startForegroundService(startUpdateIntent);
            } else {
                startService(startUpdateIntent);
            }
        } else {
            Intent stopUpdateIntent = new Intent(this, UpdateService.class);
            stopService(stopUpdateIntent);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = getSharedPreferences("Weather", MODE_PRIVATE).edit();
        switch (v.getId()) {
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.now_view:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.locate_view:
                editor.putString("adress", "auto_ip");
                editor.apply();
                drawerLayout.closeDrawer(GravityCompat.START);
                loadMain("auto_ip");
                break;
            case R.id.search_view:
                isLoading = false;
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(WeatherActivity.this, LocationResultActivity.class);
                startActivity(intent);
                break;
            case R.id.bac_update_view:
                SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
                if(!sp.getBoolean("backgroundUpdate",false)) {
                    editor.putBoolean("backgroundUpdate",true);
                    editor.apply();
                    Toast.makeText(this,"开启成功",Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("backgroundUpdate",false);
                    editor.apply();
                    Toast.makeText(this,"关闭成功",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.change_view://自定义背景
                isLoading = true;
                //申请读写权限
                if (Build.VERSION.SDK_INT >= 23) {
                    int REQUEST_CODE_PERMISSION_STORAGE = 100;
                    String[] permissions = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    for (String str : permissions) {
                        if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                            return;
                        }
                    }
                }
                if(isFirst) {
                    Toast.makeText(this,"请自行裁剪，图片勿过大哦",Toast.LENGTH_SHORT).show();
                    isFirst = false;
                }
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
                break;
            case R.id.media_demo:
                Intent videoIntent = new Intent(WeatherActivity.this,VideoPlayActivity.class);
                startActivity(videoIntent);
                break;
        }
    }

    /**
     * 自定义背景
     * @param requestCode
     * @param resultCode
     * @param intent 包含图片数据的intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        if(requestCode == ALBUM_REQUEST_CODE)   //调用相册后返回
            if (resultCode == RESULT_OK) {
                //uri转path
                Uri uri = intent.getData();
                String[] arr = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, arr, null, null, null);
                int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String img_path = cursor.getString(img_index);
                File file = new File(img_path);
                path = file.getAbsolutePath();
                SharedPreferences.Editor editor = getSharedPreferences("Weather",MODE_PRIVATE).edit();
                editor.putString("path",path);
                editor.apply();
                try{
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    weatherLayoutAll.setBackground(new BitmapDrawable(getResources(),bitmap));
                    Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    /**
     *设置主页面数据
     */
    public void showWeatherInfo(){
        SharedPreferences sp = getSharedPreferences("Weather",MODE_PRIVATE);
        String cityName = sp.getString("titleLoc","");
        String updateTime = sp.getString("update_time","");
        String degree = sp.getString("degree","")+"℃";
        String weatherInfo = sp.getString("weather_info","");
        titleLoc.setText(cityName);
        nowText.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        //forecast部分的layout加载准备
        for(int i=0;i<7;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            ImageView weather_imagine_d = view.findViewById(R.id.forecast_imagine_d);
            ImageView weather_imagine_n = view.findViewById(R.id.forecast_imagine_n);
            //加载预报天气的图片
            Load.loadForecastImagine(sp.getString("infoCodeD"+i,""),weather_imagine_d);
            Load.loadForecastImagine(sp.getString("infoCodeN"+i,""),weather_imagine_n);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView temText = view.findViewById(R.id.tem_text);
            TextView rainPerText = view.findViewById(R.id.rain_per_text);
            dateText.setText(sp.getString("date"+i,""));
            rainPerText.setText(sp.getString("rainPer"+i,"")+"%");
            temText.setText(sp.getString("tem"+i,"")+"℃");
            forecastLayout.addView(view);
        }

        hourlyLayout.removeAllViews();
        //hourly部分的layout加载
        for(int i=0;i<8;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_item,hourlyLayout,false);
            ImageView weatherImagine = view.findViewById(R.id.hourly_imagine);
            Load.loadForecastImagine(sp.getString("infoCode"+i,""),weatherImagine);
            TextView timeText = view.findViewById(R.id.time_text);
            TextView hourlyTemText = view.findViewById(R.id.hourly_tem_text);
            TextView rainPerHourlyText = view.findViewById(R.id.wind_text);
            TextView infoText = view.findViewById(R.id.hourly_weather_text);
            timeText.setText(sp.getString("time"+i,""));
            infoText.setText(sp.getString("info"+i,""));
            rainPerHourlyText.setText(sp.getString("wind"+i,""));
            hourlyTemText.setText(sp.getString("temHourly"+i,"")+"℃");
            hourlyLayout.addView(view);
        }
        //空气指数
        aqiText.setText(sp.getString("aqi",""));
        pm25Text.setText(sp.getString("pm25",""));
        //建议
        String comfort = "舒适度："+sp.getString("comfort","");
        String carWash = "衣着："+sp.getString("drsg","");
        String sport = "户外运动："+sp.getString("sport","");
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载主界面
     * @param url 地址
     */
    public void loadMain(String url){
        Load load = new Load(this,executorService);
        load.loadOnline(url);
        //刷新后线上加载
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                    Message message = Message.obtain();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
        showWeatherInfo();
    }

}
