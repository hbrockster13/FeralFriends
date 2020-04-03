package com.example.feralfriends;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.feralfriends.Database.DatabaseAccess;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private LocationManager locationManager;
    private FloatingActionButton fab;

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getLocationPermission();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fab = findViewById(R.id.fab);
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Adding a FeralFriend", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                LatLng cur = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(cur).title("Marker Title"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cur));

                //Add marker to database
                Document document = new Document();
                document.put("MarkerId", marker.getId());
                document.put("Lat", cur.latitude);
                document.put("Lng", cur.longitude);
                document.put("Title", marker.getTitle());

                CreateItemAsyncTask task = new CreateItemAsyncTask();
                task.execute(document);

                //Call a new feral friend fragment
                Intent intent = FriendActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });

        //Initialize the LocationManager for changes in location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        }
        catch(SecurityException se)
        {
            Log.e(TAG, se.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if(location == null)
                {
                    return;
                }

                mLocation = location;
                Toast.makeText(getApplicationContext(),
                    "Long: " + mLocation.getLongitude()
                        + " Lat: " + mLocation.getLatitude()
                    ,Toast.LENGTH_SHORT).show();

                fab.show();

                //Add markers that aren't more than the maximum distance
                addMarkers(100);
            }
        });
    }

    private void getLocationPermission()
    {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        LatLng curPosition = new LatLng(location.getLatitude(), location.getLongitude());

        mLocation = location;

        //mMap.addMarker(new MarkerOptions().position(curPosition).title("Current position!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPosition));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(curPosition).zoom(20).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void addMarkers(double maxDistance)
    {
        //Get user's marker from the database
        LookupItemAsyncTask task = new LookupItemAsyncTask();

        try
        {
            ArrayList<Document> documents = task.execute().get();

            if(documents == null)
            {
                return;
            }

            for(Document document : documents)
            {
                double lat, lng;

                try
                {
                    lat = Double.parseDouble(document.get("Lat").asPrimitive().getValue().toString());
                    lng = Double.parseDouble(document.get("Lng").asPrimitive().getValue().toString());
                }
                catch(NullPointerException npe)
                {
                    Log.e(TAG, npe.getMessage());
                    continue;
                }

                LatLng markerPosition = new LatLng(lat, lng);
                LatLng curPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                double distanceBetween = SphericalUtil.computeDistanceBetween(markerPosition, curPosition);

                //Place markers that are within the maximum distance of the current position
                if(distanceBetween <= maxDistance)
                {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(document.get("Title").asString()));
                    Log.i(TAG, "Added marker: " + marker.getId() + ", Title: " + marker.getTitle() + ", Distance between: " + distanceBetween + " meters");
                }
            }
        }
        catch(InterruptedException ie)
        {
            Log.e(TAG, ie.getMessage());
        }
        catch(ExecutionException ee)
        {
            Log.e(TAG, ee.getMessage());
        }
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, Void>
    {
        @Override
        protected Void doInBackground(Document... documents)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MapsActivity.this);
            databaseAccess.create(documents[0]);

            Log.i(TAG, "Insert document into table");

            return null;
        }
    }

    private class LookupItemAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>>
    {
        @Override
        protected ArrayList<Document> doInBackground(Void... Voids)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MapsActivity.this);
            ArrayList<Document> documents = databaseAccess.lookup();

            if(documents.isEmpty())
            {
                return null;
            }

            Log.i(TAG, "Retrieved documents from database.");

            return documents;
        }
    }
}