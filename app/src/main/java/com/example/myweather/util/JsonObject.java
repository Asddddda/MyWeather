package com.example.myweather.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.myweather.presenter.locationView.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;


import static android.content.Context.MODE_PRIVATE;
import static com.example.myweather.View.WeatherActivity.UPDATE_TEXT;

public class JsonObject{

    private Context mainContext;

    private List<Location>mLocList;

    private ExecutorService executorService;

    public JsonObject(List<Location>locationList,ExecutorService executorService){
        this.mLocList = locationList;
        this.executorService = executorService;
    }

    public JsonObject(Context mainContext,ExecutorService executorService){
        this.mainContext = mainContext;
        this.executorService = executorService;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            if(msg.what == UPDATE_TEXT) {
                Toast.makeText(mainContext,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 请求数据
     * @param method 请求类型
     * @param locId 请求地名（cid）
     */

    public void requestWeather(final String method, final String locId) {
        final String link;
        if(method.equals("air"))
            link = "https://free-api.heweather.net/s6/air/now?location="
                    + locId + "&key=22ddaaff07e84ba1aa12c354d00cb476";
        else if(method.equals("find"))
            link = "https://search.heweather.net/find?location="+locId
                    +"&key=22ddaaff07e84ba1aa12c354d00cb476";
        else link = "https://free-api.heweather.net/s6/weather/" + method
                +"?location=" + locId + "&key=22ddaaff07e84ba1aa12c354d00cb476";
        executorService.execute( new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("!!!!!!",Thread.currentThread().getName());
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(link);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONdata(method, response.toString());
                } catch (Exception e) {
                    if(!method.equals("find")) {
                        Message message = Message.obtain();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message);
                    }
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }) );
    }


    /**
     * 分类数据
     * @param method 请求类型
     * @param jsonData 请求得到的数据
     */
    public void parseJSONdata(final String method ,final String jsonData){
        switch (method){
            case "now":
                parseNow(jsonData);
                break;
            case "forecast":
                parseForecast(jsonData);
                break;
            case "hourly":
                parseHourly(jsonData);
                break;
            case "lifestyle":
                parseLifestyle(jsonData);
                break;
            case "air":
                parseAir(jsonData);
                break;
            case "find":
                parseFind(jsonData);
                break;
                default:
                    break;
        }
    }


    /**
     * 解析now类型的数据并存储
     * @param jsonData 请求得到的数据
     */
    public void parseNow(String jsonData){
        try {
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            String status = dataObject.getString("status");
            if(!status.equals("ok")){
                Toast.makeText(mainContext,"网络错误",Toast.LENGTH_SHORT).show();
            }else{
                JSONObject basic = dataObject.getJSONObject("basic");
                JSONObject update = dataObject.getJSONObject("update");
                JSONObject now = dataObject.getJSONObject("now");
                Log.d("!!!!!!",basic.getString("location"));
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf= new SimpleDateFormat("MM月dd日 HH:mm");
                String updateTime =sdf.format(date);
                SharedPreferences.Editor nowEditor = mainContext.getSharedPreferences("Weather",MODE_PRIVATE).edit();
                nowEditor.putString("titleLoc",basic.getString("location"));
                nowEditor.putString("update_time",updateTime);
                nowEditor.putString("degree",now.getString("tmp"));
                nowEditor.putString("weather_info",now.getString("cond_txt"));
                nowEditor.apply();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    /**
     *解析lifestyle的数据并存储
     * @param jsonData 同上
     * comf：舒适度指数、drsg：穿衣指数、sport：运动指数
     */
    public void parseLifestyle(String jsonData){
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            JSONArray life = dataObject.getJSONArray("lifestyle");
            SharedPreferences.Editor lifeEditor = mainContext.getSharedPreferences("Weather",MODE_PRIVATE).edit();
            lifeEditor.putString("comfort",life.getJSONObject(0).getString("txt"));
            lifeEditor.putString("drsg",life.getJSONObject(1).getString("txt"));
            lifeEditor.putString("sport",life.getJSONObject(3).getString("txt"));
            lifeEditor.apply();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 解析forecast的数据并储存
     * @param jsonData
     */
    public void parseForecast(String jsonData){
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            JSONArray forecast = dataObject.getJSONArray("daily_forecast");
            SharedPreferences.Editor forecastEditor = mainContext.getSharedPreferences("Weather",MODE_PRIVATE).edit();
            for(int i=0;i<7;i++) {
                JSONObject specificObject = forecast.getJSONObject(i);
                String ori = specificObject.getString("date");
                Date all = new SimpleDateFormat("yyyy-MM-dd").parse(ori);
                SimpleDateFormat sdf= new SimpleDateFormat("MM-dd");
                String date = sdf.format(all);
                forecastEditor.putString("date"+i,date);
                forecastEditor.putString("infoCodeD"+i,specificObject.getString("cond_code_d"));
                forecastEditor.putString("infoCodeN"+i,specificObject.getString("cond_code_n"));
                forecastEditor.putString("infoD"+i,specificObject.getString("cond_txt_d"));
                forecastEditor.putString("infoN"+i,specificObject.getString("cond_txt_n"));
                forecastEditor.putString("tem"+i,specificObject.getString("tmp_min")+"/"
                        +specificObject.getString("tmp_max"));
                forecastEditor.putString("rainPer"+i,specificObject.getString("pop"));
            }forecastEditor.apply();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.getErrorOffset();
        }
    }

    /**
     * 解析hourly的数据并储存
     * @param jsonData
     */
    public void parseHourly(String jsonData){
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            JSONArray hourly = dataObject.getJSONArray("hourly");
            SharedPreferences.Editor editor = mainContext.getSharedPreferences("Weather",MODE_PRIVATE).edit();
            for(int i=0;i<8;i++) {
                JSONObject specificObject = hourly.getJSONObject(i);
                String ori = specificObject.getString("time");
                Date all = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(ori);
                SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
                String date = sdf.format(all);
                editor.putString("time"+i,date);
                editor.putString("infoCode"+i,specificObject.getString("cond_code"));
                editor.putString("info"+i,specificObject.getString("cond_txt"));
                editor.putString("temHourly"+i,specificObject.getString("tmp"));
                editor.putString("wind"+i,specificObject.getString("wind_sc"));
            }editor.apply();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.getErrorOffset();
        }
    }

    /**
     * 得到pm2.5的数据
      * @param jsonData
     */
    public void parseAir(String jsonData){
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            JSONObject airObject = dataObject.getJSONObject("air_now_city");
            SharedPreferences.Editor airEditor = mainContext.getSharedPreferences("Weather",MODE_PRIVATE).edit();
            airEditor.putString("aqi",airObject.getString("qlty"));
            airEditor.putString("pm25",airObject.getString("pm25"));
            airEditor.apply();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 解析搜索结果
     * @param jsonData
     */
    public void parseFind(String jsonData){
        try{
            JSONObject rootObject = new JSONObject(jsonData);
            JSONArray rootArray = rootObject.getJSONArray("HeWeather6");
            JSONObject dataObject = rootArray.getJSONObject(0);
            JSONArray dataArray = dataObject.getJSONArray("basic");
            for(int i=0;i<dataArray.length();i++){
                //先过滤景区
                if(dataArray.getJSONObject(i).getString("type").equals("city")) {
                    Location location = new Location(dataArray.getJSONObject(i).getString("location"),
                            dataArray.getJSONObject(i).getString("admin_area") + "，" +
                                    dataArray.getJSONObject(i).getString("cnty"),
                            dataArray.getJSONObject(i).getString("cid"));
                    mLocList.add(location);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
