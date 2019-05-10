package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FillProfile extends AppCompatActivity {

    private TextView backcolor;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRefs , databaseReference;
    private EditText fname, email, address;
    private TextInputLayout fnamewrap, emailwrap, addwrap;
    private Button save;
    String phoneNumber;
    private static final String TAG = "FillProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_profile);

        backcolor = findViewById(R.id.backcolor);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        double h1 = height * 0.35;
        backcolor.setHeight((int) h1);

        try {
            phoneNumber = getIntent().getStringExtra("PhoneNo");
        }catch (Exception e){
            Log.e(TAG, "onCreate: "+e);
        }

        mAuth = FirebaseAuth.getInstance();
        final String currentUser = mAuth.getCurrentUser().getUid();


        UserRefs = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Profile").child("ProfileInfo");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fname = findViewById(R.id.fname);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        fnamewrap = findViewById(R.id.fnameWrapper);
        emailwrap = findViewById(R.id.emailWrapper);
        addwrap = findViewById(R.id.addWrapper);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = fname.getText().toString();
                String emailid = email.getText().toString();
                String homeaddress = address.getText().toString();

                if (TextUtils.isEmpty(fullname) && (fullname.length() > 10)) {
                    fnamewrap.setError("Enter Full Number");
                } else {
                    fnamewrap.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(emailid) && !emailid.matches(Patterns.EMAIL_ADDRESS.toString())) {
                    emailwrap.setError("Enter Valid Email");
                } else {
                    emailwrap.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(homeaddress)) {
                    addwrap.setError("Enter Account Holders Name");
                } else {
                    addwrap.setErrorEnabled(false);
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("Name", fullname);
                map.put("Email", emailid);
                map.put("Address", homeaddress);
                map.put("PhoneNo", phoneNumber);
                UserRefs.updateChildren(map);

                databaseReference.child("Users").child(currentUser).child("Current_Service_Id").setValue(0);
                databaseReference.child("Users").child(currentUser).child("RequestAcceptedBy").setValue(0);
                databaseReference.child("Users").child(currentUser).child("Receipt").setValue(0);
                databaseReference.child("Users").child(currentUser).child("Payment").setValue(0);
                databaseReference.child("Users").child(currentUser).child("CurrentService").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(FillProfile.this,MainActivity.class);
                        startActivity(i);
                    }
                });

            }
        });
    }


    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().getCurrentUser().delete();
        super.onBackPressed();
    }
}
