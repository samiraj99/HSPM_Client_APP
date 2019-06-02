package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MobileForm3 extends AppCompatActivity {

    String Brand;
    private static final String TAG = "MobileForm3";
    CheckBox Ch1, Ch2, Ch3, Ch4, Ch5, Ch6, Ch7, Ch8;
    List<CheckBox> ListOfCheckBox = new ArrayList<>();
    Button Bt_Next;
    EditText ET_SpecifiedProblem;
    String SpecifiedProblem;
    TextView specifyProblem;
    String ProblemType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_form3);

        try {
            Brand = getIntent().getExtras().getString("Brand");
            Log.d(TAG, "onCreate: Brand3 " + Brand);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        ListOfCheckBox.add(Ch1 = findViewById(R.id.CheckBox1));
        ListOfCheckBox.add(Ch2 = findViewById(R.id.CheckBox2));
        ListOfCheckBox.add(Ch3 = findViewById(R.id.CheckBox3));
        ListOfCheckBox.add(Ch4 = findViewById(R.id.CheckBox4));
        ListOfCheckBox.add(Ch5 = findViewById(R.id.CheckBox5));
        ListOfCheckBox.add(Ch6 = findViewById(R.id.CheckBox6));
        ListOfCheckBox.add(Ch7 = findViewById(R.id.CheckBox7));

        ET_SpecifiedProblem = findViewById(R.id.EditText_Problem);
        specifyProblem = findViewById(R.id.specifyProblem);

        Ch8 = findViewById(R.id.CheckBox8);

        Ch8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Ch8.isChecked()) {
                    specifyProblem.setVisibility(VISIBLE);
                    ET_SpecifiedProblem.setVisibility(VISIBLE);
                } else {
                    ET_SpecifiedProblem.setVisibility(GONE);
                    specifyProblem.setVisibility(GONE);
                }
            }
        });

        Bt_Next = findViewById(R.id.Button_Next);

        Bt_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox item : ListOfCheckBox) {
                    if (item.isChecked()) {
                        ProblemType = ProblemType.concat(" " + item.getText().toString());
                    }
                }
                SpecifiedProblem = ET_SpecifiedProblem.getText().toString();

                if (ProblemType.isEmpty()) {
                    Toast.makeText(MobileForm3.this, "Please select any one option.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SpecifiedProblem.isEmpty()) {
                    SpecifiedProblem = "Not Specified";
                }


                Intent i = new Intent(MobileForm3.this, RequestService.class);
                i.putExtra("PC Type", "Mobile " + Brand);
                i.putExtra("Problem Types", ProblemType);
                i.putExtra("Specified Problem", SpecifiedProblem);
                startActivity(i);
                Bt_Next.setEnabled(false);
            }
        });


    }

    @Override
    protected void onStart() {
        Bt_Next.setEnabled(true);
        super.onStart();
    }
}
