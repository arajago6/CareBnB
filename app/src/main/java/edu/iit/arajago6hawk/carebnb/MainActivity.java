package edu.iit.arajago6hawk.carebnb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button offerPlace, seekPlace, offerRelief, seekRelief;
    TextView offerDesc, seekDesc, salutation;
    Boolean isOfferActive = false;
    Boolean isSeekActive = false;
    LocationManager mLocationManager;
    Location location;
    private Bitmap profilePic;
    private ImageView userProfilePic;
    private NavigationView navigationView;
    private View headerView;
    private TextView userName;
    private Boolean exit = false;

    public void displayButtonOffer(View view){
        offerPlace.setVisibility(View.VISIBLE);
        offerRelief.setVisibility(View.VISIBLE);
        offerDesc.setVisibility(View.VISIBLE);
        isOfferActive = true;
    }

    public void displayButtonSeek(View view){
        seekPlace.setVisibility(View.VISIBLE);
        seekRelief.setVisibility(View.VISIBLE);
        seekDesc.setVisibility(View.VISIBLE);
        isSeekActive = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (LoginActivity.newAccessToken == null) {
            userName.setText("Care BnB");
            userProfilePic.setImageBitmap(null);
            profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.location);
            userProfilePic.setImageBitmap(profilePic);
            headerView.refreshDrawableState();
            salutation.setText("Hello there...");
        }

    }

    // Routine to get user location.
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            catch(SecurityException e){

            }
        }
        return bestLocation;
    }

    public void takeToSmallMapsPage(String type){
        if (location != null) {
            Intent main = new Intent(getApplicationContext(), MapsActivity.class);
            Bundle b = new Bundle();
            b.putDouble("lat", location.getLatitude());
            b.putDouble("lng", location.getLongitude());
            b.putString("type",type);
            main.putExtras(b);
            startActivity(main);
        } else {
            String cbMsg = "Sorry! There was some issue in getting your current location!";
            String htmlString = " <font color=\"#0099cc\"><b><i>LOCATION ERROR</font></i></b><br/>" + cbMsg;
            Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        offerPlace = (Button) findViewById(R.id.placeButtonOffer);
        seekPlace = (Button) findViewById(R.id.placeButtonSeek);
        offerRelief = (Button) findViewById(R.id.reliefButtonOffer);
        seekRelief = (Button) findViewById(R.id.reliefButtonSeek);
        offerDesc = (TextView) findViewById(R.id.offerDesc);
        seekDesc = (TextView) findViewById(R.id.seekDesc);
        salutation = (TextView) findViewById(R.id.salutation);
        location = getLastKnownLocation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Hey, have you tried the new CareBnB app?!\nTry today at play.google.com!";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        updateNavHeader(LoginActivity.userName);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        offerPlace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takeToSmallMapsPage("place");
            }
        });

        offerRelief.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takeToSmallMapsPage("relief");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isOfferActive==true){
            offerPlace.setVisibility(View.GONE);
            offerRelief.setVisibility(View.GONE);
            offerDesc.setVisibility(View.GONE);
            isOfferActive = false;
        }
        else if(isSeekActive==true){
            seekPlace.setVisibility(View.GONE);
            seekRelief.setVisibility(View.GONE);
            seekDesc.setVisibility(View.GONE);
            isSeekActive = false;
        }
        else {
            try {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    if (exit) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        String cbMsg = "Press Back again to Exit.";
                        String htmlString = " <font color=\"#27AD80\"><b><i>LEAVING?!</font></i></b><br/>" + cbMsg;
                        Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                        exit = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exit = false;
                            }
                        }, 3 * 1000);

                    }
                }
            }
            catch (Exception e){

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_access){
            finishLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Get facebook profile pic
    private class getProfilePic extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL imageURL = new URL(urls[0]);
                profilePic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                return null;

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Update UI
            userProfilePic.setImageBitmap(profilePic);
        }
    }

    // Update navigation drawer header on login and log out
    public void updateNavHeader(String name){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem nav_access = menu.findItem(R.id.nav_access);
        if (LoginActivity.newAccessToken == null){
            nav_access.setTitle("Log in");
            nav_access.setIcon(R.drawable.ic_menu_login);

        }

        headerView = navigationView.getHeaderView(0);
        userName = (TextView) headerView.findViewById(R.id.nav_name);
        userProfilePic = (ImageView) headerView.findViewById(R.id.nav_image);

        if (name != ""){
            userName.setText(name);
            new getProfilePic().execute("https://graph.facebook.com/"+ LoginActivity.userId +"/picture?type=normal");
            try{
                salutation.setText("Hello "+name.split("\\s+")[0]+"...");
            } catch(Exception e){
                salutation.setText("Hello "+name+"...");
            }
        }
    }

    public boolean finishLogout(){
        if (LoginActivity.newAccessToken != null) {
            LoginManager.getInstance().logOut();
            LoginActivity.newAccessToken = null;
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else{
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            return true;
        }
    }
}
