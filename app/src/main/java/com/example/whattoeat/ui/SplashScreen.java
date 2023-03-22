package com.example.whattoeat.ui;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.Login;
import com.example.whattoeat.ui.account.Register;

import org.osmdroid.config.Configuration;

public class SplashScreen extends AppCompatActivity {
    private Button createAccount;
//    private View splash0;
//    private View splash1;
    private Button signIn;

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
//        splash0 = findViewById(R.id.splashTop);
//        splash1 = findViewById(R.id.splashTop1);

//        int width = getResources().getDisplayMetrics().widthPixels/3;
//        int height = getResources().getDisplayMetrics().heightPixels/3;
//
//        splash0.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
//        splash1.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE
                    }, 1);


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
            }
        });

    }
}