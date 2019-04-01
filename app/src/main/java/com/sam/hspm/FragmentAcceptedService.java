package com.sam.hspm;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class FragmentAcceptedService extends Fragment {

    private static final String TAG = "FragmentAcceptedService";
    View v1;
    DatabaseReference clientDatabase, employeeDatabase;
    FirebaseDatabase firebaseDatabase;
    FirebaseApp employeeApp;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid;
    String EmpID, Name, PhoneNo;
    TextView TV_Name, TV_PhoneNo;
    ProgressDialog progressDialog;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap map;
    private static final float DEFAULT_ZOOM = 15f;
    double Lat=0, Lng=0;
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
        getLocationPermission();
        checkLocationState();


        return v1;
    }

    private void updatedEmployeeLocation() {
        if (EmpID != null) {
            employeeDatabase.child("Users").child(EmpID).child("Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Lat = (double) dataSnapshot.child("Lat").getValue();
                        Lng = (double) dataSnapshot.child("Lng").getValue();
                        moveCamera(new LatLng(Lat, Lng), 15f, Name);
                        Log.d(TAG, "onDataChange: LAT" + Lat);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
                updatedEmployeeLocation();
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


    //Map


    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Clearing Previous location");
        map.clear();
        Log.d(TAG, "moveCamera: Moving Camera to current location");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motor_sports));
            map.addMarker(options);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (mLocationPermissionGranted) {
                  if (Lat != 0 ){ moveCamera(new LatLng(Lat, Lng), DEFAULT_ZOOM, Name);}
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }

        }
    }
    private void checkLocationState() {
        final LocationManager manager = (LocationManager)  getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static class Co_Ordinate {
        public double Lat, Lng;

        public Co_Ordinate() {
        }

        public Co_Ordinate(double lat, double lng) {
            Lat = lat;
            Lng = lng;
        }

    }
}
