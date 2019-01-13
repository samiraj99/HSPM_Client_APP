package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompleteProfile extends AppCompatActivity {

    String St_Name, St_Email, St_PhoneNo;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String uid;
    FirebaseUser user;
    EditText Et_Name, Et_Email, Et_PhoneNo;
    String PcType, ProblemType, SpecifiedProblem;
    Button Bt_Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        if (getIntent() != null) {
            PcType = getIntent().getExtras().getString("PC Type");
            ProblemType = getIntent().getExtras().getString("Problem Types");
            SpecifiedProblem = getIntent().getExtras().getString("Specified Problem");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Et_Email = findViewById(R.id.EditText_Email);
        Et_Name = findViewById(R.id.EditText_Name);
        Bt_Next = findViewById(R.id.Button_Next);
        Et_PhoneNo = findViewById(R.id.EditText_PhoneNo);

        if (user != null) {
            uid = user.getUid();
        }
        databaseReference.child("Users").child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                St_Name = dataSnapshot.child("ProfileInfo").child("Name").getValue(String.class);
                St_Email = dataSnapshot.child("ProfileInfo").child("Email").getValue(String.class);
                Et_Email.setText(St_Email);
                Et_Name.setText(St_Name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Bt_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                St_Email = Et_Email.getText().toString();
                St_Name = Et_Name.getText().toString();
                St_PhoneNo = Et_PhoneNo.getText().toString();

                if (TextUtils.isEmpty(St_Name)) {
                    Et_Name.setError("Fields can't be Empty");
                } else if (TextUtils.isEmpty(St_PhoneNo)) {
                    Et_PhoneNo.setError("Fields can't be Empty");
                } else if (St_PhoneNo.length() != 10) {
                    Et_PhoneNo.setError("Enter Valid Phone No.");
                } else if (TextUtils.isEmpty(St_Email)) {
                    Et_Email.setError("Fields can't be Empty");
                } else {
                    RegistrationData data = new RegistrationData(St_Name, St_Email, St_PhoneNo);
                    databaseReference.child("Users").child(uid).child("Profile").child("ProfileInfo").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(CompleteProfile.this, RequestService.class);
                                i.putExtra("PC Type", PcType);
                                i.putExtra("Problem Types", ProblemType);
                                i.putExtra("Specified Problem", SpecifiedProblem);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }
}
