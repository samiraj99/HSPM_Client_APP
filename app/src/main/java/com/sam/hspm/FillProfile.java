package com.sam.hspm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FillProfile extends AppCompatActivity {

    private TextView backcolor, txttc;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRefs, databaseReference;
    private EditText fname, email;
    private TextInputLayout fnamewrap, emailwrap, addwrap;
    private Button save;
    String phoneNumber, uid;
    private static final String TAG = "FillProfile";
    CheckBox ch, ch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_profile);

        backcolor = findViewById(R.id.backcolor);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        double h1 = height * 0.35;
        backcolor.setHeight((int) h1);

        if (getIntent() != null) {
            phoneNumber = getIntent().getStringExtra("PhoneNo");
        }

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        final String currentUser = mAuth.getCurrentUser().getUid();

        UserRefs = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Profile").child("ProfileInfo");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fname = findViewById(R.id.fname);
        email = findViewById(R.id.email);
        fnamewrap = findViewById(R.id.fnameWrapper);
        emailwrap = findViewById(R.id.emailWrapper);
//        addwrap = findViewById(R.id.addWrapper);
        txttc = findViewById(R.id.tc);
        ch = findViewById(R.id.check);
        ch1 = findViewById(R.id.check1);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = fname.getText().toString();
                String emailid = email.getText().toString();

                if (TextUtils.isEmpty(fullname) && (fullname.length() > 10)) {
                    fnamewrap.setError("Enter Full Number");
                    return;
                } else {
                    fnamewrap.setErrorEnabled(false);
                }

                if (!ch.isChecked()) {
                    Toast.makeText(FillProfile.this, "Please Accepted Policy.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(emailid) && !emailid.matches(Patterns.EMAIL_ADDRESS.toString())) {
                    emailwrap.setError("Enter Valid Email");
                    return;
                } else {
                    emailwrap.setErrorEnabled(false);
                }
                if (ch1.isChecked()) {
                    HashMap<String, Object> map2 = new HashMap<>();
                    map2.put("Downloads/" + uid, "5_Minutes_Engg");
                    databaseReference.updateChildren(map2);
                } else {
                    HashMap<String, Object> map2 = new HashMap<>();
                    map2.put(uid, "NA");
                    databaseReference.updateChildren(map2);
                }


                HashMap<String, Object> map = new HashMap<>();
                map.put("Name", fullname);
                map.put("Email", emailid);
                map.put("PhoneNo", phoneNumber);
                UserRefs.updateChildren(map);

                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("Current_Service_Id", "0");
                map1.put("RequestAcceptedBy", "0");
                map1.put("Receipt", "0");
                map1.put("Payment", "0");
                map1.put("CurrentService", "0");
                map1.put("IsPending", "0");

                databaseReference.child("Users").child(currentUser).updateChildren(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(FillProfile.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(i);
                    }
                });


            }
        });

        txttc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.activity_tc, null);
                WebView w = v.findViewById(R.id.WebView);
                w.loadUrl("https://www.hspmsolutions.com/terms-and-conditions/");
                final AlertDialog.Builder builder = new AlertDialog.Builder(FillProfile.this);
                builder.setTitle("Terms and Conditions \n");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(v);
                builder.create();
                builder.show();
            }
        });

    }

    @Override
    protected void onUserLeaveHint() {
        FirebaseAuth.getInstance().signOut();
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}
