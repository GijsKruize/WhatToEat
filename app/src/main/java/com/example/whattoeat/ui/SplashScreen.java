package com.example.whattoeat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.Login;
import com.example.whattoeat.ui.account.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

import org.osmdroid.config.Configuration;

public class SplashScreen extends AppCompatActivity {
    private Button createAccount;
//    private View splash0;
//    private View splash1;
    private Button signIn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    protected DatabaseReference myRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        createAccount = findViewById(R.id.btn_create_account);
        signIn = findViewById(R.id.signup_button);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

//        splash0 = findViewById(R.id.splashTop);
//        splash1 = findViewById(R.id.splashTop1);

//        int width = getResources().getDisplayMetrics().widthPixels/3;
//        int height = getResources().getDisplayMetrics().heightPixels/3;
//
//        splash0.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
//        splash1.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashScreen.this, Register.class));
            }
        });

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

    }
}