package hiker.weatherapp.weather;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import hiker.weatherapp.util.NetUtil;
import hiker.weatherapp.bean.TodayWeather;
import hiker.weatherapp.R;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView pmImg;
    private ImageView mUpdateBtn;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv, tmp_nowTv;
    private ImageView weatherImg;
    private ImageView mCitySelect;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("MyWeather", "network is ok");

        }
        else{
            Log.d("MyWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了",Toast.LENGTH_SHORT).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city);
        mCitySelect.setOnClickListener(this);

        initView();
    }

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city_name);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity_info);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm2_5_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temp_today);
        climateTv = (TextView) findViewById(R.id.climate_today);
        windTv = (TextView) findViewById(R.id.wind_today);
        tmp_nowTv = (TextView) findViewById(R.id.temp_now);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        city_name_Tv.setText(sharedPreferences.getString("city_name","N/A") + "天气");
        cityTv.setText(sharedPreferences.getString("city_name","N/A"));
        timeTv.setText(sharedPreferences.getString("time","N/A") + "发布");
        humidityTv.setText("湿度：" + sharedPreferences.getString("humidity","N/A"));
        pmDataTv.setText(sharedPreferences.getString("pmData","N/A"));
        pmQualityTv.setText(sharedPreferences.getString("pmQuality","N/A"));
        weekTv.setText(sharedPreferences.getString("week","N/A"));
        temperatureTv.setText(sharedPreferences.getString("tmp","N/A"));
        climateTv.setText(sharedPreferences.getString("type","N/A"));
        windTv.setText("风力:"+ sharedPreferences.getString("fengdir","N/A"));
        tmp_nowTv.setText("当前温度："+ sharedPreferences.getString("tmpnow","N/A"));

        updateTodayWeatherImage(sharedPreferences.getString("type","N/A"));
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        tmp_nowTv.setText("当前温度："+todayWeather.getWendu());

        updateTodayWeatherImage(todayWeather.getType());

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

        SharedPreferences settings = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("city_name", todayWeather.getCity());
        editor.putString("time", todayWeather.getUpdatetime());
        editor.putString("humidity", todayWeather.getShidu());
        editor.putString("pmData", todayWeather.getPm25());
        editor.putString("pmQuality", todayWeather.getQuality());
        editor.putString("week", todayWeather.getDate());
        editor.putString("tmp", todayWeather.getHigh()+"~"+todayWeather.getLow());
        editor.putString("type", todayWeather.getType());
        editor.putString("fengli", todayWeather.getFengli());
        editor.putString("fengdir", todayWeather.getFengxiang());
        editor.putString("tmpnow", todayWeather.getWendu());
        editor.putString("main_city_code", "101020100");

        editor.commit();
    }

    void updateTodayWeatherImage(String weathertype) {
        if (weathertype == "多云") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_duoyun));
        } else if (weathertype == "晴") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_qing));
        } else if (weathertype == "阴") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_yin));
        } else if (weathertype == "小雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_xiaoyu));
        } else if (weathertype == "中雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_zhongyu));
        } else if (weathertype == "阵雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_zhenyu));
        } else if (weathertype == "暴雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_baoyu));
        } else if (weathertype == "特大暴雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_tedabaoyu));
        } else if (weathertype == "雷震雨") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_leizhenyu));
        } else if (weathertype == "小雪") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_xiaoxue));
        } else if (weathertype == "中雪") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_zhongxue));
        } else if (weathertype == "阵雪") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_zhenxue));
        } else if (weathertype == "暴雪") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_baoxue));
        } else if (weathertype == "雷震雨冰雹") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_leizhenyubingbao));
        } else if (weathertype == "雨夹雪") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_yujiaxue));
        } else if (weathertype == "沙尘暴") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_shachenbao));
        } else if (weathertype == "雾") {
            weatherImg.setImageDrawable(getDrawable(R.mipmap.biz_plugin_weather_wu));
        }
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int newCount = 0;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;


        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("MyWeather", "parseXML");

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        Log.d("MyWeather", "start");
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if (xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                            Log.d("myWeather", "wendu: "+xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;

                }
                eventType = xmlPullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    /**
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" +cityCode;

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;

                try {
                    Log.d("MyWeather",address);
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    Log.d("MyWeather", "test4");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;

                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        //Log.d("MyWeather", str);
                    }
                    String responseStr = response.toString();

                    Log.d("MyWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                    }
                    Message msg =new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj=todayWeather;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city){
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("MyWeather", "network is ok");
                queryWeatherCode(cityCode);
            }
            else{
                Log.d("MyWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("MyWeather", "选择城市为" + newCityCode);


            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("MyWeather", "network is ok");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("MyWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
