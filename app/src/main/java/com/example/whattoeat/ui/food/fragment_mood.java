package com.example.whattoeat.ui.food;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.PreferencesPage;
import com.example.whattoeat.ui.home.food_card;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_mood extends Fragment {
    RadioGroup radioGroup;
    Button mSubmit;
    RadioButton radioButton;
    private String mood;
    FirebaseAuth auth;
    FirebaseUser user;
    private String UID;
    DatabaseReference preferencesRef;

    private List<String> moods = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        moods.clear();
        super.onCreate(savedInstanceState);
        this.mood = "None";
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        UID = user.getUid();
        preferencesRef = FirebaseDatabase.getInstance().getReference().child("Preference");
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
        View view = inflater.inflate(R.layout.fragment_mood, container, false);


        mSubmit = view.findViewById(R.id.buttonSubmitMood);

        MultiLineRadioGroup mMultiLineRadioGroup = (MultiLineRadioGroup) view.findViewById(R.id.main_activity_multi_line_radio_group);

        mMultiLineRadioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ViewGroup group, RadioButton button) {
                int buttonID = ((button.getId()-1) % 8);
                mood = moods.get(buttonID);
            }
        });


        mSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatabaseReference otherPreferences = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Preference");

                //Set the mood and location in the database
                otherPreferences.child(UID)
                        .child("Mood")
                        .setValue(mood)
                        .addOnCompleteListener(task ->
                                Log.d("Mood page: ", "Mood added to user!"))
                        .addOnFailureListener(e ->
                                Log.d("Mood page: ", e.toString())
                        );

                FragmentManager fragmentManager = getChildFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_mood);
                if (currentFragment instanceof FoodFragment) {
                    return; // do nothing if Preferences fragment is already displayed
                }

                //Get the new fragment
                Fragment fragment2 = new FoodFragment(mood);

                //Change it
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_mood, fragment2, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("mood selector")
                        .commit();
            }


        });

        return view;
    }

}