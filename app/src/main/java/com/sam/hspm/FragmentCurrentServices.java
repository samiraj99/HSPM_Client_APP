package com.sam.hspm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sam.hspm.utils.ProblemDetails;

public class FragmentCurrentServices extends Fragment {
    @Nullable
    View v;
    String uid, ST_Problem;
    String serviceId;
    FirebaseAuth mAuth;
    DatabaseReference mdatabaseReference;
    Button BT_Cancel;
    ProgressDialog progressdialog;
    TextView TV_p1, TV_FindEmp, pctypedetails, problemtypedetails, specifiedproblemdetails;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_current_services, container, false);
        TV_p1 = v.findViewById(R.id.TextView_P1);
        BT_Cancel = v.findViewById(R.id.Button_Cancel);
        TV_FindEmp = v.findViewById(R.id.TextView_findingEmp);
        pctypedetails = v.findViewById(R.id.pctypedetails);
        problemtypedetails = v.findViewById(R.id.problemtypedetails);
        specifiedproblemdetails = v.findViewById(R.id.specifiedproblemdetails);

        FirebaseApp.initializeApp(getContext());

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        progressdialog = new ProgressDialog(getContext());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    serviceId = dataSnapshot.child("Users").child(uid).child("Current_Service_Id").getValue().toString();

                } catch (Exception e) {
                    Log.e("DataNotFound", "" + e);
                }
                if (!serviceId.equals("0")) {
                    DisplayProblem(serviceId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BT_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressdialog.setMessage("Canceling");
                progressdialog.show();


                new AlertDialog.Builder(getContext())
                        .setTitle("Canceling Service")
                        .setMessage("Are you sure you want to cancel the Service? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                mdatabaseReference.child("Services").child(serviceId).removeValue();
                                mdatabaseReference.child("Users").child(uid).child("Current_Service_Id").setValue(0);
                                mdatabaseReference.child("Users").child(uid).child("CurrentService").setValue(0);
                                progressdialog.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressdialog.dismiss();
                    }
                }).setCancelable(false)
                        .show();
            }
        });


        return v;
    }

    private void DisplayProblem(final String id) {

        mdatabaseReference.child("Services").child(id).child("Problem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ProblemDetails pd = dataSnapshot.getValue(ProblemDetails.class);
                    pctypedetails.setText(pd.getPcType());
                    problemtypedetails.setText(pd.getProblemType());
                    specifiedproblemdetails.setText(pd.getSpecifiedProblem());
                } catch (Exception e) {
                    Log.e("Error", "" + e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
