package com.dimpossitorus.exploreitb;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dimpos Sitorus on 13/03/2016.
 */
public class ServerResponse {

    /* Status check on runtime
     * If status == "ok" continue accepting response from server
     * else if status = "finish" complete the connection and stop internet resource
     */
    private String status;
    private int nim;
    private double longitude;
    private double latitude;
    private String token;
    private int check;

    public ServerResponse() {
        status = "ok";
        nim = 13513083;
        longitude = 0;
        latitude = 0;
        token = "token";
        check = 0;
    }

    public ServerResponse(String _response) {
        JSONObject response = null;
        try {
            response = new JSONObject(_response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            status = response.getString("status");
            nim = response.getInt("nim");
            longitude = response.getDouble("longitude");
            latitude = response.getDouble("latitude");
            token = response.getString("token");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }try {
            check = response.getInt("check");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getStatus() {
        return status;
    }

    public int getNim() {
        return nim;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getToken() {
        return token;
    }

    public void setStatus(String _status) {
        status = _status;
    }

    public void setNim (int _nim) {
        nim = _nim;
    }

    public void setLongitude (double _long) {
        longitude = _long;
    }

    public void setLatitude (double _lat) {
        latitude = _lat;
    }

    public void setToken (String _token) {
        token = _token;
    }


    public int getCheck() {
        return check;
    }

    public void setCheck(int _check) {
        check = _check;
    }
}
