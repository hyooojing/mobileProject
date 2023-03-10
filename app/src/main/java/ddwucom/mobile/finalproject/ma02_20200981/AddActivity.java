package ddwucom.mobile.finalproject.ma02_20200981;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    final int REQ_PERMISSION_CODE = 100;
    FusedLocationProviderClient flpClient;
    LatLng currentLoc;
    Location mLastLocation;
    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private LocationManager locationManager;

    MuseumDTO museum;
    TextView mTitle;
    TextView mType;
    TextView mTel;
    TextView mAddress;
    TextView mRest;
    TextView mOpen;
    TextView mClose;
    Float mLat;
    Float mLng;

    final static String TAG = "Toast";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_museum);
        museum = (MuseumDTO) getIntent().getSerializableExtra("museum");

        mTitle = (TextView) findViewById(R.id.mTitle);
        mType = (TextView) findViewById(R.id.mType);
        mTel = (TextView) findViewById(R.id.mTel);
        mAddress = (TextView) findViewById(R.id.mAddress);
        mRest = (TextView) findViewById(R.id.mRest);
        mOpen = (TextView) findViewById(R.id.mOpen);
        mClose = (TextView) findViewById(R.id.mClose);

        mTitle.setText(museum.getName());
        mType.setText(museum.getType());
        mTel.setText(museum.getNum());
        mAddress.setText(museum.getAddress());
        mRest.setText(museum.getRestDay());
        mOpen.setText(museum.getOpen());
        mClose.setText(museum.getClose());
        mLat = Float.parseFloat(museum.getLat());
        mLng = Float.parseFloat(museum.getLng());

        // ?????? ?????? ??????
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.museumMap);
        mapFragment.getMapAsync(mapReadyCallback);

        // ?????? ?????? ?????????
        if (checkPermission()) {
            flpClient.requestLocationUpdates(
                    getLocationRequest(),
                    mLocCallback,
                    Looper.getMainLooper());
        }

    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //?????? ??? ????????? ????????????
            if (checkPermission()) {
                mGoogleMap.setMyLocationEnabled(true);
            }

            //?????? ?????? ??????
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(mLat, mLng));
            options.title("????????? ??????");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            //????????? ?????? ?????? ??? ????????? ?????? ?????? ??????
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();
        }
    };

    LocationCallback mLocCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                //double lat = loc.getLatitude();
                //double lng = loc.getLongitude();
                double lat = mLat;
                double lng = mLng;
                //setTvText(String.format("(%.6f, %.6f)", lat, lng));

//                ?????? ?????? ??????
                mLastLocation = loc;
                LatLng currentLoc = new LatLng (lat, lng);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

//                ?????? ?????? ?????? ??????
                centerMarker.setPosition(currentLoc);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        flpClient.removeLocationUpdates(mLocCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "???????????? ?????????", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // ????????? ?????? ?????? ????????? ??????
        } else {
            // ?????? ??????
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    /* ???????????? ?????? ?????? */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_map:
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
