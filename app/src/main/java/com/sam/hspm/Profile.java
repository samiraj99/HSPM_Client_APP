package com.sam.hspm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText ET_Address;
    EditText TIET_Name, TIET_PhoneNO;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String St_Name, St_PhoneNo, uid, St_Email, St_Address, St_ProfileUrl, St_ProfileImageFileName;
    StorageReference storageReference;
    public FusedLocationProviderClient client;
    ProgressDialog dialog;
    Button BT_LogOut;
    FirebaseAuth auth;
    TextView TV_Email, TV_Name, TV_Auto_Loc;
    Uri imageUri;
    ImageView IV_Edit, IV_Done, IV_Back, IV_Edit_Profile, IV_Profile, IV_Auto_Loc_Fetch;
    List<Address> ST_location;
    Geocoder geocoder;
    String Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("UsersProfileImages");
        auth = FirebaseAuth.getInstance();
        TIET_Name = findViewById(R.id.Profile_first_name);
        TIET_PhoneNO = findViewById(R.id.Profile_PhoneNo);
        ET_Address = findViewById(R.id.Profile_Address);
        user = FirebaseAuth.getInstance().getCurrentUser();
        BT_LogOut = findViewById(R.id.Button_LogOut);
        if (user != null) {
            uid = user.getUid();
        }
        IV_Profile = findViewById(R.id.ImageView_Profile);
        IV_Edit_Profile = findViewById(R.id.ImageView_Edit_Profile);
        IV_Back = findViewById(R.id.ImageView_Arrow_Back);
        IV_Edit = findViewById(R.id.ImageView_Edit);
        IV_Done = findViewById(R.id.ImageView_Done);
        IV_Auto_Loc_Fetch = findViewById(R.id.ImageView_Auto_Location);
        TV_Auto_Loc = findViewById(R.id.TextView_Auto_Loc);
        TV_Email = findViewById(R.id.Profile_image_email);
        TV_Name = findViewById(R.id.Profile_Image_first_name);

        geocoder = new Geocoder(this, Locale.getDefault());
        client = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Setting up Profile..!");
        dialog.show();
        databaseReference.child("Users").child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                St_Name = dataSnapshot.child("ProfileInfo").child("Name").getValue(String.class);
                St_Email = dataSnapshot.child("ProfileInfo").child("Email").getValue(String.class);
                St_PhoneNo = dataSnapshot.child("ProfileInfo").child("PhoneNo").getValue(String.class);
                St_Address = dataSnapshot.child("ProfileInfo").child("Address").getValue(String.class);
                try {
                    St_ProfileUrl = dataSnapshot.child("ProfileImage").child("ProfileImageUri").getValue(String.class);
                    St_ProfileImageFileName = dataSnapshot.child("ProfileImage").child("FileName").getValue(String.class);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                TIET_Name.setText(St_Name);
                TV_Name.setText(St_Name);
                TIET_PhoneNO.setText(St_PhoneNo);
                ET_Address.setText(St_Address);
                TV_Email.setText(St_Email);
                if (St_ProfileUrl != null) {
                    Picasso.get().load(St_ProfileUrl).into(IV_Profile);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (St_Email.length() > 1) {
                            dialog.dismiss();
                        }
                    }
                }, 2000);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        IV_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIET_Name.setEnabled(true);
                TIET_PhoneNO.setEnabled(true);
                ET_Address.setEnabled(true);
                IV_Done.setVisibility(View.VISIBLE);
                IV_Edit.setVisibility(View.INVISIBLE);
                IV_Auto_Loc_Fetch.setVisibility(View.VISIBLE);
                TV_Auto_Loc.setVisibility(View.VISIBLE);

                TIET_Name.setFocusable(true);
                TIET_Name.setFocusableInTouchMode(true);
                TIET_Name.requestFocus();

                TV_Auto_Loc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.setMessage("Loading Address");
                        dialog.show();
                        if (ActivityCompat.checkSelfPermission(Profile.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            dialog.dismiss();
                            Toast.makeText(Profile.this, "Failed to Detect Address.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        client.getLastLocation().addOnSuccessListener(Profile.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    try {
                                        ST_location = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        String address = ST_location.get(0).getAddressLine(0);
                                        ET_Address.setText(address);
                                        dialog.dismiss();
                                    } catch (IOException e) {
                                        Toast.makeText(Profile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });


        IV_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                St_Name = TIET_Name.getText().toString().trim();
                St_PhoneNo = TIET_PhoneNO.getText().toString().trim();
                St_Address = ET_Address.getText().toString();

                if (TextUtils.isEmpty(St_Name)) {
                    TIET_Name.setError("Fields can't be Empty");
                } else if (TextUtils.isEmpty(St_PhoneNo)) {
                    TIET_PhoneNO.setError("Fields can't be Empty");
                } else if (St_PhoneNo.length() != 10) {
                    TIET_PhoneNO.setError("Enter Valid Phone No.");
                } else if (TextUtils.isEmpty(St_Address)) {
                    ET_Address.setError("Fields can't be Empty");
                } else {
                    RegistrationData data = new RegistrationData(St_Name, St_Email, St_PhoneNo, St_Address);
                    databaseReference.child("Users").child(uid).child("Profile").child("ProfileInfo").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                TIET_Name.setEnabled(false);
                                TIET_PhoneNO.setEnabled(false);
                                ET_Address.setEnabled(false);
                                IV_Done.setVisibility(View.INVISIBLE);
                                IV_Edit.setVisibility(View.VISIBLE);
                                IV_Auto_Loc_Fetch.setVisibility(View.INVISIBLE);
                                TV_Auto_Loc.setVisibility(View.INVISIBLE);
                                TIET_Name.setFocusable(false);
                                TIET_PhoneNO.setFocusable(false);
                                Toast.makeText(Profile.this, "Profile has been updated.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "Check Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        BT_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        IV_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Id != null && Id.equals("Login")) {
                    Intent i = new Intent(Profile.this, MainActivity.class);
                    startActivity(i);
                }
                finish();
            }
        });
        IV_Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            IV_Profile.setImageURI(imageUri);
            dialog.setMessage("Uploading Image .....");
            dialog.show();
            if (St_ProfileImageFileName != null) {
                DeleteProfile();
            } else {
                uploadFile();
            }
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(CR.getType(uri));
    }

    private void uploadFile() {

        if (imageUri != null) {
            final String filename = String.valueOf(System.currentTimeMillis());
            final StorageReference reference = storageReference.child(filename + "." + getFileExtension(imageUri));
            reference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        UploadImageData upload = new UploadImageData(filename + "." + getFileExtension(imageUri), downloadUri.toString());
                        databaseReference.child("Users").child(uid).child("Profile").child("ProfileImage")
                                .setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (St_ProfileUrl != null) {
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });

                    }
                }
            });

        } else {
            Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void DeleteProfile() {
        StorageReference dbStorageRef = storageReference.child(St_ProfileImageFileName);
        dbStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child("Users").child(uid).child("Profile").child("ProfileImage").removeValue();
                uploadFile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Id != null && Id.equals("Login")) {
            Intent i = new Intent(Profile.this, MainActivity.class);
            startActivity(i);
        }
        finish();
    }
}