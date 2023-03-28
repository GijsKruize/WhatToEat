//package com.example.whattoeat.ui.home;
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.j256.ormlite.stmt.query.In;
//
//import java.util.HashMap;
//import java.util.concurrent.ExecutionException;
//
//public class homepageFilters {
//
//    private FirebaseDatabase database;
//    private DatabaseReference myRef;
//
//    public String getUserMood(String uid) {
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("Preference").child(uid);
//
//        Task<DataSnapshot> task = myRef.child("Mood").get();
//
//        try {
//            Tasks.await(task); // Wait for the task to complete
//            if (task.isSuccessful()) {
//                String result = String.valueOf(task.getResult().getValue());
//                Log.d("Homepage Filter: ", result);
//                return result;
//            } else {
//                Log.e("Homepage Filter: ", "Error getting data", task.getException());
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            Log.e("Homepage Filter: ", "Error getting data", e);
//        }
//        return "Could not find mood";
//    }
//
//    public HashMap<String, Integer> getMoodRelation(String mood) {
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("General Relation").child(mood);
//        Task<DataSnapshot> task = myRef.get();
//
//        try {
//            Tasks.await(task); // Wait for the task to complete
//            if (task.isSuccessful()) {
//                HashMap<String, Integer> result = new HashMap<>();
//                for (DataSnapshot moods : task.getResult().getChildren()) {
//                    // retrieve data for each recipe
//                    String moodKey = moods.getKey();
//                    Integer moodValue = moods.getValue(Integer.class);
//                    Log.d("Homepage: ", "mood saved" + moodKey + " with value " + moodValue);
//                    if(moodValue >= 0) {
//                        result.put(moodKey, moodValue);
//                    }
//                }
//                return result;
//            } else {
//                Log.e("Homepage Filter: ", "Error getting data", task.getException());
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            Log.e("Homepage: ", e.toString());
//        }
//        return new HashMap<>();
//    }
//}
