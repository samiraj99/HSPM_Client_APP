package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    //TV = TextView
    //BT = Button

    TextView TV_SignUp;
    Button BT_SignIn;
    EditText ET_Email,ET_Pass;
    ProgressDialog dialog;
    FirebaseAuth firebaseAuth;
    String St_Email, St_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        TV_SignUp = findViewById(R.id.TextViewSignUp);
        BT_SignIn=findViewById(R.id.Button_signIn);
        ET_Email=findViewById(R.id.EditText_Email);
        ET_Pass=findViewById(R.id.EditText_Password);
        firebaseAuth= FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        if(firebaseAuth.getCurrentUser()!=null) {
            finish();
            Intent MainActivityPage = new Intent(this,MainActivity.class);
            startActivity(MainActivityPage);
        }

        BT_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                St_Email= ET_Email.getText().toString();
                St_Password = ET_Pass.getText().toString();

                if(TextUtils.isEmpty(St_Email))
                {
                    Toast.makeText(LoginScreen.this, "Enter Email ...", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(St_Password))
                {
                    Toast.makeText(LoginScreen.this, "Enter Password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.setMessage("Log In ...!");
                dialog.show();

                firebaseAuth.signInWithEmailAndPassword(St_Email, St_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent MainPage = new Intent(LoginScreen.this,MainActivity.class);
                            startActivity(MainPage);
                            Toast.makeText(LoginScreen.this, "Successfully Log In..!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }else {
                            Toast.makeText(LoginScreen.this, "Invalid Email and Password", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });


            }
        });
        TV_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp = new Intent(LoginScreen.this,SignUpScreen.class);
                startActivity(SignUp);
                finish();
            }
        });
    }
}
