package com.example.whattoeat.ui.food;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.PreferencesPage;
import com.google.firebase.auth.FirebaseAuth;
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
    private String uid;
    DatabaseReference preferencesRef;

    private List<String> moods = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        moods.clear();
        super.onCreate(savedInstanceState);
        this.mood = "None";
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        preferencesRef = FirebaseDatabase.getInstance().getReference().child("Preference");
        moods.add(0, "NONE");
        moods.add(1, "Happy");
        moods.add(2, "Sad");
        moods.add(3, "Angry");
        moods.add(4, "Tired");
        moods.add(5, "Romantic");
        moods.add(6, "Stressed");
        moods.add(7, "Excited");
        moods.add(8, "Funky");

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
                mood = moods.get(button.getId());
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
                otherPreferences.child(uid).child("Mood").setValue(mood);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_mood);
                if (currentFragment instanceof fragment_mood) {
                    return; // do nothing if food_card fragment is already displayed
                }

                //change the fragment
                Fragment fragment = new FoodFragment(mood);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_mood, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // remove the homepage fragment from the screen
                Fragment moodfragment = fragmentManager.findFragmentById(R.id.fragment_mood);
                if (moodfragment != null) {
                    fragmentTransaction.remove(moodfragment);
                }

            }

        });

        return view;
    }

}