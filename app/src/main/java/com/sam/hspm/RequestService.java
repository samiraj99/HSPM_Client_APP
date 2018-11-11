package com.sam.hspm;

import android.content.Intent;
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
    int id;
    Button Submit;
    TextView RequestType;
    FirebaseAuth mauth;
    DatabaseReference mreference;
    EditText ET_Problem;
    String ProfileIsComplete;

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

        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    id = ((int) dataSnapshot.child("Services").getChildrenCount());
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                    id = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                problem = ET_Problem.getText().toString();
                if (ProfileIsComplete.equals("True")) {
                    RequestData data = new RequestData(uid, problem, type);
                    if (id == 0) {
                        id = 1;
                    } else {
                        id++;
                    }
                    mreference.child("Services").child(String.valueOf(id)).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SaveService(id);
                            }
                        }
                    });
                }else {
                    Toast.makeText(RequestService.this, "Complete Profile ..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveService(int id) {
        mreference.child("Users").child(uid).child("Current_Service_Id").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(RequestService.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}
