package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Supertel on 13/5/16.
 */
public class Fr_Arboles extends BaseFragment implements AdapterView.OnItemClickListener{

    private ListView list;
    private ArrayList<Arbol> arboles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_arboles, container, false);
        if (v!=null){
            list = (ListView)v.findViewById(R.id.arboles_list);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getCurrentNombre().equals("") || getCurrentNombre().equals("default")){
            Toast.makeText(getActivity(),"Debe iniciar sesión con Facebook para visualizar sus árboles", Toast.LENGTH_LONG).show();
        } else {
            //Puede darse el caso de que tenga el nombre pero no el id de usuario
            new loadMisArboles().execute();
        }
        list.setOnItemClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (arboles!=null && arboles.size()>0){
            Intent in = new Intent(getActivity(), ArbolDetalleActivity.class);
            in.putExtra("arbol", arboles.get(i));
            startActivity(in);
        }

    }

    private class loadMisArboles extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {


            try {
                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopeArbolesForUser(getUserJson());
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(30000);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null) {
                    JSONObject obj = new JSONObject(resultXML.toString());
                    setCurrentUsuarioId(obj.getInt("idusuario"));
                    JSONArray mainArray = obj.getJSONArray("arboles");

                    if (mainArray!=null && mainArray.length()>0) {
                        arboles = new ArrayList<>();
                        DBHelper dbHelper = new DBHelper(getActivity());

                        // looping through All Contacts
                        for (int i = 0; i < mainArray.length(); i++) {
                            long idarbol = mainArray.getJSONObject(i).getLong("idarbol");
                            if (idarbol>0){
                                dbHelper.insertArbolUsuario(idarbol, getCurrentUsuarioId());
                                arboles.add(dbHelper.getArbolById(idarbol));
                            }
                            //Log.i("INSERT-ARBOL", ""+arbol.getIdarbol());
                        }

                        dbHelper.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //DBHelper dbHelper = new DBHelper(getActivity());
            //arboles = dbHelper.getAllArbolesForUsuarioId(getCurrentUsuarioId());
            //dbHelper.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (arboles!=null && arboles.size()>0){
                ArbolesAdapter adapter = new ArbolesAdapter(getActivity(),arboles, R.layout.list_arbol);
                list.setAdapter(adapter);
            }

        }
    }
}
