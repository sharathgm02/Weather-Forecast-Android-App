package com.example.sharath_gm.myweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class ResultActivity extends AppCompatActivity {

    final String DEGREE_SYMBOL = "\u00b0";
    DecimalFormat f = new DecimalFormat("##.00");
    static String cityStateStr = "";
    static String degree = "";
    static String forecastStr = "";
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ResultActivity");
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_result);



        forecastStr = getIntent().getStringExtra("forecastJsonStr");
        cityStateStr = getIntent().getStringExtra("cityStateStr");
        final String degreeUnit = getIntent().getStringExtra("degreeUnit");
        degree = "F";
        String windSpeedUnit = "mph";
        String visibilityUnit = "mi";
        if (degreeUnit.equals("si")) {
            degree = "C";
            windSpeedUnit = "m/s";
            visibilityUnit = "km";
        }

        try {
            JSONObject forecastJsonObj = new JSONObject(forecastStr);
            final String iconStr = forecastJsonObj.optJSONObject("currently").optString("icon");
            int drawableImgCode = getImageCode(iconStr);
            ImageView icon = (ImageView) findViewById(R.id.summaryIcon);
            if (drawableImgCode != 0) {
                icon.setImageResource(drawableImgCode);
            }

            final String summary = forecastJsonObj.optJSONObject("currently").optString("summary");
            TextView summaryText = (TextView) findViewById(R.id.summary);
            summaryText.setText(summary + " in " + cityStateStr);

            Double temperature = forecastJsonObj.optJSONObject("currently").optDouble("temperature");
            final Integer tempInt = temperature.intValue();
            TextView tempText = (TextView) findViewById(R.id.temperature);
            tempText.setText(tempInt.toString() + DEGREE_SYMBOL + degree);

            Double minTemp = forecastJsonObj.optJSONObject("daily").optJSONArray("data").optJSONObject(0).optDouble("temperatureMin");
            Integer minTempInt = minTemp.intValue();
            Double maxTemp = forecastJsonObj.optJSONObject("daily").optJSONArray("data").optJSONObject(0).optDouble("temperatureMax");
            Integer maxTempInt = maxTemp.intValue();
            TextView minMax = (TextView) findViewById(R.id.lowHigh);
            minMax.setText("L:" + minTempInt.toString() + DEGREE_SYMBOL + " | H:" + maxTempInt.toString() + DEGREE_SYMBOL);

            Double precipIntensity = forecastJsonObj.optJSONObject("currently").optDouble("precipIntensity");
            if (degreeUnit == "si") {
                precipIntensity /= 25.4;
            }
            TextView precip = (TextView) findViewById(R.id.precip);
            precip.setText(getPrecipitationStr(precipIntensity));

            Double precipProbability = forecastJsonObj.optJSONObject("currently").optDouble("precipProbability") * 100;
            Integer rainPercentage = precipProbability.intValue();
            TextView rainText = (TextView) findViewById(R.id.rainChance);
            rainText.setText(rainPercentage.toString() + " %");

            double windSpeed = forecastJsonObj.optJSONObject("currently").optDouble("windSpeed");
            TextView windText = (TextView) findViewById(R.id.windSpeed);
            windText.setText(f.format(windSpeed).toString() + " " + windSpeedUnit);

            double dewPoint = forecastJsonObj.optJSONObject("currently").optDouble("dewPoint");
            TextView dewText = (TextView) findViewById(R.id.dewPoint);
            dewText.setText(f.format(dewPoint).toString() + " " + DEGREE_SYMBOL + degree);

            Double humidity = forecastJsonObj.optJSONObject("currently").optDouble("humidity") * 100;
            Integer humidityPercent = humidity.intValue();
            TextView humidityText = (TextView) findViewById(R.id.humidity);
            humidityText.setText(humidityPercent.toString() + " %");

            double visibility = forecastJsonObj.optJSONObject("currently").optDouble("visibility");
            TextView visibilityText = (TextView) findViewById(R.id.visibility);
            visibilityText.setText(f.format(visibility).toString() + " " + visibilityUnit);

            DateFormat formatTime = new SimpleDateFormat("hh:mm a");
            Date sunriseTime = new Date(forecastJsonObj.optJSONObject("daily").optJSONArray("data").optJSONObject(0).optLong("sunriseTime") * 1000);
            Date sunsetTime = new Date(forecastJsonObj.optJSONObject("daily").optJSONArray("data").optJSONObject(0).optLong("sunsetTime") * 1000);
            formatTime.setTimeZone(TimeZone.getTimeZone(forecastJsonObj.getString("timezone")));

            TextView sunriseText = (TextView) findViewById(R.id.sunrise);
            TextView sunsetText = (TextView) findViewById(R.id.sunset);
            sunriseText.setText(formatTime.format(sunriseTime).toString());
            sunsetText.setText(formatTime.format(sunsetTime).toString());


            ImageView fbImg = (ImageView) findViewById(R.id.imageButton);
            fbImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    shareDialog = new ShareDialog(ResultActivity.this);
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Toast.makeText(ResultActivity.this, "Facebook Post Successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(ResultActivity.this, "Post Cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(ResultActivity.this, "Posting Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    String imageUrl = "http://cs-server.usc.edu:45678/hw/hw8/images/";
                    String fbTitle = "Current Weather in " + cityStateStr;
                    String fbUnit = "";
                    switch (iconStr) {
                        case "clear-day":
                            imageUrl += "clear.png";
                            break;
                        case "clear-night":
                            imageUrl += "clear_night.png";
                            break;
                        case "rain":
                            imageUrl += "rain.png";
                            break;
                        case "snow":
                            imageUrl += "snow.png";
                            break;
                        case "sleet":
                            imageUrl += "sleet.png";
                            break;
                        case "wind":
                            imageUrl += "wind.png";
                            break;
                        case "fog":
                            imageUrl += "fog.png";
                            break;
                        case "cloudy":
                            imageUrl += "cloudy.png";
                            break;
                        case "partly-cloudy-day":
                            imageUrl += "cloud_day.png";
                            break;
                        case "partly-cloudy-night":
                            imageUrl += "cloud_night.png";
                            break;
                    }
                    if (degreeUnit.equals("us"))
                        fbUnit = "\u2109";
                    else if (degreeUnit.equals("si"))
                        fbUnit = "\u2103";
                    String description = summary + ", " + tempInt + fbUnit;

                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(fbTitle)
                                .setContentDescription(description)
                                .setContentUrl(Uri.parse("http://forecast.io"))
                                .setImageUrl(Uri.parse(imageUrl))
                                .build();
                        shareDialog.show(linkContent);
                    }
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static int getImageCode(String iconStr) {
        int imageCode = 0;
        switch (iconStr) {
            case "clear-day":
                imageCode = R.drawable.clear;
                break;
            case "clear-night":
                imageCode = R.drawable.clear_night;
                break;
            case "rain":
                imageCode = R.drawable.rain;
                break;
            case "snow":
                imageCode = R.drawable.snow;
                break;
            case "sleet":
                imageCode = R.drawable.sleet;
                break;
            case "wind":
                imageCode = R.drawable.wind;
                break;
            case "fog":
                imageCode = R.drawable.fog;
                break;
            case "cloudy":
                imageCode = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                imageCode = R.drawable.cloud_day;
                break;
            case "partly-cloudy-night":
                imageCode = R.drawable.cloud_night;
        }

        return imageCode;
    }

    public static String getPrecipitationStr(double precipIntensity) {
        String precipitation = "";
        if (precipIntensity >= 0 && precipIntensity < 0.002) {
            precipitation = "None";
        } else if (precipIntensity >= 0.002 && precipIntensity < 0.017) {
            precipitation = "Very Light";
        } else if (precipIntensity >= 0.017 && precipIntensity < 0.1) {
            precipitation = "Light";
        } else if (precipIntensity >= 0.1 && precipIntensity < 0.4) {
            precipitation = "Moderate";
        } else if (precipIntensity >= 0.4) {
            precipitation = "Heavy";
        }
        return precipitation;
    }

    public void viewDetails(View view) {
        Intent detailsIntent = new Intent();
        detailsIntent.setClass(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra("cityStateStr", cityStateStr);
        detailsIntent.putExtra("degreeFahCel", degree);
        detailsIntent.putExtra("forecastStr", forecastStr);
        startActivity(detailsIntent);
    }
}
