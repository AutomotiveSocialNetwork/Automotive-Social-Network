package com.example.trackyourlocation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * Created by Speed on 03/12/2017.
 */

public class LocationManipulating {
    private static final String TAG = "LocationManipulating";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static LocationObject retObject=new LocationObject();
    //private  LocationObject locationObject;;
    private Context mContext;
    //private Activity mActivity;

    public LocationManipulating(Context context) {
        mContext = context;
    }


    public LocationObject getLocation(){


        mFusedLocationProviderClient=new FusedLocationProviderClient(mContext);


        int hasLocationPermission= ContextCompat.checkSelfPermission(mContext,ACCESS_COARSE_LOCATION);
        if(hasLocationPermission== PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        Toast.makeText(mContext.getApplicationContext(), "your location is set to "+location.toString(), Toast.LENGTH_LONG).show();

                        double latitude=location.getLatitude();
                        double longitude=location.getLongitude();
                        double altitude=location.getAltitude();
                        retObject.setLongitude(longitude);
                        retObject.setLatitude(latitude);
                        retObject.setAltitude(altitude);
                        Log.d(TAG, "addLocation:before return place long @ "+retObject.getLongitude()+" lat @"+retObject.getLatitude());

                    }

                }
            });
            Log.d(TAG, "addLocation:before else place long @ "+retObject.getLongitude()+" lat @"+retObject.getLatitude());

        }else{
            Toast.makeText(mContext,"Accept Permission",Toast.LENGTH_LONG).show();

        }
        Log.d(TAG, "addLocation:return place long @ "+retObject.getLongitude()+" lat @"+retObject.getLatitude());

        return  retObject;
    }
}
