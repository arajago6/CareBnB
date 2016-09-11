package edu.iit.arajago6hawk.carebnb;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

public class LargeMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String type="";
    TextView txtLoc, txtPreLoc;
    LatLngBounds.Builder b;
    ArrayList<LatLng> listOfPoints;
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
            Geocoder geoCoder = new Geocoder(getApplicationContext());
            List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            String addressText = String.format("%s, %s, %s", bestMatch.getMaxAddressLineIndex() > 0 ? bestMatch.getAddressLine(0) : "", bestMatch.getLocality(), bestMatch.getCountryName());
            txtLoc.setText(addressText);
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
    public void onMapReady(GoogleMap map) {
        mMap = map;
        b = new LatLngBounds.Builder();
        ParseQuery things = new ParseQuery("Things");
        if(type.equals("place")){
            things.whereEqualTo("name", "place");
        }
        else{
            things.whereNotEqualTo("name", "place");
        }
        things.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listOfThings, ParseException e) {
                if (e == null) {
                    listOfPoints = new ArrayList<LatLng>();
                    Log.d("CB",Integer.toString(listOfThings.size()));
                    for (ParseObject thing : listOfThings) {
                        String name = thing.get("name").toString();
                        String quantity = thing.get("quantity").toString();
                        // This does not require a network access.
                        LatLng pt = new LatLng( thing.getParseGeoPoint("location").getLatitude(),
                                thing.getParseGeoPoint("location").getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(pt)
                                .title(name+" / "+quantity)).setTag(0);
                        b.include(pt);

                    }
                    LatLngBounds bounds = b.build();
                    //Change the padding as per needed
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    mMap.animateCamera(cu);
                }
            }
        });
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
