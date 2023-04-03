package com.example.whattoeat.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class homepage extends Fragment {


    private List<String> listNamesRec = new ArrayList<>();
    private List<String> listStyleRec = new ArrayList<>();

    private List<String> listImagesRec = new ArrayList<>();
    private List<String> listIdsRec = new ArrayList<>();
    private List<String> listNamesRest = new ArrayList<>();
    private List<String> listImagesRest = new ArrayList<>();
    private List<String> listIdsRest = new ArrayList<>();
    private List<String> prefStyles = new ArrayList<>();
    private ProgressBar progressBar;
    private ListView mListView;
    private String prefLocation = "both";
    private String prefMood = "Happy";
    private String[][] data;
    FirebaseDatabase database;
    private boolean isDataShuffled = false;
    protected DatabaseReference myRefRecipe, myRefRestaurant;
//    String mood;
//    HashMap<String, Integer> cuisines;
//    public homepageFilters filter = new homepageFilters();

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        // Initialize the views
        mListView = view.findViewById(R.id.listview);
        progressBar = view.findViewById(R.id.progressBarHome);

        MyAdapter adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        try {
            // Get the user's preferences
            getPreference();
            // Get the styles that match the user's mood
            prefStyles = getStyles(prefMood);
        } catch (Exception e) {
            Log.e("Exception Homepage: ", "Data was empty in onCreate");
        }
        fetchData(); // Load the data

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if user is authenticated
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * Resumes fetching the data.
     */
    @Override
    public void onResume() {
        super.onResume();
        isDataShuffled = false; // Reset the variable to false
        try {
            // Get the user's preferences
            getPreference();
            // Get the styles that match the user's mood
            prefStyles = getStyles(prefMood);

        } catch (Exception e) {
            Log.e("Exception homepage", "Data was empty in onResume");
        }
        fetchData();
    }

    /**
     * This method is used to fetch data from the database.
     * It will be called when the user first opens the app or when the user returns to the homepage.
     */
    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        //Connect to the database
        database = FirebaseDatabase.getInstance();
        myRefRecipe = database.getReference("Recipe");
        myRefRestaurant = database.getReference("Restaurant");

        Log.d("Homepage: ", "Loading data........");

        //fetch data
        fetchFromDb(prefStyles, prefLocation);
    }

    /**
     * This method is used to get the user's preference from the database.
     * If the user has not set any preference, the default values will be used.
     */
    private void getPreference() {
        // Get a reference to the "Preference" node in the database
        DatabaseReference preferences = database.getReference("Preference");

        // Get the currently signed-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get the user's unique ID
        final String UID = user.getUid();

        // Retrieve the user's preference data from the database
        preferences.child(UID).get()
                .addOnCompleteListener(task -> {
                    String mood = "Happy";
                    String location = "both";
                    try {
                        // Get the user's mood and location preferences, if available
                        mood = task.getResult().child("Mood").getValue().toString();
                        location = task.getResult().child("Location").getValue().toString();
                    } catch (NullPointerException e) {
                        // If there is no mood or location preference data for the user, use default values
                        Log.d("Homepage: ", "No location or mood data found for user. " +
                                "User must set this in preferences page! " +
                                "Default values chosen.");
                    }
                    // Set the user's preferences in the app
                    setPreference(mood, location);
                }).addOnFailureListener(task2 -> {
                    // Log an error message if there is a problem retrieving the data from the database
                    Log.e("Homepage: ", "Error getting data from database! " + task2);
                });
    }

    /**
     * This method is used to set the user's preference.
     *
     * @param mood     The user's mood
     * @param location The user's location
     */
    private void setPreference(String mood, String location) {
        this.prefMood = mood;
        this.prefLocation = location;
    }

    /**
     * This method is used to fetch data from the database.
     *
     * @param styles List of styles bound to mood
     * @param pref   The user's location
     */
    public void fetchFromDb(List styles, String pref) {

        if (styles == null) {
            styles = new ArrayList();
        }
        // fetch data from recipe table
        List finalStyles = styles;
        myRefRecipe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the lists that hold recipe data
                listIdsRec.clear();
                listNamesRec.clear();
                listImagesRec.clear();
                listStyleRec.clear();

                // Check if the user's location preference is "out"
                // If it is, don't include recipes in the list
                if (pref == null || !pref.equals("out")) {
                    // Loop through all the recipes in the database
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve data for each recipe
//                        Log.e("Homepage", finalStyles.toString() + recipeSnapshot.child("Style").getValue(String.class));
                        if (finalStyles.contains(recipeSnapshot.child("Style").getValue(String.class))) {

                            String recipeId = recipeSnapshot.getKey();
                            String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                            String recipeImage = recipeSnapshot.child("Image").getValue(String.class);
                            String style = recipeSnapshot.child("Style").getValue(String.class);

                            // Add the recipe data to the appropriate lists
                            listIdsRec.add(recipeId);
                            listNamesRec.add(recipeName);
                            listImagesRec.add(recipeImage);
                            listStyleRec.add(style);
                        }
                    }
                }
//                //Adapter goes hier!!!
                MyAdapter adapter = (MyAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged(); // Refresh the adapter with new data
//                progressBar.setVisibility(View.GONE); // Hide the progress bar

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });
        //fetch data from restaurants table
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                // Clear the lists that hold restaurant data
                listIdsRest.clear();
                listNamesRest.clear();
                listImagesRest.clear();

                // Check if the user's location preference is "home"
                // If it is, don't include restaurants in the list
                if (pref == null || !pref.equals("home")) {
                    // Loop through all the restaurants in the database
                    for (DataSnapshot Restaurant : dataSnapshot1.getChildren()) {
                        // retrieve data for each recipe
                        if (finalStyles.contains(Restaurant.child("Style").getValue(String.class))) {
                            String restaurantId = Restaurant.getKey();
                            String restaurantName = Restaurant.child("Name").getValue(String.class);
                            String restaurantImage = Restaurant.child("Image").getValue(String.class);
                            Boolean verified = Restaurant.child("Verified").getValue(Boolean.class);

                            // Only add the restaurant data to the list if it is verified
                            if (verified) {
                                listIdsRest.add(restaurantId);
                                listNamesRest.add(restaurantName);
                                listImagesRest.add(restaurantImage);
                            }
                        }
                    }
                }
                data = new String[listNamesRec.size() + listNamesRest.size()][3];
                MyAdapter adapter = (MyAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged(); // Refresh the adapter with new data
                progressBar.setVisibility(View.GONE); // Hide the progress bar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });
    }

    /**
     * This method is used to get the styles from the database that are bound to the user's mood.
     *
     * @param mood The user's mood
     * @return List of styles bound to mood
     */
    private List<String> getStyles(String mood) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("General Relation");
        List<String> styles = new ArrayList<>();
        ref.child(mood).get().addOnCompleteListener(result -> {
            for (DataSnapshot data : result.getResult().getChildren()) {
                Integer value = Integer.parseInt(data.getValue().toString());
                if (value >= 0) {
                    String style = data.getKey();
                    styles.add(style);
                }
            }
            prefStyles = styles;
            fetchFromDb(styles, prefLocation);

        });
        return styles;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listNamesRec.size() + listNamesRest.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.card, viewGroup, false);

            ImageView mImageView = view.findViewById(R.id.imageViewCard); //get the ImageView
            TextView mTextView = view.findViewById(R.id.textViewCard); //get the TextView
            CardView card = view.findViewById(R.id.card); //get the CardView

            if (!isDataShuffled) {
                shuffleData();
            }
            try {
                //set the text of the TextView to the first element of the data array at position i
                mTextView.setText(data[i][0]);
                Picasso.with(getActivity().getApplicationContext()).load(data[i][1]).into(mImageView); //load the image using Picasso library
            } catch (Exception e) {
                Log.e("Homepage: ", "" + e); //log any errors that occur
            }

            card.setOnClickListener(view1 -> {
                // check if the food_card fragment is not already displayed
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.homepage_container);

                //disable interaction with homepage.
                if (currentFragment instanceof food_card || currentFragment instanceof CardWidgetFragment) {
                    return; // do nothing if food_card or CardWidgetFragment fragment is already displayed
                }

                //send data to the card
                Bundle args = new Bundle();
                args.putString("cardType", data[i][2]);

                //change the fragment
                Fragment fragment;
                if (data[i][2].contains("Recipe")) {
                    fragment = new food_card();
                } else {
                    fragment = new CardWidgetFragment();
                }

                //send card data to the fragment
                fragment.setArguments(args);

                //change to the fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.homepage_container, fragment, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("home")
                        .commit();
            });

            return view;
        }
    }

    /**
     * Shuffles the data in the data array.
     * This is done by combining the recipe and restaurant data into a single list,
     * shuffling the list, and then copying the shuffled data back into the data array.
     */
    private void shuffleData() {
        // Combine the recipe and restaurant data into a single list
        List<String[]> dataList = new ArrayList<>();
        for (int i = 0; i < listNamesRec.size(); i++) {
            dataList.add(new String[]{
                    listNamesRec.get(i),
                    listImagesRec.get(i),
                    listIdsRec.get(i),
                    listStyleRec.get(i)
            });
        }
        for (int i = 0; i < listNamesRest.size(); i++) {
            dataList.add(new String[]{
                    listNamesRest.get(i),
                    listImagesRest.get(i),
                    listIdsRest.get(i)
            });
        }

        // Shuffle the list
        Collections.shuffle(dataList);

        // Copy the shuffled data back into the data array
        for (int i = 0; i < dataList.size(); i++) {
            try {
                data[i] = dataList.get(i);
            } catch (Exception e) {
                Log.e("Homepage : ", "" + e);
            }
        }
        isDataShuffled = true;
    }
}