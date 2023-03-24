package com.example.whattoeat.ui.account;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PreferencesPage extends Fragment {

    Button mOut, mHome, mBoth, mSubmit;
    private String mood = "happy";

    RadioGroup radioGroup;

    RadioButton radioButton;

    CheckBox mItalian, mFrench, mMexican, mGreek, mChinese, mAmerican, mTurkish;

    FirebaseDatabase database;
    DatabaseReference preferencesRef;

    FirebaseAuth auth;

    String uid;
    private String selectedLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);


        //Out-Home-Both buttons
        mOut = view.findViewById(R.id.outButton);
        mHome = view.findViewById(R.id.homeButton);
        mBoth = view.findViewById(R.id.bothButton);
        mSubmit = view.findViewById(R.id.submitButton);

        //Radio group
        radioGroup = view.findViewById(R.id.emotions_radio_group);

        //Cuisine check boxes
        mItalian = view.findViewById(R.id.checkBox8);
        mFrench = view.findViewById(R.id.checkBox9);
        mMexican = view.findViewById(R.id.checkBox10);
        mGreek = view.findViewById(R.id.checkBox11);
        mChinese = view.findViewById(R.id.checkBox12);
        mAmerican = view.findViewById(R.id.checkBox13);
        mTurkish = view.findViewById(R.id.checkBox14);

        //Database

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.radio_happy:
                        mood = "happy";
                        break;
                    case R.id.radio_sad:
                        mood = "sad";
                        break;
                    case R.id.radio_excited:
                        mood = "excited";
                        break;
                    case R.id.radio_tired:
                        mood = "tired";
                        break;
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid = auth.getCurrentUser().getUid();
                preferencesRef = FirebaseDatabase.getInstance().getReference().child("Preference");

                //Clear all the previous preferences
                Map<String, Boolean> cuisineMap = new HashMap<>();
                cuisineMap.put("Italian", false);
                cuisineMap.put("French", false);
                cuisineMap.put("Mexican", false);
                cuisineMap.put("Chinese", false);
                cuisineMap.put("American", false);
                cuisineMap.put("Greek", false);
                cuisineMap.put("Turkish", false);
                preferencesRef.child(uid).child("Cuisine").setValue(cuisineMap);

                if (mItalian.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("Italian").setValue(true);
                }
//
                if (mFrench.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("French").setValue(true);
                }
//
                if (mMexican.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("Mexican").setValue(true);
                }
//
                if (mChinese.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("Chinese").setValue(true);
                }
//
                if (mAmerican.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("American").setValue(true);
                }

                if (mGreek.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("Greek").setValue(true);
                }
//
                if (mTurkish.isChecked()) {
                    preferencesRef.child(uid).child("Cuisine").child("Turkish").setValue(true);
                }


                int radioId = radioGroup.getCheckedRadioButtonId();
                DatabaseReference otherPreferences = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Preference");

                //Set the mood and location in the database
                otherPreferences.child(uid).child("Location").setValue(selectedLocation);
                otherPreferences.child(uid).child("Mood").setValue(mood);

                radioButton = view.findViewById(radioId);


                Toast.makeText(getContext(), "Saved: "+ radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        mOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedLocation = "out";
                mOut.setBackgroundColor(Color.parseColor("#0FB652"));
                mHome.setBackgroundColor(Color.TRANSPARENT);
                mBoth.setBackgroundColor(Color.TRANSPARENT);

            }
        });

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedLocation = "home";
                mOut.setBackgroundColor(Color.TRANSPARENT);
                mHome.setBackgroundColor(Color.parseColor("#0FB652"));
                mBoth.setBackgroundColor(Color.TRANSPARENT);

            }
        });

        mBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLocation = "both";
                mOut.setBackgroundColor(Color.TRANSPARENT);
                mHome.setBackgroundColor(Color.TRANSPARENT);
                mBoth.setBackgroundColor(Color.parseColor("#0FB652"));

            }
        });
        return view;
    }
}