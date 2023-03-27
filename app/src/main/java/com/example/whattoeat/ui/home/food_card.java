package com.example.whattoeat.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.ref.Reference;
import java.util.HashMap;


public class food_card extends Fragment {

    private String food_id = "FOODTEST";
    private String imageURL;
    private boolean isCardOpen = false;
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    public String getFood_type(){
        return food_id;
    }

    public void setFood_type(String type){
        this.food_id = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            setFood_type(args.getString("foodType"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_food_card, container, false);

        TextView mTitle = view.findViewById(R.id.textRecipeName);
        TextView mIngredients = view.findViewById(R.id.textRecipeIngredients);
        TextView mTime = view.findViewById(R.id.textTimeToCook);
        TextView mTutorial = view.findViewById(R.id.tutorialContent);
        ImageView mImageView = view.findViewById(R.id.imageRecipe);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        myRef.child("Recipe")
                .child(getFood_type())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isComplete()){
                            Log.e("Food Card", "error : " + task.toString());
                        } else {
                            Log.d("Food Card", "Success! ");

                            DataSnapshot dataSnapshot = task.getResult();
                            String name = dataSnapshot.child("Name").getValue(String.class);
                            String image = dataSnapshot.child("Image").getValue(String.class);
                            String time = dataSnapshot.child("Time").getValue().toString();

                            HashMap<String, String> ingredients = new HashMap<>();
                            DataSnapshot ingredientsData = dataSnapshot.child("Ingredients");

                            for(DataSnapshot d: ingredientsData.getChildren()){
                                String ingredientName = d.getKey();
                                String ingredientValue = d.getValue().toString();
                                ingredients.put(ingredientName, ingredientValue);
                            }
                            String ingredientsText = "";
                            // Loop through the ingredients HashMap and append each ingredient name and value to the string
                            for (String ingredientName : ingredients.keySet()) {
                                String ingredientValue = ingredients.get(ingredientName);
                                ingredientsText += ingredientName + ": " + ingredientValue + "\n";
                            }
                            mTitle.setText(name);
                            mTime.setText(time + " minutes");
                            mIngredients.setText(ingredientsText);


                            Picasso.with(getActivity().getApplicationContext()).load(image).into(mImageView);
                        }
                    }
                });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        Fragment foodCardFragment = getChildFragmentManager().findFragmentById(R.id.card);
        if (foodCardFragment != null) {
            getChildFragmentManager().beginTransaction().remove(foodCardFragment).commit();
        }
    }
}