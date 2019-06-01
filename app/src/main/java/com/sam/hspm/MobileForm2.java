package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MobileForm2 extends AppCompatActivity {


    RadioGroup radioGroup;
    RadioButton SelectedButton;
    TextView textView;
    EditText editText, ET_Model;
    String OtherBrand, Model;
    Button BT_next;
    int selectedId = 0;
    private static final String TAG = "MobileForm2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_form2);

        radioGroup = findViewById(R.id.RadioGroup1);
        textView = findViewById(R.id.TextView_SpecifyProblem);
        editText = findViewById(R.id.EditText_SpecifyProblem);
        BT_next = findViewById(R.id.Button_Next);
        ET_Model = findViewById(R.id.EditText_Model);

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Intent i = new Intent(MobileForm2.this, MobileForm3.class);
//                SelectedButton = findViewById(checkedId);
//                if (!SelectedButton.getText().toString().equals("Other")) {
//                    i.putExtra("Brand", SelectedButton.getText().toString());
//                } else {
//
//                    textView.setVisibility(View.VISIBLE);
//                    editText.setVisibility(View.VISIBLE);
//                }
//                startActivity(i);
//
//            }
//        });

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = radioGroup.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: SelectedId" + selectedId);
                if (selectedId == -1) {
                    Toast.makeText(MobileForm2.this, "Please select any one option.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Model = ET_Model.getText().toString();
                if (Model.isEmpty()) {
                    ET_Model.setError("Please Specify Model !");
                    return;
                }


                SelectedButton = findViewById(selectedId);
                if (SelectedButton.getText().equals("Other")) {
                    textView.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);

                    OtherBrand = editText.getText().toString();

                    if (OtherBrand.isEmpty()) {
                        editText.setError("Field's can't be empty!");
                        return;
                    }
                    Intent i = new Intent(MobileForm2.this, MobileForm3.class);
                    i.putExtra("Brand ", OtherBrand + Model);
                    startActivity(i);
                } else {
                    Intent i = new Intent(MobileForm2.this, MobileForm3.class);
                    i.putExtra("Brand ", SelectedButton.getText().toString() + Model);
                    startActivity(i);
                }
            }
        });

    }
}
