# MyWeather
简介:
   QUIN天气,是缺哥哥作为大秦帝王,用来观测天象,运筹帷幄的APP.
   能显示当天的天气数据,如温度,pm2.5,aqi等,也能预报未来24h内每3h的天气变化,也能显示7天内每天的天气变化.
   支持搜索指定地点,也能使用定位获取当前地点,还支持自定义背景图片.
   ![image](https://github.com/Asddddda/MyWeather/blob/master/使用演示.gif)
  
技术及知识点:
   JSONObject解析请求得到的数据,SharedPreference用于数据储存.
   显式intent用于activity跳转,RecyclerView用于显示搜索结果;隐式intent用于打开相册选背景图片,并将URL转文件路径,储存路径,实现背景图片的保存.
   结合drawerlayout,swiperefresh带来流畅体验,在子线程请求完数据后,再handle给主线程更新UI界面.
  
心得体会:
   通过这次安卓完整软件的开发,把安卓第一行代码的内容差不多过了一遍,并将所学投入实践,收获不小,由于之前学业繁忙,安卓java的时间并不太多,如果有时间
提前预习了的话,上课的效率应该会高很多吧.
