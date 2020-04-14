package com.example.feralfriends;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.feralfriends.models.FeralFriend;

import java.util.Date;

public class FriendActivity extends AppCompatActivity
{
    private static final String TAG = "FriendActivity";
    private static final int REQUEST_CONTACT = 1;
    private static final String ARG_FRIEND_ID = "friend_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 2;
    private static final int RESULT_DELETE = 2;

    private EditText mEditNameText;
    private Button mFedButton;
    private Button mDeleteButton;
    private Button mSaveButton;
    private Button mCancelButton;
    private FeralFriend mFriend;
    private ImageButton mImageButton;
    private EditText mFriendDetails;
    private Button mShareFriendReport;
    private TextView mLastFedTextView;
    private ToggleButton mTNRButton;
    private EditText mNumFriendsTextView;

    public static Intent newIntent(Context packageContext, FeralFriend friend)
    {
        Intent intent = new Intent(packageContext, FriendActivity.class);
        intent.putExtra("friend_model", friend);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mFriend = (FeralFriend) getIntent().getSerializableExtra("friend_model");

        if(mFriend == null)
        {
            Log.i(TAG, "Creating a new FeralFriend");
            mFriend = new FeralFriend();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Date d = mFriend.getDate();
        SimpleDateFormat dateFormatter = new SimpleDateFormat(
        "MM/dd/yyyy");
        String strDate = dateFormatter.format(d);
        mLastFedTextView = findViewById(R.id.last_fed);
        mLastFedTextView.setText(strDate);

        /*Set up title button*/
        mEditNameText = findViewById(R.id.friend_title);
        mEditNameText.setText(mFriend.getTitle());
        mEditNameText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                //this is useless
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                mFriend.setTitle(charSequence.toString());
                Context context = getApplicationContext();
                Toast.makeText(context, "onTextChanged()", Toast.LENGTH_SHORT);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //this is useless
            }
        });
        /*Setup the details button so when the user changes the text it updates*/
        mFriendDetails = findViewById(R.id.friend_details);
        mFriendDetails.setText(mFriend.getDetails());
        mFriendDetails.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                //this is useless
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                mFriend.setDetails(charSequence.toString());
                Context context = getApplicationContext();
                Toast.makeText(context, "Changed details", Toast.LENGTH_SHORT);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //this is useless
            }
        });
        /*Set up the fed button*/
        mFedButton = findViewById(R.id.friend_fed);
        mFedButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "Fed FeralFriend button was clicked");

                mFriend.setFed(true);
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mFriend.getDate());
                dialog.show(fragmentManager, DIALOG_DATE);

                Log.i(TAG, "DatePickerFragment new instance was called");

                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                mLastFedTextView.setText(dateFormatter.format(mFriend.getDate()));
                Context context = getApplicationContext();
                Toast.makeText(context, mFriend.getDate().toString(), Toast.LENGTH_SHORT);
            }
        });
        /*Allow the user to share the basic information about the friend*/
        mShareFriendReport = findViewById(R.id.friend_report);
        mShareFriendReport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String report = mFriend.buildReport();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, report);
                i.putExtra(Intent.EXTRA_SUBJECT, "FERAL FRIEND REPORT");
                startActivity(i);

                Log.i(TAG, "Share FeralFriend button was clicked");

                Context context = getApplicationContext();
                Toast.makeText(context, "Sharing Friend Report", Toast.LENGTH_SHORT);
            }
        });
        /*Set up if the animal has been tnr'ed*/
        mTNRButton = findViewById(R.id.button_tnr);
        mTNRButton.setChecked(mFriend.isTNRed());
        mTNRButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                Log.i(TAG, "mTNRButton PRESSED");
                if(b)
                {
                    mFriend.setTNRed(true);
                    Context context = getApplicationContext();
                    Log.i(TAG, "Our Friend is TNR'd");
                    Toast.makeText(context, "Our Friend is TNR'd", Toast.LENGTH_SHORT);
                }
                else
                {
                    mFriend.setTNRed(false);
                    Context context = getApplicationContext();
                    Log.i(TAG, "Our Friend is NOT TNR'd");
                    Toast.makeText(context, "Our Friend is NOT TNR'd", Toast.LENGTH_SHORT);
                }
                Context context = getApplicationContext();
                Toast.makeText(context, "Our Friend is NOT TNR'd", Toast.LENGTH_SHORT);

            }
        });

        mCancelButton = findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "Cancel FeralFriend button was clicked");

                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        /*Set up delete button*/
        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "Delete FeralFriend button was clicked");

                Intent intent = new Intent();
                intent.putExtra("friend_model", mFriend);
                setResult(RESULT_DELETE, intent);

                finish();
            }
        });
        /*User can identify how many cats present textview*/
        mNumFriendsTextView = findViewById(R.id.number_friends);
        mNumFriendsTextView.setText(String.valueOf(mFriend.getNumberOfFriends()));
        mNumFriendsTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                try
                {
                    mFriend.setNumberOfFriends(Integer.parseInt(charSequence.toString()));
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Changed number of friends: " + Integer.parseInt(charSequence.toString()), Toast.LENGTH_SHORT);
                    Log.i(TAG, "Changed number of friends: " + Integer.parseInt(charSequence.toString()));
                }
                catch(NumberFormatException nfe)
                {
                    Log.e(TAG, nfe.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "Save FeralFriend button was clicked");

                Intent intent = new Intent();
                intent.putExtra("friend_model", mFriend);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }
}
