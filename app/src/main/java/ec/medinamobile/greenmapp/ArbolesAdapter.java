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
public class ArbolesAdapter extends ArrayAdapter <Arbol>{

    private Context context;
    private ArrayList<Arbol> arboles;

    public ArbolesAdapter(Context context, ArrayList<Arbol> arboles, int resource){
        super(context,resource);
        this.context=context;
        this.arboles=arboles;
    }

    public ArbolesAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getPosition(Arbol item) {
        return arboles.indexOf(item);
    }

    @Override
    public int getCount() {
        return arboles.size();
    }

    @Override
    public Arbol getItem(int position) {
        return arboles.get(position);
    }

    @Override
    public long getItemId(int position) {

        return arboles.get(position).getIdarbol();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = LayoutInflater.from(context).inflate(R.layout.list_arbol, parent, true);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View v  = inflater.inflate(R.layout.list_arbol, parent, false);

        //((ImageView)v.findViewById(R.id.arbol_imagen)).setImageDrawable(context.getResources().get);
        Arbol tmp = arboles.get(position);
        ((TextView)v.findViewById(R.id.arbol_tipo)).setText(tmp.getTipo());
        if (!tmp.getDireccion().equals("") && !tmp.getSector().equals("")){
            ((TextView)v.findViewById(R.id.arbol_direccion)).setText(tmp.getDireccion() + " - " + tmp.getSector());
        } else if (!tmp.getDireccion().equals("")){
            ((TextView)v.findViewById(R.id.arbol_direccion)).setText(tmp.getDireccion());
        } else {
            ((TextView)v.findViewById(R.id.arbol_direccion)).setText("");
        }
        ((TextView)v.findViewById(R.id.arbol_ciudad)).setText(tmp.getCiudad()+" - "+tmp.getProvincia());

        if (tmp.getDistancia()>0){
            //Hacer visible el textview de distancia y mostrar el valor
            TextView dist = (TextView)v.findViewById(R.id.arbol_distancia);
            dist.setVisibility(View.VISIBLE);
            String distancia = String.format("%.0f",tmp.getDistancia());
            dist.setText("a "+distancia+" mts.");
        }

        return v;
        //return super.getView(position, convertView, parent);

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
}
