package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    android.support.v7.widget.Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    DatabaseReference mdDatabaseReference;
    String uid;
    int count;
    String ST_ProfileName;
    TextView ProfileName;
    boolean ServiceId;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        mdDatabaseReference = FirebaseDatabase.getInstance().getReference();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        dialog = new ProgressDialog(this);
        View header = navigationView.getHeaderView(0);
        ProfileName = header.findViewById(R.id.TextView_UserName);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.opendrawer, R.string.closedrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        dialog.setMessage("Loading...!");
        dialog.show();
        dialog.setCancelable(false);
        CheckedProfileIsComplete();
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Profile = new Intent(MainActivity.this, Profile.class);
                startActivity(Profile);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.Nav_Home:
                if (ServiceId) {
                    selectedFragment = new FragmentCurrentServices();
                } else {
                    selectedFragment = new FragmentHome();
                }
                break;
            case R.id.Nav_History:
                selectedFragment = new FragmentHistory();
                break;
            case R.id.Nav_Help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container,
                    selectedFragment).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    void CheckedProfileIsComplete() {

        mdDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ST_ProfileName = dataSnapshot.child("Users").child(uid).child("Profile").child("Name").getValue().toString();
                } catch (Exception e) {
                    Log.d("Exception", "" + e);
                }
                ProfileName.setText(ST_ProfileName);
                count = (int) dataSnapshot.child("Users").child(uid).child("Profile").getChildrenCount();
                if (count == 4) {
                    mdDatabaseReference.child("Users").child(uid).child("ProfileIsComplete").setValue("True").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("Profile", "Profile is complete.");
                            }
                        }
                    });
                } else {
                    mdDatabaseReference.child("Users").child(uid).child("ProfileIsComplete").setValue("False").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("Profile", "Profile is not complete." + count);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    protected void onStart() {
        mdDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ServiceId = dataSnapshot.child("Users").child(uid).hasChild("Current_Service_Id");
                dialog.dismiss();
                if (ServiceId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentCurrentServices()).commitAllowingStateLoss();
                    navigationView.setCheckedItem(R.id.Nav_Home);
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container, new FragmentHome()).commitAllowingStateLoss();
                    navigationView.setCheckedItem(R.id.Nav_Home);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
}
