package com.example.feralfriends;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FriendFragment extends Fragment
{
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mFedCheckBox;
    private Button mDeleteButton;
    private Button mSaveButton;
    private int REQUEST_DATE = 0;


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
        mTitleField = v.findViewById(R.id.friend_title);
        //mTitleField.setText(mTitle.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                //this is useless
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                //mCrime.setTitle(charSequence.toString());
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
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(FriendFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mFedCheckBox = v.findViewById(R.id.crime_solved);
        //mFedCheckBox.setChecked(mCrime.ismSolved());
        mFedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
               // mCrime.setmSolved(b);
                Toast.makeText(getContext(), "onCheckedChanged()", Toast.LENGTH_SHORT);
            }
        });

        mDeleteButton = v.findViewById(R.id.delete_friend);
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
        mDateButton.setText(mCrime.getmDate().toString());
    }
}
