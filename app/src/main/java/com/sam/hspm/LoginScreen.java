package com.sam.hspm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    TextView TV_SignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        TV_SignUp = findViewById(R.id.TextViewSignUp);
        TV_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp = new Intent(LoginScreen.this,SignUp_Screen.class);
                startActivity(SignUp);
            }
        });

    }
}
