package com.example.feralfriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.feralfriends.models.FeralFriend;

public class FriendActivity extends AppCompatActivity
{
    private static final int REQUEST_CONTACT = 1;
    private static final String ARG_FRIEND_ID = "friend_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 2;

    private EditText mEditNameText;
    private Button mDateButton;
    private CheckBox mFedCheckBox;
    private Button mDeleteButton;
    private Button mSaveButton;
    private FeralFriend mFriend;
    private ImageButton mImageButton;

    public static Intent newIntent(Context packageContext)
    {
        Intent intent = new Intent(packageContext, FriendActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mFriend = new FeralFriend();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        mEditNameText = findViewById(R.id.friend_title);
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

        mDateButton = findViewById(R.id.friend_date);
        updateDate();
        mDateButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //FragmentManager fragmentManager = getFragmentManager();
                //DatePickerFragment dialog = DatePickerFragment.newInstance(mFriend.getmDate());
                //dialog.setTargetFragment(FriendActivity.this, REQUEST_DATE);
                //dialog.show(fragmentManager, DIALOG_DATE);
                Context context = getApplicationContext();
                Toast.makeText(context, "onCheckedChanged()", Toast.LENGTH_SHORT);
            }
        });


        mDeleteButton = findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //buildAlertDialog();
                Context context = getApplicationContext();
                Toast.makeText(context, "onTextChanged()", Toast.LENGTH_SHORT);
            }
        });

        mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });



    }

    private void updateDate()
    {
        //mDateButton.setText(mCrime.getmDate().toString());
    }
}
