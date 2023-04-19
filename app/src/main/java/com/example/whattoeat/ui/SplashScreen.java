package com.example.whattoeat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.Login;
import com.example.whattoeat.ui.account.Register;
import com.example.whattoeat.ui.account.RegisterRestaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

import org.osmdroid.config.Configuration;

public class SplashScreen extends AppCompatActivity {
    private TextView createAccount;

    private TextView createOwnerAccount;
    private Button signIn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        createAccount = findViewById(R.id.btn_create_account);
        createOwnerAccount = findViewById(R.id.btn_create_owner_account);
        signIn = findViewById(R.id.signup_button);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE
                    }, 1);

        // User pressed the create account button, send them to the register page.
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashScreen.this, Register.class));
            }
        });

        // User pressed the create owner account button, send them to the register restaurant page.
        createOwnerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashScreen.this, RegisterRestaurant.class));
            }
        });

        // User pressed the sign in button, send them to the login page.
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashScreen.this, Login.class));
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final String UID = mAuth.getUid();
                if(UID != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("last login", timestamp.toString());
                    //Send a log of the login to the database.
                    myRef.child("User")
                            .child(UID)
                            .updateChildren(map)
                            .addOnSuccessListener(unused ->
                                    Log.d("Login Page : ",
                                            "Timestamp successfully updated!"))
                            .addOnFailureListener(error ->
                                    Log.d("Login Page: ",
                                            "Timestamp failed to save : " + error));
                }
            }
        });
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "Please turn on GPS before using our app", Toast.LENGTH_LONG).show();
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }

    }
}