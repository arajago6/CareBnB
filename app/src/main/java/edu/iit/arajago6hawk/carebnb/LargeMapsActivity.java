package edu.iit.arajago6hawk.carebnb;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class LargeMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String type="";
    TextView txtLoc, txtPreLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_maps);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtPreLoc = (TextView) findViewById(R.id.txtPreLoc);
        try {
            Bundle b = getIntent().getExtras();
            latitude = b.getDouble("lat");
            longitude = b.getDouble("lng");
            type = b.getString("type");
            if(type.equals("place")){
                txtPreLoc.setText("Showing places with free accomodation near");
            }
            else if(type.equals("relief")){
                txtPreLoc.setText("Showing places with free relief materials near");
            }
            txtLoc.setText(Double.toString(latitude)+", "+Double.toString(longitude));
        } catch (Exception e) {
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        // Add a marker in Sydney and move the camera; this is a dummy marker
        //LatLng sydney = new LatLng(-34, 151);
        ParseQuery things = new ParseQuery("Things");
        things.whereEqualTo("name", "place");
        things.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listOfThings, ParseException e) {
                if (e == null) {
                    for (ParseObject thing : listOfThings) {
                        // This does not require a network access.
                        String name = thing.getParseObject("name").toString();
                        LatLng point = new LatLng(thing.getParseGeoPoint("location").getLongitude(),
                                thing.getParseGeoPoint("location").getLatitude());
                        Marker m = mMap.addMarker(new MarkerOptions().position(point).title("You are here"));
                        m.showInfoWindow();
                    }
                } else {
                    //handle the error
                    LatLng sydney = new LatLng(-34, 151);
                }
                /* CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(point)      // Sets the center of the map to Mountain View
                        .zoom(12)                   // TODO: 9/11/2016  Make it generic
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); */
            }
        });

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //onLocationChanged(latitude,longitude,12);
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
