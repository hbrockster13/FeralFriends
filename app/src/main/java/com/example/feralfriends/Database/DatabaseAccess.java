package com.example.feralfriends.Database;

import android.content.Context;
import android.util.Log;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.internal.Key;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.example.feralfriends.models.FeralFriend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DatabaseAccess
{
    private final String DYNAMODB_TABLE = "Markers";
    private final String COGNITO_IDENTITY_POOL_ID = "us-east-2:005b7c74-a373-4a55-8a47-04f044e1a061";
    private final Regions COGNITO_IDENTITY_POOL_REGION = Regions.US_EAST_2;
    private Table table;
    private Context context;
    private AmazonDynamoDBClient client;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private static volatile DatabaseAccess instance;

    private static final String TAG = "DatabaseAccess";

    private DatabaseAccess(Context context)
    {
        this.context = context;

        credentialsProvider = new CognitoCachingCredentialsProvider(context, COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_POOL_REGION);
        client = new AmazonDynamoDBClient(credentialsProvider);
        client.setRegion(Region.getRegion(Regions.US_EAST_2));
        table = Table.loadTable(client, DYNAMODB_TABLE);
    }

    public static synchronized DatabaseAccess getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new DatabaseAccess(context);
        }

        return instance;
    }

    public boolean create(Document document)
    {
        try
        {
            if(!document.get("UserId").asString().equals(credentialsProvider.getCachedIdentityId()))
            {
                Log.i(TAG, "Cannot modify other user's markers!");
                return false;
            }
        }
        catch(NullPointerException npe)
        {
            Log.e(TAG, npe.getMessage());
        }

        document.put("UserId", credentialsProvider.getCachedIdentityId());
        Log.i(TAG, "Insert document into " + DYNAMODB_TABLE + ", UserId: " + credentialsProvider.getCachedIdentityId() + ", MarkerId: " + document.get("MarkerId").asString());

        table.putItem(document);

        return true;
    }

    public ArrayList<FeralFriend> lookup()
    {
        ArrayList<FeralFriend> friends = new ArrayList<FeralFriend>();
        ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE);
        ScanResult scanResult = client.scan(scanRequest);

        Log.i(TAG, "Creating FeralFriend models from documents in database");

        for(Map<String, AttributeValue> item : scanResult.getItems())
        {
            FeralFriend friend = null;

            try
            {
                friend = new FeralFriend(item.get("UserId").getS(), item.get("MarkerId").getS(), Double.parseDouble(item.get("Lat").getN()), Double.parseDouble(item.get("Lng").getN()),
                    item.get("Title").getS(), item.get("Description").getS(), item.get("LastFed").getS(), item.get("TNR").getBOOL(),
                    Integer.parseInt(item.get("NumFriends").getN()));
            }
            catch(NullPointerException npe)
            {
                Log.e(TAG, npe.getMessage());
            }

            if(friend != null)
            {
                friends.add(friend);
            }
        }

        return friends;
    }

    public boolean delete(Document document)
    {
        try
        {
            if(!document.get("UserId").asString().equals(credentialsProvider.getCachedIdentityId()))
            {
                Log.i(TAG, "Cannot delete other user's markers!");
                return false;
            }
        }
        catch(NullPointerException npe)
        {
            Log.e(TAG, npe.getMessage());
        }

        Log.i(TAG, "Deleting marker ID: " + document.get("MarkerId").asString());

        //Delete document from database
        table.deleteItem(document.get("UserId").asPrimitive(), document.get("MarkerId").asPrimitive());

        return true;
    }

    public String getUserID()
    {
        return credentialsProvider.getCachedIdentityId();
    }
}
