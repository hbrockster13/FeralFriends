package com.example.feralfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.feralfriends.Database.DatabaseAccess;

public class FriendActivity extends AppCompatActivity
{

    Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        CreateItemAsyncTask task = new CreateItemAsyncTask();
        task.execute(new Document());
    }

    private class CreateItemAsyncTask extends AsyncTask<Document, Void, Void>
    {
        @Override
        protected Void doInBackground(Document... documents)
        {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(FriendActivity.this);
            databaseAccess.create(documents[0]);

            return null;
        }
    }
}
