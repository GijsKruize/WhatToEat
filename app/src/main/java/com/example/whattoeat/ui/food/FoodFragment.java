package com.example.whattoeat.ui.food;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.library.Koloda;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private SwipeListener listener;
    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeImages = new ArrayList<>();
    private List<String> recipeIds = new ArrayList<>();
    Koloda koloda;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://what-to-eat-tue-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("Recipe");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeIds.clear();
                recipeNames.clear();
                recipeImages.clear();
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String recipeId = recipeSnapshot.getKey();
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);

                    recipeIds.add(recipeId);
                    recipeNames.add(recipeName);
                    recipeImages.add(recipeImage);
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
                System.out.println(recipeIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);
        koloda = rView.findViewById(R.id.koloda);
        adapter = new SwipeAdapter(this.getContext(), this.recipeImages, this.recipeNames);
        listener = new SwipeListener(this.getContext());
        koloda.setAdapter(adapter);
        koloda.setKolodaListener(listener);
        Toast.makeText(this.getContext(), "Swipe meals, just like on Tinder!", Toast.LENGTH_SHORT).show();
        return rView;
    }

}