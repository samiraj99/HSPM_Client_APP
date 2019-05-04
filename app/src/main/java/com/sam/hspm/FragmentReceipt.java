package com.sam.hspm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentReceipt extends Fragment {

    View v1;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ArrayList<ReceiptHelper> IssuedListwithAmount = new ArrayList<ReceiptHelper>();
    String uid, serviceId;
    ReceiptListAdapter adapter;
    ListView listView;
    TextView TV_Total;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_receipt, container, false);
        FirebaseApp.initializeApp(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listView = v1.findViewById(R.id.listview1);
        TV_Total = v1.findViewById(R.id.tvTotal);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();


        databaseReference.child("Users").child(uid).child("Current_Service_Id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    serviceId = dataSnapshot.getValue().toString();
                    retrieveReceiptData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v1;
    }

    public void retrieveReceiptData() {
        databaseReference.child("Services").child(serviceId).child("Receipt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ReceiptHelper helper = ds.getValue(ReceiptHelper.class);
                        IssuedListwithAmount.add(helper);
                    }
                    if (getActivity()!=null) {
                        adapter = new ReceiptListAdapter(getContext(), R.layout.receipt_adapter_layout, IssuedListwithAmount);
                        listView.setAdapter(adapter);
                    }
                    calculateTotal();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void calculateTotal() {
        int Total = 0;
        for (int i = 0; i < IssuedListwithAmount.size(); i++) {
            ReceiptHelper helper = IssuedListwithAmount.get(i);
            Total = helper.getAmount() + Total;
        }
        TV_Total.setText(String.valueOf(Total));
        progressDialog.dismiss();
    }
}
