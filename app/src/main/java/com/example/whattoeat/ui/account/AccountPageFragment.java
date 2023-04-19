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
import com.example.whattoeat.ui.SplashScreen;
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
 */
public class AccountPageFragment extends Fragment {

    private FirebaseUser user;
    private Context context;
    private Boolean isUserOwner;

    FirebaseDatabase database;
    protected DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = new homepage();
        EditProfile eP = new EditProfile();

        //Assign items to their visual button
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Button editProfileBtn = view.findViewById(R.id.editProfileBtn);
        Button resetPwBtn = view.findViewById(R.id.resetPasswordBtn);
        Button changePrefBtn = view.findViewById(R.id.changePreferencesBtn);
        Button logoutButton = view.findViewById(R.id.logOutBtn);
        Button deleteUserBtn = view.findViewById(R.id.deleteAccountBtn);
        Button mStatistics = view.findViewById(R.id.statisticsBtn);
        TextView textView = view.findViewById(R.id.userDetails);

        //Database and user setup
        user = auth.getCurrentUser();
        context = getActivity().getApplicationContext();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if (user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            myRef.child("User").child(user.getUid()).child("name")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                            textView.setText(String.valueOf(task.getResult().getValue()));
                        }
                    });
        }

        // Save the user inputs to the Firebase database
        DatabaseReference myRefOwner = myRef.child("User").child(user.getUid()).child("Owner");
        myRefOwner.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("Edit Profile: ", "result: " + task.getResult().getValue());
                Boolean result = (Boolean) task.getResult().getValue();
                isUserOwner = result;
                if (Boolean.TRUE.equals(result)) {
                    Log.e("Edit profile", "user is owner.");
                    mStatistics.setVisibility(View.VISIBLE);

                }
            } else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        });

        // Change to the statistics page if the user is allowed to
        mStatistics.setOnClickListener(view3 -> {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.containerAccount);
            if (currentFragment instanceof StatisticFragment) {
                return; // do nothing if EditProfile fragment is already displayed
            }
            //Get the new fragment
            Fragment fragment1 = new StatisticFragment();
            //Change it
            fragmentManager.beginTransaction()
                    .replace(R.id.containerAccount, fragment1, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("account1")
                    .commit();
        });

        // Actions for the reset password button
        resetPwBtn.setOnClickListener(view1 -> {
            String email = user.getEmail();
            Context context = getActivity().getApplicationContext();
            // if the email is empty then there was an error
            if (email.isEmpty()) {
                Toast.makeText(context,
                        "Something went wrong there.. Try again later!",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Firebase built in function to send reset password email
                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(unused -> Toast.makeText(context,
                                "Check your email! Password reset email " +
                                        "was sent to you!",
                                Toast.LENGTH_LONG).show()).addOnFailureListener(e -> {
                            Log.e("Login Page: ", e.toString());
                            Toast.makeText(context,
                                    "There was a problem with sending the email! " +
                                            "Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Open the edit profile page.
        editProfileBtn.setOnClickListener(view1 -> {
            // Change the fragment to the preferences fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.containerAccount);
            if (currentFragment instanceof EditProfile) {
                return; // do nothing if EditProfile fragment is already displayed
            }

            // explanation of how the page works
            Toast.makeText(context, "Fill in either of the bars " +
                            "to change your Profile! Empty means it wont change.",
                    Toast.LENGTH_LONG).show();

            //Get the new fragment
            Fragment fragment1 = new EditProfile();
            //Change it
            fragmentManager.beginTransaction()
                    .replace(R.id.containerAccount, fragment1, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("account1")
                    .commit();
        });

        changePrefBtn.setOnClickListener(view1 -> {
            // Change the fragment to the preferences fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.containerAccount);
            if (currentFragment instanceof PreferencesPage) {
                return; // do nothing if Preferences fragment is already displayed
            }

            //Get the new fragment
            Fragment fragment2 = new PreferencesPage();

            //Change it
            fragmentManager.beginTransaction()
                    .replace(R.id.containerAccount, fragment2, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("account2")
                    .commit();
        });

        // Firebase built in delete account method.
        // Remove user from the authentication
        // and delete the user data from the firebase realtime db
        deleteUserBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Deleting this account means that it will permanently" +
                    " be removed from our systems. There is no way back!");
            dialog.setPositiveButton("Delete", (dialogInterface, i) -> user.delete().addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            user.getUid();
                            Toast.makeText(context,
                                    "Account deleted!",
                                    Toast.LENGTH_SHORT).show();

                            myRef = myRef.child("User").child(user.getUid());
                            myRef.removeValue()
                                    .addOnSuccessListener(unused ->
                                            Log.d("Account Page ",
                                                    "Account successfully deleted from the database!"));
                            FirebaseAuth.getInstance().signOut();
                            getActivity().startActivity(new Intent(getActivity(), Login.class));
                        } else {
                            Toast.makeText(context,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            ));
            // If the user cancels the delete account action
            // then the dialog will close
            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });

        // Button that logs the user out and sends them to the splashscreen
        logoutButton.setOnClickListener(view1 -> {
            // Firebase built in sign out method
            FirebaseAuth.getInstance().signOut();

            // Send the user to the splashscreen
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            getActivity().startActivity(new Intent(getActivity(), SplashScreen.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resumed Accountpage", "Success!");
        ConstraintLayout accountPageContainer = this.getView().findViewById(R.id.containerAccount);
        accountPageContainer.setVisibility(View.VISIBLE);
    }
}