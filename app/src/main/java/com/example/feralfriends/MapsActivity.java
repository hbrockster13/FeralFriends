package com.example.feralfriends;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.example.feralfriends.Database.DatabaseAccess;
import com.example.feralfriends.models.FeralFriend;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<FeralFriend> friends;
    private HashMap<Marker, FeralFriend> mHashMap = new HashMap<Marker, FeralFriend>();

    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_EDIT = 2;
    private static final int RESULT_DELETE = 2;
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
                Log.i(TAG, "Action button for adding feral friend clicked");

                //Call a new feral friend activity and get user input
                Intent intent = FriendActivity.newIntent(getApplicationContext(), null);
                startActivityForResult(intent, REQUEST_ADD);
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

        //Initialize Firebase Cloud Messaging
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
        {
            @Override
            public void onComplete(Task<InstanceIdResult> task)
            {
                if(!task.isSuccessful())
                {
                    Log.e(TAG, task.getException().getMessage());
                    return;
                }

                String token = task.getResult().getToken();
                Log.i(TAG, "Firebase token: " + token);

                //Register token with Amazon SNS
                RegisterEndPoint task2 = new RegisterEndPoint();

                try
                {
                    if(!task2.execute(token).get().booleanValue())
                    {
                        Log.i(TAG, "Failed to register token with Amazon SNS");
                        return;
                    }
                }
                catch(ExecutionException ee)
                {
                    Log.e(TAG, ee.getMessage());
                }
                catch(InterruptedException ie)
                {
                    Log.e(TAG, ie.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(MessageReceiver, new IntentFilter("Firebase"));
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(MessageReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                addFeralFriend(data);
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                Log.i(TAG, "New FeralFriend was cancelled");
            }
        }
        else if(requestCode == REQUEST_EDIT)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                editFeralFriend(data);
            }
            else if(resultCode == RESULT_DELETE)
            {
                deleteFeralFriend(data);
            }
            else if(resultCode == Activity.RESULT_CANCELED)
            {
                Log.i(TAG, "Editing of FeralFriend was cancelled");
            }
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

                //Update the user's location
                mLocation = location;

                //Allow the user to add a FeralFriend
                fab.show();

                //Add markers that aren't more than the maximum distance
                //1600 meters in a mile
                //addMarkersWithinDistance(1600);

                //-1 to show all markers regardless of distance
                addMarkersWithinDistance(-1);
            }
        });

        //Create custom info window for markers
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                FeralFriend friend = null;

                //Get FeralFriend from marker
                try
                {
                    friend = getFriend(marker);
                }
                catch(IndexOutOfBoundsException ioobe)
                {
                    Log.e(TAG, ioobe.getMessage());
                }

                if(friend == null)
                {
                    return null;
                }

                View view = getLayoutInflater().inflate(R.layout.info_window, null);

                //Setup the view from the FeralFriend model
                EditText title = view.findViewById(R.id.friend_title);
                title.setText(friend.getTitle());

                EditText description = view.findViewById(R.id.friend_details);
                description.setText(friend.getDetails());

                ToggleButton tnrButton = view.findViewById(R.id.button_tnr);
                tnrButton.setChecked(friend.isTNRed());

                EditText numFriends = view.findViewById(R.id.number_friends);
                numFriends.setText(String.valueOf(friend.getNumberOfFriends()));

                TextView lastFed = view.findViewById(R.id.last_fed);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                lastFed.setText(dateFormatter.format(friend.getDate()));

                Log.i(TAG, "Returned info_window view from layout");

                return view;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                FeralFriend friend;

                try
                {
                    friend = getFriend(marker);

                    Log.i(TAG, "Clicked on marker, " + "ID: " + friend.getID() + ", " + "Title: " + friend.getTitle() + ", " + "Num: " + marker.getId());
                }
                catch(IndexOutOfBoundsException ioobe)
                {
                    Log.e(TAG, ioobe.getMessage());
                }

                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                Log.i(TAG, "InfoWindow was clicked");

                FeralFriend friend = getFriend(marker);

                //Call the feral friend activity and get user input
                Intent intent = FriendActivity.newIntent(getApplicationContext(), friend);
                startActivityForResult(intent, 2);
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
        Log.i(TAG, "Location changed to: " + location.getLatitude() + ", " + location.getLongitude());

        mLocation = location;
        LatLng curPosition = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder().target(curPosition).zoom(20).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void addFeralFriend(Intent data)
    {
        Log.i(TAG, "Adding a new FeralFriend");

        FeralFriend friend = (FeralFriend) data.getSerializableExtra("friend_model");
        friend.setUserID(DatabaseAccess.getInstance(MapsActivity.this).getUserID());
        friend.setLatitude(mLocation.getLatitude());
        friend.setLongitude(mLocation.getLongitude());

        //Add the FeralFriend to the database
        Document document = friend.asDocument();

        CreateItemAsyncTask task = new CreateItemAsyncTask();

        try
        {
            if(!task.execute(document).get().booleanValue())
            {
                Log.i(TAG, "Failed to add a new FeralFriend");
                return;
            }
        }
        catch(ExecutionException ee)
        {
            Log.e(TAG, ee.getMessage());
        }
        catch(InterruptedException ie)
        {
            Log.e(TAG, ie.getMessage());
        }

        //Add the marker for location of FeralFriend on map
        LatLng cur = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions().position(cur));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cat_icon_sleep_3));

        //Add new friend to data structures
        friends.add(friend);
        mHashMap.put(marker, friend);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(cur));
    }

    private void editFeralFriend(Intent data)
    {
        FeralFriend editedFriend = (FeralFriend) data.getSerializableExtra("friend_model");
        FeralFriend oldFriend = null;

        for(FeralFriend friend : friends)
        {
            if(friend.getID().equals(editedFriend.getID()))
            {
                oldFriend = friend;
                break;
            }
        }

        //Check if the models are identical before updating database
        if(oldFriend.equals(editedFriend))
        {
            Log.i(TAG, "No changes made to the model");
            return;
        }

        Log.i(TAG, "Replacing old FeralFriend with recently modified one in the database");

        //Add FeralFriend to the database
        Document document = editedFriend.asDocument();

        CreateItemAsyncTask task = new CreateItemAsyncTask();

        try
        {
            if(!task.execute(document).get())
            {
                Log.i(TAG, "Failed to replace old FeralFriend");
                return;
            }
        }
        catch(ExecutionException ee)
        {
            Log.e(TAG, ee.getMessage());
        }
        catch(InterruptedException ie)
        {
            Log.e(TAG, ie.getMessage());
        }

        //Replace old model with new one in data structure
        friends.set(friends.indexOf(oldFriend), editedFriend);

        //Update current marker on map
        for(Marker marker : mHashMap.keySet())
        {
            if(Double.compare(marker.getPosition().latitude, editedFriend.getLatitude()) == 0 && Double.compare(marker.getPosition().longitude, editedFriend.getLongitude()) == 0)
            {
                FeralFriend temp = getFriend(marker);

                try
                {
                    if(temp.getID().equals(editedFriend.getID()))
                    {
                        //Redisplay the InfoWindow to show the edited information
                        mHashMap.put(marker, editedFriend);
                        marker.showInfoWindow();

                        break;
                    }
                }
                catch(NullPointerException npe)
                {
                    Log.e(TAG, npe.getMessage());
                }
            }
        }
    }

    private void deleteFeralFriend(Intent data)
    {
        Log.i(TAG, "Deleting FeralFriend");

        FeralFriend friend = (FeralFriend) data.getSerializableExtra("friend_model");

        DeleteItemAsyncTask task = new DeleteItemAsyncTask();

        try
        {
            if(!task.execute(friend.asDocument()).get())
            {
                Log.i(TAG, "Failed to delete FeralFriend");
                return;
            }
        }
        catch(ExecutionException ee)
        {
            Log.e(TAG, ee.getMessage());
        }
        catch(InterruptedException ie)
        {
            Log.e(TAG, ie.getMessage());
        }

        //Remove marker from map and data structures
        for(Marker marker : mHashMap.keySet())
        {
            if(Double.compare(marker.getPosition().latitude, friend.getLatitude()) == 0 && Double.compare(marker.getPosition().longitude, friend.getLongitude()) == 0)
            {
                FeralFriend friend1 = getFriend(marker);

                if(friend1.getID().equals(friend.getID()))
                {
                    marker.remove();

                    mHashMap.remove(marker);
                    friends.remove(friend);

                    break;
                }
            }
        }
    }

    private void addMarkersWithinDistance(double maxDistance)
    {
        //Get user's marker from the database
        LookupItemAsyncTask task = new LookupItemAsyncTask();

        try
        {
            friends = task.execute().get();

            if(friends == null)
            {
                friends = new ArrayList<FeralFriend>();
                return;
            }

            for(int i = 0; i < friends.size(); i++)
            {
                LatLng markerPosition = new LatLng(friends.get(i).getLatitude(), friends.get(i).getLongitude());
                LatLng curPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                double distanceBetween = SphericalUtil.computeDistanceBetween(markerPosition, curPosition);

                //Place markers that are within the maximum distance of the current position
                if(distanceBetween <= maxDistance || maxDistance == -1)
                {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerPosition));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cat_icon_sleep_3));

                    mHashMap.put(marker, friends.get(i));

                    Log.i(TAG, "Added marker: " + friends.get(i).getID() + ", Title: " + friends.get(i).getTitle() + ", Distance between: " + distanceBetween + " meters");
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

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Document... documents)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MapsActivity.this);

            if(!databaseAccess.create(documents[0]))
            {
                Log.i(TAG, "AsyncTask: Failed to insert document into table");
                return false;
            }

            Log.i(TAG, "Inserted document into table");
            return true;
        }
    }

    private class LookupItemAsyncTask extends AsyncTask<Void, Void, ArrayList<FeralFriend>>
    {
        @Override
        protected ArrayList<FeralFriend> doInBackground(Void... Voids)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MapsActivity.this);
            ArrayList<FeralFriend> friends = databaseAccess.lookup();

            if(friends.isEmpty())
            {
                Log.i(TAG, "Database has no records");
                return null;
            }

            Log.i(TAG, "AsyncTask: Received FeralFriends ArrayList from database");
            return friends;
        }
    }

    private class DeleteItemAsyncTask extends AsyncTask<Document, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Document... documents)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MapsActivity.this);
            if(!databaseAccess.delete(documents[0]))
            {
                Log.i(TAG, "AsyncTask: Failed to delete document from table");
                return false;
            }

            Log.i(TAG, "Deleted document from table");
            return true;
        }
    }

    private class RegisterEndPoint extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... token)
        {
            try
            {
                AmazonSNSClient client = DatabaseAccess.getInstance(MapsActivity.this).getSNSClient();

                CreatePlatformEndpointRequest createPlatformEndpointRequest = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn("arn:aws:sns:us-east-1:619509416239:app/GCM/FeralFriends")
                    .withToken(token[0]);

                CreatePlatformEndpointResult createPlatformEndpointResult = client.createPlatformEndpoint(createPlatformEndpointRequest);
                Log.i(TAG, createPlatformEndpointResult.getEndpointArn());

                SubscribeRequest subscribeRequest = new SubscribeRequest("arn:aws:sns:us-east-1:619509416239:Firebase", "application", createPlatformEndpointResult.getEndpointArn());
                client.subscribe(subscribeRequest);
            }
            catch(InvalidParameterException ipe)
            {
                Log.e(TAG, ipe.getMessage());
                return false;
            }
            catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
                return false;
            }

            return true;
        }
    }

    private BroadcastReceiver MessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            FeralFriend newFriend = (FeralFriend) intent.getExtras().getSerializable("friend_model");

            if(newFriend == null)
            {
                return;
            }

            if(intent.getExtras().get("event").toString().equals("INSERT"))
            {
                if(newFriend.getUserID().equals(DatabaseAccess.getInstance(MapsActivity.this).getUserID()))
                {
                    return;
                }

                //Add the marker for location of FeralFriend on map
                LatLng location = new LatLng(newFriend.getLatitude(), newFriend.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(location));
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cat_icon_sleep_3));

                //Add new friend to data structures
                friends.add(newFriend);
                mHashMap.put(marker, newFriend);

                Log.i(TAG, "Added new FeralFriend from Broadcast");
            }
            else if(intent.getExtras().get("event").toString().equals("MODIFY"))
            {
                //Update current marker on map
                for(Marker marker : mHashMap.keySet())
                {
                    if(Double.compare(marker.getPosition().latitude, newFriend.getLatitude()) != 0 && Double.compare(marker.getPosition().longitude, newFriend.getLongitude()) != 0)
                    {
                        continue;
                    }

                    FeralFriend temp = getFriend(marker);

                    if(temp.getID().equals(newFriend.getID()) && temp.getUserID().equals(newFriend.getUserID()))
                    {
                        //Update data structure
                        mHashMap.put(marker, newFriend);

                        Log.i(TAG, "Modified existing FeralFriend from Broadcast");

                        break;
                    }
                }
            }
            else if(intent.getExtras().get("event").toString().equals("REMOVE"))
            {
                //Remove marker from map and data structures
                for(Marker marker : mHashMap.keySet())
                {
                    if(Double.compare(marker.getPosition().latitude, newFriend.getLatitude()) != 0 && Double.compare(marker.getPosition().longitude, newFriend.getLongitude()) != 0)
                    {
                        continue;
                    }

                    FeralFriend temp = getFriend(marker);

                    if(temp.getID().equals(newFriend.getID()))
                    {
                        marker.remove();

                        mHashMap.remove(marker);
                        friends.remove(temp);

                        Log.i(TAG, "Deleted existing FeralFriend from Broadcast");

                        break;
                    }
                }
            }
        }
    };

    private FeralFriend getFriend(Marker marker)
    {
        try
        {
            return mHashMap.get(marker);
        }
        catch(NullPointerException npe)
        {
            Log.e(TAG, npe.getMessage());
        }

        return null;
    }
}