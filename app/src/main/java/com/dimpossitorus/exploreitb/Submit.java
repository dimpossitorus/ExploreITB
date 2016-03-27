package com.dimpossitorus.exploreitb;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

public class Submit extends AppCompatActivity {

    ServerRequest mRequest;
    ServerResponse mResponse;
    TextView mTestView;

    //TextView mTestView;
    /*SensorManager mSensorManager;
    List<Sensor> deviceSensor; // List all the sensors available in the device*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mTestView = (TextView) findViewById(R.id.test);

        /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);*/

        final Spinner locList = (Spinner) findViewById(R.id.locationList);
        Intent intentOne = getIntent();
        //mRequest = new ServerRequest(getPlaceId((int) locList.getSelectedItemId()), intentOne.getDoubleExtra("long",0),intentOne.getDoubleExtra("lat",0),intentOne.getStringExtra("token"));
        mRequest = new ServerRequest();
        mResponse = new ServerResponse(mRequest.createJsonObjectAnswer());
        /*Toast.makeText(Submit.this, mResponse.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(Submit.this, (new ServerRequest()).toString(), Toast.LENGTH_SHORT).show();
        try {
            Toast.makeText(Submit.this, (new ServerRequest()).createJsonObjectAnswer().getString("com").toString(),Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the data to the Server
                mTestView.setText("" + getPlaceId((int) locList.getSelectedItemId()) + "\n" + mRequest.createJsonObjectRequest().toString());
                Intent resultIntent = new Intent();
                resultIntent.putExtra("loc", getPlaceId((int) locList.getSelectedItemId()));
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });


    }

    public String getPlaceId(int id) {
        String place;
        switch (id) {
            case 0  : place = "gku_timur";
                      break;
            case 1  : place = "gku_barat";
                      break;
            case 2  : place = "intel";
                      break;
            case 3  : place = "cc_barat";
                      break;
            case 4  : place = "cc_timur";
                      break;
            case 5  : place = "dpr";
                      break;
            case 6  : place = "oktagon";
                      break;
            case 7  : place = "perpus";
                      break;
            case 8  : place = "pau";
                      break;
            case 9  : place = "kubus";
                      break;
            default : place = "null";
                      break;
        }
        return place;
    }
}