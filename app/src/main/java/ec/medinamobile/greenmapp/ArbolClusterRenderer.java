package ec.medinamobile.greenmapp;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Supertel on 13/5/16.
 */
class ArbolClusterRenderer extends DefaultClusterRenderer<ArbolCluster> {

    public ArbolClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<ArbolCluster> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ArbolCluster item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.title(item.getArbol().getTipo());
        markerOptions.snippet(""+item.getArbol().getIdarbol());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_tree));
        //markerOptions.snippet(item.getArbol().getDescripcion());
    }

    @Override
    protected void onClusterItemRendered(ArbolCluster clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        //here you have access to the marker itself


    }
}
