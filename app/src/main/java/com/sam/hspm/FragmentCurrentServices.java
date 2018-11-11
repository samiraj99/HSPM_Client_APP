package com.sam.hspm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentCurrentServices extends Fragment {
    @Nullable
    View v;
    TextView TV_p1, TV_Problem, TV_NoService;
    String uid, ST_Problem;
    String serviceId;
    FirebaseAuth mAuth;
    DatabaseReference mdatabaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_current_services, container, false);
        TV_p1 = v.findViewById(R.id.TextView_P1);
        TV_Problem = v.findViewById(R.id.TextView_Problem);
        TV_NoService = v.findViewById(R.id.TextView_NoService);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();

        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    serviceId =  dataSnapshot.child("Users").child(uid).child("Current_Service_Id").getValue().toString();
                    Log.v("ServiceId",""+serviceId);
                } catch (Exception e) {
                    Log.e("DataNotFound", "" + e);
                    serviceId = "0";
                }

                DisplayProblem(serviceId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }

    private void DisplayProblem(final String id) {
        if (!id.equals("0")) {
            TV_NoService.setVisibility(View.GONE);
            TV_p1.setVisibility(View.VISIBLE);
            TV_Problem.setVisibility(View.VISIBLE);

            mdatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ST_Problem = dataSnapshot.child("Services").child(id).child("Problem").getValue().toString();
                    TV_Problem.setText(ST_Problem);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            TV_NoService.setVisibility(View.VISIBLE);
            TV_p1.setVisibility(View.GONE);
            TV_Problem.setVisibility(View.GONE);
        }

    }
}
