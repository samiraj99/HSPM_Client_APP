package com.sam.hspm;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentHistory extends Fragment {

    View v1;
    DatabaseReference databaseReference, employeeDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseApp employeeApp;
    String uid;
    ListView listView;
    ArrayList<String> AddressList = new ArrayList<>();
    ArrayList<String> PcTypeList = new ArrayList<>();
    ArrayList<String> ProblemTypeList = new ArrayList<>();
    ArrayList<String> EmployeeName = new ArrayList<>();
    ArrayList<String> AcceptDate = new ArrayList<>();
    private static final String TAG = "FragmentHistory";
    String RequestAcceptedBy;
    CustomAdapter customAdapter;
    ProgressDialog progressDialog;
    TextView TextView_NoService;
    boolean flag;
    @Override
    public void onDestroy() {
        employeeApp.delete();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_history, container, false);

        //Initialization
        Log.d(TAG, "onCreateView: Initializing variables");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

        //Employee Database
        if(employeeApp == null){
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(getString(R.string.ApplicationId))
                    .setApiKey(getString(R.string.ApiKey))
                    .setDatabaseUrl(getString(R.string.DatabaseUrl))
                    .build();
            FirebaseApp.initializeApp(getContext(), options, "EmployeeDatabase");
            employeeApp = FirebaseApp.getInstance("EmployeeDatabase");
            firebaseDatabase = FirebaseDatabase.getInstance(employeeApp);
            employeeDatabase = firebaseDatabase.getReference();
        }

        listView = v1.findViewById(R.id.ListView);
        TextView_NoService = v1.findViewById(R.id.TextView_NoService);

        if (EmployeeName!=null) {
            customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
        }
        progressDialog  = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return v1;
    }

    @Override
    public void onStart() {

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("History")){
                    databaseReference.child("Users").child(uid).child("History").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                retrieveData(dataSnapshot);
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
                            Log.d(TAG, "onCancelled: DatabaseError "+databaseError);
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    TextView_NoService.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onStart();
    }



    private void retrieveData(DataSnapshot dataSnapshot) {

        Log.d(TAG, "retrieveData: 1 "+ dataSnapshot.getValue());

        for (DataSnapshot ds : dataSnapshot.child("Address").getChildren()) {
            try {
                Co_Ordinates co_ordinates = ds.getValue(Co_Ordinates.class);
                assert co_ordinates != null;
                String Address = convertAddress(new LatLng(co_ordinates.Lat, co_ordinates.Lng));
                AddressList.add(Address);
            } catch (Exception e) {
                Log.d(TAG, "retrieveData: Exception " + e.getMessage());
            }
        }
        AcceptDate.add(dataSnapshot.child("DateTime").child("Date").getValue().toString()+ " " +dataSnapshot.child("DateTime").child("Time").getValue().toString());
        PcTypeList.add(dataSnapshot.child("Problem").child("PcType").getValue().toString());
        ProblemTypeList.add(dataSnapshot.child("Problem").child("ProblemType").getValue().toString());

        RequestAcceptedBy = dataSnapshot.child("RequestAcceptedBy").getValue().toString();
        retrieveEmployeeData(RequestAcceptedBy);
    }

    private void retrieveEmployeeData(String requestAcceptedBy) {

        employeeDatabase.child("Users").child(requestAcceptedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    EmployeeName.add(dataSnapshot.child("Name").getValue().toString());
                    customAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return AddressList.size();
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
            view = getLayoutInflater().inflate(R.layout.history_custom_listview, null);
            TextView TV_ProblemType, TV_Address, TV_Employee_Name, TV_PcType, TV_AcceptDate;

            TV_AcceptDate = view.findViewById(R.id.AcceptDate);
            TV_Address = view.findViewById(R.id.Address);
            TV_ProblemType = view.findViewById(R.id.ProblemType);
            TV_Employee_Name = view.findViewById(R.id.Name);
            TV_PcType = view.findViewById(R.id.PcType);

            TV_AcceptDate.setText(AcceptDate.get(position));
            TV_Address.setText(AddressList.get(position));
            TV_ProblemType.setText(ProblemTypeList.get(position));
            TV_PcType.setText(PcTypeList.get(position));
            TV_Employee_Name.setText(EmployeeName.get(position));

            return view;
        }
    }

    public static class Co_Ordinates {
        public double Lat, Lng;

        public Co_Ordinates() {
        }

        public Co_Ordinates(double lat, double lng) {
            Lat = lat;
            Lng = lng;
        }

    }

    private String convertAddress(LatLng latLng) {
        String address = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> Location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = Location.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

}
