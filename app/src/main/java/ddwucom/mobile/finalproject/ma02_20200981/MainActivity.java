package ddwucom.mobile.finalproject.ma02_20200981;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;

public class MainActivity extends AppCompatActivity {
    final int REQ_PERMISSION_CODE = 100;

    FusedLocationProviderClient flpClient;
    LatLng currentLoc;
    Location mLastLocation;
    private GoogleMap mGoogleMap;
    private PlaceBasicManager placeBasicManager;
    private PlacesClient placesClient;
    private Marker centerMarker;
    private LocationManager locationManager;
    private EditText editText;
    Double mLat;
    Double mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        placeBasicManager = new PlaceBasicManager(getString(R.string.api_key));
        placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);
        // Places ????????? ??? ??????????????? ??????
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        // ?????? ?????? ?????????
        if (checkPermission()) {
            flpClient.requestLocationUpdates(
                    getLocationRequest(),
                    mLocCallback,
                    Looper.getMainLooper());
        }
    }

    OnPlaceBasicResult onPlaceBasicResult = new OnPlaceBasicResult() {
        @Override
        public void onPlaceBasicResult(List<ddwu.mobile.place.placebasic.pojo.PlaceBasic> list) {
            for (PlaceBasic place : list) {
                MarkerOptions options = new MarkerOptions()
                        .title(place.getName())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                Marker marker = mGoogleMap.addMarker(options);
                /*?????? ????????? place_id ??? ????????? ????????? ??????*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

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
            options.position(new LatLng(R.string.init_lat, R.string.init_lng));
            options.title("????????????");
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
                double lat = loc.getLatitude();
                mLat = lat;
                double lng = loc.getLongitude();
                mLng = lng;
                //setTvText(String.format("(%.6f, %.6f)", lat, lng));

//                ?????? ?????? ??????
                mLastLocation = loc;
                LatLng currentLoc = new LatLng (lat, lng);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14));

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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShow:
                if (editText.getText().toString().equals("?????????")) {
                    searchStart(mLat, mLng,
                            5000, PlaceTypes.MUSEUM);
                }
                break;
            case R.id.btnMemo:  //?????????
                Intent memoIntent = new Intent(MainActivity.this, MemoActivity.class);
                startActivity(memoIntent);
                break;
            case R.id.btnMuseum:    //????????? ??????
                Intent museumIntent = new Intent(MainActivity.this, MuseumActivity.class);
                startActivity(museumIntent);
                break;
        }
    }

    /*????????? ????????? ?????? ????????? ??????
     * PlaceBasicManager ??? ????????? type ??? ????????? PlaceBasic ??? ???????????? ???????????? ????????? ???????????? ?????? */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);
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
}