package com.example.whattoeat.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.whattoeat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

public class EditProfile extends Fragment {

    private EditText mNameEditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;
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

        final String UID = user.getUid();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        timestamp = new Timestamp(System.currentTimeMillis());


        // Find the UI elements
        mNameEditText = view.findViewById(R.id.editProfileName);
        mPhoneEditText = view.findViewById(R.id.editProfilePhone);
        mEmailEditText = view.findViewById(R.id.editProfileEmail);
        mUpdateProfileBtn = view.findViewById(R.id.updateProfileBtn);

        // Set an OnClickListener for the Update Profile button
        mUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user inputs from the EditText fields
                String name = mNameEditText.getText().toString();
                String phone = mPhoneEditText.getText().toString();
                String email = mEmailEditText.getText().toString();

                Log.d("Edit profile page: ", "Update button pressed!");
                // Save the user inputs to the Firebase database
                saveUserProfile(name, phone, email, UID);
            }
        });

        return view;
    }

    private void saveUserProfile(String name,
                                 String phone,
                                 String email,
                                 String UID) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("last login", timestamp.toString());
        map.put("name", name);
        map.put("phone", phone);
        map.put("email", email);


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        Fragment foodCardFragment = getChildFragmentManager().findFragmentById(R.id.EditUserProfile);
        if (foodCardFragment != null) {
            getChildFragmentManager().beginTransaction().remove(foodCardFragment).commit();
        }
    }

}