package com.example.whattoeat.ui.account;

import static org.eazegraph.lib.utils.Utils.dpToPx;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.whattoeat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class EditProfile extends Fragment {

    private EditText mNameEditText;
    private TextView mName, mEmail, mStyle, mWebsite, mAddress, mRestName, mRestPhone;
    private RelativeLayout mStyleContainer,
            mWebsiteContainer,
            mAddressContainer,
            mRestNameContainer,
            mRestPhoneContainer;
    private double latitude, longitude;
    private EditText mPhoneEditText;
    private Button mUpdateProfileBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Boolean isUserOwner = false;
    private DatabaseReference mDatabaseRef;
    private Timestamp timestamp;
    private String restaurantName, restaurantId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user_profile,
                container,
                false);
        View scrollView = view.findViewById(R.id.scrollView);

        // Get the screen height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate the desired height for the scroll view
        int desiredHeight = (int) (screenHeight - dpToPx(200));

        // Set the height of the scroll view
        ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
        layoutParams.height = desiredHeight;
        scrollView.setLayoutParams(layoutParams);

        // Get a reference to the Firebase database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        timestamp = new Timestamp(System.currentTimeMillis());
        final String UID = user.getUid();

        // Find the UI elements
        mNameEditText = view.findViewById(R.id.editProfileName);
        mPhoneEditText = view.findViewById(R.id.editProfilePhone);
        mStyle = view.findViewById(R.id.editProfileStyle);
        mWebsite = view.findViewById(R.id.editProfileWebsite);
        mAddress = view.findViewById(R.id.editProfileAddress);
        mRestName = view.findViewById(R.id.editProfileRestName);
        mRestPhone = view.findViewById(R.id.editProfilePhoneRest);
        mStyleContainer = view.findViewById(R.id.editProfileStyleContainer);
        mWebsiteContainer = view.findViewById(R.id.editProfileWebsiteContainer);
        mAddressContainer = view.findViewById(R.id.editProfileAddressContainer);
        mRestNameContainer = view.findViewById(R.id.editProfileRestNameContainer);
        mRestPhoneContainer = view.findViewById(R.id.editProfilePhoneRestContainer);

        mUpdateProfileBtn = view.findViewById(R.id.updateProfileBtn);

        if(isUserOwner) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
            getRestaurantName(ref, user);
        }
//        getRestaurantId(restaurantName);

        // Set the top part correct with user data.
        mName = view.findViewById(R.id.editProfileUserName);
        mDatabaseRef.child("User").child(user.getUid()).child("name")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        mName.setText(String.valueOf(task.getResult().getValue()));
                    }
                });

        mEmail = view.findViewById(R.id.editProfileUserEmail);
        mEmail.setText(user.getEmail());

        // Set an OnClickListener for the Update Profile button
        mUpdateProfileBtn.setOnClickListener(v -> {
            // Get the user inputs from the EditText fields
            String name = mNameEditText.getText().toString();
            String phone = mPhoneEditText.getText().toString();
            String style = mStyle.getText().toString();
            String website = mWebsite.getText().toString();
            String address = mAddress.getText().toString();
            String restName = mRestName.getText().toString();
            String restPhone = mRestPhone.getText().toString();

            Log.d("Edit profile page: ", "Update button pressed!");
            // Save the user inputs to the Firebase database
            if (isUserOwner) {
                saveOwnerProfile(name, phone, style, website, address, restName, restPhone);
            } else {
                saveUserProfile(name, phone, UID);
            }
        });

        DatabaseReference myRef = mDatabaseRef.child("User").child(user.getUid()).child("Owner");
        myRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("Edit Profile: ", "result: " + task.getResult().getValue());
                Boolean result = (Boolean) task.getResult().getValue();
                isUserOwner = result;
                if (Boolean.FALSE.equals(result) || result == null) {
                    Log.e("Edit profile", "user is not an owner.");
                    mWebsiteContainer.setVisibility(View.GONE);
                    mStyleContainer.setVisibility(View.GONE);
                    mAddressContainer.setVisibility(View.GONE);
                    mRestNameContainer.setVisibility(View.GONE);
                    mRestPhoneContainer.setVisibility(View.GONE);
                }
            } else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        });

        return view;
    }

    private void saveOwnerProfile(String name,
                                  String phone,
                                  String style,
                                  String website,
                                  String address,
                                  String restName,
                                  String restPhone) {

        //The map data that is to change
        HashMap<String, Object> mapUser = new HashMap<>();
        HashMap<String, Object> mapRest = new HashMap<>();

        //manual check for address because of double type
        if(!address.isEmpty()){
            RegisterRestaurant regPage = new RegisterRestaurant();
            LatLng location = regPage.getLocationFromAddress(getContext(), address);
            double latitude = location.latitude;
            double longitude = location.longitude;
            mapRest.put("Latitude", latitude);
            mapRest.put("Longitude", longitude);
        }

        // Add data to the users hashmap
        processData(name, "name", mapUser);
        processData(phone,"Phone", mapUser);
        processData(restName, "Restaurant", mapUser);

        // Add data to the Restaurant hashmap
        processData(restPhone, "Phone", mapRest);
        processData(style,"Style", mapRest);
        processData(website, "Hyperlink", mapRest);
        processData(address, "Location", mapRest);
        processData(restName, "Name", mapRest);


        DatabaseReference userRef = mDatabaseRef.child("User");
        userRef.child(user.getUid())
                .updateChildren(mapUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Edit user profile: ",
                                "Account data updated!");
                    }
                })
                .addOnFailureListener(error ->
                        Log.d("Edit user profile: ",
                                "Account data failed to update : " + error));

        DatabaseReference restRef = mDatabaseRef.child("Restaurant");
        restRef.child(getRestaurantId())
                .updateChildren(mapRest)
                .addOnSuccessListener(unused ->
                        Log.d("Edit user profile: ",
                                "Restaurant data updated!" + mapRest))
                .addOnFailureListener(error ->
                        Log.d("Edit user profile: ",
                                "Account data failed to update : " + error));
    }

    private void processData(String toCheck,String key, HashMap map){
        if(!toCheck.isEmpty()){
            map.put(key, toCheck);
        }
    }

    private void getRestaurantName(DatabaseReference ref, FirebaseUser name){
        ref.child(name.getUid()).child("Restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("Restaurant", "Logged name " + task.getResult().getValue());
                        getRestaurantId(task.getResult().getValue().toString());
                    }
                });
    }

    private void setRestaurant(Object value) {
        this.restaurantName = value.toString();
    }
    private void setRestaurantId(Object value) {
        this.restaurantId = value.toString();
    }

    private String getRestaurantId() {
        return restaurantId;
    }

    private void getRestaurantId(String name){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Restaurant");
        ref.get().addOnCompleteListener(task -> {
            Log.d("TASK", task.toString());
            for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                if (childSnapshot.child("Name").getValue(String.class).equals(name)) {
                    // Do something with the matching child
                    Log.e("Edit profile: ", "Result of id: " + childSnapshot.getKey() + "Looking for : "+ name);
                    setRestaurantId(childSnapshot.getKey());
                } else {
                    Log.e("Firebase fetch:", "Could not find it in" +childSnapshot.getKey() + "Looking for : "+ name );
                }
            }
        });
    }
    private void saveUserProfile(String name,
                                 String phone,
                                 String UID) {

        HashMap<String, Object> map = new HashMap<>();

        processData(name, "name", map);
        processData(phone, "Phone", map);


        Log.d("Edit User Profile: ", "data from text"+ name + " | " +phone);
        mDatabaseRef.child("User")
                .child(UID)
                .updateChildren(map)
                .addOnSuccessListener(unused ->
                        Log.d("Edit user profile: ",
                                "Account data updated!"))
                .addOnFailureListener(error ->
                        Log.d("Edit user profile: ",
                                "Account data failed to update : " + error));


    }
}