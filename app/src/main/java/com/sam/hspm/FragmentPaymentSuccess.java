package com.sam.hspm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FragmentPaymentSuccess extends Fragment {

    View v1;
    RatingBar ratingBar;
    TextView TV_RatingBar, TV_Employee_Name;
    FirebaseAuth firebaseAuth;
    DatabaseReference clientDatabase, employeeDatabase;
    FirebaseDatabase firebaseDatabase;
    String uid, empId;
    FirebaseApp employeeApp;
    String EmpName, CurrentServiceId; //Employee Name
    ProgressDialog progressDialog;
    Button BT_submit;
    int rating;
    private static final String TAG = "FragmentPaymentSuccess";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_paymentsucess, container, false);
        FirebaseApp.initializeApp(getContext());

        //Declaration of all variables
        ratingBar = v1.findViewById(R.id.RatingBar);
        TV_RatingBar = v1.findViewById(R.id.TextView_RatingBar);
        firebaseAuth = FirebaseAuth.getInstance();
        TV_Employee_Name = v1.findViewById(R.id.TextView_PleaseRate);
        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading... from payment");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        BT_submit = v1.findViewById(R.id.Button_Submit);

        //Client Database
        uid = firebaseAuth.getUid();
        clientDatabase = FirebaseDatabase.getInstance().getReference();

        //Employee Database
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(getString(R.string.ApplicationId))
                .setApiKey(getString(R.string.ApiKey))
                .setDatabaseUrl(getString(R.string.DatabaseUrl))
                .build();
        FirebaseApp.initializeApp(getContext(), options, "EmployeeDatabase");
        employeeApp = FirebaseApp.getInstance("EmployeeDatabase");
        firebaseDatabase = FirebaseDatabase.getInstance(employeeApp);
        employeeDatabase = firebaseDatabase.getReference();


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch (((int) rating)) {
                    case 1:
                        TV_RatingBar.setText("poor");
                        break;
                    case 2:
                        TV_RatingBar.setText("average");
                        break;
                    case 3:
                        TV_RatingBar.setText("good");
                        break;
                    case 4:
                        TV_RatingBar.setText("Very good");
                        break;
                    case 5:
                        TV_RatingBar.setText("Excellent");
                        break;
                }
            }
        });

        getEmployeeId();

        BT_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Submitting");
                progressDialog.show();
                rating = ((int) ratingBar.getRating());
                if (rating != 0) {
                    clientDatabase.child("Services").child(CurrentServiceId).child("Ratings").setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //TODO: move service from services to history
                                clientDatabase.child("Users").child(uid).child("Current_Service_Id").setValue(0);
                                clientDatabase.child("Users").child(uid).child("RequestAcceptedBy").setValue(0);
                                clientDatabase.child("Users").child(uid).child("Receipt").setValue(0);
                                clientDatabase.child("Services").child(CurrentServiceId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        clientDatabase.child("Users").child(uid).child("History").child(CurrentServiceId).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                if (databaseError != null) {
                                                    Log.e(TAG, "onComplete: Copy Failed");
                                                } else {
                                                    clientDatabase.child("Users").child(uid).child("History").
                                                            child(CurrentServiceId).child("Uid").removeValue();
                                                    clientDatabase.child("Users").child(uid).child("History")
                                                            .child(CurrentServiceId).child("Status").removeValue();

                                                    clientDatabase.child("Services").child(CurrentServiceId).removeValue();
                                                    clientDatabase.child("Users").child(uid).child("Payment").setValue(0);
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

        return v1;
    }

    // Fetching Employee ID from Client database
    private void getEmployeeId() {
        clientDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    empId = Objects.requireNonNull(dataSnapshot.child("RequestAcceptedBy").getValue()).toString();
                    CurrentServiceId = Objects.requireNonNull(dataSnapshot.child("Current_Service_Id").getValue()).toString();
                    getEmployeeDetails(empId);
                   // progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Fetching Employee Details from Employee database.
    //                  OR
    //Fetch Current service Id from Employee database
    private void getEmployeeDetails(String eId) {

        employeeDatabase.child("Users").child(eId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    EmpName = Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString();
                    String temp = "Please rate " + EmpName;
                    TV_Employee_Name.setText(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onDestroy() {
        employeeApp.delete();
        super.onDestroy();
    }
}
