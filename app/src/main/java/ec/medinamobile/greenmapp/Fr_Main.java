package ec.medinamobile.greenmapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Supertel on 10/5/16.
 */
public class Fr_Main extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        if (v != null) {
            ((TextView)v.findViewById(R.id.section_label)).setText(getCurrentNombre());
        }
        return v;
    }
}
