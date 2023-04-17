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
    DatabaseReference statsRef, ownerRef, restaurantRef;
    String uid;
    Integer totLikes, sumOfLikes;
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
        statsRef = FirebaseDatabase.getInstance().getReference("Personal Relation");
        ownerRef = FirebaseDatabase.getInstance().getReference("User");
        restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        addData();
        return view;
    }

    /**
     * Adds data to textviews and piechart
     */
    private void addData() {
        
        // replace restaurantValue with "Restaurant_1" if there is no result
        // some restaurants haven't been swiped yet so there might be no data.

        int[] colorArray = new int[]{
                Color.parseColor("#ff595e"),
                Color.parseColor("#ff924c"),
                Color.parseColor("#ffca3a"),
                Color.parseColor("#8ac926"),
                Color.parseColor("#52a675"),
                Color.parseColor("#1982c4"),
                Color.parseColor("#4267ac"),
                Color.parseColor("#6a4c93"),

                // Add more colors if needed
        };



        ownerRef.child(uid).child("Restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String restaurantValue = snapshot.getValue(String.class); // gives the actual name of the resto

                    getRestaurantIndex(restaurantValue, new RestaurantKeyCallback() {
                        @Override
                        public void onRestaurantKeyReceived(String restaurantKey) {
                            getStats(restaurantKey, new StatsCallback() {
                                @Override
                                public void onStatsReceived(HashMap<String, Integer> stats) {
                                    mostFrequentMood = getMostFrequentMood(map);
                                    fieldMood.setText(mostFrequentMood);

                                    //gets the total like and adds it to textView
                                    sumOfLikes = getTotalLikes(map);
                                    fieldLikes.setText(String.valueOf(sumOfLikes));

                                    //Adding values to piechart
                                    int colorIndex = 0;

                                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                        if(!map.isEmpty()) {
                                            int color = colorArray[colorIndex % colorArray.length];
                                            pieChart.addPieSlice(new PieModel(entry.getKey(), entry.getValue(), color));
                                            TextView legendItem = new TextView(getContext());
                                            legendItem.setText(entry.getKey());
                                            legendItem.setTextSize(16);
                                            legendItem.setTextColor(color);
                                            graphLegend.addView(legendItem);
                                            colorIndex++;
                                        }
                                        else {
                                            Log.d("Statistics", "There is no data to show right now.");
                                        }

                                    }

                                }

                            });


                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        int trueCount = 0;
                        for (DataSnapshot restaurantSnapshot : moodSnapshot.getChildren()) {
                            if (Objects.equals(restaurantSnapshot.getKey(), restaurantKey)
                                    && Boolean.TRUE.equals(restaurantSnapshot.getValue(Boolean.class))) {
                                trueCount++;
                            }
                        }
                        String mood = moodSnapshot.getKey();
                        int count = map.getOrDefault(mood, 0);
                        map.put(mood, count + trueCount);
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


    public interface RestaurantKeyCallback {
        void onRestaurantKeyReceived(String restaurantKey);
    }

    public void getRestaurantIndex(String restaurantName, RestaurantKeyCallback callback) {
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot restoSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(restoSnapshot.child("Name").getValue(String.class), restaurantName)) {
                            callback.onRestaurantKeyReceived(restoSnapshot.getKey());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



}
