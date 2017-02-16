package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Supertel on 19/5/16.
 */
public class LandingActivity extends BaseActivity {

    private static final long TIME_TRESHOLD = 1000*60*60; //*24*1;
    private long currentDate=-1;
    private boolean doStartActivity = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Se debe poner como landing page, donde se debe sincronizar la informacion cada 48hrs
        setContentView(R.layout.activity_landing);
        if (getIntent()!=null && getIntent().hasExtra("tipo")){
            doStartActivity = false;
        }
        currentDate = System.currentTimeMillis();
        if (isInternetAvailable()) {
            new getDataFromWS().execute();
        } else {
            closeActivity();
        }
    }

    private void closeActivity() {
        if (doStartActivity)
            startActivity(new Intent(LandingActivity.this, NavigationActivity.class));
        showMessage("Datos Actualizados");
        this.finish();
    }

    private class getDataFromWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopeArbolesSinceID(getUltimoID());
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(-1);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null){
                    //Hacer JsonParsing y guardar en  Base de Datos
                    //JSONObject jsonObj = new JSONObject(resultXML.toString());

                    // Getting JSON Array node
                    JSONArray mainArray = new JSONArray(resultXML.toString());

                    if (mainArray!=null && mainArray.length()>0) {

                        DBHelper dbHelper = new DBHelper(LandingActivity.this);

                        // looping through All Contacts
                        for (int i = 0; i < mainArray.length(); i++) {
                            Arbol arbol = new Arbol(mainArray.getJSONObject(i));
                            //if (arbol.getFecha()>getLastUpdateDate()){
                                dbHelper.insertArbol(arbol);
                                setUltimoID(arbol.getIdarbol());
                            //}
                            //Log.i("INSERT-ARBOL", "" + arbol.getIdarbol());
                        }
                        dbHelper.close();
                    }
                    setLastupdateDate(currentDate);
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            closeActivity();
        }
    }
}
