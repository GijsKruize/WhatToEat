package com.example.whattoeat.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.whattoeat.R;
import com.yalantis.library.Koloda;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private SwipeListener listener;
    private List<Integer> img;
    private List<String> name;
    Koloda koloda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);

        koloda = rView.findViewById(R.id.koloda);
        img = new ArrayList<>();
        name = new ArrayList<>();
        name.add("test1");
        name.add("test2");
        img.add(R.drawable.meal);
        img.add(R.drawable.meal);
        adapter = new SwipeAdapter(this.getContext(), img, name);
        listener = new SwipeListener(this.getContext());
        koloda.setAdapter(adapter);
        koloda.setKolodaListener(listener);
        Toast.makeText(this.getContext(), "Swipe meals, just like on Tinder!", Toast.LENGTH_SHORT).show();

        return rView;
    }
}