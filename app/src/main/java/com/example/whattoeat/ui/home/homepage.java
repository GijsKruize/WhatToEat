package com.example.whattoeat.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;


public class homepage extends Fragment {


    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeImages = new ArrayList<>();
    private List<String> recipeIds = new ArrayList<>();

    FirebaseDatabase database;
    protected DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Recipe");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeIds.clear();
                recipeNames.clear();
                recipeImages.clear();
                for(DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String recipeId = recipeSnapshot.getKey();
                    String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                    String recipeImage = recipeSnapshot.child("Image").getValue(String.class);

                    recipeIds.add(recipeId);
                    recipeNames.add(recipeName);
                    recipeImages.add(recipeImage);
                    Log.d("Firebase", "Recipe Name: " + recipeName +
                            ", Image source: " + recipeImage);
                }
                // notify the adapter that the data set has changed
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });


        //Check if user is authenticated
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recipeNames.size();
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
            View view1 = getLayoutInflater().inflate(R.layout.fragment_food_card, viewGroup, false);

            ImageView mImageView = view.findViewById(R.id.imageViewCard);
            TextView mTextView = view.findViewById(R.id.textViewCard);
            CardView card = view.findViewById(R.id.card);
//            ImageView returnBtn = view1.findViewById(R.id.returnBtn);

            mTextView.setText(recipeNames.get(i));
            Picasso.with(getActivity().getApplicationContext()).load(recipeImages.get(i)).into(mImageView);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // check if the food_card fragment is not already displayed
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.homepage_container);
                    if (currentFragment instanceof food_card) {
                        return; // do nothing if food_card fragment is already displayed
                    }

                    //send data to the card
                    Bundle args = new Bundle();
                    args.putString("foodType", recipeIds.get(i));

                    //change the fragment
                    Fragment fragment = new food_card();
                    fragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.homepage_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    // remove the homepage fragment from the screen
                    Fragment homepageFragment = fragmentManager.findFragmentById(R.id.homepage_container);
                    if (homepageFragment != null) {
                        fragmentTransaction.remove(homepageFragment);
                    }


                    Log.d("Cards", "Bro pressed on the card. " + recipeNames.get(i));
                }
            });

//            returnBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    Fragment fragment = fragmentManager.findFragmentById(R.id.food);
//                    if (fragment != null) {
//                        Log.d("Food", "Test");
//                        fragmentManager.popBackStack(); // remove the food card and go back to the previous fragment
//                    }
//                }
//            });


            return view;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        ListView mListView = view.findViewById(R.id.listview);

        MyAdapter adapter = new MyAdapter();
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.food);
        Log.d("Homepage", "onStop Called.");
        if (fragment != null) {
            fragmentManager.popBackStackImmediate();
        }
    }
}
