package de.simon.brandhuber.lauf;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GpsMain extends FragmentActivity implements LocationListener, OnClickListener {
    List<Location> weg = MainActivity.weg;


    private LocationManager locationManager;
    private TextView showlenght;
    private TextView showwidth;
    private TextView showhight;
    private int i = 0;


    private SimpleDateFormat gpxZeitFormat;


    private Button startButton;
    private Button weiterButton;
    private Button pauseButton;
    private Button stopButton;
    private Button halloButton;
    private Button saveButton;
    private boolean gather;
    private List<Location> position;
    private TextView testtext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_overview);

        Intent newAct = getIntent();





        LocationManager myLocalManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Testen, ob GPS verfügbar
        if (!myLocalManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            warnungUndBeenden();
        }


        // Start-Schaltfläche
        startButton = (Button) this.findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);

        // Weiter-Schaltfläche
        weiterButton = (Button) this.findViewById(R.id.btnWeiter);
        weiterButton.setOnClickListener(this);
        weiterButton.setEnabled(false);


        // Pause-Schaltfläche
        pauseButton = (Button) this.findViewById(R.id.btnPause);
        pauseButton.setOnClickListener(this);
        pauseButton.setEnabled(false);

        // Stopp-Schaltfläche
        stopButton = (Button) this.findViewById(R.id.btnStopp);
        stopButton.setOnClickListener(this);
        stopButton.setEnabled(false);


        // Speichern-Schaltfläche
        saveButton = (Button) this.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(this);
        saveButton.setEnabled(false);

        gather = false;
        position = new ArrayList<Location>();


        showlenght = (TextView) this.findViewById(R.id.viewlat);
        showwidth = (TextView) this.findViewById(R.id.viewlong);
        showhight = (TextView) this.findViewById(R.id.viewhight);

        gpxZeitFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
            final Dialog dialog = new Dialog(this);
            dialog.setOwnerActivity(this);
            dialog.setContentView(R.layout.save_track_main);

            // Dialogelemente initialisieren
            Resources res = getResources();
            dialog.setTitle(res.getText(R.string.speichernDialogTitel));

            final EditText dateiname = (EditText) dialog.findViewById(R.id.txtSave);
            dateiname.setText("dateiname.txt");

            Button speichern = (Button) dialog.findViewById(R.id.btnSave2);
            speichern.setOnClickListener(new OnClickListener() {
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
            datei = new File(getFilesDir() + File.separator + dateiName);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources res = getResources();
        String text = res.getString(R.string.keinGPS);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish(); // Activity beenden
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }


    public void onLocationChanged(Location loc) {

        double laenge = loc.getLongitude();
        double breite = loc.getLatitude();

        showwidth.setText(Location.convert(breite, Location.FORMAT_SECONDS));
        showlenght.setText(Location.convert(laenge, Location.FORMAT_SECONDS));

        if (loc.hasAltitude()) {
            showhight.setText(String.valueOf(loc.getAltitude()));
        }


        if (gather) {
            position.add(loc);
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
