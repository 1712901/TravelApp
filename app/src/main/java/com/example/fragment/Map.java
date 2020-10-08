package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap;
    private View mMapView;
    private ImageButton btnAddLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int Permission_Request_code=1;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleApiClient mGoogleApiClient;

    Location mLocation = new Location(LocationManager.GPS_PROVIDER);
    private double mLatitude,mLongitude;
    private String descrip;
    final int RESULTCODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btnAddLocation=(ImageButton)findViewById(R.id.btnAddLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();
        // Hàm chạy tiếp theo là onMapReady();
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_map_api_key));
        }


        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS))
                            //.setTypeFilter(TypeFilter.REGIONS)
                            .setCountry("VN");
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(13)
                        .build();;      // Sets the center of the map to Mountain View
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                MarkerOptions marker = new MarkerOptions().position(place.getLatLng()).title(place.getAddress());
                mMap.clear();
                mMap.addMarker(marker);
                // save data;

                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                descrip=getDescrip(location);
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(Map.this, "An error occurred: " + status,Toast.LENGTH_SHORT).show();
            }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.putExtra("Longitude",mLocation.getLongitude());
                intent.putExtra("Latitude",mLocation.getLatitude());
                intent.putExtra("Descrip",descrip);
                setResult(RESULTCODE,intent);
                finish();
            }
        });
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(Map.this);
    }
    private void checkPermission() {
        //kiểm tra quyền vị trí
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permission_Request_code);
            return;
        }
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionsGranted = true;
            //khởi tạo bản đồ
            initMap();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Permission_Request_code);
            return;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

         mMap = googleMap;
        if (mLocationPermissionsGranted) {//kiểm tr quyền
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMarkerClickListener(this);

            // set position of  button my current position
            if (mMapView != null &&
                    mMapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 30);
            }
            // Lắng nghe sự kiện click trên bản đồ và thêm marker trên bản đồ
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    // save Data
                    mLocation.setLongitude(point.longitude);
                    mLocation.setLatitude(point.latitude);
                    descrip=getDescrip(mLocation);

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(descrip);
                    mMap.clear();
                    mMap.addMarker(marker);
                    System.out.println(point.latitude+"---"+ point.longitude);

                }
            });
            //init();
        }
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    private void getDeviceLocation() {
        if(mLocationPermissionsGranted){
            final Task<Location> task=fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    //save data
                    saveLatLong(location,getDescrip(location));
//                    Location currentLocation = (Location) task.getResult();
                    /////////////////////////////
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));

                }else {
                    Toast.makeText(Map.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Permission_Request_code:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    checkPermission();
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        marker.remove();
        return false;
    }
    public String getDescrip(Location location){
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        String s="";
        try {
            addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addresses.size()>0){
                s=addresses.get(0).getAddressLine(0);
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void saveLatLong(Location location,String descrip){
        this.mLocation=location;
        this.descrip=descrip;
    }
}