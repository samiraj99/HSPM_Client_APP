package com.sam.hspm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class FragmentCurrentServices extends Fragment {
    @Nullable
    View v;
    public static final long START_TIME_IN_MILLIS = 600000;
    String uid, ST_Problem;
    String serviceId;
    FirebaseAuth mAuth;
    DatabaseReference mdatabaseReference;
    Button BT_Cancel, BT_Edit;
    ProgressDialog progressdialog;
    TextView TV_p1, TV_Problem, TimerText, TV_FindEmp;
    AnimatedCircleLoadingView animatedCircle;
    CountDownTimer countDownTimer;
    private long millisLeft = START_TIME_IN_MILLIS;
    private long endTime = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_current_services, container, false);
        TV_p1 = v.findViewById(R.id.TextView_P1);
        TV_Problem = v.findViewById(R.id.TextView_Problem);
        BT_Cancel = v.findViewById(R.id.Button_Cancel);
        BT_Edit = v.findViewById(R.id.Button_Edit);
        animatedCircle = v.findViewById(R.id.AnimatedCircleView);
        TimerText = v.findViewById(R.id.TimerText);
        TV_FindEmp = v.findViewById(R.id.TextView_findingEmp);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        progressdialog = new ProgressDialog(getContext());
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    serviceId =  dataSnapshot.child("Users").child(uid).child("Current_Service_Id").getValue().toString();

                } catch (Exception e) {
                    Log.e("DataNotFound", "" + e);
                }
                DisplayProblem(serviceId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BT_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressdialog.setMessage("Canceling");
                progressdialog.show();


                new AlertDialog.Builder(getContext())
                        .setTitle("Canceling Service")
                        .setMessage("Are you sure you want to cancel the Service? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                mdatabaseReference.child("Services").child(serviceId).removeValue();
                                mdatabaseReference.child("Users").child(uid).child("Current_Service_Id").setValue(null);
                                progressdialog.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressdialog.dismiss();
                    }
                }).setCancelable(false)
                        .show();
            }
        });
        BT_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.layout_dialog, null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                final EditText ET_Updated_Problem = mView.findViewById(R.id.EditText_Dialog_Problem);
                ET_Updated_Problem.setText(ST_Problem);
                Button BT_Update, BT_Cancel;
                BT_Update = mView.findViewById(R.id.Button_dialog_Update);
                BT_Cancel = mView.findViewById(R.id.Button_dialog_Cancel);
                final Spinner sp = mView.findViewById(R.id.Spinner_Services);
                String[] ServiceType = {"Hardware", "Software"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, ServiceType);
                sp.setAdapter(adapter);

                BT_Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Problem = ET_Updated_Problem.getText().toString();
                        String ProbleType = sp.getSelectedItem().toString();
                        if (!Problem.isEmpty()) {
                            mdatabaseReference.child("Services").child(serviceId).child("ProblemType").setValue(ProbleType);
                            mdatabaseReference.child("Services").child(serviceId).child("Problem").setValue(Problem);
                            dialog.dismiss();
                        } else {
                            ET_Updated_Problem.setError("Fields Can't be Empty");
                        }
                    }
                });
                BT_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });


        return v;
    }

    private void startTimer() {
        animatedCircle.startIndeterminate();
        countDownTimer = new CountDownTimer(millisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                animatedCircle.stopFailure();
                TimerText.setVisibility(View.INVISIBLE);
                TV_FindEmp.setText("Unable to find nearest employ..!");

            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (millisLeft / 1000) / 60;
        int seconds = (int) (millisLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        TimerText.setText(timeLeftFormatted);
    }

    private void DisplayProblem(final String id) {

        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ST_Problem = dataSnapshot.child("Services").child(id).child("Problem").getValue().toString();
                    TV_Problem.setText(ST_Problem);
                } catch (Exception e) {
                    Log.e("Error", "" + e);
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
        if (serviceId != null) {
            endTime = System.currentTimeMillis();
            SharedPreferences preferences = this.getActivity().getSharedPreferences("Prefs", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("millisLeft", millisLeft);
            editor.putLong("lastTime", endTime);
            editor.apply();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = this.getActivity().getSharedPreferences("Prefs", 0);
        millisLeft = preferences.getLong("millisLeft", START_TIME_IN_MILLIS);
        endTime = preferences.getLong("lastTime", 0);
        if (endTime != 0) {
            long diff = System.currentTimeMillis() - endTime;
            millisLeft = millisLeft - diff;
        }
        startTimer();
    }
}
