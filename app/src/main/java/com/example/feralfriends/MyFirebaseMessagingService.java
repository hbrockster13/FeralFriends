package com.example.feralfriends;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.feralfriends.models.FeralFriend;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private LocalBroadcastManager broadcastManager;

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onCreate()
    {
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        try
        {
            JSONObject json = new JSONObject(remoteMessage.getData().get("default"));

            Log.i(TAG, "Received JSON object from Lambda function");

            Intent intent = new Intent("Firebase");

            FeralFriend friend = new FeralFriend(json.get("UserID").toString().replaceAll("\"", ""), json.get("MarkerID").toString().replaceAll("\"", ""),
                Double.parseDouble(json.get("Lat").toString().replaceAll("\"", "")), Double.parseDouble(json.get("Lng").toString().replaceAll("\"", "")),
                json.get("Title").toString().replaceAll("\"", ""), json.get("Description").toString().replaceAll("\"", ""), json.get("LastFed").toString().replaceAll("\"", ""),
                Boolean.parseBoolean(json.get("TNR").toString().replaceAll("\"", "")), Integer.parseInt(json.get("NumFriends").toString().replaceAll("\"", "")));

            intent.putExtra("friend_model", friend);
            intent.putExtra("event", json.get("Event").toString().replaceAll("\"", ""));

            broadcastManager.sendBroadcast(intent);
        }
        catch(JSONException je)
        {
            Log.e(TAG, je.getMessage());
        }
    }
}
