package com.example.whattoeat.ui.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnCompleteListener;
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
            myRef.child("User").child(user.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase return", String.valueOf(task.getResult().getValue()));
                        textView.setText(String.valueOf(task.getResult().getValue()));
                    }
                }
            });
        }

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfile.class));
            }
        });

        deleteUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account means that it will permanently" +
                        " be removed from our systems. There is no way back!");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.delete().addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
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
                                }
                        );


                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), Login.class));

            }
        });

        return view;
    }
}