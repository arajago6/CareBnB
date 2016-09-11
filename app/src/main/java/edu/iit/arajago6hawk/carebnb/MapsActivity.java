package edu.iit.arajago6hawk.carebnb;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    TextView txtLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txtLoc = (TextView) findViewById(R.id.txtLoc);

        try {
            Bundle b = getIntent().getExtras();
            latitude = b.getDouble("lat");
            longitude = b.getDouble("lng");
            txtLoc.setText(Double.toString(latitude)+", "+Double.toString(longitude));
        } catch (Exception e) {
        }
        // Obtain the SupportMapFragment and get notified when the map is rseady to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        onLocationChanged(latitude,longitude,12);
    }

    public void onLocationChanged (double latitude, double longitude, int zoom){
        try {
            LatLng point = new LatLng(latitude, longitude);
            Marker m = mMap.addMarker(new MarkerOptions().position(point).title("You are here"));
            m.showInfoWindow();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point)      // Sets the center of the map to Mountain View
                    .zoom(zoom)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        catch(Exception e){

        }
    }
}