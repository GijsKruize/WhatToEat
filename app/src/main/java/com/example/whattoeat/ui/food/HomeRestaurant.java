package com.example.whattoeat.ui.food;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whattoeat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.LayoutInflater;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class HomeRestaurant extends Fragment {
    PieChart pieChart;
    TextView viewCountOfPage;
    TextView likeCountOfPage;
    TextView skipCountOfPage;
    DatabaseReference database;


//    @Override
//    public View onCreateView(LayoutInflater inflater, Bundle savedInstanceState, ViewGroup container) {
////        super.onCreate(savedInstanceState);
//        View view = inflater.inflate(R.layout.fragment_home_page_restaurant, container, false);
////        setContentView(R.layout.fragment_home_page_restaurant);
//        pieChart = view.findViewById(R.id.piechart);
//        viewCountOfPage = view.findViewById(R.id.viewCount);
//        likeCountOfPage = view.findViewById(R.id.likesCount);
//        skipCountOfPage = view.findViewById(R.id.skipCount);
//        database = FirebaseDatabase.getInstance().getReference("Meal Relation");
//
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Integer positive = snapshot.child("Positive").getValue(Integer.class);
//                Integer negative = snapshot.child("Negative").getValue(Integer.class);
//                if (positive != null && negative != null) {
//                    likeCountOfPage.setText(positive);
//                    skipCountOfPage.setText(negative);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        addData();
//        return view;
//    }
//    public int convertPercentage(TextView likeCountOfPage, TextView skipCountOfPage, int index) {
//        int n = Integer.parseInt(likeCountOfPage.getText().toString());
//        int m = Integer.parseInt(skipCountOfPage.getText().toString());
//        int percentage_1 = (n / (n + m)) * 100;
//        int percentage_2 = (m / (n + m)) * 100;
//        int[] percentages = {percentage_1, percentage_2};
//        int totalViews = n + m;
//        return percentages[index];
//    }
//
//
//
//
//    private void addData() {
//
//
//        likeCountOfPage.setText(Integer.toString(convertPercentage(likeCountOfPage, skipCountOfPage, 0)));
//        pieChart.addPieSlice(
//                new PieModel(
//                        "Liked",
//                        Integer.parseInt(likeCountOfPage.getText().toString()),
//                        Color.parseColor("#0FB652")
//                )
//        );
//
//        skipCountOfPage.setText(Integer.toString(convertPercentage(likeCountOfPage, skipCountOfPage, 2)));
//        pieChart.addPieSlice((
//                new PieModel(
//                        "Skipped",
//                        Integer.parseInt(skipCountOfPage.getText().toString()),
//                        Color.parseColor("#7B7E7C"))
//                )
//        );
//        int likedCount = Integer.parseInt(likeCountOfPage.getText().toString());
//        int skipCount = Integer.parseInt(skipCountOfPage.getText().toString());
//
//        viewCountOfPage.setText(Integer.toString(likedCount + skipCount));
//
//    }

}
