package ec.medinamobile.greenmapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.ShareActionProvider;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Supertel on 10/5/16.
 */
public class Fr_Nuevo_Arbol extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener  {


    private Location currentLocation;
    private SupportMapFragment map;
    private GoogleMap mapa;
    private boolean isFirstLocation=true;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private boolean isRequestingLocationUpdates=false;
    private float currentZoom=16;
    private ImageButton imagen;
    private String imagenPath="default";
    private String imagenName = "default";
    private Bitmap imageBitmap;
    private Button btn_enviar;
    private EditText tipo;
    private EditText descripcion;
    private CheckBox patrimonial;
    private Address address;
    private View snackbar_container;
    private ArrayList<Arbol> cercanos;
    private ShareActionProvider mShareActionProvider;
    private MenuItem shareItem;
    private ProgressBar progress_bar;
    private View v;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_nuevo_arbol, container, false);
        if (v!=null){
            imagen = (ImageButton)v.findViewById(R.id.arbol_imagen);
            btn_enviar = (Button)v.findViewById(R.id.btn_enviar);
            tipo = (EditText)v.findViewById(R.id.arbol_tipo);
            descripcion = (EditText)v.findViewById(R.id.arbol_descripcion);
            patrimonial = (CheckBox)v.findViewById(R.id.arbol_patrimonial);
            snackbar_container = v.findViewById(R.id.arbol_snackbar);
            progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isInternetAvailable()){

            checkPlayServices();
            map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.arbol_map);
            if (map!=null)
                map.getMapAsync(this);
        } else {
            showMessage("Debe estar conectado a Internet");
        }
        currentLocation = new Location("");
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        imagenPath = photoFile.getAbsolutePath();
                        imagenName = photoFile.getName();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, 900);
                    }
                }
            }
        });
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentNombre().equals("") || getCurrentNombre().equals("default")) {
                    showMessage("Primero debe iniciar sesión en Facebook");
                } else {
                    checkArbol();
                }
            }

        });

        if (savedInstanceState!=null) {
            imageBitmap = (Bitmap) savedInstanceState.getParcelable("image");
            imagen.setImageBitmap(imageBitmap);
            currentLocation = savedInstanceState.getParcelable("ubicacion");
        }

        //context = getActivity().getApplicationContext();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.navigation, menu);
        shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        // Call to update the share intent

    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId==R.id.action_cercanos){
            if (address!=null){
                new getArbolesCercanos().execute(currentLocation);
            } else{
                isFirstLocation = true;
                getArbol();
            }
        } else if (itemId==R.id.action_share){
            showMessage("Share");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Estoy apadrinando un arbol.");
            sendIntent.setType("text/plain");
            //startActivity(sendIntent);
            setShareIntent(sendIntent);
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void checkArbol(){
        switch (getArbol().isComplete()){

            case Arbol.ARBOL_COMPLETE:{
                new enviarArbol().execute(getArbol());
                break;
            }
            case Arbol.ARBOL_MISSING_CIUDAD:
                showMessage("No se ha podido obtener la ciudad donde se encuentra el árbol.");
                break;
            case Arbol.ARBOL_MISSING_DESCRIPCION: {
                showMessage("Por favor agregue una descripción");
                break;
            }
            case Arbol.ARBOL_MISSING_LONGITUD:
            case Arbol.ARBOL_MISSING_LATITUD: {
                showMessage("No se ha podido obtener la ubicación del árbol");
                break;
            }
            case Arbol.ARBOL_MISSING_PROVINCIA: {
                showMessage("No se ha podido obtener la provincia donde se encuentra el árbol.");
                break;
            }
            case Arbol.ARBOL_MISSING_TIPO: {
                showMessage("Por favor agregue el tipo de árbol");
                break;
            }
            case Arbol.ARBOL_MISSING_IMAGE: {
                showMessage("Por favor tome una fotografía del árbol");
                break;
            }

        }
    }

    private Arbol getArbol() {
        Arbol a = null;
        if (currentLocation != null) {
            a = new Arbol();
            a.setIdarbol(0);
            a.setLongitud(currentLocation.getLongitude());
            a.setLatitud(currentLocation.getLatitude());
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0);
                    try{
                        a.setProvincia(address.getAdminArea().toUpperCase());
                    } catch (NullPointerException e){

                    }
                    try{
                        a.setCiudad(address.getLocality().toUpperCase());
                    } catch (NullPointerException e){

                    }
                    try{
                        a.setSector(address.getSubLocality().toUpperCase());
                    } catch (NullPointerException e){

                    }
                    try{
                        a.setDireccion(address.getThoroughfare().toUpperCase());
                    }catch (NullPointerException e){

                    }

                    if (isFirstLocation){
                        new getArbolesCercanos().execute(currentLocation);
                        isFirstLocation=false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            a.setTipo(tipo.getText().toString());
            a.setDescripcion(descripcion.getText().toString());
            a.setImagen_path(imagenPath);
            a.setImagen(imagenName);
        }
        return a;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File imgFile = new  File(imagenPath);
        if(imgFile.exists()){
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imagen.setImageBitmap(imageBitmap);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageBitmap!=null) {
            outState.putParcelable("imagen", imageBitmap);
            outState.putParcelable("ubicacion", currentLocation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            mapa = googleMap;
            mapa.setMyLocationEnabled(true);
            mapa.setOnCameraChangeListener(this);
            mapa.setOnMapClickListener(this);
            //mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setZoomGesturesEnabled(true);
            mapa.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e){
            // no tiene permiso de usar locacion

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
        mapa.clear();
        mapa.addMarker(new MarkerOptions().position(latLng)
                .draggable(true));
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
        currentLocation.setLatitude(latLng.latitude);
        currentLocation.setLongitude(latLng.longitude);

    }

    private void checkPlayServices()
    {
        if (isGooglePlayServicesAvailable())
        {
            buildGoogleApiClient();
        }
        else
        {
            showMessage("GooglePlayServices no disponible");
            //Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        /*if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())){
            showMessage("Por favor active el GPS de su dispositivo");
        }*/
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
            //isFirstLocation = false;
            getArbol(); //se llama a get Arbol para obtener el address de esta locacion, solo por primera vez
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        boolean result = false;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(context);
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
        SupportMapFragment fr = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.arbol_map);
        if (fr != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fr).commit();
        }
    }

    /*private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }*/


    private class enviarArbol extends AsyncTask<Arbol, Void, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_bar.setVisibility(View.VISIBLE);
            v.setAlpha(0.5f);
            //v.setEnabled(false);
        }

        @Override
        protected Long doInBackground(Arbol... arboles) {
            long out = -1;
            try {
                Arbol a = arboles[0];

                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopeInsertArbol(a.toJsonString(), getUserJson(), getEncodedImage());
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(30000);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null) {
                    //Hacer JsonParsing y guardar en  Base de Datos el idarbol y idusuario

                    // Getting JSON Array node
                    JSONObject obj = new JSONObject(resultXML.toString());
                    JSONObject response = obj.getJSONObject("response");
                    int idusuario = response.getInt("idusuario");
                    long idarbol = response.getLong("idarbol");
                    String filename = response.getString("filename");
                    File f = new File(imagenPath);
                    File tmp = File.createTempFile(filename, ".jpg", Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES));
                    f.renameTo(tmp);
                    if (idusuario > 0) {
                        setCurrentUsuarioId(idusuario);
                        //out = idusuario;
                    }
                    DBHelper dbHelper = new DBHelper(getActivity());
                    a.setIdarbol(idarbol);
                    a.setImagen(filename);
                    out = dbHelper.insertArbol(a);
                    dbHelper.insertArbolUsuario(idarbol,idusuario);
                    dbHelper.close();

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return out;
        }

        @Override
        protected void onPostExecute(Long integer) {
            super.onPostExecute(integer);
            if (integer > 0) {
                //Se agrego arbol correctamente
                showMessage("El árbol fue enviado correctamente");
                btn_enviar.setEnabled(false);
                tipo.setEnabled(false);
                descripcion.setEnabled(false);
                shareItem.setVisible(true);
            } else {
                //Hubo un error
                showMessage("Hubo un error al enviar el árbol. Intente nuevamente");
            }
            progress_bar.setVisibility(View.GONE);
            v.setAlpha(1.0f);
            //v.setEnabled(true);
        }
    }

    private String getEncodedImage() {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

    }

    private class getArbolesCercanos extends AsyncTask<Location, Void, ArrayList<Arbol>>{

        @Override
        protected ArrayList<Arbol> doInBackground(Location... locations) {
            ArrayList<Arbol> result = new ArrayList<>();
            Location location = locations[0];
            if (location!=null && location.getLatitude()!=900 && location.getLongitude()!=900 && address!=null){
                DBHelper dbHelper = new DBHelper(context);
                ArrayList<Arbol> cercanos = dbHelper.getArbolesCercanos(address.getAdminArea(), address.getLocality());
                dbHelper.close();
                if (cercanos!=null && cercanos.size()>0){
                    for (Arbol a:cercanos){
                        float distance = location.distanceTo(a.getLocation());
                        if (distance<100){
                            a.setDistancia(distance);
                            result.add(a);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(final ArrayList<Arbol> arbols) {
            super.onPostExecute(arbols);
            if (arbols.size()>0){
                cercanos = arbols;
                String msg=arbols.size()==1?"Hay un árbol cercano a tu posición":"Hay "+arbols.size()+" árboles cercanos a tu posición";
                snackbar_container.setVisibility(View.VISIBLE);
                Snackbar.make(snackbar_container, msg, Snackbar.LENGTH_LONG)
                        .setAction("Ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               openArbolesCercanosActivity();
                            }
                        }).show();
            }
        }
    }

    private void openArbolesCercanosActivity() {
        if (cercanos!=null && cercanos.size()>0){
            Intent i = new Intent(context, ArbolesCercanosActivity.class);
            i.putParcelableArrayListExtra("cercanos",cercanos);
            startActivity(i);
        } else {
            showMessage("No hay árboles apadrinados cerca de su posición");
        }

    }

    protected void showMessage(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
