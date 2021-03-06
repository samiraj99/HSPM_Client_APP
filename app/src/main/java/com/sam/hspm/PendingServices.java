package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingServices extends Fragment {

    View v1;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid, serviceId;
    ListView listView;
    ArrayList<String> ServiceID = new ArrayList<>();
    ArrayList<String> ServiceStatus = new ArrayList<>();
    ArrayList<String> DateTime = new ArrayList<>();
    ArrayList<String> Amount = new ArrayList<>();
    private static final String TAG = "FragmentHistory";
    CustomAdapter customAdapter;
    ProgressDialog progressDialog;
    TextView TextView_NoService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_pending_services, container, false);

        FirebaseApp.initializeApp(getContext());

        Log.d(TAG, "onCreateView: Initializing variables");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        listView = v1.findViewById(R.id.ListView);
        TextView_NoService = v1.findViewById(R.id.TextView_NoService);

        if (Amount != null) {
            customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), PendingServicesDetails.class);
                i.putExtra("ServiceId", ServiceID.get(position));
                startActivity(i);
            }
        });
        return v1;
    }

    @Override
    public void onStart() {
        try {
            databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("History/PendingServices")) {
                        databaseReference.child("Users").child(uid).child("History").child("PendingServices").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.exists()) {
                                    serviceId = dataSnapshot.getValue(String.class);
                                    retrieveData(serviceId);
                                }

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: DatabaseError " + databaseError);
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        TextView_NoService.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStart();
    }


    private void retrieveData(final String sid) {

        Log.d(TAG, "retrieveData: 1 " + sid);
        try {
            databaseReference.child("PendingServices").child(sid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ServiceID.add(sid);
                        ServiceStatus.add("Service Pending");
                        DateTime.add(dataSnapshot.child("DateTime").child("Accepted").child("Date").getValue().toString() + ", " + dataSnapshot.child("DateTime").child("Accepted").child("Time").getValue().toString());
                        Amount.add(dataSnapshot.child("EstimateTime").getValue().toString());
                        customAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ServiceStatus.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.pendingservices_custom_listview, null);
            TextView serviceStatus, dateAndTime, amount;

            serviceStatus = view.findViewById(R.id.servicestatus);
            dateAndTime = view.findViewById(R.id.dateandtime);
            amount = view.findViewById(R.id.amount);

            serviceStatus.setText(ServiceStatus.get(position));
            dateAndTime.setText(DateTime.get(position));
            amount.setText(Amount.get(position));

            return view;
        }
    }
}
