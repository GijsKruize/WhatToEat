package com.example.whattoeat.ui.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.home.food_card;
import com.example.whattoeat.ui.home.homepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class AccountPageFragment extends Fragment {

    private FirebaseUser user;
    private Context context;

    FirebaseDatabase database;
    protected DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = new homepage();

        //Assign items to their visual button
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Button editProfileBtn = view.findViewById(R.id.editProfileBtn);
        Button resetPwBtn = view.findViewById(R.id.resetPasswordBtn);
        Button changePrefBtn = view.findViewById(R.id.changePreferencesBtn);
        Button logoutButton = view.findViewById(R.id.logOutBtn);
        Button deleteUserBtn = view.findViewById(R.id.deleteAccountBtn);
        TextView textView = view.findViewById(R.id.userDetails);
        user = auth.getCurrentUser();
        context = getActivity().getApplicationContext();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if (user == null){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            myRef.child("User").child(user.getUid()).child("name")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                            textView.setText(String.valueOf(task.getResult().getValue()));
                        }
                    });
        }

        resetPwBtn.setOnClickListener(view1 -> {
            String email = user.getEmail();
            Context context = getActivity().getApplicationContext();
            if (email.isEmpty()){
                Toast.makeText(context,
                        "Enter an Email address",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(unused -> Toast.makeText(context,
                                "Check your email! Password reset email " +
                                        "successfully sent!",
                                Toast.LENGTH_LONG).show()).addOnFailureListener(e -> {
                                    Log.e("Login Page: ", e.toString());
                                    Toast.makeText(context,
                                            "There was a problem with sending the email! " +
                                                    "Please try again later.",
                                            Toast.LENGTH_SHORT).show();
                                });
            }
        });

        editProfileBtn.setOnClickListener(view1 -> {
            // Change the fragment to the preferences fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.containerAccount);
            Log.d("Account: ", fragmentManager.toString());
            if (currentFragment instanceof EditUserProfile) {
                return; // do nothing if food_card fragment is already displayed
            }

            //change the fragment
            Fragment fragment1 = new EditUserProfile();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerAccount, fragment1);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        changePrefBtn.setOnClickListener(view1 -> {
            // Change the fragment to the preferences fragment
            FragmentManager fragmentManager1 = getChildFragmentManager();
            Fragment currentFragment = fragmentManager1.findFragmentById(R.id.containerAccount);
            if (currentFragment instanceof PreferencesPage) {
                return; // do nothing if food_card fragment is already displayed
            }

            //change the fragment
            Fragment fragment12 = new PreferencesPage();
            FragmentTransaction fragmentTransaction = fragmentManager1.beginTransaction();
            fragmentTransaction.replace(R.id.containerAccount, fragment12);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        deleteUserBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Deleting this account means that it will permanently" +
                    " be removed from our systems. There is no way back!");
            dialog.setPositiveButton("Delete", (dialogInterface, i) -> user.delete().addOnCompleteListener(
                    task -> {
                        if(task.isSuccessful()){
                            user.getUid();
                            Toast.makeText(context ,
                                    "Account deleted!",
                                    Toast.LENGTH_SHORT).show();
                            myRef = myRef.child("User").child(user.getUid());
                            myRef.removeValue()
                                  .addOnSuccessListener(unused ->
                                          Log.d("Account Page ",
                                                  "Account successfully deleted from the database!"));
                            getActivity().startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            Toast.makeText(context,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            ));
            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });
        logoutButton.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            getActivity().startActivity(new Intent(getActivity(), Login.class));

        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.containerAccount);
        Log.d("Account Page: ", "onStop Called.");
        if (fragment != null) {
            fragmentManager.popBackStackImmediate();
        }
    }
}