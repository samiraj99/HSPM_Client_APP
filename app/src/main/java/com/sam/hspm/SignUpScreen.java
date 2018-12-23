package com.sam.hspm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignUpScreen extends AppCompatActivity {

    //TV = TextView
    //BT = Button
    Button BT_LogIn, BT_SignUp;
    EditText ET_Name, ET_Email, ET_PhoneNo, ET_Password,ET_Address;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog dialog;
    String St_Name, St_Email, St_PhoneNo, St_Password,St_Address;
    ImageView IV_LocView;
    TextView TV_Auto_Loc;
    FusedLocationProviderClient client;
    List<Address> ST_location;
    Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);
        IV_LocView = findViewById(R.id.ImageView_location_Auto);
        TV_Auto_Loc = findViewById(R.id.Textview_Auto_detect);
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
        geocoder = new Geocoder(this, Locale.getDefault());
        client = LocationServices.getFusedLocationProviderClient(this);
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

        ET_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TV_Auto_Loc.setVisibility(View.VISIBLE);
                IV_LocView.setVisibility(View.VISIBLE);
                ET_Address.setFocusableInTouchMode(true);
            }
        });
        TV_Auto_Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Loading Address");
                dialog.show();
                if (ActivityCompat.checkSelfPermission(SignUpScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss();
                    Toast.makeText(SignUpScreen.this, "Failed to Detect Address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                client.getLastLocation().addOnSuccessListener(SignUpScreen.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            try {
                                ST_location = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String address = ST_location.get(0).getAddressLine(0);
                                ET_Address.setText(address);
                                dialog.dismiss();
                            } catch (IOException e) {
                                Toast.makeText(SignUpScreen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
    }

    private void Registration() {
        RegistrationData data = new RegistrationData(St_Name, St_Email, St_PhoneNo,St_Address);
        try {
            if (firebaseAuth.getUid() != null) {
                databaseReference.child("Users").child(firebaseAuth.getUid()).child("Profile").child("ProfileInfo").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            }
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
