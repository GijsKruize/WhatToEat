package com.example.whattoeat.ui;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.ui.account.Login;
import com.example.whattoeat.ui.account.Register;

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

        createAccount = findViewById(R.id.btn_create_account);
        signIn = findViewById(R.id.signup_button);
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
            }
        });

    }
}