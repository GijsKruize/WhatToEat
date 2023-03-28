package com.example.whattoeat.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

public class RegisterRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPhone, editTextName, editTextPassword, editTextAddress;
    Button btnReg;
    TextView returnBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_restaurant);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailRestaurantRegister);
        editTextName = findViewById(R.id.nameRestaurantRegister);
        editTextPhone = findViewById(R.id.phoneRestaurantRegister);
        editTextPassword = findViewById(R.id.passwordRestaurantRegister);
        progressBar = findViewById(R.id.progressBarRestaurant);
        btnReg = findViewById(R.id.registerBtnRestaurant);
        editTextAddress = findViewById(R.id.addressRestaurantRegister);
        returnBtn = findViewById(R.id.existingUser);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

//        textView.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        });

        returnBtn.setOnClickListener(view -> startActivity(new Intent(RegisterRestaurant.this, Login.class)));
        btnReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String name = String.valueOf(editTextName.getText());
            String phone = String.valueOf(editTextPhone.getText());
            String password = String.valueOf(editTextPassword.getText());
            String address = String.valueOf(editTextAddress.getText());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            HashMap<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("name", name);
            map.put("phone", phone);
            map.put("address", address);
            map.put("last login", timestamp.toString());

            if(TextUtils.isEmpty(email)){
                Toast.makeText(RegisterRestaurant.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(RegisterRestaurant.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(address)){
                Toast.makeText(RegisterRestaurant.this, "Enter Address", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                final String UID = mAuth.getUid();

                                myRef.child("User")
                                        .child(UID)
                                        .setValue(map)
                                        .addOnSuccessListener(unused -> Log.d(
                                                "Register Page: ",
                                                "Successfully sent data to database!"
                                        ))
                                        .addOnFailureListener(e -> Log.d(
                                                "Register Page: ",
                                                "Failed to send data!" + e
                                        ));
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterRestaurant.this, "Account Created.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterRestaurant.this, Login.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                String Error = task.getException().getMessage();
                                Log.d("Register Page: ", Error);
                                Toast.makeText(RegisterRestaurant.this, "Authentication failed: " + Error,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }
}