package vineel.noel.com.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherApp extends AppCompatActivity {
    EditText etCityName;
    TextView tvWeatherInfo;

    public void findWeather(View view) {
        try {
            WeatherTask task = new WeatherTask();
            String encodedCity = URLEncoder.encode(etCityName.getText().toString(),"UTF-8");
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCity+"&APPID=ea574594b9d36ab688642d5fbeab847e");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather!", Toast.LENGTH_LONG).show();
        }
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etCityName.getWindowToken(),0);
    }

    public class WeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                Log.i("from background method",result);
                return result;

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Could not find weather!", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray array = new JSONArray(weatherInfo);
                for(int i=0; i< array.length(); i++){
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    if(main != "" && description != ""){
                        message += main+": "+description+"\r\n";
                    }
                }

                if(message != ""){
                    tvWeatherInfo.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"Could not find weather!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Could not find weather!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        etCityName = (EditText) findViewById(R.id.etCityName);
        tvWeatherInfo = (TextView) findViewById(R.id.tvWeatherInfo);

    }
}
