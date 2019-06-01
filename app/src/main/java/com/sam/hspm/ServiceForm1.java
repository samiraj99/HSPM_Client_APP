package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ServiceForm1 extends AppCompatActivity {

    private static final String TAG = "ServiceForm1";
    private static String key = "PC Type";
    RadioGroup radioGroup;
    Button BT_next;
    RadioButton SelectedButton;
    int selectedId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_form1);

        BT_next = findViewById(R.id.Button_Next1);
        radioGroup = findViewById(R.id.RadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent i = new Intent(ServiceForm1.this, ServiceForm2.class);
                switch (checkedId) {
                    case R.id.RadioButtonDesktop:
                        i.putExtra(key, "Desktop");
                        startActivity(i);
                        break;
                    case R.id.RadioButtonLaptop:
                        i.putExtra(key, "Laptop");
                        startActivity(i);
                        break;
                    case R.id.RadioButtonMobile:
                        Intent mobile = new Intent(ServiceForm1.this, MobileForm2.class);
                        startActivity(mobile);
                }

            }
        });

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = radioGroup.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: SelectedId" + selectedId);
                if (selectedId == -1) {
                    Toast.makeText(ServiceForm1.this, "Please select any one option.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SelectedButton = findViewById(selectedId);
                if (SelectedButton.getText().equals("Mobile")) {
                    Intent mobile = new Intent(ServiceForm1.this, MobileForm2.class);
                    startActivity(mobile);
                } else {
                    Intent i = new Intent(ServiceForm1.this, ServiceForm2.class);
                    i.putExtra(key, SelectedButton.getText().toString());
                    startActivity(i);
                }
            }
        });
    }
}
