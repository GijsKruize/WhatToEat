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

        // Set db reference to get owner data
        DatabaseReference myRef = mDatabaseRef.child("User").child(user.getUid()).child("Owner");
        myRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //default value
                Boolean result = false;
                if (task.getResult().getValue() != null) {
                    result = (Boolean) task.getResult().getValue();
                }

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

        // if the user is an owner then get their restaurant data
        if (isUserOwner) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
            getRestaurantName(ref, user);
        }


        // Set the top part correct with user data.
        mName = view.findViewById(R.id.editProfileUserName);
        mDatabaseRef.child("User").child(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        mName.setText(String.valueOf(task.getResult().child("name").getValue()));
                        if (isUserOwner) {
                            String rest = String.valueOf(task.getResult().child("Restaurant").getValue());
                            setRestaurant(rest);
                            getRestaurantId(rest);
                        }
                    }
                });

        // Display the users email on the edit profile page.
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

            // Save the user inputs to the Firebase database
            if (isUserOwner) {
                saveOwnerProfile(name, phone, style, website, address, restName, restPhone);
            } else {
                saveUserProfile(name, phone, UID);
            }
        });

        return view;
    }

    /**
     * Saves the owner profile data to the Firebase database.
     *
     * @param name      the name of the owner
     * @param phone     the phone number of the owner
     * @param style     the style of the restaurant
     * @param website   the website of the restaurant
     * @param address   the address of the restaurant
     * @param restName  the name of the restaurant
     * @param restPhone the phone number of the restaurant
     *                  <p>The method updates two child nodes, "User" and "Restaurant", in the Firebase database
     *                  with the provided owner profile data. The method processes the input data and stores them in
     *                  two separate hashmaps, mapUser and mapRest, with keys corresponding to the Firebase database fields.
     *                  If the address is not empty, the method uses the RegisterRestaurant class to get the latitude
     *                  and longitude of the address, and updates the mapRest accordingly.
     *                  Finally, the method updates the Firebase database with the processed data using the Firebase
     *                  updateChildren() method. The method logs success or failure messages for each update operation.
     */
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
        if (!address.isEmpty()) {
            RegisterRestaurant regPage = new RegisterRestaurant();
            LatLng location = regPage.getLocationFromAddress(getContext(), address);
            double latitude = location.latitude;
            double longitude = location.longitude;
            mapRest.put("Latitude", latitude);
            mapRest.put("Longitude", longitude);
        }

        // Add data to the users hashmap
        processData(name, "name", mapUser);
        processData(phone, "Phone", mapUser);
        processData(restName, "Restaurant", mapUser);

        // Add data to the Restaurant hashmap
        processData(restPhone, "Phone", mapRest);
        processData(style, "Style", mapRest);
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
        String restaurantID = getRestaurantId();
        restRef.child(restaurantID)
                .updateChildren(mapRest)
                .addOnSuccessListener(unused ->
                        Log.d("Edit user profile: ",
                                "Restaurant data updated!" + mapRest))
                .addOnFailureListener(error ->
                        Log.d("Edit user profile: ",
                                "Account data failed to update : " + error));
    }

    /**
     * Processes the input data and updates the provided hashmap with the data.
     *
     * @param toCheck the input data to check
     * @param key     the key to use when updating the hashmap
     * @param map     the hashmap to update
     *                <p>The method checks if the input data is not empty. If the input data is not empty,
     *                the method updates the provided hashmap with the input data using the provided key.
     */
    private void processData(String toCheck, String key, HashMap map) {
        if (!toCheck.isEmpty()) {
            map.put(key, toCheck);
        }
    }

    private void getRestaurantName(DatabaseReference ref, FirebaseUser name) {
        ref.child(name.getUid()).child("Restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.e("Restaurant", "Logged name " + task.getResult().getValue());
                        getRestaurantId(task.getResult().getValue().toString());
                    }
                });
    }

    private void setRestaurant(Object value) {
        this.restaurantName = value.toString();
    }

    /**
     * This method sets the value of the restaurant id
     * @modifies restaurantId with the value that was set.
     * @param value
     */
    private void setRestaurantId(Object value) {
        Log.e("Set restid", " yes" + value);
        this.restaurantId = value.toString();
    }

    /**
     * Returns the restaurant ID.
     * <p>The method returns the value of the private field restaurantId, which is a String
     * representing the ID of the restaurant.
     * @return the restaurant ID
     */
    private String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Retrieves the ID of the restaurant with the provided name from the Firebase database.
     * @param name the name of the restaurant to search for
     * <p>The method searches the "Restaurant" child node in the Firebase database for a restaurant with
     * the provided name. If a matching restaurant is found, the method sets the private field
     * restaurantId to the ID of the matching restaurant. The method logs success or failure messages
     * accordingly.
     */
    private void getRestaurantId(String name) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Restaurant");
        ref.get().addOnCompleteListener(task -> {
            Log.d("TASK", task.toString());
            for (DataSnapshot childSnapshot : task.getResult().getChildren()) {
                if (childSnapshot.child("Name").getValue(String.class).equals(name)) {
                    // Do something with the matching child
                    Log.e("Edit profile: ", "Result of id: " + childSnapshot.getKey() + "Looking for : " + name);
                    setRestaurantId(childSnapshot.getKey());
                } else {
                    Log.e("Firebase fetch:", "Could not find it in" + childSnapshot.getKey() + "Looking for : " + name);
                }
            }
        });
    }

    /**
     * Saves user profile data to the Firebase database.
     * @param name the name of the user to save
     * @param phone the phone number of the user to save
     * @param UID the unique ID of the user to save
     * <p>The method creates a hashmap with the provided user profile data and updates the "User" child
     * node in the Firebase database with the new data. The method logs success or failure messages
     * accordingly.
     */
    private void saveUserProfile(String name,
                                 String phone,
                                 String UID) {

        HashMap<String, Object> map = new HashMap<>();

        processData(name, "name", map);
        processData(phone, "Phone", map);

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