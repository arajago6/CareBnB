package edu.iit.arajago6hawk.carebnb;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by suraj on 9/11/2016.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xPYHXw9v2dLp5XPWxR94F6rOFbgyiZABf2a3IdYy", "bLEMYPfBBkddKMH1GISM6azduznTZN8nMwCymvp5");
    }
}
