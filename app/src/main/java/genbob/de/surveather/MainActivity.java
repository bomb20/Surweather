package genbob.de.surveather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private EditText editText;
    private Button buttonGo;
    private ApiClient call;
    private DecimalFormat decimalFormat;
    private TextView countdown;
    private SimpleDateFormat sdf;
    private Geocoder geocoder;
    /*
     * Created by genbob on 22.04.16
     * This software were developed during the NASA Spaceapp Hackathon 2016 in Würzburg
     * Members of the Project are Sabrina Höhn, Robert Leppich (genbob) and Vincent Truchseß
     *
     * TODO: source out some functions to a extra class
     * TODO: reduce global variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.geocoder = new Geocoder(this, Locale.GERMANY);
        this.countdown = (TextView) findViewById(R.id.textView_countdown);
        this.sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        this.editText = (EditText) findViewById(R.id.textfield_city);
        this.buttonGo = (Button) findViewById(R.id.button_select_city);
        Calendar cal = Calendar.getInstance();
        this.buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Pressed Go");
                if (editText.getText() == null || editText.equals("")) return;
                findCity(editText.getText().toString());
            }
        });
        this.decimalFormat = new DecimalFormat("##.##");


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CountDownTimer cdt = new CountDownTimer(Long.parseLong(getString(R.string.remaining)), 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                String str = sdf.format(new Date(millisUntilFinished)).substring(3);
                countdown.setText(str);
            }

            @Override
            public void onFinish() {

            }

        };
        cdt.start();
    }

    //TODO: save a given location request
    public void findCity(String address) {
        if(address == null) return;
//        getPreferences(MODE_PRIVATE).edit().putString("address", address).commit();
//        Geocoder geocoder = new Geocoder(this, Locale.GERMANY);

        try {
            final List<Address> result = geocoder.getFromLocationName(address, 10);
            Log.i(TAG, "Address: " + address);
            this.editText.setText(result.get(0).getAddressLine(0));
            URL[] urlArray = new URL[1];
            urlArray[0] = buildURL(result.get(0), "weather");

            this.call = new ApiClient();

            this.call.execute(urlArray);
            JsonNode[] root = call.get();

            if (root != null) {
                setPics(root[0]);//Debug purpose
            }


        } catch (IOException e) {
            Log.e(TAG, "Error while requesting Location");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //TODO: Change category to enum
    //TODO: use URIBuilder
    private URL buildURL(Address address, String category) {
        try {
            StringBuilder stringBuilder = new StringBuilder(getString(R.string.url));
            stringBuilder.append(category);
            Log.i(TAG, "LAT: " + address.getLatitude());
            Log.i(TAG, "LON: " + address.getLongitude());
            stringBuilder.append("?lat=" + String.valueOf(address.getLatitude()));
            stringBuilder.append("&lon=" + String.valueOf(address.getLongitude()));
            stringBuilder.append("&APPID=" + getString(R.string.apikey));
            return new URL(stringBuilder.toString());

        } catch (MalformedURLException u) {
            Log.e(TAG, "Error Urlconst" + u.getStackTrace());
        }
        return null;
    }
    //Listerner for GPS data
    //TODO: Change position currently
    final LocationListener locationlistener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "LocationChanged");
                try {
                    List<Address> loc = geocoder
                            .getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    findCity(loc.get(0).getAddressLine(0).toString());
                } catch (IOException e) {
                    Log.e(TAG, "GPS error");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAG, "onStatusChanged " + provider + " " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAG, "onProviderEnabled " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAG, "onProviderDisabled " + provider);
            }
        };
    //register listener
    public void getPos(View view) {

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
    }



    private void setPics(JsonNode root){
        JsonNode weather = root.get("weather").findValue("id");
        JsonNode rain = root.get("rain");
        JsonNode wind = root.get("wind");
        JsonNode tempAver = root.get("main").get("temp");
        JsonNode tempMin = root.get("main").get("temp_min");
        JsonNode tempMax = root.get("main").get("temp_max");
        JsonNode pressure = root.get("main").get("pressure");

        TextView rainT = (TextView) findViewById(R.id.textView_inRain);
        if(rain != null){
            Iterator<JsonNode> iterator = rain.getElements();
            while(iterator.hasNext()){
                rainT.setText(this.decimalFormat.format(iterator.next().asDouble()) + " l");
            }
        }
        TextView windT = (TextView) findViewById(R.id.textView_inWind);
        if(windT != null){
            String str = this.decimalFormat.format(wind.findValue("speed").asDouble()) + " km/h";
            if(str != null){
                windT.setText(str);
            }
        }
        TextView tempT = (TextView) findViewById(R.id.textView_inTemp);
        if(tempT != null){
            double temp = tempAver.asDouble() - 273.15;
            tempT.setText(this.decimalFormat.format(temp) + " °C");
        }
        TextView tempMinT = (TextView) findViewById(R.id.textView_inTempMin);
        if(tempMinT != null){
            double minTemp = tempMin.asDouble() -273.15;
            tempMinT.setText(this.decimalFormat.format(minTemp) + " °C");
        }
        TextView tempMaxT = (TextView) findViewById(R.id.textView_inTempMax);
        if (tempMaxT != null){
            double maxTemp = tempMax.asDouble() -273.15;
            tempMaxT.setText(this.decimalFormat.format(maxTemp) + " °C");
        }



        if(weather != null){
            ImageButton button = (ImageButton) findViewById(R.id.imageButton_weather);
            Log.d(TAG, "Weather: " + weather.asInt());
           if(weather.asInt() < 502 || (weather.asInt() > 800 && weather.asInt()<805)){
               Log.d(TAG, "here");
               button.setBackgroundResource(R.mipmap.badweather);
           }
           else if(weather.asInt() < 800 & weather.asInt() > 501){
                button.setBackgroundResource(R.mipmap.uglyweather);
            }
           else if(weather.asInt() == 800){
                button.setBackgroundResource(R.mipmap.goodweather);
            }
        }
        //set pics for conditions and update progressbar
        // FOR DEMONSTRATION PURPOSE ON RANDOM VALUES!
        Random rnd = new Random();
        ImageButton buttonAir = (ImageButton) findViewById(R.id.imageButton_Air);
        ImageButton buttonEye = (ImageButton) findViewById(R.id.imageButton_eye);
        ImageButton buttonEar = (ImageButton) findViewById(R.id.imageButton_ear);
        ArrayList<ImageButton> list = new ArrayList<>();
        list.add(buttonAir);
        list.add(buttonEye);
        list.add(buttonEar);
        Iterator<ImageButton> iterator = list.iterator();
        int laufer = 0;
        int progress = 0;
        int[] cases = {R.mipmap.goodair, R.mipmap.badair, R.mipmap.uglyair,
                R.mipmap.goodeye, R.mipmap.badeye, R.mipmap.uglyeye,
                R.mipmap.goodear, R.mipmap.badear, R.mipmap.uglyear,
                };
        while(iterator.hasNext()) {
            int rand =  rnd.nextInt(3);
            int value = rand + laufer;
            iterator.next().setBackgroundResource(cases[value]);
            progress += rand;
            laufer += 3;

        }
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(progress);

    }
    //Buttons:
    public void onClickWeather(View view){
        Intent intent = new Intent(this, WeatherGraph.class);
        startActivity(intent);
    }
    public void onClickMap(View view){
        Intent intent = new Intent(this, MapWue.class);
        intent.putExtra("map", R.mipmap.mapall);
        startActivity(intent);
    }
    public void onClickEye(View view){
        Intent intent = new Intent(this, MapWue.class);
        intent.putExtra("map", R.mipmap.mapallerg);
        startActivity(intent);
    }
    public void onClickEar(View view){
        Intent intent = new Intent(this, MapWue.class);
        intent.putExtra("map", R.mipmap.mapnoise);
        startActivity(intent);

    }
    public void onClickAir(View view){
        Intent intent = new Intent(this, MapWue.class);
        intent.putExtra("map", R.mipmap.mapdirt);
        startActivity(intent);

    }
    public void inClickHAY(View view){
        Intent intent = new Intent(this, UserCommit.class);
        startActivity(intent);
    }




}
