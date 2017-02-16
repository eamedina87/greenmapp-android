package ec.medinamobile.greenmapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class Fr_Mapa extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mapa;
    private Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private boolean isRequestingLocationUpdates=false;
    private SupportMapFragment map;
    private float currentZoom=16;
    private boolean isFirstLocation=true;
    private ArrayList<Arbol> arboles;
    private int selectedPolygon=-1;
    private BroadcastReceiver receiver;
    private loadArboles task_load;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        if (v!=null){

        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isInternetAvailable()){
            checkPlayServices();
            //map = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_activity);
            map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
            if (map!=null)
                map.getMapAsync(this);
        } else {
            showMessage("Debe estar conectado a Internet para ver el mapa");
        }

        currentLocation = new Location("");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        if (task_load!=null)
            task_load.cancel(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_mapa, menu);
        // Call to update the share intent

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId==R.id.action_actualizar){
            Intent i = new Intent(getActivity(), LandingActivity.class);
            i.putExtra("tipo", true);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);

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
        try{
            mapa = googleMap;
            mapa.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            mapa.setMyLocationEnabled(true);
            mapa.setOnCameraChangeListener(this);
            //mapa.setOnMapClickListener(this);
            mapa.setOnInfoWindowClickListener(this);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            task_load = new loadArboles();
            task_load.execute();
        } catch (SecurityException e){
            // no tiene permiso de usar locacion

        }
        /*
        if (isDBCreated()){
            loadArboles();
        }*/

    }

    private void checkPlayServices()
    {
        if (isGooglePlayServicesAvailable())
        {
            buildGoogleApiClient();
        }
        else
        {
            Toast.makeText(getActivity(), "GooglePlayServices no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (!mGoogleApiClient.isConnected()){
            showMessage("Por favor active el GPS de su dispositivo");
        }
        createLocationRequest();

    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }



    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            isRequestingLocationUpdates = true;
        } catch (SecurityException e){
            // no tiene permiso para usar locacion

        }

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected() && !isRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (!isRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null || (location.distanceTo(currentLocation)>5)) {
            setCurrentLocation(location);
        }
    }

    private void setCurrentLocation(Location location) {
        this.currentLocation = location;
        if (isFirstLocation){
            LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, currentZoom));
            isFirstLocation = false;
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        boolean result = false;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS != status) {
            if (apiAvailability.isUserResolvableError(status)){
                apiAvailability.getErrorDialog(getActivity(), status,
                        2000).show();
            }
        } else {
            result = true;
        }
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment fr = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (fr != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fr).commit();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //currentZoom = cameraPosition.zoom;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        /*if (msg.getVisibility()==View.VISIBLE){
            msg.setVisibility(View.GONE);
        }*/
       // Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //EN el snippet recibo el id del arbol
        DBHelper dbhelper = new DBHelper(getActivity());
        Arbol a = dbhelper.getArbolById(Long.valueOf(marker.getSnippet()));
        dbhelper.close();
        if (a!=null && a.getIdarbol()>0){
            Intent i = new Intent(getActivity(), ArbolDetalleActivity.class);
            i.putExtra("arbol",a);
            startActivity(i);
        }
    }

    private class loadArboles extends AsyncTask<Void, Void, ArrayList<Arbol>>{

        @Override
        protected ArrayList<Arbol> doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(getActivity());
            ArrayList<Arbol> arboles = dbHelper.getAllArboles();
            dbHelper.close();
            //Log.i("Arboles_Siza", ""+arboles.size());
            return arboles;
        }

        @Override
        protected void onPostExecute(ArrayList<Arbol> arbols) {
            super.onPostExecute(arbols);
            ClusterManager<ArbolCluster> clusterProvincia = new ClusterManager<ArbolCluster>(getActivity().getApplicationContext(), mapa);
            for (Arbol a:arbols){
                //Agrupar en cluster
                //Log.i("Arbol", a.toString());
                ArbolCluster ci = new ArbolCluster(a);
                clusterProvincia.addItem(ci);
            }
            clusterProvincia.setRenderer(new ArbolClusterRenderer(getActivity().getApplicationContext(), mapa, clusterProvincia));
            mapa.setOnCameraIdleListener(clusterProvincia);


        }
    }

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;
        private Arbol a;

        CustomInfoWindowAdapter() {
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.info_window_arbol, null);
            mContents = getActivity().getLayoutInflater().inflate(R.layout.info_window_arbol, null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;

            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            /*if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
            */
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.arbol_tipo));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            /*String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.arbol_descripcion));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }*/
        }
    }

    protected void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
    }

}