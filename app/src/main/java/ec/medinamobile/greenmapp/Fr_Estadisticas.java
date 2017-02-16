package ec.medinamobile.greenmapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Supertel on 18/5/16.
 */
public class Fr_Estadisticas extends BaseFragment implements  AdapterView.OnItemSelectedListener {


    private Spinner sp_provincias;
    private ListView list_estadisticas;
    String[] provincias;
    private View header;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estadistica, container, false);
        if (v!=null){
            sp_provincias = (Spinner)v.findViewById(R.id.sp_provincias);
            list_estadisticas = (ListView)v.findViewById(R.id.list_estadisticas);
        }
        header = inflater.inflate(R.layout.list_estadistica, container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new loadProvincias().execute();
        sp_provincias.setOnItemSelectedListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        new loadEstadisticas().execute(provincias[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class loadProvincias extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(getActivity());
            provincias = dbHelper.getAllProvincias();
            dbHelper.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (provincias!=null){
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, provincias);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_provincias.setAdapter(spinnerArrayAdapter);
            }

        }
    }

    private class loadEstadisticas extends AsyncTask<String, Void, ArrayList<Estadistica>>{

        @Override
        protected ArrayList<Estadistica> doInBackground(String... strings) {
            ArrayList<Estadistica> estadisticas = new ArrayList<>();
            String provincia = strings[0];
            DBHelper dbHelper = new DBHelper(getActivity());
            estadisticas = dbHelper.getEstadisticasForProvincia(provincia);
            dbHelper.close();
            return estadisticas;
        }

        @Override
        protected void onPostExecute(ArrayList<Estadistica> estadisticas) {
            super.onPostExecute(estadisticas);
            if (estadisticas!=null && estadisticas.size()>0){

                LayoutInflater inflater = getActivity().getLayoutInflater();
                if (list_estadisticas.getHeaderViewsCount()==0) {
                    View header = inflater.inflate(R.layout.list_estadistica, null);
                    header.setBackgroundColor(Color.LTGRAY);
                    ((TextView) header.findViewById(R.id.estadistica_ciudad)).setText("Ciudad");
                    ((TextView) header.findViewById(R.id.estadistica_arboles)).setText("Árboles");
                    ((TextView) header.findViewById(R.id.estadistica_patrimoniales)).setText("Patrim.");
                    list_estadisticas.addHeaderView(header);
                }
                EstadisticasAdapter adapter = new EstadisticasAdapter(getActivity(), estadisticas, R.layout.list_estadistica);
                list_estadisticas.setAdapter(adapter);

            }
        }
    }
}
