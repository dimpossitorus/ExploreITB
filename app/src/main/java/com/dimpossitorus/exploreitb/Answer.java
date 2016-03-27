package com.dimpossitorus.exploreitb;

import android.util.JsonReader;

import org.json.JSONObject;

/**
 * Created by Dimpos Sitorus on 24/03/2016.
 */
public class Answer {
    private String header;
    private int nim;
    private String answer;
    private double longitude;
    private double latitude;
    private String token;

    public Answer() {
        header = "com";
        nim = 13513083;
        answer = "gku_timur";
        longitude = 0;
        latitude = 0;
        token = "token";
    }

    public Answer (String _answer, double _long, double _lat, String _token) {
        header = "com";
        answer = _answer;
        nim = 13513083;
        longitude = _long;
        latitude = _lat;
        token = _token;
    }

    public String createStringJson () {

        //{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}

        return ( "{\"com\":\""+header+"\"," +
                "\"nim\":"+nim+"\","+
                "\"answer\":\""+answer+"\","+
                "\"longitude\":\""+longitude+"\","+
                "\"latitude\":\""+latitude+"\","+
                "\"token\":\""+token+"\"}");
    }

    public JSONObject createJsonObject() {

        JSONObject result;

        try {
            result =  new JSONObject(createStringJson());
        }
        catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

}
