package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView texto;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_mapa));
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_mapa));
        navigationView.setCheckedItem(R.id.nav_mapa);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_nuevo));
                navigationView.setCheckedItem(R.id.nav_nuevo);
            }
        });

        //Se setea el boton de ingreso por medio de facebook

        View v = navigationView.getHeaderView(0);
        texto = ((TextView)v.findViewById(R.id.textView));
        loginButton = ((LoginButton)v.findViewById(R.id.btn_facebook));
        //loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email"));
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        if (AccessToken.getCurrentAccessToken()!=null){
            setearNombre();

        } else {
            clearUsuario();
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken==null){
                    //Logged Out
                    clearUsuario();
                }
            }
        };
        accessTokenTracker.startTracking();
        // Callback registration
        loginButton.registerCallback(callbackManager, callback);

/*
        if (!isDBCreated()){
            DBHelper dbh = new DBHelper(NavigationActivity.this);
            dbh.getWritableDatabase();
            dbh.close();
            setDBCreated();
        }*/

        if (getCurrentNombre().equals(""))
            drawer.openDrawer(Gravity.LEFT);

    }

    private void clearUsuario() {
        setCurrentUsuarioId(-1);
        setCurrentNombre("");
        setCurrentPictureUri("");
        setCurrentToken("");
        setCurrentCedula("");
        setCurrentEmail("");
        setCurrentTelefono("");
        setearNombre();
    }

    private void setearNombre() {
        texto.setText((getCurrentNombre().equals("default")||getCurrentNombre().equals("")) ? "Inicie Sesión" : "Hola, " + getCurrentNombre());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nuevo) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.content_layout, new Fr_Nuevo_Arbol());
            ft.commit();
            setTitle("Apadrinar Árbol");
            if (fab!=null) {
                fab.setVisibility(View.GONE);
            }

        } else if (id == R.id.nav_mapa) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.content_layout, new Fr_Mapa());
            ft.commit();
            setTitle("Mapa");
            if (fab!=null) {
                fab.setVisibility(View.VISIBLE);
            }
        }  else if (id == R.id.nav_estadisticas){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.content_layout, new Fr_Estadisticas());
            ft.commit();
            setTitle("Estadísticas");
            if (fab!=null) {
                fab.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.nav_arboles){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.content_layout, new Fr_Arboles());
            ft.commit();
            setTitle("Mis Árboles");
            if (fab!=null) {
                fab.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.nav_secret){
            showMessage("Desarrollado por Erick Medina. Medina Mobile. 2016");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    private ProfileTracker profileTracker;

    private Profile currentProfile;
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

    private void saveDataAndClose() {
        setCurrentNombre(currentProfile.getFirstName() + " " + currentProfile.getLastName());
        texto.setText("Hola, "+getCurrentNombre());
        setCurrentPictureUri(currentProfile.getProfilePictureUri(200, 200).toString());
    }


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
