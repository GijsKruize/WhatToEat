package com.example.whattoeat.ui.account;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.SplashScreen;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.google.android.gms.maps.model.LatLng;

public class RegisterRestaurant extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPhone, editTextName, editTextPassword, editTextAddress, restaurantName;
    Button btnReg;

    Switch delivery;
    TextView returnBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    Boolean deliveryStatus;

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
        restaurantName = findViewById(R.id.restaurantNameRestaurantRegister);
        returnBtn = findViewById(R.id.existingUser);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Switch delivery = (Switch) findViewById(R.id.deliveryToggleButton);

//        textView.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                String name = String.valueOf(editTextName.getText());
                String phone = String.valueOf(editTextPhone.getText());
                String password = String.valueOf(editTextPassword.getText());
                String restaurant = String.valueOf(restaurantName.getText());
                String address = String.valueOf(editTextAddress.getText());
                deliveryStatus = delivery.isChecked();

                editTextName.setVisibility(View.INVISIBLE);
                editTextAddress.setVisibility(View.INVISIBLE);
                editTextEmail.setVisibility(View.INVISIBLE);
                editTextPassword.setVisibility(View.INVISIBLE);
                editTextPhone.setVisibility(View.INVISIBLE);
                restaurantName.setVisibility(View.INVISIBLE);
                btnReg.setVisibility(View.INVISIBLE);

                Toast.makeText(RegisterRestaurant.this, "Request for verification sent!\nYou will hear from us soon.",
                        Toast.LENGTH_LONG).show();

                LatLng location = getLocationFromAddress(getApplicationContext(), address);
                double latitude = location.latitude;
                double longitude = location.longitude;

                try {
                    String stringSenderEmail = "whattoeattue@gmail.com";
                    String stringReceiverEmail = "rikjanssen1@hotmail.com";
                    String stringPasswordSenderEmail = "hxctyygnjfydqswa";

                    String stringHost = "smtp.gmail.com";

                    Properties properties = System.getProperties();

                    properties.put("mail.smtp.host", stringHost);
                    properties.put("mail.smtp.port", "465");
                    properties.put("mail.smtp.ssl.enable", "true");
                    properties.put("mail.smtp.auth", "true");

                    javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                        }
                    });

                    MimeMessage mimeMessage = new MimeMessage(session);
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

                    mimeMessage.setSubject("Restaurant verification for: " + restaurant);
                    mimeMessage.setText("Hello WhatToEat team, \n\n" + name + " would like to verify their restaurant.\n\nPhone: " + phone + "\n\nEmail: " + email + "\n\nAddress: " + address);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Transport.send(mimeMessage);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                HashMap<String, Object> mapRestUser = new HashMap<>();
                mapRestUser.put("email", email);
                mapRestUser.put("name", name);
                mapRestUser.put("restaurant", restaurant);

                mapRestUser.put("last login", timestamp.toString());

                HashMap<String, Object> mapRest = new HashMap<>();
                mapRest.put("Delivers", deliveryStatus);
                mapRest.put("Hyperlink", "");
                mapRest.put("Image", "");
                mapRest.put("Latitude", latitude);
                mapRest.put("Longitude", longitude);
                mapRest.put("Name", restaurant);
                mapRest.put("Phone", phone);
                mapRest.put("Style", "");
                mapRest.put("Verified", false);




                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                final String UID = mAuth.getUid();

                                myRef.child("Owner")
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
                                Toast.makeText(RegisterRestaurant.this, "Account Created.",
                                        Toast.LENGTH_SHORT).show();
                                myRef.child("Restaurant")
                                        .child(restaurant)
                                        .setValue(mapRest)
                                        .addOnSuccessListener(unused -> Log.d(
                                                "Register Page Restaurant: ",
                                                "Successfully sent data to database!"
                                        ))
                                        .addOnFailureListener(e -> Log.d(
                                                "Register Page Restaurant: ",
                                                "Failed to send data!" + e
                                        ));
                                startActivity(new Intent(RegisterRestaurant.this, Login.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                String Error = task.getException().getMessage();
                                Log.d("Register Page: ", Error);
                                Toast.makeText(RegisterRestaurant.this, "Authentication failed: " + Error,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
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
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}