package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Supertel on 10/5/16.
 */
public class Fr_Login extends BaseFragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private Profile currentProfile;
    private TextView texto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        if (v!=null){

            texto = (TextView)v.findViewById(R.id.usuario);

            loginButton = (LoginButton) v.findViewById(R.id.btn_facebook);
            loginButton.setReadPermissions("user_friends");
            // If using in a fragment
            loginButton.setFragment(this);
            // Other app specific specialization
            loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email"));


        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken()!=null){
            texto.setText("Hola, "+getCurrentNombre());
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken==null){
                    //Logged Out
                    texto.setText("Inicia Sesión");
                }
            }
        };
        /*
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                currentProfile = newProfile;
            }
        };*/
        accessTokenTracker.startTracking();
//        profileTracker.startTracking();
        // Callback registration
        loginButton.registerCallback(callbackManager, callback);
    }


    private void saveDataAndClose() {
        setCurrentNombre(currentProfile.getFirstName() + " " + currentProfile.getLastName());
        texto.setText("Hola, "+getCurrentNombre());
        setCurrentPictureUri(currentProfile.getProfilePictureUri(200, 200).toString());

    }


    @Override
    public void onStop() {
        super.onStop();
        //Facebook login
        if (accessTokenTracker!=null) {
            accessTokenTracker.stopTracking();
        }
        if (profileTracker!=null) {
            profileTracker.stopTracking();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            Log.i("FB-LOGIN", "Success");Log.i("FB-LOGIN", "Success");
            Profile profile = Profile.getCurrentProfile();
            if (profile==null){

                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        currentProfile = profile2;
                        setCurrentToken(loginResult.getAccessToken().getToken());
                        getEmail(loginResult.getAccessToken());
                        saveDataAndClose();
                        profileTracker.stopTracking();
                    }
                };
                profileTracker.startTracking();

            } else {
                currentProfile = profile;
                getEmail(loginResult.getAccessToken());
                setCurrentToken(loginResult.getAccessToken().getToken());
                saveDataAndClose();
            }
        }
        @Override
        public void onCancel() {        }
        @Override
        public void onError(FacebookException e) {      }
    };

    private void getEmail(AccessToken currentToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.v("LoginActivity Response ", response.toString());

                        try {
                            setCurrentEmail(object.getString("email"));
                            texto.setText(getCurrentNombre()+";"+getCurrentEmail());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }


}
