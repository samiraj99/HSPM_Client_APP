package com.sam.hspm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestService extends AppCompatActivity {

    String type, uid, problem;
    String id;
    Button Submit;
    TextView RequestType;
    FirebaseAuth mauth;
    DatabaseReference mreference;
    EditText ET_Problem;
    String ProfileIsComplete;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);
        type = getIntent().getExtras().getString("Value");
        Submit = findViewById(R.id.Button_Submit);
        RequestType = findViewById(R.id.TextView_RequestType);
        ET_Problem = findViewById(R.id.EditText_Problem);
        RequestType.setText(type);
        mauth = FirebaseAuth.getInstance();
        mreference = FirebaseDatabase.getInstance().getReference();
        uid = mauth.getUid();
        progressDialog = new ProgressDialog(this);

        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileIsComplete = dataSnapshot.child("Users").child(uid).child("ProfileIsComplete").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Sending Request");
                progressDialog.show();
                progressDialog.setCancelable(false);

                problem = ET_Problem.getText().toString();
                if (problem.isEmpty()) {
                    ET_Problem.setError("Request can't be empty ..!");
                    progressDialog.dismiss();
                    return;
                }
                if (ProfileIsComplete.equals("True")) {
                    RequestData data = new RequestData(uid, problem, type);

                    id = mreference.child("Services").push().getKey();

                    mreference.child("Services").child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SaveService(id);
                            } else {
                                Log.e("Error", "Failed to write in users data");
                            }
                        }
                    });
                }else {
                    Toast.makeText(RequestService.this, "Complete Profile ..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveService(String id) {
        mreference.child("Users").child(uid).child("Current_Service_Id").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(RequestService.this, "Request Send.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
