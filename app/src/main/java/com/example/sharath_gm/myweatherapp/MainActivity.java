package com.example.sharath_gm.myweatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    static String webServerUrl = "";
    static String jsonString = "";

    static String streetStr = "";
    static String cityStr = "";
    static String stateStr = "";
    static String degreeUnit = "";

    static Map<String, String> statesMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerState);
        spinner.setOnItemSelectedListener(this);
        statesMap = populateSpinner();
        List<String> statesList = new ArrayList<String>();
        statesList.addAll(statesMap.keySet());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        ImageView mImage = (ImageView) findViewById(R.id.imageIcon);
        mImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.forecast.io"));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        /*Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();*/
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public static Map<String, String> populateSpinner() {
        Map<String, String> states = new LinkedHashMap<String, String>();
        states.put("Select a State", "-1");
        states.put("Alabama", "AL");
        states.put("Alaska", "AK");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Rhode Island", "RI");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
        return states;

    }

    public void displayPersonalDetails(View view) {
        Intent bioIntent = new Intent(this, PersonalDetails.class);
        startActivity(bioIntent);
    }

    public void clearScreen(View view) {
        Log.d("MainActivity", "clearScreen");
        EditText street = (EditText) this.findViewById(R.id.etStreet);
        EditText city = (EditText) this.findViewById(R.id.etCity);
        Spinner state = (Spinner) this.findViewById(R.id.spinnerState);
        RadioButton degree = (RadioButton) this.findViewById(R.id.rbFah);
        TextView errorMsg = (TextView) this.findViewById(R.id.errorStr);
        if (street != null) street.setText("");
        if (city != null) city.setText("");
        state.setSelection(0);
        degree.setChecked(true);
        errorMsg.setText("");
        street.requestFocus();
    }

    public void submitForm(View view) {
        Log.d("MainActivity", "submitForm");
        EditText street = (EditText) this.findViewById(R.id.etStreet);
        EditText city = (EditText) this.findViewById(R.id.etCity);
        Spinner state = (Spinner) this.findViewById(R.id.spinnerState);
        RadioGroup degreeGroup = (RadioGroup) this.findViewById(R.id.radioDegree);
        TextView errorMsg = (TextView) this.findViewById(R.id.errorStr);
        streetStr = (street != null) ? street.getText().toString().trim() : "";
        cityStr = (city != null) ? city.getText().toString().trim() : "";
        stateStr = (state != null) ? state.getSelectedItem().toString() : "";
        int radioId = degreeGroup.getCheckedRadioButtonId();
        RadioButton degreeStr = (RadioButton) findViewById(radioId);
        degreeUnit = "us";
        if (degreeStr.getText().toString().equals("Celsius")) {
            degreeUnit = "si";
        }

        if (streetStr.length() <= 0) {
            errorMsg.setText("Please enter a Street");
            return;
        }
        if (cityStr.length() <= 0) {
            errorMsg.setText("Please enter a City");
            return;
        }
        if (stateStr.length() <= 0 || stateStr.equals("Select a State")) {
            errorMsg.setText("Please select a State");
            return;
        }

        try {
            String encodedStreetStr = URLEncoder.encode(streetStr, "UTF-8");
            String encodedCityStr = URLEncoder.encode(cityStr, "UTF-8");
            String encodedStateStr = URLEncoder.encode(stateStr, "UTF-8");
            String encodedDegreeUnit = URLEncoder.encode(degreeUnit, "UTF-8");

            webServerUrl = "http://sharathgm.elasticbeanstalk.com/Homework8/forecastSearch.php?street=" + encodedStreetStr + "&city=" + encodedCityStr + "&state=" + encodedStateStr + "&temperature=" + encodedDegreeUnit;
            try {
                new JsonAsyncTask().execute();

            } catch (Exception e) {
                e.printStackTrace();
                errorMsg.setText("Failed");
            }
        } catch (UnsupportedEncodingException ex) {
            errorMsg.setText(" Url Exeption ");
        }
    }

    private class JsonAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet request = new HttpGet(webServerUrl);
            InputStream resultStream = null;
            String result = null;
            try {
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                resultStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObj;

            try {
                jsonObj = new JSONObject(result != null ? result : "");
                jsonString = jsonObj.toString();
                Log.d("MainActivity", "json" + jsonString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            Intent resultsIntent = new Intent();
            resultsIntent.setClass(getApplicationContext(), ResultActivity.class);

            EditText city = (EditText) findViewById(R.id.etCity);
            Spinner state = (Spinner) findViewById(R.id.spinnerState);
            String stateCode = statesMap.get(stateStr);
            resultsIntent.putExtra("cityStateStr", cityStr + ", " + stateCode);

            resultsIntent.putExtra("degreeUnit", degreeUnit);
            resultsIntent.putExtra("forecastJsonStr", jsonString);
            startActivity(resultsIntent);
        }
    }


}

