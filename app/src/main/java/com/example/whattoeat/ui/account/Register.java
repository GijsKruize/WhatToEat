package com.example.whattoeat.ui.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPhone, editTextName, editTextPassword;
    Button btnReg;
    TextView returnBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    private List<String> userNames;
    private Boolean allowedToRegister;
    FirebaseDatabase database;
    protected DatabaseReference myRef;
    private List<String> phoneNumbers;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try to get the user data
        try {
            userNames = loadUsernames();
            phoneNumbers = loadPhones();
        } catch (Exception e) {
            Log.e("Error", "Couldn't load data.");
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        // Set data to the variables.
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailRegister);
        editTextName = findViewById(R.id.nameRegister);
        editTextPhone = findViewById(R.id.phoneRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        progressBar = findViewById(R.id.progressBar);
        btnReg = findViewById(R.id.registerBtn);
        returnBtn = findViewById(R.id.existingUser);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // assign default value
        allowedToRegister = true;

        // If the username is not unique send a toast message
        editTextName.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String name = String.valueOf(editTextName.getText());
                if (userNames.contains(name.toLowerCase(Locale.ROOT))) {
                    Log.e("Register: ", "non valid username");
                    Toast.makeText(Register.this,
                            "Username already exists",
                            Toast.LENGTH_SHORT).show();
                    allowedToRegister = false; // user is not allowed to signup
                }
            }
        });

        // If the phone number is not unique send a toast message
        editTextPhone.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String phone = String.valueOf(editTextPhone.getText());
                Log.e("Register:", "Checking : " + phone + " in " + phoneNumbers);
                if (phoneNumbers.contains(phone)) {
                    Log.e("Register: ", "non valid restaurant phoneNumbers");
                    Toast.makeText(Register.this,
                            "Phone number already registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        returnBtn.setOnClickListener(view -> startActivity(new Intent(Register.this, Login.class)));
        btnReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            // Get the data that was input into the bars
            String email = String.valueOf(editTextEmail.getText());
            String name = String.valueOf(editTextName.getText());
            String phone = String.valueOf(editTextPhone.getText());
            String password = String.valueOf(editTextPassword.getText());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            HashMap<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("name", name);
            map.put("phone", phone);
            map.put("Owner", false);
            map.put("last login", timestamp.toString());

            //Check the users entries.
            allowedToRegister = true;
            if (!emptyEntries(email, name, phone, password)) {
                Log.e("Register: ", "empty entry!");
                allowedToRegister = false; // user is not allowed to signup
            }

            if (!validEntries(name, password, phone)) {
                Log.e("Register: ", "non valid entry!");
                allowedToRegister = false; // user is not allowed to signup
            }

            if (phoneNumbers.contains(phone)) {
                Log.e("Register: ", "non valid entry!");
                allowedToRegister = false; // user is not allowed to signup
            }

            Log.e("Register: ",
                    "name "
                            + userNames.contains(name.toLowerCase(Locale.ROOT))
                            + "List:"
                            + userNames);
            if (userNames.contains(name.toLowerCase(Locale.ROOT))) {
                Toast.makeText(Register.this,
                        "Please chose a different username!",
                        Toast.LENGTH_SHORT).show();
                Log.e("Register: ", "non valid username");
                allowedToRegister = false; // user is not allowed to signup
            }

            if (allowedToRegister) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
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
                                Toast.makeText(Register.this, "Account Created.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                String Error = task.getException().getMessage();
                                Log.d("Register Page: ", Error);
                                Toast.makeText(Register.this, "Authentication failed: " + Error,
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(task2 -> {
                            if (task2 instanceof FirebaseAuthUserCollisionException) {
                                // Handle the email already in use exception here
                                Toast.makeText(getApplicationContext(),
                                        "Email already in use",
                                        Toast.LENGTH_SHORT).show();
                            }
                            Log.e("Register: ", task2.getMessage() + "AAA");
                        });
            }
        });
    }

    /**
     * This method checks whether all fields are filled in
     *
     * @param email    email from user
     * @param name     name from the user
     * @param phone    phone number from the user
     * @param password password from the user
     * @return true if entries are not empty, false otherwise.
     */
    private boolean emptyEntries(String email, String name, String phone, String password) {

        // Checks if email bar is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Checks if name bar is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(Register.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Checks if phone bar is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(Register.this, "Enter Phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Checks if password bar is empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * This method loads a list of usernames from the Firebase Realtime Database.
     * It retrieves the data from the "User" node and adds each username to a temporary list.
     * The temporary list is then returned.
     *
     * @return a list of usernames as strings
     */
    public List<String> loadUsernames() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("User");
        List<String> usernamesTemp = new ArrayList<>();

        ref.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String nameToAdd = list
                            .child("name")
                            .getValue()
                            .toString()
                            .toLowerCase();
                    usernamesTemp.add(nameToAdd);
                } catch (Exception e) {
                    Log.e("Register: ", "User without name found. Continuing..");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "Error fetching usernames"));
        Log.e("USERNAMES LIST: ", "" + usernamesTemp);
        return usernamesTemp;
    }

    /**
     * This method loads a list of phone numbers from the Firebase Realtime Database.
     * It retrieves the data from the "User" node and adds each phone number to a temporary list.
     * The temporary list is then returned.
     *
     * @return a list of phone numbers as strings
     */
    public List<String> loadPhones() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("User");
        List<String> phonesTemp = new ArrayList<>();

        ref.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String nameToAdd = list
                            .child("phone")
                            .getValue()
                            .toString()
                            .toLowerCase();
                    phonesTemp.add(nameToAdd);
                } catch (Exception e) {
                    Log.e("Register: ", "Phones error");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "error"));
        return phonesTemp;
    }


    /**
     * Checks if the entered user details are valid or not. Returns false if the entries are invalid.
     *
     * @param name     the name of the user to be checked
     * @param password the password of the user to be checked
     * @param phone    the phone number of the user to be checked
     * @return true if the entries are valid, false otherwise
     */
    private boolean validEntries(String name, String password, String phone) {
        // Compiles pattern for special characters
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        // Checks if name is longer than 16 characters
        if (name.length() > 16) {
            Toast.makeText(getApplicationContext(),
                    "Username needs to be 16 characters or shorter!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // Checks if phone number is already in use
        if (phoneNumbers.contains(phone)) {
            Toast.makeText(getApplicationContext(),
                    "Phone number already in use!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        // Checks if password contains special character
        if (!pattern.matcher(password).find()) {
            Toast.makeText(getApplicationContext(),
                    "Password needs to contain a special character!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        // Checks if password is at least 8 characters long
        if (password.length() <= 8) {
            Toast.makeText(getApplicationContext(),
                    "Password needs to be at least 8 characters long",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}