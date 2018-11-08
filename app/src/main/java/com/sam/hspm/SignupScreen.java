package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpScreen extends AppCompatActivity {

    //TV = TextView
    //BT = Button
    Button BT_LogIn, BT_SignUp;
    EditText ET_Name, ET_Email, ET_PhoneNo, ET_Password,ET_Address;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog dialog;
    String St_Name, St_Email, St_PhoneNo, St_Password,St_Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);
        BT_LogIn = findViewById(R.id.Button_LogIn);
        BT_SignUp = findViewById(R.id.Button_SignUp);
        ET_Email = findViewById(R.id.EditText_Email);
        ET_Name = findViewById(R.id.EditText_Name);
        ET_PhoneNo = findViewById(R.id.EditText_PhoneNo);
        ET_Password = findViewById(R.id.EditText_Password);
        ET_Address=findViewById(R.id.EditText_Address);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dialog = new ProgressDialog(this);

        BT_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LogIn = new Intent(SignUpScreen.this, LoginScreen.class);
                startActivity(LogIn);
                finish();
            }
        });
        BT_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    dialog.setMessage("Signing Up ...!");
                    dialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(St_Email, St_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                            Log.d("Registration","Process Is SuccessFul");
                                Registration();
                            } else {
                                Toast.makeText(SignUpScreen.this, "Failed To Register", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }
        });
    }

    private void Registration() {
        RegistrationData data = new RegistrationData(St_Name, St_Email, St_PhoneNo,St_Address);
        try {
            databaseReference.child("Users").child(firebaseAuth.getUid()).child("Profile").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(SignUpScreen.this, "Registration Successful ..!", Toast.LENGTH_SHORT).show();
                        Intent MainActivity = new Intent(SignUpScreen.this, com.sam.hspm.MainActivity.class);
                        startActivity(MainActivity);
                        finish();
                    } else {
                        firebaseAuth.getCurrentUser().delete();
                        Toast.makeText(SignUpScreen.this, "Registration Failed !", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Exception", "" + e);
        }
    }

    private boolean validation() {
        St_Email = ET_Email.getText().toString().trim();
        St_Password = ET_Password.getText().toString().trim();
        St_Name = ET_Name.getText().toString().trim();
        St_PhoneNo = ET_PhoneNo.getText().toString().trim();
        St_Address = ET_Address.getText().toString();
        if (TextUtils.isEmpty(St_Name)) {
            ET_Name.setError("Fields can't be empty.");
            ET_Name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(St_Email)) {
            ET_Email.setError("Please enter email.");
            ET_Email.requestFocus();
            return false;
        }
        if (!emailValidator(St_Email)) {
            ET_Email.setError("Please Enter Valid Email Address");
            ET_Email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(St_PhoneNo)) {
            ET_PhoneNo.setError("Please enter PhoneNo.");
            ET_PhoneNo.requestFocus();
            return false;
        }
        if (St_PhoneNo.length() != 10) {
            ET_PhoneNo.setError("Please enter Valid PhoneNo.");
            ET_PhoneNo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(St_Address)) {
            ET_Password.setError("Please enter Address.");
            ET_Password.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(St_Password)) {
            ET_Password.setError("Please enter password.");
            ET_Password.requestFocus();
            return false;
        }
        if (St_Password.length() < 8) {
            ET_Password.setError("Password should be more than 8 chars");
            ET_Password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean emailValidator(String st_email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(st_email);
        return matcher.matches();
    }


    @Override
    public void onBackPressed() {
        Intent LoginPage = new Intent(this, LoginScreen.class);
        startActivity(LoginPage);
        super.onBackPressed();
    }
}
