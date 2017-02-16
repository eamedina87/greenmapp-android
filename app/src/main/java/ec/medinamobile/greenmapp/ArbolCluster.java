package ec.medinamobile.greenmapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Supertel on 12/5/16.
 */
public class ArbolCluster implements ClusterItem {

    private Arbol arbol;

    public ArbolCluster(){

    }

    public ArbolCluster(Arbol a){
        arbol = a;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(arbol.getLatitud(), arbol.getLongitud());
    }

    public Arbol getArbol() {
        return arbol;
    }
}
