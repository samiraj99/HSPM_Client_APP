package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceForm2 extends AppCompatActivity {

    String PcType;
    CheckBox Ch1, Ch2, Ch3, Ch4, Ch5, Ch6, Ch7;
    List<CheckBox> ListOfCheckBox = new ArrayList<>();
    String ProblemType = "";
    Button Bt_Next;
    EditText ET_SpecifiedProblem;
    String SpecifiedProblem;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid;
    String ProfileIsComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_form2);
        if (getIntent() != null) {
            PcType = getIntent().getExtras().getString("PC Type");
        }
        ListOfCheckBox.add(Ch1 = findViewById(R.id.CheckBox1));
        ListOfCheckBox.add(Ch2 = findViewById(R.id.CheckBox2));
        ListOfCheckBox.add(Ch3 = findViewById(R.id.CheckBox3));
        ListOfCheckBox.add(Ch4 = findViewById(R.id.CheckBox4));
        ListOfCheckBox.add(Ch5 = findViewById(R.id.CheckBox5));
        ListOfCheckBox.add(Ch6 = findViewById(R.id.CheckBox6));
        ListOfCheckBox.add(Ch7 = findViewById(R.id.CheckBox7));
        Bt_Next = findViewById(R.id.Button_Next);
        ET_SpecifiedProblem = findViewById(R.id.EditText_Problem);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }
        databaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    ProfileIsComplete = dataSnapshot.child("ProfileIsComplete").getValue().toString();
                } catch (Exception e) {
                    Log.e("Error", "" + e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Bt_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (CheckBox item : ListOfCheckBox) {
                    if (item.isChecked()) {
                        ProblemType = ProblemType + " " + item.getText().toString();
                    }
                }
                SpecifiedProblem = ET_SpecifiedProblem.getText().toString();

                if (ProblemType.isEmpty()) {
                    Toast.makeText(ServiceForm2.this, "Please select any one option.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (SpecifiedProblem.isEmpty()) {
                    SpecifiedProblem = " ";
                }


                if (ProfileIsComplete.equals("True")) {
                    Intent i = new Intent(ServiceForm2.this, RequestService.class);
                    i.putExtra("PC Type", PcType);
                    i.putExtra("Problem Types", ProblemType);
                    i.putExtra("Specified Problem", SpecifiedProblem);
                    startActivity(i);
                } else {
                    Intent i = new Intent(ServiceForm2.this, CompleteProfile.class);
                    i.putExtra("PC Type", PcType);
                    i.putExtra("Problem Types", ProblemType);
                    i.putExtra("Specified Problem", SpecifiedProblem);
                    startActivity(i);
                }

            }
        });
    }
}
