package com.sam.hspm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentAcceptedService extends Fragment {

    private static final String TAG = "FragmentAcceptedService";
    View v1;
    DatabaseReference clientDatabase, employeeDatabase;
    FirebaseDatabase firebaseDatabase;
    FirebaseApp employeeApp;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid, RequestId;
    String EmpID, Name, PhoneNo;
    TextView TV_Name, TV_PhoneNo;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_accepted_service, container, false);

        clientDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(getString(R.string.ApplicationId))
                    .setApiKey(getString(R.string.ApiKey))
                    .setDatabaseUrl(getString(R.string.DatabaseUrl))
                    .build();
            FirebaseApp.initializeApp(getContext(), options, "EmployeeDatabase");
            employeeApp = FirebaseApp.getInstance("EmployeeDatabase");
            firebaseDatabase = FirebaseDatabase.getInstance(employeeApp);
            employeeDatabase = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        TV_Name = v1.findViewById(R.id.TextView_Employee_Name);
        TV_PhoneNo = v1.findViewById(R.id.TextView_Employee_Phone);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...!");
        progressDialog.show();

        RetrieveEmployeeId();

        return v1;
    }

    @Override
    public void onDestroy() {
        employeeApp.delete();
        super.onDestroy();
    }

    private void RetrieveEmployeeData(String EmployeeId) {
        employeeDatabase.child("Users").child(EmployeeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("Name").getValue().toString();
                PhoneNo = dataSnapshot.child("PhoneNo").getValue().toString();
                TV_Name.setText(Name);
                TV_PhoneNo.setText(PhoneNo);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void RetrieveEmployeeId() {
        clientDatabase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmpID = dataSnapshot.child("RequestAcceptedBy").getValue().toString();
               if (!EmpID.equals("0")){RetrieveEmployeeData(EmpID);}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
