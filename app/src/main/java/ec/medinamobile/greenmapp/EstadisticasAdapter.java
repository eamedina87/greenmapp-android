package ec.medinamobile.greenmapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Supertel on 13/5/16.
 */
public class EstadisticasAdapter extends ArrayAdapter <Estadistica>{

    private Context context;
    private ArrayList<Estadistica> estadisticas;

    public EstadisticasAdapter(Context context, ArrayList<Estadistica> estadisticas, int resource){
        super(context,resource);
        this.context=context;
        this.estadisticas = estadisticas;
    }

    public EstadisticasAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getPosition(Estadistica item) {
        return estadisticas.indexOf(item);
    }

    @Override
    public int getCount() {
        return estadisticas.size();
    }

    @Override
    public Estadistica getItem(int position) {
        return estadisticas.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = LayoutInflater.from(context).inflate(R.layout.list_arbol, parent, true);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View v  = inflater.inflate(R.layout.list_estadistica, parent, false);

        //((ImageView)v.findViewById(R.id.arbol_imagen)).setImageDrawable(context.getResources().get);
        Estadistica tmp = estadisticas.get(position);
        ((TextView)v.findViewById(R.id.estadistica_ciudad)).setText(tmp.getCiudad());
        ((TextView)v.findViewById(R.id.estadistica_arboles)).setText(String.valueOf(tmp.getArboles()));
        ((TextView)v.findViewById(R.id.estadistica_patrimoniales)).setText(String.valueOf(tmp.getPatrimoniales()));
        return v;
        //return super.getView(position, convertView, parent);

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
}
