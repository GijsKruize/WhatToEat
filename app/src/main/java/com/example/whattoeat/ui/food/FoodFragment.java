package com.example.whattoeat.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.whattoeat.R;
import com.yalantis.library.Koloda;
import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private SwipeAdapter adapter;
    private List<Integer> list;
    Koloda koloda;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        koloda = getView().findViewById(R.id.koloda);
//        list = new ArrayList<>();
//        adapter = new SwipeAdapter(this.getContext(),list);
//        koloda.setAdapter(adapter);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.koloda, container, false);

        koloda = rView.findViewById(R.id.koloda);
        list = new ArrayList<>();
        adapter = new SwipeAdapter(this.getContext(),list);
        koloda.setAdapter(adapter);

        return rView;
    }
}