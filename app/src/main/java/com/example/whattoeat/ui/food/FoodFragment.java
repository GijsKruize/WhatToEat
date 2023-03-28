package com.example.whattoeat.ui.food;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private List<String> recipeIds = new ArrayList<>();
    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeImages = new ArrayList<>();
    private List<String> recipeStyles = new ArrayList<>();

    private List<String> historyFilter = new ArrayList<>();


    private ProgressBar progressBar;
    Koloda koloda;

    public FoodFragment(String mood) {
        this.mood = mood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);
        progressBar = rView.findViewById(R.id.progressBar2);
        koloda = rView.findViewById(R.id.koloda);

        Toast.makeText(this.getContext(), "Swipe meals, just like on Tinder!", Toast.LENGTH_SHORT).show();
        return rView;
    }

    @Override
    public void onResume() {
        super.onResume();
        filterData();
        getRestaurants();
    }

    protected void filterData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference historyRef = database.getReference("Swipe History");
        historyRef.child(uid).child(mood).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot historySnapshot : snapshot.getChildren()) {
                    historyFilter.add(historySnapshot.getKey());
                }
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
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);
                    String recipeStyle = recipeSnapshot.child("Style").getValue(String.class);
                    if (!historyFilter.contains(recipeId)) {
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
                    if (!historyFilter.contains(recipeId)) {
                        recipeIds.add(recipeId);
                        recipeNames.add(recipeName);
                        recipeImages.add(recipeImage);
                        recipeStyles.add(recipeStyle);
                    }
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
                progressBar.setVisibility(View.GONE);
                adapter = new SwipeAdapter(getContext(), recipeIds, recipeNames, recipeImages);
                koloda.setAdapter(adapter);
                listener = new SwipeListener(getContext(), recipeIds, recipeStyles, mood);
                koloda.setKolodaListener(listener);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });

    }

}