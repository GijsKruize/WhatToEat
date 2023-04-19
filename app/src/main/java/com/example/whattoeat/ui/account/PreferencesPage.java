package com.example.whattoeat.ui.account;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import java.util.ArrayList;
import java.util.List;


public class PreferencesPage extends Fragment {

    Button mOut, mHome, mBoth, mSubmit, mReset;
    private String mood;

    FirebaseDatabase database;
    DatabaseReference preferencesRef;
    DatabaseReference historyRef;
    FirebaseAuth auth;
    String uid;
    private String selectedLocation;
    private List<String> moods = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods.add(0, "Happy");
        moods.add(1, "Sad");
        moods.add(2, "Angry");
        moods.add(3, "Tired");
        moods.add(4, "Romantic");
        moods.add(5, "Stressed");
        moods.add(6, "Excited");
        moods.add(7, "Funky");

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
        mReset = view.findViewById(R.id.resetHistoryButton);

        //Database
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        historyRef = FirebaseDatabase.getInstance().getReference().child("Swipe History");
        preferencesRef = FirebaseDatabase.getInstance().getReference().child("Preference");
        preferencesRef.child(uid).child("Mood")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        this.mood = (String) task.getResult().getValue();
                    }
                });
        preferencesRef.child(uid).child("Location")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        // Get the location from the database and set the colors of the buttons to default values
                        String location = (String) task.getResult().getValue();
                        mOut.setBackgroundColor(Color.LTGRAY);
                        mHome.setBackgroundColor(Color.LTGRAY);
                        mBoth.setBackgroundColor(Color.LTGRAY);
                        selectedLocation = location;

                        // If the location is null, set it to both
                        if (location == null) {
                            location = "both";
                            selectedLocation = "both";
                        }
                        // Set the color of the button that is pressed
                        if (location.equals("both")) {
                            mBoth.setBackgroundColor(Color.parseColor("#0FB652"));
                        } else if (location.equals("out")) {
                            mOut.setBackgroundColor(Color.parseColor("#0FB652"));
                        } else if (location.equals("home")) {
                            mHome.setBackgroundColor(Color.parseColor("#0FB652"));
                        }
                    }
                });


        // Radio buttons, get id from the one that is pressed
        MultiLineRadioGroup mMultiLineRadioGroup = (MultiLineRadioGroup) view.findViewById(R.id.main_activity_multi_line_radio_group);
        mMultiLineRadioGroup.setOnCheckedChangeListener((MultiLineRadioGroup.OnCheckedChangeListener) (group, button) -> {
            int buttonID = ((button.getId() - 1) % 8);
            mood = moods.get(buttonID);
        });

        // Reset the users swiping history
        mReset.setOnClickListener(view1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Deleting your accounts swiping history" +
                    " cannot be undone.");
            dialog.setPositiveButton("Delete", (dialogInterface, i) -> historyRef.child(uid).removeValue());

            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });

        mSubmit.setOnClickListener(v -> {
            //Set the mood and location in the database
            preferencesRef.child(uid).child("Location").setValue(selectedLocation);
            preferencesRef.child(uid).child("Mood").setValue(mood);
            Toast.makeText(getContext(), "Saved: " + mood, Toast.LENGTH_SHORT).show();
        });

        //set the location value to out
        mOut.setOnClickListener(v -> {
            selectedLocation = "out";
            mOut.setBackgroundColor(Color.parseColor("#0FB652"));
            mHome.setBackgroundColor(Color.LTGRAY);
            mBoth.setBackgroundColor(Color.LTGRAY);

        });

        //set the location value to home
        mHome.setOnClickListener(v -> {
            selectedLocation = "home";
            mOut.setBackgroundColor(Color.LTGRAY);
            mHome.setBackgroundColor(Color.parseColor("#0FB652"));
            mBoth.setBackgroundColor(Color.LTGRAY);

        });

        //set the location value to both
        mBoth.setOnClickListener(v -> {
            selectedLocation = "both";
            mOut.setBackgroundColor(Color.LTGRAY);
            mHome.setBackgroundColor(Color.LTGRAY);
            mBoth.setBackgroundColor(Color.parseColor("#0FB652"));

        });
        return view;
    }
}