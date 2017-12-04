package com.example.trackyourlocation;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MapsActivityDemo1 extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    /**/
    private final static int MY_PERMISSON_FINE_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    ZoomControls zoom;
    Button btnMark;
    Button btnSearch;
    Button btnsatView;
    Button btnSave;
    Double mylatitude = null;
    Double mylongitude = null;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    protected static final String TAG = "MapsActivityDemo1";
    DBaddressHandler dBaddressHandler;
    TextView mytextView;
    Address databaseaddress;
    String databaseString;
    Double lastlatitude = null;
    Double lastlongitude = null;


    Calendar currenttime;
    Calendar previoustime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_demo1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO initialize calender
       currenttime = Calendar.getInstance();
       previoustime = Calendar.getInstance();


        //database and printing data base
        dBaddressHandler = new DBaddressHandler(this,null,null,1);

        mytextView =(TextView)findViewById(R.id.mytextView);




        //google api for mark place


        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        locationRequest = new LocationRequest();
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /// set to PRIORITY_BALANCED_POWER_ACCURACY for real devices



        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //zoom controls

        zoom = (ZoomControls) findViewById(R.id.zoom);
        zoom.setOnZoomInClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    }
                });
        zoom.setOnZoomOutClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMap.animateCamera(CameraUpdateFactory.zoomOut());
                    }
                });


        //send your location as string lat & long

        Intent i=new Intent();
        i.setAction("com.graduationproject.positiondetector");
        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);





        btnSave = (Button) findViewById(R.id.btnsave);
        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText tt = (EditText)findViewById(R.id.etLocationEntry);
                        if(databaseaddress!=null) {

                            // Address Databaseaddress = new Address(Locale.US );
                            //   Databaseaddress.setLatitude(123);
                            // String send = toDatabase(Databaseaddress,tt.getText().toString());

                            // String send = toDatabase(databaseaddress,tt.getText().toString());
                            // dBaddressHandler.addProduct(send);
                            dBaddressHandler.addUserPlace(tt.getText().toString(),
                                    Double.toString(databaseaddress.getLatitude()),
                                    Double.toString(databaseaddress.getLongitude()));

                            Toast.makeText(getApplicationContext(), " Saved Successfully", Toast.LENGTH_LONG).show();
                            //dBaddressHandler.deleteProduct("location name@123.0~123.0");
                            //tt.setText(dBaddressHandler.databseToString());
                        }
                        else
                            Toast.makeText(getApplicationContext(), "No Address available to save", Toast.LENGTH_LONG).show();
                    }

                });
        btnSave.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "put mark on the map and press save ", Toast.LENGTH_LONG).show();

                return true;
            }
        });
        Button btndelete = (Button)findViewById(R.id.delete);
        btndelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), " Enter the desired place number to be deleted from favorite places", Toast.LENGTH_LONG).show();
                return true;
            }
        });

/*        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction("com.graduationproject.positiondetector");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mylatitude+" & "+mylongitude);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            }
        };
        Thread ahmedThread = new Thread(r);
        ahmedThread.start();
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RelativeLayout mylayout = (RelativeLayout) findViewById(R.id.mylayout);
        switch (item.getItemId()) {
            case R.id.menu_database:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);

                Intent i = new Intent(this,DataBase.class);

                i.putExtra("database", dBaddressHandler.databseToString());

                startActivity(i);
                return true;

            case  R.id.menu_sat:

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                return true;

            case R.id.menu_search:

                EditText searchText = (EditText) findViewById(R.id.etLocationEntry);
                String searchpalce = searchText.getText().toString();
                if (!searchpalce.equals("")) {
                    List<android.location.Address> addresslist = null;
                    Geocoder geocoder = new Geocoder(MapsActivityDemo1.this);
                    boolean a = Geocoder.isPresent();

                    try {
                        addresslist = geocoder.getFromLocationName(searchpalce, 1);
                        while (addresslist.size() == 0) {
                            addresslist = geocoder.getFromLocationName(searchpalce, 1);
                            //if (addresslist.size() > 0) {
                            //    Address address = addresslist.get(0);
                            // }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (addresslist != null) {
                        Address address = addresslist.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(address.getFeatureName()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        databaseaddress=address;

                    }
                } else
                    Toast.makeText(getApplicationContext(), "please enter a valid location", Toast.LENGTH_LONG).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

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

        // Add a marker in Sydney and move the camera
        final LatLng sydney = new LatLng(-34, 151);

        //TODO we will return back
//        mFusedLocationProviderClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if(location!=null)
//                        {
//                            LatLng l=new LatLng(location.getLatitude(),location.getLongitude());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
//                        }
//                        else
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                    }
//                });
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        //cheeck if permessions Was Granted or not
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            mMap.setMyLocationEnabled(true);
        }
        // if permissin is not granted
        else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // surrunding if cndition for less API
                // requist permission from user  because fine location is classified as dangerous permission
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSON_FINE_LOCATION);
            }

        }


        // on map click listner
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("fromOnMapClick"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        List<android.location.Address> addresslist = null;
                        Geocoder geocoder = new Geocoder(MapsActivityDemo1.this);
                        boolean a = Geocoder.isPresent();

                        try {
                            addresslist = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            while (addresslist.size() == 0) {
                                addresslist = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        if (addresslist != null) {

                            databaseaddress  = addresslist.get(0);

                            LatLng latlng = new LatLng(databaseaddress.getLatitude(), databaseaddress.getLongitude());
                            EditText tt = (EditText) findViewById(R.id.etLocationEntry);
                            tt.setText(databaseaddress.getFeatureName());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(databaseaddress.getFeatureName()));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


                        } else
                            Toast.makeText(getApplicationContext(), "Unknown location", Toast.LENGTH_LONG).show();
                    }

                });

        mMap.setOnMapClickListener(
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                    }
                });




    }

    //method to handel the requisted permission
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //switch case
        switch (requestCode) {
            case MY_PERMISSON_FINE_LOCATION:
                // once the permissin is granted we need to set mylocation to true
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), " This app requires Location Permission to be Granted ",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, " Conntection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Faild : connectionResult.getErrorCode()" + connectionResult.getErrorCode());
    }

    @Override

    public void onLocationChanged(final Location location) {
        mylatitude = location.getLatitude();
        mylongitude = location.getLongitude();
        Runnable r = new Runnable() {
            @Override
            public void run() {

                mylatitude = location.getLatitude();
                mylongitude = location.getLongitude();
                currenttime = Calendar.getInstance();

                long currentTimeInMills = currenttime.getTimeInMillis();
                long previousTimeInMills = previoustime.getTimeInMillis();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(currenttime.getTime());
                df = new SimpleDateFormat("HH:mm:ss");
                String time = df.format(currenttime.getTime());

                long totoo = currentTimeInMills - previousTimeInMills;
                if (currentTimeInMills - previousTimeInMills > (3*60000)) // three min
                {
                    long l = (currentTimeInMills - previousTimeInMills) / 1000;
                    l+=(3*60);
                    String s = Long.toString(l);

                    dBaddressHandler.addPlace(lastlatitude.toString(), lastlongitude.toString(), date, time, s);

                }

                lastlatitude = mylatitude;
                lastlongitude = mylongitude;
                previoustime = currenttime;
            }
        };

        Thread a = new Thread(r);
        a.start();

        //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String dd= df.format(now.getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    public  void  printDatabase(){

    }

    /*
    public String toDatabase(Address ad,String s){
        String ss = new String("");
        ss=s+" @"+ad.getLatitude()+"~"+ad.getLongitude();
        return ss;
    }*/

    public void delete (View view){
        EditText tt = (EditText)findViewById(R.id.etLocationEntry);
        String ss=tt.getText().toString();
        //int i = Integer.parseInt(ss);
        dBaddressHandler.deleteProduct(ss);
        Toast.makeText(getApplicationContext(), " DONE ", Toast.LENGTH_LONG).show();
    }

}