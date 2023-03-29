package com.example.whattoeat.ui.account;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.j256.ormlite.stmt.query.In;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    PieChart pieChart;
    TextView mostLinkedMood, totalLikes;
    FirebaseDatabase database;
    DatabaseReference statsRef;
    String uid;
    Integer trueCount, totLikes, sumOfLikes;
    String mostFrequentMood;
    HashMap<String, Integer> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        pieChart = view.findViewById(R.id.piechart);
        mostLinkedMood = view.findViewById(R.id.mostLinkedMood);
        totalLikes = view.findViewById(R.id.textTotalLikes);
        addData();

        return view;
    }

    /**
     * Adds data to textviews and piechart
     */
    private void addData() {
        statsRef = database.getReference("Restaurant");

        //gets likes of the restaurant and adds it to textView
        getStats(statsRef.getKey());
        mostFrequentMood = getMostFrequentMood(map);
        mostLinkedMood.setText(mostFrequentMood);

        //gets the total like and adds it to textView
        sumOfLikes = getTotalLikes(map);
        totalLikes.setText(String.valueOf(sumOfLikes));

        //Adding values to piechart
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            pieChart.addPieSlice(new PieModel(entry.getKey(), entry.getValue(),
                    Color.parseColor("#" + Integer.toHexString((int) (Math.random() * 16777215)))));
        }
    }

    /**
     * returns a map with moods as keys and total number of trues for a restaurant in that mood as values
     * @param restaurantKey
     * @return  map
     *
     */
    public HashMap<String,Integer> getStats(String restaurantKey) {
//
        statsRef = database.getReference("Swipe History").child(uid);
        statsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot moodSnapshot : snapshot.getChildren()) {
                    trueCount = 0;
                    for (DataSnapshot restaurantSnapshot : moodSnapshot.getChildren()) {
                        if (restaurantSnapshot.getKey().equals(restaurantKey)
                                && Boolean.TRUE.equals(restaurantSnapshot.getValue(Boolean.class))) {
                            trueCount++;
                        }
                    }
                    map.put(moodSnapshot.getKey(), trueCount);
                    Log.d("Firebase", "moods: " + moodSnapshot.getKey() + " - true count: " + trueCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return map;
    }

    public String getMostFrequentMood(HashMap<String, Integer> map) {
        String mostFrequentMood = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentMood = entry.getKey();
            }
        }
        return mostFrequentMood;
    }

    public int getTotalLikes(HashMap<String, Integer> map) {
        totLikes = 0;
        for(Integer value : map.values()) {
            totLikes += value;
        }
        return totLikes;
    }
}
