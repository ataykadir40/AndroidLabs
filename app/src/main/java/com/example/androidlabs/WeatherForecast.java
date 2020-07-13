package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ForecastQuery fq = new ForecastQuery();
        fq.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String uvRate;
        private String minTemp;
        private String maxTemp;
        private String currentTemp;
        private Bitmap image;

        @Override
        protected String doInBackground(String ... args) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                String iconName = null;
                while(eventType != XmlPullParser.END_DOCUMENT) {

                    if(eventType == XmlPullParser.START_TAG) {
                        //If you get here, then you are pointing at a start tag
                        if(xpp.getName().equals("temperature")) {
                            currentTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(25);
                            minTemp = xpp.getAttributeValue(null, "min");
                            publishProgress(50);
                            maxTemp = xpp.getAttributeValue(null, "max");
                            publishProgress(75);
                        } else if(xpp.getName().equals("weather")) {
                            iconName = xpp.getAttributeValue(null, "icon");
                        }

                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }

                URL urlImage = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                }
                publishProgress(100);

                if(fileExistence(iconName + ".png")){ // Already downloaded
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(iconName + ".png");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    image = BitmapFactory.decodeStream(fis);
                    Log.i("FileImage","Looking for " +iconName + ".png" + " (was already downloaded)");
                }else{ // Have to download image
                    FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i("FileImage","Looking for " +iconName + ".png" + " (had to download it)");
                }


                URL uvUrl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");

                //open the connection
                HttpURLConnection uvUrlConnection = (HttpURLConnection) uvUrl.openConnection();

                //wait for data:
                InputStream uvResponse = uvUrlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(uvResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                // convert string to JSON:
                JSONObject jObject = new JSONObject(result);

                //get the double associated with "value"
                double value = jObject.getDouble("value");
                uvRate = String.valueOf(value);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return "Done";
        }

        public boolean fileExistence(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        @Override
        public void onProgressUpdate(Integer ... value) {
            ProgressBar bar = findViewById(R.id.ProgressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(value[0]);
        }

        @Override
        public void onPostExecute(String fromDoInBackground) {

            ImageView weatherIcon = findViewById(R.id.WeatherView);
            weatherIcon.setImageBitmap(image);

            TextView tempView = findViewById(R.id.CurrentTempView);
            tempView.setText("Temperature: " + currentTemp + " °C");

            TextView minView = findViewById(R.id.MinimumTempView);
            minView.setText("Min: " + minTemp + " °C");

            TextView maxView = findViewById(R.id.MaximumTempView);
            maxView.setText("Max: " + maxTemp + " °C");

            TextView uvView = findViewById(R.id.UVRatingView);
            uvView.setText("UV Rating: " + uvRate);

            ProgressBar bar = findViewById(R.id.ProgressBar);
            bar.setVisibility(View.INVISIBLE );

        }

    }

}
