package com.sam.hspm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sam.hspm.Services.MyFirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    android.support.v7.widget.Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    DatabaseReference mdDatabaseReference;
    String uid;
    String ST_ProfileName;
    TextView ProfileName;
    String ServiceId, EmployeeId, Payment, IsPending;
    ProgressDialog dialog;
    private static final String TAG = "MainActivity";
    ImageView imageView;
    MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
    FirebaseUser firebaseUser;
    View v;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        firebaseUser = firebaseAuth.getCurrentUser();

        mdDatabaseReference = FirebaseDatabase.getInstance().getReference();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imageView = findViewById(R.id.ImageView_NoInternet);
        dialog = new ProgressDialog(this);
        View header = navigationView.getHeaderView(0);
        ProfileName = header.findViewById(R.id.TextView_UserName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.opendrawer, R.string.closedrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        dialog.setMessage("Loading...!");
        dialog.setCancelable(false);
        dialog.show();

        setProfileName();

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Profile = new Intent(MainActivity.this, Profile.class);
                startActivity(Profile);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        initFCM();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.Nav_Home:
                onStart();
                break;
            case R.id.Nav_History:
                selectedFragment = new FragmentHistory();
                break;
            case R.id.Nav_Help:
                Intent helpIntent = new Intent(MainActivity.this, ContactUs.class);
                startActivity(helpIntent);
                break;
            case R.id.Nav_Logout:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Logout");
                build.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                firebaseAuth.signOut();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(loginIntent);
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = build.create();
                alert.show();
                break;
            case R.id.Nav_Tnc:
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.activity_tc, null);
                WebView w = v.findViewById(R.id.WebView);
                w.loadUrl("https://www.hspmsolutions.com/terms-and-conditions/");
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Terms and Conditions \n");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(v);
                builder.create();
                builder.show();
                break;
            case R.id.Nav_PrivacyPolicy:
                LayoutInflater inflater1 = getLayoutInflater();
                v = inflater1.inflate(R.layout.activity_tc, null);
                WebView w1 = v.findViewById(R.id.WebView);
                w1.loadUrl("https://www.hspmsolutions.com/privacy-policy/");
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Privacy Policy \n");
                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder1.setView(v);
                builder1.create();
                builder1.show();
                break;

        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,
                    selectedFragment).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    void setProfileName() {
        try {
            mdDatabaseReference.child("Users").child(uid).child("Profile").child("ProfileInfo").child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        ST_ProfileName = dataSnapshot.getValue().toString();
                    } catch (Exception e) {
                        Log.d("Exception", "" + e);
                    }
                    ProfileName.setText(ST_ProfileName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInternetConnectionAvilable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Called");

        if (isInternetConnectionAvilable()) {
            try {
                mdDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ServiceId = dataSnapshot.child("CurrentService").getValue().toString();

                        Payment = dataSnapshot.child("Payment").getValue().toString();
                        IsPending = dataSnapshot.child("IsPending").getValue().toString();
                        dialog.dismiss();
                        if (!ServiceId.equals("0")) {
                            EmployeeId = dataSnapshot.child("RequestAcceptedBy").getValue().toString();
                            if (!EmployeeId.equals("0")) {
                                String Receipt = dataSnapshot.child("Receipt").getValue().toString();
                                if (Receipt.equals("1")) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentReceipt()).commitAllowingStateLoss();
                                } else {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentAcceptedService()).commitAllowingStateLoss();
                                }
                            } else if (IsPending.equals("1")) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentReceipt()).commitAllowingStateLoss();
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentCurrentServices()).commitAllowingStateLoss();
                            }
                        } else if (Payment.equals("1")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentPaymentSuccess()).commitAllowingStateLoss();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentHome()).commitAllowingStateLoss();
                        }
                        navigationView.setCheckedItem(R.id.Nav_Home);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dialog.dismiss();
            imageView.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onStart();
                            dialog.show();
                            imageView.setVisibility(View.GONE);
                        }
                    });
            snackbar.show();
        }
        super.onStart();
    }

    private void initFCM() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);
        myFirebaseInstanceIDService.sendRegistrationToServer(token);
    }
}
