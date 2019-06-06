package com.sam.hspm;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.ProgressView;
import com.sam.hspm.utils.PlaceAutocompleteAdapter;
import com.sam.hspm.utils.RequestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestService extends AppCompatActivity {

    private static final String TAG = "RequestService";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    FirebaseAuth mauth;
    DatabaseReference mreference;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(18.569871, 73.680315), new LatLng(18.580548, 73.993418));
    private static LatLng LatLng;
    String PcType, uid, ProblemType, SpecifiedProblem;
    String id, Area, HouseNo, Landdmark;
    Button Submit;
    Button BT_Submit2;
    TextInputEditText ET_Address, ET_Area, ET_HouseNo, ET_Landmark;
    TextInputLayout IL_Area, IL_HouseNo, IL_Landmark;
    AutoCompleteTextView ET_searchText;
    ImageView IV_gps;
    FirebaseUser user;
    String address;
    String pincode;
    private GoogleMap map;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    ProgressView progressView;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);
        if (getIntent() != null) {
            PcType = getIntent().getExtras().getString("PC Type");
            ProblemType = getIntent().getExtras().getString("Problem Types");
            SpecifiedProblem = getIntent().getExtras().getString("Specified Problem");
        }
        //Declaring variables
        mauth = FirebaseAuth.getInstance();
        user = mauth.getCurrentUser();
        mreference = FirebaseDatabase.getInstance().getReference();
        uid = user.getUid();

        ET_Address = findViewById(R.id.EditText_Address);
        ET_searchText = findViewById(R.id.EditText_input_search);
        IV_gps = findViewById(R.id.ic_gps);
        BT_Submit2 = findViewById(R.id.Button_Submit2);
        IL_Area = findViewById(R.id.InputLayout_Area);
        IL_HouseNo = findViewById(R.id.InputLayout_HouseNo);
        IL_Landmark = findViewById(R.id.InputLayout_Landmark);
        ET_Area = findViewById(R.id.EditText_Area);
        ET_HouseNo = findViewById(R.id.EditText_HouseNo);
        ET_Landmark = findViewById(R.id.EditText_Landmark);
        progressView = findViewById(R.id.ProgressView);
        // methods

        dialog = new ProgressDialog(this);
        dialog.setMessage("Submitting..!");
        dialog.setCancelable(false);
        progressView.start();

        BT_Submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Area = ET_Area.getText().toString();
                    HouseNo = ET_HouseNo.getText().toString();
                    Landdmark = ET_Landmark.getText().toString();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: Exception" + e.getMessage());
                }

                if (Area.isEmpty()) {
                    ET_Area.setError("Fields can't be empty");
                } else if (HouseNo.isEmpty()) {
                    ET_HouseNo.setError("Fields can't be empty");
                } else if (Landdmark.isEmpty()) {
                    Landdmark = "Landmark not specified.";
                } else {

                    if (AddressVerified()) {
                        if (!pincode.equals("412115")) {
                            showServiceGuaranteeDialogBox("Submit2");
                        } else {
                            showConfirmationDialogBox("Submit2");
                        }

                    } else {
                        Toast.makeText(RequestService.this, "WE ARE NOT THERE YET!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    public void showServiceGuaranteeDialogBox(final String btn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RequestService.this);
        builder.setTitle("Service Guarantee !")
                .setMessage("Your area is not eligible for 90 minutes service guarantee but our technician will reach you ASAP.")
                .setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                next(btn);
            }

        });
        builder.create();
        builder.show();
    }


    private void showConfirmationDialogBox(final String btn) {
        //Alert dialog box to confirm order
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RequestService.this);
        alertDialog.setTitle("Confirm Request !")
                .setMessage("Are you Sure, you want to confirm request ?")
                .setCancelable(false);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                next(btn);
            }

        });
        alertDialog.create();
        alertDialog.show();
    }

    void next(String btn) {

        Intent i = new Intent(RequestService.this, ReviewActivity.class);
        i.putExtra("PC Type", PcType);
        i.putExtra("Problem Types", ProblemType);
        i.putExtra("Specified Problem", SpecifiedProblem);
        i.putExtra("Latitude", LatLng.latitude);
        i.putExtra("Longitude", LatLng.longitude);
        i.putExtra("Address", address);

        if (btn.equals("Submit")) {
            i.putExtra("SubmitType", "Submit");
        }
        if (btn.equals("Submit2")) {
            i.putExtra("SubmitType", "Submit2");
            i.putExtra("Area", Area);
            i.putExtra("HouseNo", HouseNo);
            i.putExtra("Landmark", Landdmark);

        }
        startActivity(i);
    }

    private boolean AddressVerified() {

        String[] AreaPinCodes = {"412115", "411017", "411018", "412061", "411019", "411026", "411027", "411061", "411020", "411029", "411069", "411033", "411034", "411035", "411039", "411044", "411045", "411046", "411043", "411008", "411010", "411005", "411031", "411012", "411007", "411015", "411016", "411042", "411043", "411044", "411045", "411046", "411038", "411047", "411064", "411078"};

        if (pincode != null) {
            for (String AreaPinCode : AreaPinCodes) {
                if (pincode.equals(AreaPinCode)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting device Current location");
        ET_searchText.setText("");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {

                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location curruentLocation = (Location) task.getResult();
                            if (curruentLocation != null) {
                                moveCamera(new LatLng(curruentLocation.getLatitude(), curruentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                                displayLocation(new LatLng(curruentLocation.getLatitude(), curruentLocation.getLongitude()));
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(RequestService.this, "Unable to find current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Clearing Previous location");
        map.clear();
        Log.d(TAG, "moveCamera: Moving Camera to current location");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            map.addMarker(options);
        }
    }

    private void displayLocation(LatLng latLng) {
        LatLng = latLng;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> St_Location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = St_Location.get(0).getAddressLine(0);
            pincode = St_Location.get(0).getPostalCode();
            ET_Address.setText(address);
            progressView.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (mLocationPermissionGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(RequestService.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RequestService.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    init();
                }

                Log.d(TAG, "onMapReady: Map is ready");
            }
        });
    }


    public void IsServiceOK() {
        Log.d(TAG, "IsServiceOK: Checking Google Service Version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "IsServiceOK: Google play service working");
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "IsServiceOK: An error occurred but we can fixed it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RequestService.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "IsServiceOK: We cant make map request");
        }
    }

    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
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

    private void init() {
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, Places.getGeoDataClient(getApplicationContext(), null), LAT_LNG_BOUNDS, null);

        ET_searchText.setAdapter(placeAutocompleteAdapter);

        ET_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    geoLocate();
                }
                return false;
            }
        });
        IV_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
                checkLocationState();
            }
        });
        ET_searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                geoLocate();
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Geolocation");
        String searchString = ET_searchText.getText().toString();
        Geocoder geocoder = new Geocoder(RequestService.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: Address" + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            displayLocation(new LatLng(address.getLatitude(), address.getLongitude()));
        }

    }

    private void checkLocationState() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class Coordinates {
        public double Lat, Lng;

        public Coordinates(double lat, double lng) {
            Lat = lat;
            Lng = lng;
        }
    }

    public static class AddressData {
        public String Area, HouseNo, Landmark;
        public Coordinates Co_Ordinates;

        public AddressData(String area, String houseNo, String landmark, Coordinates coordinates) {
            Area = area;
            HouseNo = houseNo;
            Landmark = landmark;
            this.Co_Ordinates = coordinates;
        }

        public AddressData(Coordinates coordinates) {
            Co_Ordinates = coordinates;
        }
    }

    public static class AllData {
        public RequestData Problem;
        public AddressData Address;
        public String Uid, Status;


        public AllData(RequestData requestData, AddressData addressData, String uid, String aFalse) {
            Problem = requestData;
            Address = addressData;
            Uid = uid;
            Status = aFalse;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IsServiceOK();
        checkLocationState();
        getLocationPermission();

    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}
