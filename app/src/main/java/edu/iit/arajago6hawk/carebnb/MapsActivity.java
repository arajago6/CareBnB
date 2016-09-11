package edu.iit.arajago6hawk.carebnb;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String type = "";
    TextView txtLoc, firstText, startText;
    Spinner spinner;
    EditText editText;
    Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        firstText = (TextView) findViewById(R.id.firstText);
        startText = (TextView) findViewById(R.id.startText);
        spinner = (Spinner) findViewById(R.id.spinner);
        editText = (EditText) findViewById(R.id.editText);
        submitButton = (Button) findViewById(R.id.submitButton);

        try {
            Bundle b = getIntent().getExtras();
            latitude = b.getDouble("lat");
            longitude = b.getDouble("lng");
            type = b.getString("type");
            Geocoder geoCoder = new Geocoder(getApplicationContext());
            List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            String addressText = String.format("%s, %s, %s", bestMatch.getMaxAddressLineIndex() > 0 ? bestMatch.getAddressLine(0) : "", bestMatch.getLocality(), bestMatch.getCountryName());
            txtLoc.setText(addressText);
        } catch (Exception e) {
        }
        // Obtain the SupportMapFragment and get notified when the map is rseady to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(type.equals("place")){
            firstText.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        } else if (type.equals("relief")){
            firstText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            startText.setText("How many people can your current relief material serve?!");
        }

        //OnCLick Listener Event
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseObject Things = new ParseObject("Things");
                ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
                Things.put("location", point);
                if(type.equals("place")){
                    Things.put("name", type);
                }
                else{
                    Things.put("name", spinner.getSelectedItem());
                }
                Things.put("quantity", Integer.parseInt(editText.getText().toString()));
                Things.saveInBackground();
            }
        });

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
