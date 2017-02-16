package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Supertel on 13/5/16.
 */
public class ArbolDetalleActivity extends BaseActivity {

    private Arbol arbol;
    String [] padrinos = null;
    private android.support.v7.widget.ShareActionProvider mShareActionProvider;
    private ProgressBar progress_bar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbol);
        arbol = getIntent().getParcelableExtra("arbol");
        progress_bar = (ProgressBar)findViewById(R.id.progress_bar);
        ((TextView)findViewById(R.id.arbol_tipo)).setText(arbol.getTipo());
        ((TextView)findViewById(R.id.arbol_descripcion)).setText(arbol.getDescripcion());
        if (!arbol.getDireccion().equals("") && !arbol.getSector().equals("")){
            ((TextView)findViewById(R.id.arbol_direccion)).setText(arbol.getDireccion() + " - " + arbol.getSector());
        } else if (!arbol.getDireccion().equals("")){
            ((TextView)findViewById(R.id.arbol_direccion)).setText(arbol.getDireccion());
        }

        if (arbol.getPatrimonial()==1)
            (findViewById(R.id.arbol_patrimonial)).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.arbol_ciudad)).setText(arbol.getCiudad() + " - " + arbol.getProvincia());
        //
        (findViewById(R.id.btn_apadrinar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verificar si ha iniciado sesion en Facebook
                if (getCurrentNombre().equals("") || getCurrentNombre().equals("default")){
                    showMessage("Debe iniciar sesión en Facebook para apadrinar un árbol");
                } else {
                    //Iniciar contacto con WS para apadrinar y actualizar lista
                    new apadrinarArbol().execute();
                }
            }
        });

        (findViewById(R.id.btn_denunciar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abrir Denuncia Activity
                Intent i = new Intent(ArbolDetalleActivity.this, DenunciaActivity.class);
                i.putExtra("arbol", arbol);
                startActivity(i);

            }
        });

        try {
            //Debo intentar abrir el archivo
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File f = new File(dir, arbol.getImagen());
            if (f.exists()){
                ((ImageView) findViewById(R.id.arbol_imagen)).setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
            } else {
                new getImageFromServer().execute(arbol);
            }

        } catch (Exception e){

        }
        new getPadrinos().execute();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        item.setVisible(true);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        (menu.findItem(R.id.action_cercanos)).setVisible(false);
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_share){

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Estoy apadrinando un arbol.");
            sendIntent.putExtra(Intent.EXTRA_STREAM, "file:"+arbol.getImagen());
            sendIntent.setType("text/plain");
            //startActivity(sendIntent);
            setShareIntent(sendIntent);
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class getImageFromServer extends AsyncTask<Arbol, Void, Boolean>{

    private Arbol arbol = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Arbol... arboles) {
            Boolean out = false;
            HttpURLConnection connection = null;
            arbol = arboles[0];
            try{
                URL direccion = new URL(WebServiceHelper.DIRECTORY_IMAGES+arbol.getImagen());
                connection = (HttpURLConnection) direccion.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();

                if (connection.getResponseCode()==200){
                    File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file = new File(sdcard, arbol.getImagen());
                    InputStream in = connection.getInputStream();
                    FileOutputStream fileOutput = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ( (bufferLength = in.read(buffer)) > 0 ) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                    out = true;
                }
                connection.disconnect();
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                try {
                    connection.disconnect();
                } catch (Exception e){

                }
            }

            return out;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean==true){
                try {
                    //Debo intentar abrir el archivo
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File f = new File(dir, arbol.getImagen());
                    if (f.exists()){
                        ((ImageView) findViewById(R.id.arbol_imagen)).setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                    } else {
                        new getImageFromServer().execute(arbol);
                    }

                } catch (Exception e){

                }
            }
            progress_bar.setVisibility(View.GONE);

        }
    }

    private class getPadrinos extends AsyncTask<Void, Void, String[]>{



        @Override
        protected String[] doInBackground(Void... voids) {

            try {
                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopePadrinos(arbol.getIdarbol());
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(-1);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null){
                    //Hacer JsonParsing y mostrar en Lista

                    JSONArray mainArray = new JSONArray(resultXML.toString());

                    if (mainArray!=null && mainArray.length()>0) {
                        padrinos = new String[mainArray.length()];
                        for (int i = 0; i < mainArray.length(); i++) {
                            JSONObject a = mainArray.getJSONObject(i);
                            JSONObject ar = a.getJSONObject("padrino");
                            padrinos[i]=ar.getString("nombre");
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return padrinos;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (result!=null){
                (findViewById(R.id.arbol_padrinos_tv)).setVisibility(View.VISIBLE);
                (findViewById(R.id.arbol_padrinos_sv)).setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ArbolDetalleActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, result);
                for (String s:result){
                    if (s.equals(getCurrentNombre())){
                        (findViewById(R.id.btn_apadrinar)).setVisibility(View.GONE);
                    }
                }
                ((TextView)findViewById(R.id.arbol_padrinos_tv)).setText("Padrinos ("+result.length+")");
                ((ListView)findViewById(R.id.arbol_padrinos)).setAdapter(adapter);
            }
        }
    }

    private class apadrinarArbol extends AsyncTask<Void, Void, Integer>{


        @Override
        protected Integer doInBackground(Void... voids) {
            int out = -1;
            try {
                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopeArboles(arbol.toJsonString(),getUserJson());
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(-1);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null){
                    //Hacer JsonParsing y guardar en  Base de Datos el idusuario si es nuevo

                    // Getting JSON Array node
                    JSONObject obj = new JSONObject(resultXML.toString());
                    JSONObject response = obj.getJSONObject("response");
                    int idusuario = response.getInt("idusuario");
                    if (idusuario>0){
                        setCurrentUsuarioId(idusuario);
                        out = idusuario;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return out;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result>0){
                showMessage("Árbol apadrinado con éxito");
                new getPadrinos().execute();
            }
        }
    }
}
