package com.devpact.facebooklogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        // setting up callbacks
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        // register callbacks
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        info.setText(
                                "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                        );
                        makeToast(loginResult.getAccessToken().toString());
                    }

                    @Override
                    public void onCancel() {
                        String cancelMessage = "Login attempt canceled.";
                        info.setText(cancelMessage);
                        makeToast(cancelMessage);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        String errorMessage = "Login attempt failed.";
                        info.setText(errorMessage);
                        makeToast(errorMessage);
                    }
                }
        );

        // initialize textbox
        info = (TextView) findViewById(R.id.info);

        if(isLoggedIn())
            info.setText("User ID: "
                    + AccessToken.getCurrentAccessToken().getUserId());
    }

    /**
     * Check if user is logged in to Facebook and the AccessToken is active
     * @return
     */
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return (accessToken != null) && (!accessToken.isExpired());
    }

    /**
     * Forward data to the callback manager
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Makes a long toast
     * @param text
     */
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
