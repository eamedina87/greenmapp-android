package ec.medinamobile.greenmapp;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Supertel on 13/5/16.
 */
public class DenunciaActivity extends BaseActivity{


    private Arbol arbol;
    private TextView btn_enviar;
    private TextView reclamo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_denuncia);
        setTitle("Denuncia");

        if (getIntent().hasExtra("arbol")){
            arbol = getIntent().getParcelableExtra("arbol");
            ((TextView)findViewById(R.id.arbol_id)).setText("Id: "+arbol.getIdarbol());
            ((TextView)findViewById(R.id.arbol_tipo)).setText(arbol.getTipo());
            ((TextView)findViewById(R.id.arbol_provincia)).setText(arbol.getProvincia());
            ((TextView)findViewById(R.id.arbol_ciudad)).setText(arbol.getCiudad());
            ((ImageView)findViewById(R.id.arbol_imagen)).setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+arbol.getImagen()));
        }

        reclamo = ((TextView) findViewById(R.id.reclamo_descripcion));

        btn_enviar = (TextView)findViewById(R.id.btn_enviar);
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentNombre().equals("") || getCurrentNombre().equals("default")){
                    showMessage("Debe iniciar sesión en Facebook para enviar una denuncia");
                } else if  (reclamo.getText().toString().length()>5){
                    //Iniciar contacto con WS para apadrinar y actualizar lista

                    StringBuilder sb = new StringBuilder("DATOS DEL ÁRBOL\n");
                    sb.append("Id:").append(arbol.getIdarbol()).append("\n");
                    sb.append("Tipo:").append(arbol.getTipo()).append("\n");
                    sb.append("Provincia:").append(arbol.getProvincia()).append("\n");
                    sb.append("Ciudad:").append(arbol.getCiudad()).append("\n");
                    sb.append("Direccion:").append(arbol.getDireccion()).append("\n");
                    sb.append("Latitud:").append(arbol.getLatitud()).append(" Longitud:").append(arbol.getLongitud()).append("\n");
                    sb.append("RECLAMO\n");
                    sb.append(reclamo.getText().toString());

                    new enviarDenuncia().execute(sb.toString());
                } else if (reclamo.getText().toString().length()==0){
                    showMessage("Por favor escriba su reclamo.");
                }
            }
        });

    }

    private class enviarDenuncia extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reclamo.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(String... voids) {
            Boolean out = false;
            try {
                // open a connection to the site
                //URL url = new URL("http://54.187.29.10/helpdesk/api/api_ticket_create.php");
                URL url = new URL("http://greenmapp.ambiente.gob.ec/osticket/api/api_ticket_create.php");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                // activate the output
                con.setDoOutput(true);
                PrintStream ps = new PrintStream(con.getOutputStream());
                // send your parameters to your site
                ps.print("&name="+getCurrentNombre());
                ps.print("&email="+getCurrentEmail());
                ps.print("&subject=Denuncia");
                ps.print("&message="+voids[0]);

                // we have to get the input stream in order to actually send the request
                con.getInputStream();
                if (con.getResponseCode()==200){
                    // Reclamo Enviado
                    out = true;
                }

                // close the print stream
                ps.close();
                con.disconnect();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }


            return out;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean==true){
                showMessage("Denuncia enviada correctamente");
                closeActivity();
            } else {
                showMessage("Denuncia no pudo ser enviada. Intente nuevamente.");
                reclamo.setEnabled(true);
            }

        }
    }

    private void closeActivity() {
        this.finish();
    }

}
