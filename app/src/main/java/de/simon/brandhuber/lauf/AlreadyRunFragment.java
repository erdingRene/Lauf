package de.simon.brandhuber.lauf;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static de.simon.brandhuber.lauf.OverViewFragment.karte;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlreadyRunFragment extends Fragment {

    DatabaseHelper rundb;
    Button  btnDelete;
    EditText txtDeleteName;
    ListAdapter adapter;
    ListView listRuns;


    public AlreadyRunFragment() {
        // Required empty public constructor
    }
    public static GoogleMap map;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_already_run, container, false);

        txtDeleteName = (EditText) v.findViewById(R.id.txtDeleteName);
        btnDelete = (Button) v.findViewById(R.id.btnDelete);
        rundb = new DatabaseHelper(getContext());

        String[] runs = rundb.getColumnsString();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,runs);
        listRuns = (ListView) v.findViewById(R.id.listRuns);
        listRuns.setAdapter(adapter);
        //viewColumns();
        deleteData();


        listRuns.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String run = String.valueOf(parent.getItemAtPosition(position));
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getActivity(), run, duration);
                        toast.show();
                        txtDeleteName.setText(run);
                        showRun(run);
                                            }
                });

        return v;
    }

    private void showRun(String runName) {
        MapFragment mapFragment = new MapFragment();
        map = mapFragment.mMap;
        //FragmentManager manager = getActivity().getSupportFragmentManager();
        //manager.beginTransaction().replace(R.id.alreadyRunMapFragment, mapFragment).commit();
        Integer run = rundb.whatIsTheRunNumber(runName);
        if(rundb.howOftenExistsRunNumber(run) >= 2){
            Double[] latLonArray = new Double[4];
            Integer startId = rundb.idCounter(run);
            Integer endId = startId + rundb.howOftenExistsRunNumber(run) - 1;
            for (int id = startId; id <= endId - 1 ;id++) {
               latLonArray = rundb.dataForDrawLine(id);
                Polyline line = karte.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(new LatLng(latLonArray[0], latLonArray[1]),
                                new LatLng(latLonArray[2], latLonArray[3]))
                        .width(5)
                        .color(Color.RED));
                Log.d("coord","coords were loaded");
                
            }
            karte.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonArray[2], latLonArray[3]), 15));
            Log.d("zoom","zoom was executed");

        }}

    public void deleteData(){
        btnDelete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Integer deletedRows = rundb.deleteData(txtDeleteName.getText().toString() );
                        String[] runs = rundb.getColumnsString();
                        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,runs);

                        listRuns.setAdapter(adapter);
                        if (deletedRows > 0)
                            Toast.makeText(getActivity() ,deletedRows + " row(s) deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Data not deleted",Toast.LENGTH_LONG).show();


                    }

                }
        );
    }

    /*
    public void viewColumns(){
        Cursor res = rundb.getColumns();
        if(res.getCount() == 0){
            //show message
            showMessage("Error","No data found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            buffer.append("RUN_NUMBER: " +res.getString(0)+"\n");
            buffer.append("RUN_NAME: " +res.getString(1)+"\n\n");
        }
        // show two columns
        showMessage("Data",buffer.toString());
    }

    public void showMessage (String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
    */

}
