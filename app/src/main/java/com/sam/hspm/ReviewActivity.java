package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sam.hspm.utils.RequestData;

public class ReviewActivity extends AppCompatActivity {

    String PcType, ProblemType, uid, id, SpecifiedProblem, SubmitType, Area, HouseNo, Landmark;
    Double Longitude, Latitude;
    FirebaseAuth mauth;
    FirebaseUser user;
    DatabaseReference mreference;
    ProgressDialog dialog;
    Button BT_Submit;
    TextView pctypedetails, problemtypedetails, specifiedproblemdetails, TV_Address;
    private static final String TAG = "ReviewActivity";
    private String Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mauth = FirebaseAuth.getInstance();
        user = mauth.getCurrentUser();
        mreference = FirebaseDatabase.getInstance().getReference();
        uid = user.getUid();

        BT_Submit = findViewById(R.id.Button_Submit1);
        pctypedetails = findViewById(R.id.pctypedetails);
        problemtypedetails = findViewById(R.id.problemtypedetails);
        specifiedproblemdetails = findViewById(R.id.specifiedproblemdetails);
        TV_Address = findViewById(R.id.TextView_Address1);

        try {
            if (getIntent() != null) {
                PcType = getIntent().getExtras().getString("PC Type");
                ProblemType = getIntent().getExtras().getString("Problem Types");
                SpecifiedProblem = getIntent().getExtras().getString("Specified Problem");
                Longitude = getIntent().getExtras().getDouble("Longitude");
                Latitude = getIntent().getExtras().getDouble("Latitude");
                Log.e(TAG, "onCreate: " + Latitude);
                SubmitType = getIntent().getExtras().getString("SubmitType");
                Address = getIntent().getExtras().getString("Address");
                assert SubmitType != null;
                if (SubmitType.equals("Submit2")) {
                    Area = getIntent().getExtras().getString("Area");
                    HouseNo = getIntent().getExtras().getString("HouseNo");
                    Landmark = getIntent().getExtras().getString("Landmark");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        pctypedetails.setText(PcType);
        specifiedproblemdetails.setText(SpecifiedProblem);
        problemtypedetails.setText(ProblemType);
        TV_Address.setText(Address);

        dialog = new ProgressDialog(ReviewActivity.this);


        BT_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Loading");
                if (SubmitType.equals("Submit"))
                    submit();
                else submit2();
            }
        });

    }

    private void submit() {
        dialog.show();
        RequestData data = new RequestData(PcType, ProblemType, SpecifiedProblem);
        RequestService.Coordinates coordinates = new RequestService.Coordinates(Latitude, Longitude);
        RequestService.AddressData addressData = new RequestService.AddressData(coordinates);
        RequestService.AllData allData = new RequestService.AllData(data, addressData, uid, "false");

        try {
            id = mreference.child("Services").push().getKey();
            mreference.child("Services").child(id).setValue(allData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SaveService(id);
                        dialog.dismiss();
                    } else {
                        Log.e("Error", "Failed to write in users data");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submit2() {
        dialog.show();
        RequestData requestData = new RequestData(PcType, ProblemType, SpecifiedProblem);
        final RequestService.Coordinates coordinates = new RequestService.Coordinates(Latitude, Longitude);
        final RequestService.AddressData addressData = new RequestService.AddressData(Area, HouseNo, Landmark, coordinates);
        RequestService.AllData allData = new RequestService.AllData(requestData, addressData, uid, "false");

        try {
            id = mreference.child("Services").push().getKey();
            mreference.child("Services").child(id).setValue(allData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SaveService(id);
                        dialog.dismiss();
                    } else {
                        Log.e("Error", "Failed to write in users data");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void SaveService(String id) {
        try {
            mreference.child("Users").child(uid).child("Current_Service_Id").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mreference.child("Users").child(uid).child("CurrentService").setValue(1);
                        Toast.makeText(ReviewActivity.this, "Request Send.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ReviewActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
