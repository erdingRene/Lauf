package de.simon.brandhuber.lauf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class offlinemapFragment extends Fragment {


    public offlinemapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_offlinemap, container, false);

        WebView offlineview = (WebView) v.findViewById(R.id.WebViewOffline);
        offlineview.loadUrl("http://sg.geodatenzentrum.de/wms_webatlasde.light?Request=GetMap&" +
                "VERSION=1.1.1&SERVICE=WMS&SRS=EPSG:31467&LAYERS=webatlasde.light&STYLES=&" +
                "BBOX=3682681.3929,5323395.9921,3701513.2858,5341697.6758&FORMAT=image/png&WIDTH=720&HEIGHT=460");
                return v;
    }

}
