package com.sam.hspm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpScreen extends AppCompatActivity {

    //TV = TextView
    //BT = Button
    Button BT_LogIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);
        BT_LogIn = findViewById(R.id.ButtonLogIn);
        BT_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LogIn = new Intent(SignUpScreen.this,LoginScreen.class);
                startActivity(LogIn);
                finish();
            }
        });
    }
}
