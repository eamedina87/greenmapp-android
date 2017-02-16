package ec.medinamobile.greenmapp;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Supertel on 10/5/16.
 */
public class LoginActivity extends BaseActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private Profile currentProfile;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
        /*FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btn_facebook);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email"));

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                currentProfile = newProfile;
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("FB-LOGIN", "Success");
                Profile profile = Profile.getCurrentProfile();
                if (profile!=null){
                    currentProfile = profile;
                    setCurrentToken(loginResult.getAccessToken().getToken());
                    saveDataAndClose();
                }
            }

            @Override
            public void onCancel() {
                // App code
                Log.i("FB-LOGIN", "Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("FB-LOGIN", "Error");
            }
        });*/
    }

    private void saveDataAndClose() {
        setCurrentNombre(currentProfile.getFirstName());
        setCurrentPictureUri(currentProfile.getProfilePictureUri(200, 200).toString());
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
            //Facebook login
        if (accessTokenTracker!=null) {
            accessTokenTracker.stopTracking();
        }
        if (profileTracker!=null) {
            profileTracker.stopTracking();
        }

    }


}
