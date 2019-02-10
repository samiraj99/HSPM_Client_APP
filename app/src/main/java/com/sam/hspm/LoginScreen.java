package com.sam.hspm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2;

    //TV = TextView
    //BT = Button

    TextView TV_SignUp, TV_ForgotPass;
    Button BT_SignIn;
    EditText ET_Email, ET_Pass;
    ProgressDialog dialog;
    FirebaseAuth firebaseAuth;
    String St_Email;
    String St_Password;
    String St_Google_email;
    String St_Google_Name;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        TV_SignUp = findViewById(R.id.TextViewSignUp);
        BT_SignIn = findViewById(R.id.Button_signIn);
        ET_Email = findViewById(R.id.EditText_Email);
        ET_Pass = findViewById(R.id.EditText_Password);
        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.Google_sign_in_button);
        TV_ForgotPass = findViewById(R.id.TextView_ForgotPass);
        dialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent MainActivityPage = new Intent(this, MainActivity.class);
            startActivity(MainActivityPage);
            finish();
        }
        requestPermission();

        BT_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                St_Email = ET_Email.getText().toString();
                St_Password = ET_Pass.getText().toString();

                if (TextUtils.isEmpty(St_Email)) {
                    Toast.makeText(LoginScreen.this, "Enter Email ...", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(St_Password)) {
                    Toast.makeText(LoginScreen.this, "Enter Password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.setMessage("Log In ...!");
                dialog.show();
                dialog.setCancelable(false);

                firebaseAuth.signInWithEmailAndPassword(St_Email, St_Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent MainPage = new Intent(LoginScreen.this, MainActivity.class);
                            startActivity(MainPage);
                            Toast.makeText(LoginScreen.this, "Successfully Log In..!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(LoginScreen.this, "Invalid Email and Password", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });


            }
        });
        TV_ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Sending Email..!");
                dialog.show();
                dialog.setCancelable(false);

                St_Email = ET_Email.getText().toString();
                if (TextUtils.isEmpty(St_Email)) {
                    Toast.makeText(LoginScreen.this, "Enter Email ...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                if (St_Email != null) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(St_Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginScreen.this, "Reset mail successful send.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
        TV_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp = new Intent(LoginScreen.this, SignUpScreen.class);
                startActivity(SignUp);
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Log In....");
                dialog.show();
                dialog.setCancelable(false);
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean flag = dataSnapshot.child(user.getUid()).exists();
                                        if (!flag) {
                                            St_Google_email = user.getEmail();
                                            St_Google_Name = user.getDisplayName();
                                            addData(St_Google_Name, St_Google_email);
                                        } else {
                                            dialog.dismiss();
                                            Intent I = new Intent(LoginScreen.this, MainActivity.class);
                                            startActivity(I);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Database Error", "Failed to retrieve data");
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(LoginScreen.this, "Failed To Log In ", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void addData(String st_google_Name, String st_google_email) {
        RegistrationData data = new RegistrationData(st_google_Name, st_google_email);
        String uid = firebaseAuth.getUid();
        databaseReference.child("Users").child(uid).child("Current_Service_Id").setValue(0);
        databaseReference.child("Users").child(uid).child("RequestAcceptedBy").setValue(0);
        databaseReference.child("Users").child(uid).child("Profile").child("ProfileInfo").setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Intent I = new Intent(LoginScreen.this, MainActivity.class);
                    startActivity(I);
                    finish();
                    Toast.makeText(LoginScreen.this, "Log In Successful ...!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.d("Database", "Failed to write data");
                        firebaseAuth.getCurrentUser().delete();
                    } catch (Exception e) {
                        Log.e("Exception", "" + e);
                    }
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

    }

}
