package com.example.feralfriends;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feralfriends.models.FeralFriend;

public class FriendFragment extends Fragment
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


    public static FriendFragment newInstance()
    {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_friend, container, false);
        mEditNameText = v.findViewById(R.id.friend_title);

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
                Toast.makeText(getContext(), "onTextChanged()", Toast.LENGTH_SHORT);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //this is useless
            }
        });

        mDateButton = v.findViewById(R.id.friend_date);
        updateDate();
        mDateButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mFriend.getmDate());
                dialog.setTargetFragment(FriendFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
                Toast.makeText(getContext(), "onCheckedChanged()", Toast.LENGTH_SHORT);
            }
        });


        mDeleteButton = v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //buildAlertDialog();
                Toast.makeText(getContext(), "onTextChanged()", Toast.LENGTH_SHORT);
            }
        });

        mSaveButton = v.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });

        return v;
    }

    private void updateDate()
    {
        //mDateButton.setText(mCrime.getmDate().toString());
    }
}
