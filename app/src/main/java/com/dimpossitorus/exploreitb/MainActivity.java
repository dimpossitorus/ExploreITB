package com.dimpossitorus.exploreitb;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.Image;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.res.Configuration;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, SensorEventListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int SUBMIT_INTENT = 1;
    private Uri fileUri;
    //Orientation Sensor
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private Marker now;
    private Marker target;

    // Image
    private String mCurrentPhotoPath;

    // ServerRequest and ServerResponse
    private ServerRequest mRequest;
    private ServerResponse mResponse;

    private final static String url="89.36.220.146";
    //private final static String url="167.205.34.132";
    //private final static String url="google.co.";
    private final int port = 3111;
    private final int nim = 13513083;
    private String msg_send;
    PrintWriter _send  ;
    BufferedReader _receive  ;

    private final static String TAG = "ExploreITB";

    private ImageView compass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMaps);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        compass = (ImageView) findViewById(R.id.compass);

        Button cameraButton = (Button) findViewById(R.id.runCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    //Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Continue only if the File was succesfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }

            }
        });

        Button answerButton = (Button) findViewById(R.id.runSubmit);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerIntent = new Intent(MainActivity.this, Submit.class);
                startActivityForResult(answerIntent,SUBMIT_INTENT);
            }
        });

        // Create the sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //Initiate mRequest and mResponse
        mRequest = new ServerRequest();
        mResponse = new ServerResponse();
        JSONObject json = new JSONObject() ;
        try {
            json.put("com", "req_loc");
            json.put("nim",nim);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ms = json.toString();
        msg_send = ms ;
        Log.i(TAG, ms);
        new ClientTask().execute(ms);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // On Resume, check the latest status, if ok request again, if finish stop
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            galleryAddPic();
        }
        else if (requestCode ==SUBMIT_INTENT) {
            // Make new request and accept the response
            Log.d("Explore ITB", data.getStringExtra("loc"));
            Toast.makeText(MainActivity.this, "Location : "+data.getStringExtra("loc"), Toast.LENGTH_SHORT).show();
            mRequest.setAnswer(data.getStringExtra("loc"));
            mRequest = new ServerRequest(data.getStringExtra("loc"), mResponse.getLongitude(), mResponse.getLatitude(), mResponse.getToken());
            new ClientTask().execute(mRequest.createStringAnswer());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (now != null) {
            now.remove();
        }

        mLastLocation = location;
        LatLng lastLoc = new LatLng(location.getLatitude(), location.getLongitude());
        now = mMap.addMarker(new MarkerOptions().position(lastLoc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 17));

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        LatLng position;
        LatLng itb = new LatLng(-6.89148, 107.6095648);
        now = mMap.addMarker(new MarkerOptions().position(itb));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itb, 17));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    private File createImageFile() throws IOException {
        // Create an image file name'
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_"+timeStamp+"_";
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                mediaStorageDir /* directory */
        );

        // Save a file : path foruse with ACTION_VIEW intents
        mCurrentPhotoPath = "file:"+image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (! mediaStorageDir.exists()) {
            if(! mediaStorageDir.mkdirs()) {
                Log.d("Explore ITB", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+timeStamp + ".jpg");

        return mediaFile;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth_angle = event.values[0];
        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];
        /* Rotate the compass view just by using the azimuth_angle
         * Azimuth angle show the inclination from the north direction
         * Azimuth angle follow the clockwise rule
         * The rotation view follow the counter-clockwise rule
         * So, to rotate the compass in order to show north just directly use the value
         */

        //Code here to rotate the compass view
        if (getResources().getConfiguration().orientation == 1) {
            compass.setRotation(-1 * azimuth_angle);
        }
        else {
            compass.setRotation(-1 * azimuth_angle - 90);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
        // But, actually I don't know what's the function of this
        // Holy Shit
    }

    private class ClientTask extends AsyncTask<String,Void,String> {
        String msg_send ;

        @Override
        protected String doInBackground(String... params) {
            msg_send = params[0];
            Log.i("ExploreITB","Message to send : "+msg_send);
            Socket s = null ;
            try {
                s =new Socket (url,port);
                Log.i(TAG,"Socket created");
            } catch (UnknownHostException e) {
                Log.i(TAG,"Unknown host create socket exception");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i(TAG,"IOExc create socket");
                e.printStackTrace();
            }
            Log.i(TAG,"Create socket done");
            try {
                _send = new PrintWriter(s.getOutputStream(),true);
                _receive = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                Log.i(TAG,"IOExc _send _receive");
            }
            Log.i(TAG, "Sending message ");
            _send.write(msg_send + "\n");
            _send.flush();
            Log.i(TAG, "Send done. Wait for receive . . .");
            String response="" ;
            try {
                response = _receive.readLine();
            } catch (Exception e) {
                Log.i(TAG,"Read response exception");
            }
            Log.i(TAG,"response : "+response);
            return response ;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject obj=null ;
            try {
                obj = new JSONObject(result);
            } catch (JSONException e) {
                Log.i(TAG,"Convert string to json exception");
            }
            try {
                String status = obj.getString("status");
                if (status.contains("ok")) {
                    String nim = obj.getString("nim");
                    String token = obj.getString("token");
                    double longi = obj.getDouble("longitude");
                    double lati = obj.getDouble("latitude");
                    now.remove();
                    mResponse = new ServerResponse("ok", longi, lati, token, 0);
                    //mResponse.setLongitude(longi);
                    //mResponse.setLatitude(lati);
                    //mResponse.setToken(token);

                    LatLng pos = new LatLng(mResponse.getLatitude(),mResponse.getLongitude());
                    now = mMap.addMarker(new MarkerOptions().position(pos).title("New Marker"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
                    String temp = "Stat : "+status+" Token : "+token+" nim : "+nim+" longi : "+mResponse.getLongitude()+" lati : "+mResponse.getLongitude() ;
                    Log.i(TAG,"Parse result : "+temp);
                    Toast.makeText(getApplicationContext(), "New position to find : {"+mResponse.getLatitude()+","+mResponse.getLongitude()+"}",
                            Toast.LENGTH_LONG).show();
                } else if (status.contains("wrong_answer")) {
                    String token = obj.getString("token");
                    mRequest.setToken(token); ;
                    Toast.makeText(getApplicationContext(), "Wrong Answer",
                            Toast.LENGTH_LONG).show();
                } else if (status.contains("finish")) {
                    String token = obj.getString("token");
                    Toast.makeText(getApplicationContext(), "Finish. Marker will move to default position",
                            Toast.LENGTH_LONG).show();
                    LatLng itb = new LatLng(-6.89148, 107.6095648);
                    now = mMap.addMarker(new MarkerOptions().position(itb).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itb, 17));
                } else if (status.contains("err")) {
                    String nim = obj.getString("nim");
                    String token = obj.getString("token");
                    if (nim.length()<1) {
                        Toast.makeText(getApplicationContext(), "No NIM send. Please re-open the application",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No command send. Please re-open the application",
                                Toast.LENGTH_LONG).show();
                    }
                }


            } catch (JSONException e) {
                Log.i("ExploreITB","Exception in parse JSON");
            }

        }
    }
}
