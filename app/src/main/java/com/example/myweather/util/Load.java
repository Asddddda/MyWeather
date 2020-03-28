package com.example.myweather.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.myweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

public class Load {

    private Context mainContext;

    private ExecutorService executorService;//这个是由单例得到的

    public Load(Context mainContext,ExecutorService executorService){
        this.mainContext = mainContext;
        this.executorService = executorService;
    }

    /**
     * 网络加载
     */
    public void loadOnline(String adress){
        JsonObject jsonObject = new JsonObject(mainContext,executorService);
        jsonObject.requestWeather("now",adress);
        jsonObject.requestWeather("lifestyle",adress);
        jsonObject.requestWeather("forecast",adress);
        jsonObject.requestWeather("hourly",adress);
        jsonObject.requestWeather("air",adress);
    }

    /**
     * 判断是否白天，未启用
     */
    public boolean loadIfDay(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());
        int time =Integer.parseInt(simpleDateFormat.format(date));
        if(time>18||time<7){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 透明化状态栏
     * @param activity 哪一个活动下的
     */
    public static void makeStatusBarTransparent(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 加载图片
     * @param data 图片String名
     * @param imageView 图片的imageView位
     */
    public static void loadForecastImagine(String data, ImageView imageView){
        switch(data){
            case "101": imageView.setImageResource(R.drawable.w101); break;
            case "100": imageView.setImageResource(R.drawable.w100); break;
            case "102": imageView.setImageResource(R.drawable.w102); break;
            case "103": imageView.setImageResource(R.drawable.w103); break;
            case "104": imageView.setImageResource(R.drawable.w104); break;
            case "200": imageView.setImageResource(R.drawable.w200); break;
            case "201": imageView.setImageResource(R.drawable.w201); break;
            case "202": imageView.setImageResource(R.drawable.w202); break;
            case "203": imageView.setImageResource(R.drawable.w203); break;
            case "204": imageView.setImageResource(R.drawable.w204); break;
            case "205": imageView.setImageResource(R.drawable.w205); break;
            case "206": imageView.setImageResource(R.drawable.w206); break;
            case "207": imageView.setImageResource(R.drawable.w207); break;
            case "208": imageView.setImageResource(R.drawable.w208); break;
            case "209": imageView.setImageResource(R.drawable.w209); break;
            case "210": imageView.setImageResource(R.drawable.w210); break;
            case "211": imageView.setImageResource(R.drawable.w211); break;
            case "212": imageView.setImageResource(R.drawable.w212); break;
            case "213": imageView.setImageResource(R.drawable.w213); break;
            case "300": imageView.setImageResource(R.drawable.w300); break;
            case "301": imageView.setImageResource(R.drawable.w301); break;
            case "302": imageView.setImageResource(R.drawable.w302); break;
            case "303": imageView.setImageResource(R.drawable.w303); break;
            case "304": imageView.setImageResource(R.drawable.w304); break;
            case "305": imageView.setImageResource(R.drawable.w305); break;
            case "306": imageView.setImageResource(R.drawable.w306); break;
            case "307": imageView.setImageResource(R.drawable.w307); break;
            case "309": imageView.setImageResource(R.drawable.w309); break;
            case "310": imageView.setImageResource(R.drawable.w310); break;
            case "311": imageView.setImageResource(R.drawable.w311); break;
            case "312": imageView.setImageResource(R.drawable.w312); break;
            case "313": imageView.setImageResource(R.drawable.w313); break;
            case "314": imageView.setImageResource(R.drawable.w314); break;
            case "315": imageView.setImageResource(R.drawable.w315); break;
            case "316": imageView.setImageResource(R.drawable.w316); break;
            case "317": imageView.setImageResource(R.drawable.w317); break;
            case "318": imageView.setImageResource(R.drawable.w318); break;
            case "399": imageView.setImageResource(R.drawable.w399); break;
            case "400": imageView.setImageResource(R.drawable.w400); break;
            case "401": imageView.setImageResource(R.drawable.w401); break;
            case "402": imageView.setImageResource(R.drawable.w402); break;
            case "403": imageView.setImageResource(R.drawable.w403); break;
            case "404": imageView.setImageResource(R.drawable.w404); break;
            case "405": imageView.setImageResource(R.drawable.w405); break;
            case "406": imageView.setImageResource(R.drawable.w406); break;
            case "407": imageView.setImageResource(R.drawable.w407); break;
            case "408": imageView.setImageResource(R.drawable.w408); break;
            case "409": imageView.setImageResource(R.drawable.w409); break;
            case "410": imageView.setImageResource(R.drawable.w410); break;
            case "499": imageView.setImageResource(R.drawable.w499); break;
            case "500": imageView.setImageResource(R.drawable.w500); break;
            case "501": imageView.setImageResource(R.drawable.w501); break;
            case "502": imageView.setImageResource(R.drawable.w502); break;
            case "503": imageView.setImageResource(R.drawable.w503); break;
            case "504": imageView.setImageResource(R.drawable.w504); break;
            case "507": imageView.setImageResource(R.drawable.w507); break;
            case "508": imageView.setImageResource(R.drawable.w508); break;
            case "509": imageView.setImageResource(R.drawable.w509); break;
            case "510": imageView.setImageResource(R.drawable.w510); break;
            case "511": imageView.setImageResource(R.drawable.w511); break;
            case "512": imageView.setImageResource(R.drawable.w512); break;
            case "513": imageView.setImageResource(R.drawable.w513); break;
            case "514": imageView.setImageResource(R.drawable.w514); break;
            case "515": imageView.setImageResource(R.drawable.w515); break;
            case "900": imageView.setImageResource(R.drawable.w900); break;
            case "901": imageView.setImageResource(R.drawable.w901); break;
            case "999": imageView.setImageResource(R.drawable.w999); break;
            case "100n": imageView.setImageResource(R.drawable.w100n); break;
            case "103n": imageView.setImageResource(R.drawable.w103n); break;
            case "104n": imageView.setImageResource(R.drawable.w104n); break;
            case "300n": imageView.setImageResource(R.drawable.w300n); break;
            case "301n": imageView.setImageResource(R.drawable.w301n); break;
            case "406n": imageView.setImageResource(R.drawable.w406n); break;
            case "407n": imageView.setImageResource(R.drawable.w407n); break;
        }
    }
}
