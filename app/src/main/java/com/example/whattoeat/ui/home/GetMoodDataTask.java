//package com.example.whattoeat.ui.home;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.HashMap;
//
//public class GetMoodDataTask extends AsyncTask<Void, Void, HashMap<String, Integer>> {
//
//    private homepage mHomepage;
//
//    public GetMoodDataTask(homepage homepage) {
//        mHomepage = homepage;
//    }
//
//    @Override
//    protected HashMap<String, Integer> doInBackground(Void... voids) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//        if(user != null){
//            Log.d("Homepage", "Trying to get mood...");
//            String mood = mHomepage.filter.getUserMood(user.getUid());
//            return mHomepage.filter.getMoodRelation(mood);
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(HashMap<String, Integer> cuisines) {
//        if (cuisines != null) {
//            Log.d("Homepage", "Got mood data" + cuisines.keySet());
//            mHomepage.cuisines = cuisines;
//        }
//    }
//}
