//package com.example.whattoeat.ui.account;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import com.example.whattoeat.R;
//import com.example.whattoeat.ui.food.FoodFragment;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//
//public class Preferences extends Fragment {
//    Button mOut, mHome, mBoth, mSubmit;
//
//    RadioGroup radioGroup;
//
//    RadioButton radioButton;
//
//    CheckBox mItalian, mFrench, mMexican, mGreek, mChinese, mAmerican, mTurkish;
//
//    FirebaseDatabase database;
//    DatabaseReference preferencesRef;
//
//    FirebaseAuth auth;
//
//    String uid;
//
//    private String selectedLocation;
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
//        setContentView(R.layout.fragment_preferences);
//
//        //Out-Home-Both buttons
//        mOut = findViewById(R.id.outButton);
//        mHome = findViewById(R.id.homeButton);
//        mBoth = findViewById(R.id.bothButton);
//        mSubmit = findViewById(R.id.submitButton);
//
//        //Radio group
//        radioGroup = findViewById(R.id.emotions_radio_group);
//
//        //Cuisine check boxes
//        mItalian = findViewById(R.id.checkBox8);
//        mFrench = findViewById(R.id.checkBox9);
//        mMexican = findViewById(R.id.checkBox10);
//        mGreek = findViewById(R.id.checkBox11);
//        mChinese = findViewById(R.id.checkBox12);
//        mAmerican = findViewById(R.id.checkBox13);
//        mTurkish = findViewById(R.id.checkBox14);
//
//        //Database
//
//        database = FirebaseDatabase.getInstance();
//        auth = FirebaseAuth.getInstance();
//
//        mSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uid = auth.getCurrentUser().getUid();
//                preferencesRef = FirebaseDatabase.getInstance().getReference().child("Preference");
//
//                if (mItalian.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("Italian").setValue(true);
//                }
//
////
//                if (mFrench.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("French").setValue(true);
//                }
////
//                if (mMexican.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("Mexican").setValue(true);
//                }
////
//                if (mChinese.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("Chinese").setValue(true);
//                }
////
//                if (mAmerican.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("American").setValue(true);
//                }
//
//                if (mGreek.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("Greek").setValue(true);
//                }
////
//                if (mTurkish.isChecked()) {
//                    preferencesRef.child(uid).child("Cuisine").child("Turkish").setValue(true);
//                }
//
//
//                int radioId = radioGroup.getCheckedRadioButtonId();
//
//                radioButton = view.findViewById(radioId);
//
//
//                Toast.makeText(getContext(), "Saved: "+ radioButton.getText(), Toast.LENGTH_SHORT).show();
//
////                Intent intent = new Intent(Preferences.this, AccountPageFragment.class);
////                startActivity(intent);
//
//            }
//        });
//
//
//        mOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                    preferencesRef. setValue(selectedLocation);
//                selectedLocation = "out";
//                mOut.setBackgroundColor(Color.GREEN);
//                mHome.setBackgroundColor(Color.TRANSPARENT);
//                mBoth.setBackgroundColor(Color.TRANSPARENT);
//
//            }
//        });
//
//        mHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                selectedLocation = "home";
//                mOut.setBackgroundColor(Color.TRANSPARENT);
//                mHome.setBackgroundColor(Color.GREEN);
//                mBoth.setBackgroundColor(Color.TRANSPARENT);
//
//            }
//        });
//
//        mBoth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedLocation = "both";
//                mOut.setBackgroundColor(Color.TRANSPARENT);
//                mHome.setBackgroundColor(Color.TRANSPARENT);
//                mBoth.setBackgroundColor(Color.GREEN);
//
//            }
//        });
//
//        return view;
//    }
//
//    public void checkButton(View v){
//        int radioId = radioGroup.getCheckedRadioButtonId();
//
//        radioButton = v.findViewById(radioId);
//
//        Toast.makeText(getContext(), "Selected Radio Button: " + radioButton.getText(),
//                Toast.LENGTH_SHORT).show();
//    }
//}
//
