package com.sam.hspm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    //TV = TextView
    //BT = Button

    TextView TV_SignUp;
    Button  SignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        TV_SignUp = findViewById(R.id.TextViewSignUp);
        SignIn=findViewById(R.id.button_signIn);
        TV_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp = new Intent(LoginScreen.this,SignUpScreen.class);
                startActivity(SignUp);
                finish();
            }
        });
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainPage = new Intent(LoginScreen.this,MainActivity.class);
                startActivity(MainPage);
                finish();
            }
        });
    }
}
