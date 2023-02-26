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
        // Places 초기화 및 클라이언트 생성
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        // 현재 위치 띄우기
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
                /*현재 장소의 place_id 를 각각의 마커에 보관*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            //현재 내 위치로 돌아가게
            if (checkPermission()) {
                mGoogleMap.setMyLocationEnabled(true);
            }

            //마커 정보 지정
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(R.string.init_lat, R.string.init_lng));
            options.title("현재위치");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            //지도에 마커 추가 후 추가한 마커 정보 기록
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

//                지도 위치 이동
                mLastLocation = loc;
                LatLng currentLoc = new LatLng (lat, lng);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14));

//                지도 마커 위치 이동
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
                if (editText.getText().toString().equals("박물관")) {
                    searchStart(mLat, mLng,
                            5000, PlaceTypes.MUSEUM);
                }
                break;
            case R.id.btnMemo:  //메모장
                Intent memoIntent = new Intent(MainActivity.this, MemoActivity.class);
                startActivity(memoIntent);
                break;
            case R.id.btnMuseum:    //박물관 검색
                Intent museumIntent = new Intent(MainActivity.this, MuseumActivity.class);
                startActivity(museumIntent);
                break;
        }
    }

    /*입력된 유형의 주변 정보를 검색
     * PlaceBasicManager 를 사용해 type 의 정보로 PlaceBasic 을 사용하여 현재위치 주변의 관심장소 확인 */
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
                    Toast.makeText(this, "위치권한 획득 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치권한 미획득", Toast.LENGTH_SHORT).show();
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
            // 권한이 있을 경우 수행할 동작
        } else {
            // 권한 요청
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}