package com.example.whattoeat.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.sql.Timestamp;
import java.util.HashMap;

public class EditProfile extends Fragment {

    private EditText mNameEditText;
    private TextView mName, mEmail;
    private EditText mPhoneEditText;
    private Button mUpdateProfileBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseRef;
    private Timestamp timestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user_profile,
                container,
                false);

        // Get a reference to the Firebase database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        timestamp = new Timestamp(System.currentTimeMillis());
        final String UID = user.getUid();

        // Find the UI elements
        mNameEditText = view.findViewById(R.id.editProfileName);
        mPhoneEditText = view.findViewById(R.id.editProfilePhone);
        mUpdateProfileBtn = view.findViewById(R.id.updateProfileBtn);

        //Set the top part correct with user data.
        mName = view.findViewById(R.id.editProfileUserName);
        mDatabaseRef.child("User").child(user.getUid()).child("name")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        mName.setText(String.valueOf(task.getResult().getValue()));
                    }
                });
        mEmail = view.findViewById(R.id.editProfileUserEmail);
        mEmail.setText(user.getEmail());

        // Set an OnClickListener for the Update Profile button
        mUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user inputs from the EditText fields
                String name = mNameEditText.getText().toString();
                String phone = mPhoneEditText.getText().toString();

                Log.d("Edit profile page: ", "Update button pressed!");
                // Save the user inputs to the Firebase database
                saveUserProfile(name, phone, UID);
            }
        });

        return view;
    }

    private void saveUserProfile(String name,
                                 String phone,
                                 String UID) {

        HashMap<String, Object> map = new HashMap<>();

        if(!name.isEmpty()){
            map.put("last login", timestamp.toString());
            map.put("name", name);
        }
        if (!phone.isEmpty()){
            map.put("last login", timestamp.toString());
            map.put("phone", phone);
        }


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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.popBackStack();
//        Fragment foodCardFragment = getChildFragmentManager().findFragmentById(R.id.EditUserProfile);
//        if (foodCardFragment != null) {
//            getChildFragmentManager().beginTransaction().remove(foodCardFragment).commit();
//        }
//    }
}