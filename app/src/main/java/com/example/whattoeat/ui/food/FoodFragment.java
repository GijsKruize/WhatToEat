package com.example.whattoeat.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.whattoeat.R;
import com.yalantis.library.Koloda;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private SwipeListener listener;
    private List<Integer> list;
    Koloda koloda;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);

        koloda = rView.findViewById(R.id.koloda);
        list = new ArrayList<>();
        adapter = new SwipeAdapter(this.getContext(),list);
        listener = new SwipeListener(this.getContext());
        koloda.setAdapter(adapter);
        koloda.setKolodaListener(listener);
        Toast.makeText(this.getContext(), "Swipe meals, just like on Tinder!", Toast.LENGTH_SHORT).show();

        return rView;
    }
}