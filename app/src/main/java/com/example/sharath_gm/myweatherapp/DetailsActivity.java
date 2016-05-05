package com.example.sharath_gm.myweatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.widget.LinearLayout.LayoutParams;
import android.widget.ToggleButton;


public class DetailsActivity extends AppCompatActivity {

    final String DEGREE_SYMBOL = "\u00b0";
    private ToggleButton toggleButton1, toggleButton2;
    private View possiblyHiddenView1, possiblyHiddenView2;
    static String degree = "";
    static String forecastStr = "";
    DateFormat formatTime = new SimpleDateFormat("hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toggleButton1 = (ToggleButton) findViewById(R.id.button24Hrs);
        toggleButton2 = (ToggleButton) findViewById(R.id.button7Days);

        int drawableImgCode = 0;

        forecastStr = getIntent().getStringExtra("forecastStr");

        String cityStateStr = getIntent().getStringExtra("cityStateStr");
        degree = getIntent().getStringExtra("degreeFahCel");

        TextView headline = (TextView) findViewById(R.id.header);
        headline.setText("More Details for " + cityStateStr);

        TextView degreeInHeader = (TextView) findViewById(R.id.tempInHeader);
        degreeInHeader.setText("Temp(" + DEGREE_SYMBOL + degree + ")");


        Date time = new Date();
        Integer tempInt = new Integer(0);
        LinearLayout next24HrLayout = (LinearLayout) findViewById(R.id.detail24Hrs);
        LinearLayout next7DayLayout = (LinearLayout) findViewById(R.id.detail7Days);
        try {
            JSONObject forecastJsonObj = new JSONObject(forecastStr);
            JSONArray hourlyData = forecastJsonObj.optJSONObject("hourly").optJSONArray("data");
            String timezone = forecastJsonObj.getString("timezone");
            formatTime.setTimeZone(TimeZone.getTimeZone(timezone));
            JSONObject obj = new JSONObject();
            String iconStr = "";
            Double temperature = 0.0;

            for (int index = 0; index < 24; index++) {
                obj = hourlyData.optJSONObject(index);
                time = new Date(obj.optLong("time") * 1000);

                iconStr = obj.optString("icon");
                drawableImgCode = getImageCode(iconStr);

                temperature = obj.optDouble("temperature");
                tempInt = temperature.intValue();

                LinearLayout linearLayout = new LinearLayout(this);
                LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(linearLayoutParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                if (index > 23) {
                    linearLayout.setVisibility(View.GONE);
                }
                if (index % 2 != 0) {
                    linearLayout.setBackgroundColor(0xffffffff);
                } else {
                    linearLayout.setBackgroundColor(0xAAA0A0A0);
                }
                linearLayout.setPadding(0, 20, 0, 20);

                TextView textView1 = new TextView(this);
                LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.LEFT;
                layoutParams1.weight = 1;
                textView1.setLayoutParams(layoutParams1);
                textView1.setText(formatTime.format(time));
                textView1.setPadding(0, 20, 0, 20);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                linearLayout.addView(textView1);

                ImageView imageView = new ImageView(this);
                LayoutParams params = new LayoutParams(300, 300);
                params.gravity = Gravity.LEFT;
                params.weight = 1;
                imageView.setLayoutParams(params);
                if (drawableImgCode != 0) {
                    imageView.setImageResource(drawableImgCode);
                }
                linearLayout.addView(imageView);

                TextView textView2 = new TextView(this);
                LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                layoutParams2.gravity = Gravity.RIGHT;
                layoutParams2.weight = 1;
                layoutParams2.setMargins(0, 0, 0, 0);
                textView2.setLayoutParams(layoutParams2);
                textView2.setPadding(0, 20, 0, 20);
                textView2.setText(tempInt.toString());
                textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                linearLayout.addView(textView2);

                next24HrLayout.addView(linearLayout);

            }

            LinearLayout linearLayout = new LinearLayout(this);
            LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(linearLayoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView1 = new TextView(this);
            LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER;
            layoutParams1.weight = 1;
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("+");
            textView1.setPadding(0, 20, 0, 20);
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            textView1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    displayExtra();
                }
            });
            linearLayout.addView(textView1);
            next24HrLayout.addView(linearLayout);

            JSONArray dailyData = forecastJsonObj.optJSONObject("daily").optJSONArray("data");
            JSONObject obj2 = new JSONObject();
            DateFormat formatDate = new SimpleDateFormat("E, MMM dd");
            formatDate.setTimeZone(TimeZone.getTimeZone(timezone));
            for (int i = 1; i < dailyData.length(); i++) {
                obj2 = dailyData.optJSONObject(i);
                Date date = new Date(obj.optLong("time") * 1000);
                Double minTemp = obj2.optDouble("temperatureMin");
                Double maxTemp = obj2.optDouble("temperatureMax");
                Integer minTempInt = minTemp.intValue();
                Integer maxTempInt = maxTemp.intValue();
                String icon = obj2.optString("icon");
                int imageCode = getImageCode(icon);
                int dateId = this.getResources().getIdentifier("date" + i, "id", getPackageName());
                TextView dateTextView = (TextView) findViewById(dateId);
                dateTextView.setText(formatDate.format(date).toString());
                int minMaxId = this.getResources().getIdentifier("minMax" + i, "id", getPackageName());
                TextView tempTextView = (TextView) findViewById(minMaxId);
                tempTextView.setText("Min: " + minTempInt.toString() + DEGREE_SYMBOL + degree + "| Max: " + maxTempInt.toString() + DEGREE_SYMBOL + degree);
                int imageId = this.getResources().getIdentifier("image" + i, "id", getPackageName());
                ImageView imgTextView = (ImageView) findViewById(imageId);
                imgTextView.setImageResource(imageCode);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        possiblyHiddenView1 = (View) findViewById(R.id.detail24Hrs);
        possiblyHiddenView2 = (View) findViewById(R.id.detail7Days);
        addListenerOnButton();

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

    public void addListenerOnButton() {

        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton1.isChecked()) {
                    toggleButton1.setChecked(true);
                    toggleButton2.setChecked(false);
                    possiblyHiddenView1.setVisibility(View.VISIBLE);
                    possiblyHiddenView2.setVisibility(View.INVISIBLE);
                } else {
                    toggleButton1.setChecked(true);
                    possiblyHiddenView1.setVisibility(View.VISIBLE);
                }
            }
        });

        toggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton2.isChecked()) {
                    toggleButton1.setChecked(false);
                    toggleButton2.setChecked(true);
                    possiblyHiddenView1.setVisibility(View.INVISIBLE);
                    possiblyHiddenView2.setVisibility(View.VISIBLE);
                } else {
                    toggleButton2.setChecked(true);
                    possiblyHiddenView2.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void displayExtra(){
        try{
            LinearLayout next24HrLayout = (LinearLayout) findViewById(R.id.detail24Hrs);
            JSONObject forecastJsonObj = new JSONObject(forecastStr);
            JSONArray hourlyData = forecastJsonObj.optJSONObject("hourly").optJSONArray("data");
            String timezone = forecastJsonObj.getString("timezone");
            formatTime.setTimeZone(TimeZone.getTimeZone(timezone));
            JSONObject obj = new JSONObject();
            String iconStr = "";
            Double temperature = 0.0;
            Date time = new Date();
            int drawableImgCode = 0;
            Integer tempInt;
            for (int index = 24; index < 48; index++) {
                obj = hourlyData.optJSONObject(index);
                time = new Date(obj.optLong("time") * 1000);

                iconStr = obj.optString("icon");
                drawableImgCode = getImageCode(iconStr);

                temperature = obj.optDouble("temperature");
                tempInt = temperature.intValue();

                LinearLayout linearLayout = new LinearLayout(this);
                LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(linearLayoutParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                if (index > 23) {
                    linearLayout.setVisibility(View.GONE);
                }
                if (index % 2 != 0) {
                    linearLayout.setBackgroundColor(0xffffffff);
                } else {
                    linearLayout.setBackgroundColor(0xAAA0A0A0);
                }
                linearLayout.setPadding(0, 20, 0, 20);

                TextView textView1 = new TextView(this);
                LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.LEFT;
                layoutParams1.weight = 1;
                textView1.setLayoutParams(layoutParams1);
                textView1.setText(formatTime.format(time));
                textView1.setPadding(0, 20, 0, 20);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                linearLayout.addView(textView1);

                ImageView imageView = new ImageView(this);
                LayoutParams params = new LayoutParams(300, 300);
                params.gravity = Gravity.LEFT;
                params.weight = 1;
                imageView.setLayoutParams(params);
                if (drawableImgCode != 0) {
                    imageView.setImageResource(drawableImgCode);
                }
                linearLayout.addView(imageView);

                TextView textView2 = new TextView(this);
                LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                layoutParams2.gravity = Gravity.RIGHT;
                layoutParams2.weight = 1;
                layoutParams2.setMargins(0, 0, 0, 0);
                textView2.setLayoutParams(layoutParams2);
                textView2.setPadding(0, 20, 0, 20);
                textView2.setText(tempInt.toString());
                textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                linearLayout.addView(textView2);

                next24HrLayout.addView(linearLayout);

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
