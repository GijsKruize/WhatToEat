package com.example.whattoeat.ui.account;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RegisterRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPhone, editTextName, editTextPassword, editTextAddress, restaurantName;
    Button btnReg;

    Switch delivery;
    TextView returnBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    protected DatabaseReference myRef;
    private FirebaseUser user;
    private List<String> restNames;
    private List<String> userNames;
    private Boolean allowedToRegister = true;

    Boolean deliveryStatus;
    private List<String> phoneNumbers = new ArrayList<>();


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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        try {
            restNames = loadRestaurantNames();
            userNames = loadUsernames();
            phoneNumbers = loadPhones();
            phoneNumbers.addAll(loadRestPhones());
        } catch (Exception e) {
            Log.e("Error", "Error loading data!");
        }

        // Set the views
        setContentView(R.layout.activity_register_restaurant);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailRestaurantRegister);
        editTextName = findViewById(R.id.nameRestaurantRegister);
        editTextPhone = findViewById(R.id.phoneRestaurantRegister);
        editTextPassword = findViewById(R.id.passwordRestaurantRegister);
        progressBar = findViewById(R.id.progressBarRestaurant);
        btnReg = findViewById(R.id.registerBtnRestaurant);
        editTextAddress = findViewById(R.id.addressRestaurantRegister);
        restaurantName = findViewById(R.id.restaurantNameRestaurantRegister);
        returnBtn = findViewById(R.id.existingUser);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Switch delivery = (Switch) findViewById(R.id.deliveryToggleButton);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // When the user leaves the field, check if the username is already taken.
        editTextName.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String name = String.valueOf(editTextName.getText());
                if (userNames.contains(name.toLowerCase(Locale.ROOT))) {
                    Log.e("Register: ", "non valid username");
                    Toast.makeText(RegisterRestaurant.this,
                            "Username already exists",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // When the user leaves the field, check if the restaurantName is already taken.
        restaurantName.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String name = String.valueOf(restaurantName.getText());
                Log.e("Register:", "Checking : " + name);
                if (restNames.contains(name.toLowerCase(Locale.ROOT))) {
                    Log.e("Register: ", "non valid restaurant name");
                    Toast.makeText(RegisterRestaurant.this,
                            "Restaurant already registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // when the user leaves the field, check if the phone number is already taken.
        editTextPhone.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String phone = String.valueOf(editTextPhone.getText());
                Log.e("Register:", "Checking : " + phone + " in " + phoneNumbers);
                if (phoneNumbers.contains(phone)) {
                    Log.e("Register: ", "non valid restaurant phoneNumbers");
                    Toast.makeText(RegisterRestaurant.this,
                            "Phone number already registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register button
        btnReg.setOnClickListener(view -> {
            String email = String.valueOf(editTextEmail.getText());
            String name = String.valueOf(editTextName.getText());
            String phone = String.valueOf(editTextPhone.getText());
            String password = String.valueOf(editTextPassword.getText());
            String restaurant = String.valueOf(restaurantName.getText());
            String address = String.valueOf(editTextAddress.getText());
            deliveryStatus = delivery.isChecked();

            //Check the entries.
            allowedToRegister = true;
            if (!emptyEntries(email, name, phone, password)) {
                Log.e("Register: ", "empty entry!");
                allowedToRegister = false;
            }

            //Check owner specific entries
            if (!ownerEntries(restaurant, address)) {
                Log.e("Register Restaurant:", "owner entry empty");
                allowedToRegister = false;

            }

            // Check if the entries are valid
            if (!validEntries(name, password, phone)) {
                Log.e("Register: ", "non valid entry!");
                allowedToRegister = false;

            }

            Log.e("Register:", "Checking : " + restaurant);
            if (restNames.contains(restaurant.toLowerCase(Locale.ROOT))) {
                Log.e("Register: ", "non valid restaurant name");
                Toast.makeText(RegisterRestaurant.this,
                        "Restaurant already registered!", Toast.LENGTH_SHORT).show();
                allowedToRegister = false;

            }

            if (userNames.contains(name.toLowerCase(Locale.ROOT))) {
                Toast.makeText(RegisterRestaurant.this,
                        "Please chose a different username!",
                        Toast.LENGTH_SHORT).show();
                Log.e("Register: ", "non valid username");
                allowedToRegister = false;

            }
            // Get a timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // Add the data to a hashmap
            HashMap<String, Object> mapRestUser = new HashMap<>();
            mapRestUser.put("email", email);
            mapRestUser.put("name", name);
            mapRestUser.put("Restaurant", restaurant);
            mapRestUser.put("Owner", true);
            mapRestUser.put("last login", timestamp.toString());

            HashMap<String, Object> mapRest = new HashMap<>();
            mapRest.put("Delivers", deliveryStatus);
            mapRest.put("Hyperlink", "");
            mapRest.put("Image", "");

            // Try to convert the address to longitude and latitude values.
            try {
                LatLng location = getLocationFromAddress(getApplicationContext(), address);
                double latitude = location.latitude;
                double longitude = location.longitude;
                mapRest.put("Latitude", latitude);
                mapRest.put("Longitude", longitude);
            } catch (Exception e) {
                Log.e("Register: ", "Empty address");
                Toast.makeText(this,
                        "Address not valid try another address!",
                        Toast.LENGTH_SHORT).show();
                allowedToRegister = false;
            }
            mapRest.put("Name", restaurant);
            mapRest.put("Phone", phone);
            mapRest.put("Location", address);
            mapRest.put("Style", "");
            mapRest.put("Verified", false);

            // If the owner is allowed to register then create an account
            if (allowedToRegister) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterRestaurant.this,
                                        "Request for verification sent!" +
                                                "\nYou will hear from us soon.",
                                        Toast.LENGTH_LONG).show();

                                // Try to send the email to the whattoeat email address
                                try {
                                    String stringSenderEmail = "whattoeattue@gmail.com";
                                    String stringReceiverEmail = "whattoeattue@gmail.com";
                                    String stringPasswordSenderEmail = "teyvfrxqaxlbwcac";
                                    String stringHost = "smtp.gmail.com";

                                    Properties properties = System.getProperties();

                                    // Setup mail server
                                    properties.put("mail.smtp.host", stringHost);
                                    properties.put("mail.smtp.port", "465");
                                    properties.put("mail.smtp.ssl.enable", "true");
                                    properties.put("mail.smtp.auth", "true");

                                    // Get the Session object.
                                    Session session = Session.getInstance(properties,
                                            new Authenticator() {
                                                @Override
                                                protected PasswordAuthentication getPasswordAuthentication() {
                                                    return new PasswordAuthentication(stringSenderEmail,
                                                            stringPasswordSenderEmail);
                                                }
                                            });

                                    MimeMessage mimeMessage = new MimeMessage(session);
                                    mimeMessage.addRecipient(Message.RecipientType.TO,
                                            new InternetAddress(stringReceiverEmail));

                                    mimeMessage.setSubject("Restaurant verification for: "
                                            + restaurant);
                                    mimeMessage.setText("Hello WhatToEat team, \n\n" + name +
                                            " would like to verify their restaurant.\n\nPhone: "
                                            + phone + "\n\nEmail: " + email + "\n\nAddress: "
                                            + address);

                                    Thread thread = new Thread(() -> {
                                        try {
                                            Transport.send(mimeMessage);
                                        } catch (MessagingException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    thread.start();

                                } catch (AddressException e) {
                                    e.printStackTrace();
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    Log.e("Email service: ", "" + e);
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                final String UID = mAuth.getUid();

                                myRef.child("User")
                                        .child(UID)
                                        .setValue(mapRestUser)
                                        .addOnSuccessListener(unused -> Log.d(
                                                "Register Page Restaurant: ",
                                                "Successfully sent data to database!"
                                        ))
                                        .addOnFailureListener(e -> Log.d(
                                                "Register Page Restaurant: ",
                                                "Failed to send data!" + e
                                        ));
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterRestaurant.this,
                                        "Account Created.",
                                        Toast.LENGTH_SHORT).show();
                                String id = "Restaurant_";
                                myRef.child("Restaurant")
                                        .get().addOnCompleteListener(task1 -> {
                                            Long idNumber = task1.getResult()
                                                    .getChildrenCount() + 1;
                                            idNumber.toString();

                                            myRef.child("Restaurant")
                                                    .child(id + idNumber)
                                                    .setValue(mapRest)
                                                    .addOnSuccessListener(unused -> Log.d(
                                                            "Register Page Restaurant: ",
                                                            "Successfully sent data to database!"
                                                    ))
                                                    .addOnFailureListener(e -> Log.d(
                                                            "Register Page Restaurant: ",
                                                            "Failed to send data!" + e
                                                    ));
                                        });
                                startActivity(new Intent(RegisterRestaurant.this,
                                        Login.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                String Error = task.getException().getMessage();
                                Log.d("Register Page: ", Error);
                                Toast.makeText(RegisterRestaurant.this,
                                        "Authentication failed: " + Error,
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(e -> {
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                // Handle the email already in use exception here
                                Toast.makeText(getApplicationContext(), "Email already in use", Toast.LENGTH_SHORT).show();
                                Log.e("Register Error", e.toString());
                            }
                        });
            }
        });

    }

    /**
     * This method retrieves the list of restaurant names from the Firebase Realtime Database
     * and adds them to a List of Strings, which is returned by the method.
     * If an exception is thrown, it will log an error message.
     * @return List of restaurant names
     */
    private List<String> loadRestaurantNames() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Restaurant");
        List<String> restaurantNames = new ArrayList<>();
        ref.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String nameRest = list.child("Name").getValue().toString();
                    nameRest = nameRest.toLowerCase(Locale.ROOT);
                    restaurantNames.add(nameRest);
                } catch (Exception e) {
                    Log.e("Register: ", "error finding restaurant name");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "error"));
        return restaurantNames;
    }

    /**
     * This method retrieves the list of user names from the Firebase Realtime Database
     * and adds them to a List of Strings, which is returned by the method.
     * If an exception is thrown, it will log an error message.
     * @return List of user names
     */
    public List<String> loadUsernames() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("User");
        List<String> usernamesTemp = new ArrayList<>();
        ref.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String nameToAdd = list.child("name").getValue().toString();
                    nameToAdd = nameToAdd.toLowerCase(Locale.ROOT);
                    usernamesTemp.add(nameToAdd);
                    Log.e("Register: ", nameToAdd);
                } catch (Exception e) {
                    Log.e("Register: ", "User without name found. Continuing..");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "error"));
        return usernamesTemp;
    }

    /**
     * This method retrieves the list of phone numbers from the Firebase Realtime Database
     * and adds them to a List of Strings, which is returned by the method.
     * If an exception is thrown, it will log an error message.
     * @return List of phone numbers
     */
    public List<String> loadPhones() {
        DatabaseReference refUser = FirebaseDatabase.getInstance()
                .getReference("User");
        List<String> phonesTemp = new ArrayList<>();

        refUser.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String phoneToAdd = list
                            .child("phone")
                            .getValue()
                            .toString()
                            .toLowerCase();
                    phonesTemp.add(phoneToAdd);
                } catch (Exception e) {
                    Log.e("Register: ", "Phones error");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "error"));
        return phonesTemp;
    }

    /**
     * This method retrieves the list of restaurant phone numbers from the Firebase Realtime Database
     * and adds them to a List of Strings, which is returned by the method.
     * If an exception is thrown, it will log an error message.
     * @return List of restaurant phone numbers
     */
    private List<String> loadRestPhones() {
        DatabaseReference refRest = FirebaseDatabase.getInstance()
                .getReference("Restaurant");
        List<String> phonesTemp = new ArrayList<>();

        refRest.get().addOnCompleteListener(task -> {
            for (DataSnapshot list : task.getResult().getChildren()) {
                try {
                    String phoneToAdd = list
                            .child("Phone")
                            .getValue()
                            .toString()
                            .toLowerCase();
                    Log.e("Phone rest", phoneToAdd);
                    phonesTemp.add(phoneToAdd);
                } catch (Exception e) {
                    Log.e("Register: ", "Phones error");
                }
            }
        }).addOnFailureListener(e -> Log.e("Register:", "error"));

        Log.e("PHones LIST: ", "" + phonesTemp);
        return phonesTemp;
    }

    /**
     * Retrieves the latitude and longitude coordinates of an address using the Geocoder class.
     *
     * @param context the context of the application
     * @param strAddress the string representation of the address to retrieve coordinates for
     * @return a LatLng object containing the latitude and longitude coordinates of the address
     */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            Log.e("RegisterRestaurant: ", ex.getMessage());
        }

        return p1;
    }

    /**
     * Check whether the entries of owner registration are empty
     *
     * @param restName the restaurants name
     * @param address  the address of a restaurant
     * @return boolean depending on whether they are empty
     */
    private boolean ownerEntries(String restName, String address) {
        if (TextUtils.isEmpty(restName)) {
            Toast.makeText(RegisterRestaurant.this,
                    "Enter a restaurant name!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(RegisterRestaurant.this,
                    "Enter an address!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    /**
     * Checks if the given entries are empty or not.
     * @param email Email of the user
     * @param name Name of the user
     * @param phone Phone number of the user\
     * @param password Password of the user
     * @return True if all entries are not empty, false otherwise
     */
    private boolean emptyEntries(String email, String name, String phone, String password) {

        //check if email bar is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if name bar is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if phone bar is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "Enter Phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if password bar is empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}