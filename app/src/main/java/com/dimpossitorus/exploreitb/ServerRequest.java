package com.dimpossitorus.exploreitb;

/**
 * Created by Dimpos Sitorus on 13/03/2016.
 */

import android.util.Log;

import java.lang.String;

import org.json.JSONObject;

public class ServerRequest {
    private String header;
    private int nim;
    private String answer;
    private double longitude;
    private double latitude;
    private String token;

    public ServerRequest() {
        header = "req_loc";
        nim = 13513083;
        answer = "gku_timur";
        longitude = 0;
        latitude = 0;
        token = "token";
    }

    public ServerRequest(String _answer, double _long, double _lat, String _token) {
        header = "answer";
        answer = _answer;
        nim = 13513083;
        longitude = _long;
        latitude = _lat;
        token = _token;
    }

    public String getHeader() {
        return header;
    }

    public int getNim() {
        return nim;
    }

    public String getToken() {
        return token;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAnswer() {
        return answer;
    }

    public void setHeader(String _loc) {
        header = _loc;
    }

    public void setAnswer (String _answer) {
        answer = _answer;
    }

    public void setNim(int _nim) {
        nim = _nim;
    }

    public void setLongitude (double _longitude) {
        longitude = _longitude;
    }

    public void setLatitude(double _latitude) {
        latitude = _latitude;
    }

    public void setToken(String _token) {
        token = _token;
    }

    public String createStringRequest() {
        return "{\"com\":\""+header+"\"," +
                "\"nim\":\""+nim+"\"}";
    }

    public String createStringAnswer () {

        //{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}

        return ( "{\"com\":\""+header+"\"," +
                "\"nim\":\""+nim+"\","+
                "\"answer\":\""+answer+"\","+
                "\"longitude\":"+longitude+","+
                "\"latitude\":"+latitude+","+
                "\"token\":\""+token+"\"}");
    }

    public JSONObject createJsonObjectRequest() {

        JSONObject result;

        try {
            result =  new JSONObject(createStringRequest());
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("Explore ITB", ": error creating JSON");
            result = null;
        }
        return result;
    }

    public JSONObject createJsonObjectAnswer() {

        JSONObject result;

        try {
            result =  new JSONObject(createStringAnswer());
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("Explore ITB", ": error creating JSON");
            result = null;
        }
        return result;
    }
}
