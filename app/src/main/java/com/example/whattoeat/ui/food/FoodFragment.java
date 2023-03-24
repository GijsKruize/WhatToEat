package com.example.whattoeat.ui.food;

import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.library.Koloda;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private SwipeListener listener;
    private List<String> img;
    private List<String> name;
    Koloda koloda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);
        try {
            generateList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        koloda = rView.findViewById(R.id.koloda);
        adapter = new SwipeAdapter(this.getContext(), this.img, this.name);
        listener = new SwipeListener(this.getContext());
        koloda.setAdapter(adapter);
        koloda.setKolodaListener(listener);
        Toast.makeText(this.getContext(), "Swipe meals, just like on Tinder!", Toast.LENGTH_SHORT).show();

        return rView;
    }

    public void generateList() throws IOException {
        ArrayList<String> tempImg = new ArrayList<>();
        ArrayList<String> tempName = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://what-to-eat-tue-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("Recipe");

        for (int i=1; i<3; i++) {
            myRef.child(String.valueOf(i)).child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String test = String.valueOf(task.getResult().getValue());
                    tempName.add(test);
                    System.out.println(test);
                    System.out.println(tempName);
                }
            });
            myRef.child(String.valueOf(i)).child("Image").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    tempImg.add(String.valueOf(task.getResult().getValue()));
                    System.out.println(String.valueOf(task.getResult().getValue()));

                }
            });
        }
        System.out.println(tempImg);
        System.out.println(tempImg);
        this.name = tempName;
        this.img = tempImg;
    }
}