package com.example.whattoeat.ui.account;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    PieChart pieChart;
    TextView mostLinkedMood, totalLikes, fieldMood, fieldLikes;
    DatabaseReference statsRef, ownerRef;
    String uid;
    Integer trueCount, totLikes, sumOfLikes;
    String mostFrequentMood;
    FirebaseUser user;
    LinearLayout graphLegend;
    HashMap<String, Integer> map = new HashMap<>();

    public interface StatsCallback {
        void onStatsReceived(HashMap<String, Integer> stats);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        uid = FirebaseAuth.getInstance().getUid();
        pieChart = view.findViewById(R.id.piechart);
        mostLinkedMood = view.findViewById(R.id.mostLinkedMood);
        totalLikes = view.findViewById(R.id.textTotalLikes);
        fieldLikes = view.findViewById(R.id.likesCount);
        fieldMood = view.findViewById(R.id.frequentMood);
        graphLegend = view.findViewById(R.id.legend_container);
        addData();
        return view;
    }

    /**
     * Adds data to textviews and piechart
     */
    private void addData() {
//        statsRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        statsRef = FirebaseDatabase.getInstance().getReference("Swipe History");
        ownerRef = FirebaseDatabase.getInstance().getReference("User").child(uid);
//        map.clear();
//        pieChart.clearChart();
//        graphLegend.removeAllViews();

        // replace ownerRef.child("Restaurant").getKey() with "Restaurant_1" if there is no result
        // some restaurants haven't been swiped yet so there might be no data.
        getStats(ownerRef.child("Restaurant").getKey(), new StatsCallback() {
            @Override
            public void onStatsReceived(HashMap<String, Integer> stats) {
                mostFrequentMood = getMostFrequentMood(map);
                fieldMood.setText(mostFrequentMood);

                //gets the total like and adds it to textView
                sumOfLikes = getTotalLikes(map);
                fieldLikes.setText(String.valueOf(sumOfLikes));

                //Adding values to piechart
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if(!map.isEmpty()) {
                        int color = Color.parseColor("#" + Integer.toHexString((int) (Math.random() * 16777215)));
                        pieChart.addPieSlice(new PieModel(entry.getKey(), entry.getValue(), color));
                        TextView legendItem = new TextView(getContext());
                        legendItem.setText(entry.getKey());
                        legendItem.setTextSize(16);
                        legendItem.setTextColor(color);
                        graphLegend.addView(legendItem);
                    }
                    else {
                        Log.d("Statistics", "There is no data to show right now.");
                    }

                }

            }

        });


    }

    /**
     * returns a map with moods as keys and total number of trues for a restaurant in that mood as values
     * @param restaurantKey
     * @return  map
     *
     */
    public HashMap<String,Integer> getStats(String restaurantKey, StatsCallback callback) {
        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot userSnapshot : snapshot.getChildren()) {

                    for (DataSnapshot moodSnapshot : userSnapshot.getChildren()) {
                        trueCount = 0;
                        for (DataSnapshot restaurantSnapshot : moodSnapshot.getChildren()) {
                            if (Objects.equals(restaurantSnapshot.getKey(), restaurantKey)
                                    && Boolean.TRUE.equals(restaurantSnapshot.getValue(Boolean.class))) {
                                trueCount++;
                                map.put(moodSnapshot.getKey(), trueCount);
                            }
                        }

                        Log.d("Firebase", "moods: " + moodSnapshot.getKey() + " - true count: " + trueCount);
                    }
                }

                callback.onStatsReceived(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return map;
    }

    public String getMostFrequentMood(HashMap<String, Integer> map) {
        String mostFrequentMood = "";
        int maxCount = 0;
        if(!map.isEmpty()) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentMood = entry.getKey();
                }
            }
        }
        else {
            mostFrequentMood = "No data yet";

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
