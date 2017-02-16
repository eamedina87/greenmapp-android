package ec.medinamobile.greenmapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Supertel on 19/5/16.
 */
public class ArbolesCercanosActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Arbol> arboles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_arboles);
        setTitle("Árboles Cercanos");
        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra("cercanos")){
            arboles = intent.getParcelableArrayListExtra("cercanos");
            ArbolesAdapter adapter = new ArbolesAdapter(ArbolesCercanosActivity.this, arboles, R.layout.list_arbol);
            ListView lista = (ListView)findViewById(R.id.arboles_list);
            lista.setAdapter(adapter);
            lista.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (arboles!=null && arboles.size()>0){
            Intent in = new Intent(ArbolesCercanosActivity.this, ArbolDetalleActivity.class);
            in.putExtra("arbol", arboles.get(i));
            startActivity(in);
        }

    }
}
