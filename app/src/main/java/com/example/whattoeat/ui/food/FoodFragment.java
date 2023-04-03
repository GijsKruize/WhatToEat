package com.example.whattoeat.ui.food;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.library.Koloda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private SwipeListener listener;
    private String mood;
    private String location;
    private List<String> recipeIds = new ArrayList<>();
    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeImages = new ArrayList<>();
    private List<String> recipeStyles = new ArrayList<>();

    private List<String> historyFilter = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference preferenceRef;


    private ProgressBar progressBar;
    Koloda koloda;

    public FoodFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = rView.findViewById(R.id.progressBar2);
        koloda = rView.findViewById(R.id.koloda);
        return rView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        getUserPref();

    }
    protected void getUserPref(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        preferenceRef = database.getReference("Preference");
        preferenceRef.child(user.getUid()).child("Mood")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        this.mood = (String) task.getResult().getValue();
                    }
                });
        preferenceRef.child(user.getUid()).child("Location")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        this.location = (String) task.getResult().getValue();
                        filterData();
                    }
                });
    }
    protected void filterData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference historyRef = database.getReference("Swipe History");
        historyRef.child(uid).child(mood).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot historySnapshot : snapshot.getChildren()) {
                    historyFilter.add(historySnapshot.getKey());
                    Log.d("Firebase", "Filter: " + historySnapshot.getKey());
                }
                getRestaurants();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected void getRestaurants() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("Restaurant");
        recipeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeIds.clear();
                recipeNames.clear();
                recipeImages.clear();
                recipeStyles.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String recipeId = recipeSnapshot.getKey();
                    System.out.println(recipeSnapshot.getKey());
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);
                    String recipeStyle = recipeSnapshot.child("Style").getValue(String.class);
                    boolean verified = (Boolean) recipeSnapshot.child("Verified").getValue(Boolean.class);
                    boolean wanted = location.equals("both") || location.equals("out");
                    if (!historyFilter.contains(recipeId) && verified == true && wanted ) {
                        recipeIds.add(recipeId);
                        recipeNames.add(recipeName);
                        recipeImages.add(recipeImage);
                        recipeStyles.add(recipeStyle);
                    }
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
                getRecipes();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });

    }

    protected void getRecipes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("Recipe");
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String recipeId = recipeSnapshot.getKey();
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);
                    String recipeStyle = recipeSnapshot.child("Style").getValue(String.class);
                    boolean wanted = location.equals("both") || location.equals("home");
                    if (!historyFilter.contains(recipeId) && wanted) {
                        recipeIds.add(recipeId);
                        recipeNames.add(recipeName);
                        recipeImages.add(recipeImage);
                        recipeStyles.add(recipeStyle);
                    }
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
                progressBar.setVisibility(View.GONE);
                if (!recipeIds.isEmpty()) {
                    adapter = new SwipeAdapter(getContext(), recipeIds, recipeNames, recipeImages);
                    koloda.setAdapter(adapter);
                    listener = new SwipeListener(getContext(), recipeIds, recipeStyles, mood);
                    koloda.setKolodaListener(listener);
                    adapter.notifyDataSetChanged();
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });

    }

}