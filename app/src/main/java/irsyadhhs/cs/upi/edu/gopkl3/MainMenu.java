package irsyadhhs.cs.upi.edu.gopkl3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.*;
import static irsyadhhs.cs.upi.edu.gopkl3.AppConfig.SP;

public class MainMenu extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private static final int MY_PERMISSIONS_REQUEST = 99;//int bebas, maks 1 byte
    private GoogleMap mMap;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker me;
    MarkerOptions mo;
    double latl;
    double longl;
    Marker todel;
    JSONArray jsonArray;
    ArrayList<Marker> pMarker = new ArrayList<Marker>();
    String lasttime;
    String req = "belum ada";
    String inputReq;
    Handler mHandler = new Handler();
    Intent i = new Intent(this, RegisterAct.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
       /* SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("tipe","default");
        ed.commit();*/
        inputReq = "default";
        Button btnAll = (Button) findViewById(R.id.button2);
        btnAll.setVisibility(View.INVISIBLE);
        final EditText etname = (EditText) findViewById(R.id.etmain);
        etname.setCursorVisible(false);
        etname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etname.setCursorVisible(true);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        createLocationRequest();
    }

    /**
     * Request location every 10 second
     * Request location high accuracy
     */
    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        // 10 detik sekali meminta lokasi 1000ms = 1 detik
        mLocationRequest.setInterval(1000);
        // tapi tidak boleh lebih cepat dari 1 detik
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * enable google location services API
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission diberikan, mulai ambil lokasi
                // ambilLokasi();

            } else {
                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("tipe", "default");
        ed.commit();*/
        inputReq = "default";
        Button btnAll = (Button) findViewById(R.id.button2);
        btnAll.setVisibility(View.INVISIBLE);
        mGoogleApiClient.connect();
        // Log.d("BABI",sp.getString("un","GAGAL").toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        //me.remove();
    }

    @Override
    public void onBackPressed() {
        EditText etname = (EditText) findViewById(R.id.etmain);
        etname.setCursorVisible(false);
        super.onBackPressed();
    }

    public void onClickCari(View v){
        EditText etname = (EditText) findViewById(R.id.etmain);
        String etTipe = etname.getText().toString();
        req = etTipe;
        Toast.makeText(MainMenu.this, etTipe, Toast.LENGTH_SHORT).show();
        /*SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("tipe",etTipe);
        ed.commit();*/
        inputReq = etTipe;
        Button btnAll = (Button) findViewById(R.id.button2);
        btnAll.setVisibility(View.VISIBLE);
        etname.clearFocus();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public void onDefault(View v){
        /*SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("tipe","default");
        ed.commit();*/
        inputReq = "default";
        Button btnAll = (Button) findViewById(R.id.button2);
        btnAll.setVisibility(View.INVISIBLE);
        EditText etname = (EditText) findViewById(R.id.etmain);
        etname.clearFocus();
        etname.setText("");
        etname.setCursorVisible(false);


        req = "belum";
    }

    private void handlePlayer(final int stt){
        //me.remove();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,SELECT_PEDAGANG, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "onResponse: playerResult= " + response.toString());
                        parsePlayer(response, stt);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menampilkan error pada logcat
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());

                    }
                }
        );

        AppController.getInstance().addToRequestQueue(request);
    }
    private void parsePlayer(JSONObject result, int stt){
        String id, name, tipe, lt;
        double latitude, longitude;


        try {
            jsonArray = result.getJSONArray("users");

            if (result.getString("success").equalsIgnoreCase("1")) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject pedagang = jsonArray.getJSONObject(i);

                    id = pedagang.getString("id");

                    name = pedagang.getString("nama");
                    tipe = pedagang.getString("tipe");
                    lt = pedagang.getString("lasttime");

                    latitude = pedagang.getDouble("lat");
                    longitude = pedagang.getDouble("long");
                    LatLng latLng = new LatLng(latitude, longitude);
                        /*SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
                        SharedPreferences.Editor ed = sp.edit();*/

                    if(stt == 1){ //add marker
                        setPedagang(latLng, name, tipe, lt);
                    }else if(stt == 0){//update posisi
                        if(inputReq.equals("default")){
                            pMarker.get(i).setVisible(true);
                            updatePedagang(latLng, i, name, tipe, lt);
                        }else{
                            if(inputReq.equals(tipe.toString())){
                                updatePedagang(latLng, i, name, tipe, lt);
                            }else{
                                deletePedagang(i);
                            }
                        }
                    }

                }
            } else if (result.getString("success").equalsIgnoreCase("0")) {

            }
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }

    }

    public void setPedagang(LatLng setPosisi, String setNama, String setTipe, String setTime){
        mo = new MarkerOptions().position(setPosisi).title(setNama + " - " + setTipe).snippet("Terakhir disini : " + setTime).icon(BitmapDescriptorFactory.fromResource(R.drawable.markersellers));
        pMarker.add(mMap.addMarker(mo));
    }

    public void updatePedagang(LatLng setPosisi, int i, String setNama, String setTipe, String setTime){
        pMarker.get(i).setPosition(setPosisi);
        pMarker.get(i).setTitle(setNama + " - " + setTipe);
        pMarker.get(i).setSnippet("Terakhir disini : " + setTime);
    }
    public void deletePedagang(int i){
        pMarker.get(i).setVisible(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            // tampilkan dialog minta ijin
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
//            mMap.setMyLocationEnabled(true);
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(me == null) {
            me = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You"));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),17));
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

        handlePlayer(1);
        startRepeatingTask();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                handlePlayer(0);
                Log.d("ambildata", "suksesambil");
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 1500);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onLocationChanged(Location location) {
        //me.remove();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM KK:mm a");
        lasttime = sdf.format(now);
        //handlePlayer(0);
//        mMap.clear();

        /*me =  mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("You"));*/
        latl = location.getLatitude();
        longl = location.getLongitude();
        me.setPosition(new LatLng(latl, longl));
        final Location loc = location;
        StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_PEMBELI,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response POST", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                SharedPreferences sp = getSharedPreferences(SP,MODE_PRIVATE);
                params.put("nama",sp.getString("un",""));
                params.put("lat", String.valueOf(loc.getLatitude()));
                params.put("long", String.valueOf(loc.getLongitude()));
                params.put("req", req);
                params.put("lasttime", lasttime);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(postRequest);

    }
}
