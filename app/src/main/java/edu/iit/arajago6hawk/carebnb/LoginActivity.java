package edu.iit.arajago6hawk.carebnb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends Activity {

    private Button loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    static AccessToken newAccessToken;
    private Boolean exit = false;
    private ProgressDialog progressDialog;

    static String userName = "";
    static String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView txt = (TextView) findViewById(R.id.sl_textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.otf");
        txt.setTypeface(font);

        FacebookSdk.sdkInitialize(getApplicationContext());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
                LoginActivity.newAccessToken = newAccessToken;
            }
        };
        updateWithToken(AccessToken.getCurrentAccessToken());

        callbackManager = CallbackManager.Factory.create();
        loginButton = (Button) findViewById(R.id.login_button);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Getting your data...");
                progressDialog.show();
                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        try {
                            userName = user.optString("name");
                            userId = user.optString("id");
                        }catch (Exception e){

                        }


                        // Create the ParseUser
                        ParseUser parseUser = new ParseUser();
                        // Set core properties
                        parseUser.setUsername(userName);
                        parseUser.setPassword(accessToken.toString());
                        parseUser.setEmail("email@example.com");
                        // Invoke signUpInBackground
                        parseUser.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // TODO: 9/11/2016  Success routine
                                    // Hooray! Let them use the app now.
                                } else {
                                    // TODO: 9/11/2016  Failure routine
                                    // Sign up didn't succeed. Look at the ParseException
                                    // to figure out what went wrong
                                }
                            }
                        });

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }).executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });

        final Button button2 = (Button) findViewById(R.id.skip_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
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
        catch(Exception e){}
    }

    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Processing your data...");
                    progressDialog.show();
                    GraphRequestAsyncTask request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject user, GraphResponse response) {
                            try {
                                userName = user.optString("name");
                                userId = user.optString("id");
                            }catch (Exception e){

                            }
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).executeAsync();
                }
            }, 2);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                }
            }, 2);
        }
    }
}