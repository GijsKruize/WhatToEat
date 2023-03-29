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

import com.bumptech.glide.signature.ObjectKey;
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
import java.util.HashMap;
import java.util.List;


public class homepage extends Fragment {


    private List<String> listNamesRec = new ArrayList<>();
    private List<String> listStyleRec = new ArrayList<>();

    private List<String> listImagesRec = new ArrayList<>();
    private List<String> listIdsRec = new ArrayList<>();
    private List<String> listNamesRest = new ArrayList<>();
    private List<String> listImagesRest = new ArrayList<>();
    private List<String> listIdsRest = new ArrayList<>();
    private ProgressBar progressBar;
    private ListView mListView;
    private String[][] data;
    FirebaseDatabase database;
    private boolean isDataShuffled = false;
    protected DatabaseReference myRefRecipe, myRefRestaurant;
//    String mood;
//    HashMap<String, Integer> cuisines;
//    public homepageFilters filter = new homepageFilters();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if user is authenticated
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isDataShuffled = false; // Reset the variable to false
        fetchData();
    }
    private void fetchData(){

        progressBar.setVisibility(View.VISIBLE);
        //Connect to the database
        database = FirebaseDatabase.getInstance();
        myRefRecipe = database.getReference("Recipe");
        myRefRestaurant = database.getReference("Restaurant");

        Log.d("Homepage: ", "Loading data........");

        //fetch data
        fetchFromDb();
//        new GetMoodDataTask(this).execute();
    }

    public void fetchFromDb(){
        myRefRecipe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listIdsRec.clear();
                listNamesRec.clear();
                listImagesRec.clear();
                listStyleRec.clear();
                Log.d("Homepage: ", dataSnapshot.toString());
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String recipeId = recipeSnapshot.getKey();
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);
                    String style = recipeSnapshot.child("Style").getValue(String.class);

                    listIdsRec.add(recipeId);
                    listNamesRec.add(recipeName);
                    listImagesRec.add(recipeImage);
                    listStyleRec.add(style);
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                listIdsRest.clear();
                listNamesRest.clear();
                listImagesRest.clear();
                for(DataSnapshot Restaurant : dataSnapshot1.getChildren()) {
                    // retrieve data for each recipe
                    String restaurantId = Restaurant.getKey();
                    String restaurantName = Restaurant.child("Name").getValue(String.class);
                    String restaurantImage = Restaurant.child("Image").getValue(String.class);

                    listIdsRest.add(restaurantId);
                    listNamesRest.add(restaurantName);
                    listImagesRest.add(restaurantImage);
                    Log.d("Firebase", "Recipe Name: " + restaurantName +
                            ", Image source: " + restaurantImage);
                }
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                MyAdapter adapter = (MyAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged(); // Refresh the adapter with new data
                data = new String[listNamesRec.size() + listNamesRest.size()][3];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });
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

            ImageView mImageView = view.findViewById(R.id.imageViewCard);
            TextView mTextView = view.findViewById(R.id.textViewCard);
            CardView card = view.findViewById(R.id.card);

            if(!isDataShuffled) {
                shuffleData();
            }

            mTextView.setText(data[i][0]);
            Picasso.with(getActivity().getApplicationContext()).load(data[i][1]).into(mImageView);

            card.setOnClickListener(view1 -> {
                // check if the food_card fragment is not already displayed
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.homepage_container);

                //disable interaction with homepage.
                if (currentFragment instanceof food_card) {
                    return; // do nothing if food_card fragment is already displayed
                }

                if (currentFragment instanceof CardWidgetFragment) {
                    return; // do nothing if CardWidgetFragment fragment is already displayed
                }

                //send data to the card
                Bundle args = new Bundle();
                args.putString("cardType", data[i][2]);
                Log.d("Homepage: ", "send data to card" + data[i][2]);

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


                Log.d("Cards", "Pressed on the card: " + data[i][0]);
            });

            return view;
        }
    }

    private void shuffleData() {
        // Combine the recipe and restaurant data into a single list
        List<String[]> dataList = new ArrayList<>();
        for (int i = 0; i < listNamesRec.size(); i++) {
            dataList.add(new String[] {
                    listNamesRec.get(i),
                    listImagesRec.get(i),
                    listIdsRec.get(i),
                    listStyleRec.get(i)
            });
        }
        for (int i = 0; i < listNamesRest.size(); i++) {
            dataList.add(new String[] {
                    listNamesRest.get(i),
                    listImagesRest.get(i),
                    listIdsRest.get(i)
            });
        }

        // Shuffle the list
        Collections.shuffle(dataList);

        // Copy the shuffled data back into the data array
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        isDataShuffled = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        mListView = view.findViewById(R.id.listview);
        progressBar = view.findViewById(R.id.progressBarHome);

        MyAdapter adapter = new MyAdapter();
        mListView.setAdapter(adapter);

        fetchData(); // Load the data

        return view;
    }

}
