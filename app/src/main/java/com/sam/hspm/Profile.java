package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    TextInputEditText TIET_Name, TIET_PhoneNO;
    EditText ET_Address;
    String St_Name, St_PhoneNo, uid, St_Email, St_Address;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextView TV_Edit, TV_Done, TV_Email, TV_Name;
    ProgressDialog dialog;
    Button BT_LogOut;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        TIET_Name = findViewById(R.id.Profile_first_name);
        TIET_PhoneNO = findViewById(R.id.Profile_PhoneNo);
        ET_Address = findViewById(R.id.Profile_Address);
        user = FirebaseAuth.getInstance().getCurrentUser();
        BT_LogOut = findViewById(R.id.Button_LogOut);
        if (user != null) {
            uid = user.getUid();
        }
        TV_Edit = findViewById(R.id.Profile_button_edit);
        TV_Done = findViewById(R.id.Profile_button_done);
        TV_Email = findViewById(R.id.Profile_image_email);
        TV_Name = findViewById(R.id.Profile_Image_first_name);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..!");
        dialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                St_Name = dataSnapshot.child("Users").child(uid).child("Profile").child("Name").getValue(String.class);
                St_Email = dataSnapshot.child("Users").child(uid).child("Profile").child("Email").getValue(String.class);
                St_PhoneNo = dataSnapshot.child("Users").child(uid).child("Profile").child("PhoneNo").getValue(String.class);
                St_Address = dataSnapshot.child("Users").child(uid).child("Profile").child("Address").getValue(String.class);

                TIET_Name.setText(St_Name);
                TV_Name.setText(St_Name);
                TIET_PhoneNO.setText(St_PhoneNo);
                ET_Address.setText(St_Address);
                TV_Email.setText(St_Email);

                if (TV_Email.length() > 1) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TV_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIET_Name.setEnabled(true);
                TIET_PhoneNO.setEnabled(true);
                ET_Address.setEnabled(true);
                TV_Done.setVisibility(View.VISIBLE);
                TV_Edit.setVisibility(View.INVISIBLE);

                TIET_Name.setFocusable(true);
                TIET_Name.setFocusableInTouchMode(true);
                TIET_PhoneNO.setFocusable(true);
                TIET_PhoneNO.setFocusableInTouchMode(true);
            }
        });

        TV_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                St_Name = TIET_Name.getText().toString().trim();
                St_PhoneNo = TIET_PhoneNO.getText().toString().trim();
                St_Address = ET_Address.getText().toString();

                if (TextUtils.isEmpty(St_Name)) {
                    TIET_Name.setError("Fields can't be Empty");
                } else if (TextUtils.isEmpty(St_PhoneNo)) {
                    TIET_PhoneNO.setError("Fields can't be Empty");
                } else if (St_PhoneNo.length() != 10) {
                    TIET_PhoneNO.setError("Enter Valid Phone No.");
                } else if (TextUtils.isEmpty(St_Address)) {
                    ET_Address.setError("Fields can't be Empty");
                } else {
                    RegistrationData data = new RegistrationData(St_Name, St_Email, St_PhoneNo, St_Address);
                    databaseReference.child("Users").child(uid).child("Profile").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                TIET_Name.setEnabled(false);
                                TIET_PhoneNO.setEnabled(false);
                                ET_Address.setEnabled(false);
                                TV_Done.setVisibility(View.INVISIBLE);
                                TV_Edit.setVisibility(View.VISIBLE);
                                TIET_Name.setFocusable(false);
                                TIET_PhoneNO.setFocusable(false);
                                Toast.makeText(Profile.this, "Profile has been updated.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "Check Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        BT_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}