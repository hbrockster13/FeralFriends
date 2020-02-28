package com.example.feralfriends.Database;

import android.content.Context;
import android.util.Log;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
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

    public void create(Document document)
    {
        document.put("UserId", credentialsProvider.getCachedIdentityId());
        Log.i(TAG, document.get("UserId").toString());

        table.putItem(document);
    }

    public ArrayList<Document> lookup()
    {
        ArrayList<Document> documents = new ArrayList<Document>();
        ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE);
        ScanResult scanResult = client.scan(scanRequest);

        for(Map<String, AttributeValue> item : scanResult.getItems())
        {
            Document document = new Document();
            document.put("UserId", item.get("UserId").getS());
            document.put("MarkerId", item.get("MarkerId").getS());
            document.put("Lat", item.get("Lat").getN());
            document.put("Lng", item.get("Lng").getN());
            document.put("Title", item.get("Title").getS());

            documents.add(document);
        }

        return documents;
    }
}
