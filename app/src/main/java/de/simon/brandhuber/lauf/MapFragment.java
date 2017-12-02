package de.simon.brandhuber.lauf;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{
    GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private final int[] MAP_TYPES = { GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE };
    private int curMapTypeIndex = 0;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        initListeners();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map1);
        //mapFragment.getMapAsync(this);

    }

    private void initListeners() {
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener( this );
        getMap().setOnMapClickListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

   }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        initCamera( mCurrentLocation );
    }

    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(),
                        location.getLongitude() ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        getMap().animateCamera( CameraUpdateFactory
                .newCameraPosition( position ), null );

        getMap().setMapType( MAP_TYPES[curMapTypeIndex] );
        getMap().setTrafficEnabled( true );
        getMap().setMyLocationEnabled( true );
        getMap().getUiSettings().setZoomControlsEnabled( true );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng( latLng ) );

        options.icon( BitmapDescriptorFactory.defaultMarker() );
        getMap().addMarker( options );
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng( latLng ) );

        options.icon( BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.mipmap.ic_launcher ) ) );

        getMap().addMarker( options );
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
}
