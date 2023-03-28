package com.example.whattoeat.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CardWidgetFragment extends Fragment {

    private String restaurant_id = "FOODTEST";
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    public String getRestaurant_type(){
        return restaurant_id;
    }

    public void setRestaurant_type(String type){
        this.restaurant_id = type;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            setRestaurant_type(args.getString("cardType"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cardwidget, container, false);

        TextView mTitle = view.findViewById(R.id.textRestaurantName);
        TextView mLocation = view.findViewById(R.id.textLocationContent);
        TextView mCuisine = view.findViewById(R.id.textCuisineContent);
        TextView mContact = view.findViewById(R.id.textContactContent);
        ImageView mImageView = view.findViewById(R.id.imageRestaurant);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.child("Restaurant")
                .child(getRestaurant_type())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isComplete()){
                            Log.e("Food Card", "error : " + task.toString());
                        } else {
                            Log.d("Food Card", "Success! ");

                            DataSnapshot dataSnapshot = task.getResult();
                            String name = dataSnapshot.child("Name").getValue(String.class);
                            String image = dataSnapshot.child("Image").getValue(String.class);
                            String cuisine = dataSnapshot.child("Style").getValue(String.class);
                            String location = dataSnapshot.child("Location").getValue().toString() + "\n";
                            String phone = dataSnapshot.child("Phone").getValue().toString()+ "\n";
                            String website = dataSnapshot.child("Hyperlink").getValue(String.class);
                            String contact = location + phone + website;

                            mTitle.setText(name);
                            mLocation.setText(location);
                            mCuisine.setText(cuisine);
                            mContact.setText(contact);

                            HashMap<Integer, String> tutorialSteps = new HashMap<>(); // use Integer as key to store step number
                            DataSnapshot tutorialData = dataSnapshot.child("Tutorial");

                            // Loop through the tutorial steps and add them to the HashMap
                            for(DataSnapshot d: tutorialData.getChildren()){
                                int stepNumber = Integer.parseInt(d.getKey()); // get the step number
                                String tutorialContent = d.getValue().toString(); // get the step content
                                tutorialSteps.put(stepNumber, tutorialContent); // add the step to the HashMap
                            }

                            String tutorialText = "";
                            // Sort the tutorial steps by step number and
                            // append each step to the tutorial text
                            // and make sure there the whitespace is only between steps
                            List<Integer> sortedStepNumbers = new ArrayList<>(tutorialSteps.keySet());
                            Collections.sort(sortedStepNumbers);
                            for (int i = 1; i < sortedStepNumbers.size() + 1; i++) {
                                String step = "Step " + i + ": ";
                                String tutorialContent = tutorialSteps.get(i);
                                tutorialText += step + tutorialContent;
                                if (i != sortedStepNumbers.size()) {
                                    tutorialText += "\n \n \n";
                                }
                            }

                            Picasso.with(getActivity().getApplicationContext()).load(image).into(mImageView);
                        }
                    }
                });
        return view;
    }
}