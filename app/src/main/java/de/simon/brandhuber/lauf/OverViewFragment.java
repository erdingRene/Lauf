package de.simon.brandhuber.lauf;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 16.11.2017.
 */


public class OverViewFragment extends Fragment implements LocationListener, View.OnClickListener {

    // Vorübergehende Variablen
    private Integer theNextLocation = 0;
    private Integer theNextRunNumber;
    private EditText runName;
    private Double lat;
    private Double lon;
    private Double height;
    private String datetime;
    private SimpleDateFormat normalTimeFormat;
    private Integer ID;
    private Integer controlA;


    // reguläre Variablen
    private TextView showlenght;
    private TextView showwidth;
    private TextView showhight;
    private int i = 0;


    private SimpleDateFormat gpxZeitFormat;


    private Button startButton;
    private Button weiterButton;
    private Button pauseButton;
    private Button stopButton;
    private Button saveButton;
    private boolean gather;
    private List<Location> position;

    public static GoogleMap karte;


    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    DatabaseHelper rundb;



    private LocationManager myLocalManager;

    public OverViewFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        MapFragment mapFragment = new MapFragment();
        karte = mapFragment.mMap;


        FragmentManager manager = getActivity().getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.small_fragment, mapFragment).commit();

        View v = inflater.inflate(R.layout.fragment_overview, container, false);



        myLocalManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);



        // Testen, ob GPS verfügbar
        if (!myLocalManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            warnungUndBeenden();
        }


        //DB
        rundb = new DatabaseHelper(getContext());
        runName = (EditText) v.findViewById(R.id.edtRunName);


        // Start-Schaltfläche
        startButton = (Button) v.findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);

        // Weiter-Schaltfläche
        weiterButton = (Button) v.findViewById(R.id.btnWeiter);
        weiterButton.setOnClickListener(this);
        weiterButton.setEnabled(false);


        // Pause-Schaltfläche
        pauseButton = (Button) v.findViewById(R.id.btnPause);
        pauseButton.setOnClickListener(this);
        pauseButton.setEnabled(false);

        // Stopp-Schaltfläche
        stopButton = (Button) v.findViewById(R.id.btnStopp);
        stopButton.setOnClickListener(this);
        stopButton.setEnabled(false);


        // Speichern-Schaltfläche
        saveButton = (Button) v.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(this);
        saveButton.setEnabled(false);

        gather = false;
        position = new ArrayList<Location>();


        showlenght = (TextView) v.findViewById(R.id.viewlat);
        showwidth = (TextView) v.findViewById(R.id.viewlong);
        showhight = (TextView) v.findViewById(R.id.viewhight);

        gpxZeitFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return v;
    }

    /**
     * Klickbehandlung für Start/Stopp/Speichern
     */
    public void onClick(View v) {
        if (v == startButton) {
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            saveButton.setEnabled(false);
            weiterButton.setEnabled(false);

            controlA = 0;
            int howOftenWasTheStartButtonPuched = rundb.lastRunNumber();
            theNextRunNumber = 1 + howOftenWasTheStartButtonPuched;



            gather = true;

        }
        else if (v == weiterButton) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            saveButton.setEnabled(true);
            pauseButton.setEnabled(true);
            weiterButton.setEnabled(false);

            gather = true;
        }
        else if (v == pauseButton) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            saveButton.setEnabled(true);
            pauseButton.setEnabled(false);
            weiterButton.setEnabled(true);
            gather = false;


        }

        else if (v == stopButton) {
            startButton.setEnabled(false);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            saveButton.setEnabled(true);
            weiterButton.setEnabled(false);
            gather = false;
        }



        else if(v ==saveButton)

        {
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            saveButton.setEnabled(false);
            gather = false;

            datenSpeichern();


        }

    }





    /**
     * Dialog für Speichern anzeigen und Daten dann speichern falls gewünscht
     */
    private void datenSpeichern() {
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setOwnerActivity(getActivity());
            dialog.setContentView(R.layout.save_track_main);

            // Dialogelemente initialisieren
            Resources res = getResources();
            dialog.setTitle(res.getText(R.string.speichernDialogTitel));

            final EditText dateiname = (EditText) dialog.findViewById(R.id.txtSave);
            dateiname.setText("dateiname.txt");

            Button speichern = (Button) dialog.findViewById(R.id.btnSave2);
            speichern.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Editable ed = dateiname.getText();

                    try {
                        positionenSchreiben(ed.toString());
                        position.clear();
                    } catch (Exception ex) {
                        Log.d("carpelibrum", ex.getMessage());
                    }

                    dialog.dismiss();
                }
            });


            dialog.show();

        } catch (Exception ex) {
            Log.d("carpelibrum", ex.getMessage());

        }

    }

    /**
     * Schreibt die Daten auf SD-Karte oder internen Speicher im GPX Format
     *
     * @throws Exception
     */
    private void positionenSchreiben(String dateiName) throws Exception {
        File sdKarte = Environment.getExternalStorageDirectory();
        boolean sdKarteVorhanden = (sdKarte.exists() && sdKarte.canWrite());
        File datei;

        if (sdKarteVorhanden) {
            datei = new File(sdKarte.getAbsolutePath() + File.separator + dateiName);
        } else {
            datei = new File(getContext().getFilesDir() + File.separator + dateiName);
            ;
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(datei));


        // Dateikopf schreiben
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
        writer.newLine();
        writer.write("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" ");
        writer.write("creator=\"carpelibrum\" xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" ");
        writer.write("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1/gpx.xsd\"");
        writer.newLine();

        // Daten schreiben

        for (Location loc : position) {
            lokationSpeichern(loc, writer);
        }


        // Dateiende schreiben
        writer.write("</gpx>");

        writer.close();
    }


    /**
     * Übergebene Lokation in den gegebenen Writer schreiben
     *
     * @param loc
     * @param writer
     */
    private void lokationSpeichern(Location loc, BufferedWriter writer) throws IOException {
        writer.write("<wpt lat=\"" + loc.getLatitude() + "\" lon=\"" + loc.getLongitude() + "\">");
        writer.newLine();
        writer.write("<ele>" + loc.getAltitude() + "</ele>");
        writer.newLine();

        String zeit = gpxZeitFormat.format(new Date(loc.getTime()));
        writer.write("<time>" + zeit + "</time>");
        writer.newLine();
        writer.write("</wpt>");
        writer.newLine();
    }


    /**
     * Benutzer auffordern GPS zu aktivieren und Activity beenden
     */
    private void warnungUndBeenden() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Resources res = getResources();
        String text = res.getString(R.string.keinGPS);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                getActivity().finish(); // Activity beenden
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //myLocalManager.removeUpdates();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocalManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }


    public void onLocationChanged(Location loc) {

        theNextLocation += theNextLocation;
        double laenge = loc.getLongitude();
        double breite = loc.getLatitude();
        lat = loc.getLatitude();
        lon = loc.getLongitude();
        height = loc.getAltitude();
        normalTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        datetime = normalTimeFormat.format(new Date(loc.getTime()));


        showwidth.setText(Location.convert(breite, Location.FORMAT_SECONDS));
        showlenght.setText(Location.convert(laenge, Location.FORMAT_SECONDS));

        if (loc.hasAltitude()) {
            showhight.setText(String.valueOf(loc.getAltitude()));
        }


        if (gather) {
            position.add(loc);
            AddData(theNextRunNumber);

            //ID Suchen
            if (controlA == 0) {
                ID = rundb.idCounter(theNextRunNumber);
            }
            else {
                ID = ID + 1;
            }

            drawLine(ID, theNextRunNumber);
        }

    }


    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }


    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }






    //Methode zum Hinzufügen der Eingaben zur DB per Schaltfläche
    public void AddData(Integer theNextRunNumber) {
        boolean isInserted = rundb.insertData(theNextRunNumber, runName.getText().toString(),lat,lon, height,datetime);
                        if(isInserted = true)
                            Toast.makeText(getActivity() ,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Data not Inserted",Toast.LENGTH_LONG).show();

    }




    //Linie zeichnen
    public void drawLine (Integer LaufNR, Integer runNumber){
        if(rundb.howOftenExistsRunNumber(runNumber) >= 2){
            Double[] latLonArray = rundb.dataForDrawLine(LaufNR);
            Polyline line= karte.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(   new LatLng(latLonArray[0],latLonArray[1]),
                            new LatLng(latLonArray[2],latLonArray[3]))
                    .width(5)
                    .color(Color.RED));
                    karte.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonArray[2],latLonArray[3]), 4));
            controlA = 1;

        }


    }



}