package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobileForm2 extends AppCompatActivity {


    RadioGroup radioGroup;
    RadioButton SelectedButton;
    // TextView textView;
    // EditText editText;//  , ET_Model;
    String OtherBrand, Model;
    Button BT_next;
    int selectedId = 0;
    private static final String TAG = "MobileForm2";
    Spinner spinner_model;
    ArrayList<String>[] s = new ArrayList[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_form2);

        radioGroup = findViewById(R.id.RadioGroup1);
        //textView = findViewById(R.id.TextView_SpecifyProblem);
        //editText = findViewById(R.id.EditText_SpecifyProblem);
        BT_next = findViewById(R.id.Button_Next);
        // ET_Model = findViewById(R.id.EditText_Model);
        spinner_model = findViewById(R.id.spinner_model);


        s[0] = new ArrayList<>(Arrays.asList("Select Model", "  Galaxy M20e", " Galaxy A80", " Galaxy A90(upcoming)", " Galaxy A2 Core ", " Galaxy A60", " Galaxy A70", " Galaxy A40", " Galaxy A20", " Galaxy A10"
                , " Galaxy A30"
                , " Galaxy A50"
                , " Galaxy A70"
                , " Galaxy S10 5G"
                , " Galaxy Fold"
                , " Galaxy S10e"
                , " Galaxy S10 +"
                , " Galaxy S10"
                , " Galaxy M30"
                , " Galaxy A9 pro(2019)"
                , " Galaxy M20"
                , " Galaxy M10"
                , " Galaxy A8"
                , " W2019"
                , " Galaxy j4 core"
                , " Galaxy A6s"
                , " Galaxy A9(2018)"
                , " Galaxy A7(2018)"
                , " Galaxy J6 +"
                , " Galaxy J4 +"
                , " Galaxy J2core"
                , " Galaxy On8(2018)"
                , " Galaxy NOte9"
                , " Galaxy jean"
                , " Galaxy On6"
                , " Galaxy A8star"
                , " Galaxy J7(2018)"
                , " Galaxy J7(2016)"
                , " Galaxy J3(2018)"
                , " Galaxy A9 star"
                , " Galaxy A9 star lite"
                , " Galaxy wide"
                , " Galaxy J8"
                , " Galaxy J6"
                , " Galaxy J4"
                , " Galaxy A6"
                , " Galaxy A6 +"
                , " Galaxy A6"
                , " Galaxy J7 duo"
                , " Galaxy J7 prime2"
                , " Galaxy S9"
                , " Galaxy s9 +"
                , " Galaxy j2(2018)"
                , " Galaxy J2 pro(2018)"
                , " Galaxy On7 prime"
                , " Galaxy A9 pro"
                , " Galaxy A5(2016)"
                , " Galaxy J3(6)"
                , " Galaxy Note5"
                , " Galaxy S6 Edge"
                , " Galaxy S8 +"
                , " Galaxy S8"
                , " Galaxy J7(2017)"
                , " Galaxy J3"
                , " Galaxy J7max"
                , " Galaxy J7pro"
                , " Galaxy Note8"
                , " Galaxy J7 +"
                , " Galaxy C8"
                , " Galaxy J2(2017)"
                , " Galaxy A8"
                , " Galaxy S III"
                , " Galaxy note"
                , " Galaxy Champ 2"
                , " Galaxy Wave3"
                , " Galaxy Y"
                , " Galaxy S II"
                , " Galaxy S"
                , " Galaxy S8 active"
                , " Galaxy J2 pro"
                , " Galaxy C7"
                , " Galaxy C5"
                , " Galaxy Amp prime"
                , " Galaxy young 2));"));

        s[1] = new ArrayList<>(Arrays.asList("Select Model", "iPhone 5"

                , "iPhone 5c"

                , "iPhone 5s"

                , "iPhone 6"

                , "iPhone 6 Plus"

                , "iPhone SE"

                , "iPhone 7"

                , "iPhone 7 Plus"

                , "iPhone 8"

                , "iPhone 8 plus"

                , "iPhone X"

                , "iPhone XR"

                , "iPhone XS"

                , "iPhone XS Max"

        ));

        s[2] = new ArrayList<>(Arrays.asList("Select Model", " 3"

                , " 3T"

                , " 5"

                , " 5T"

                , " 6"

                , " 6T"

                , " 6T McLaren Edition"

                , " 7"

                , " 7 pro"));

        s[3] = new ArrayList<>(Arrays.asList("Select Model", " F11 pro"

                , " Reno"

                , " A5s"

                , " F9 pro"

                , " A5"

                , " K1"

                , " A3s"

                , " A7"

                , " F9"

                , " F11"

                , " F7"

                , " A1K"

                , " F5"

                , " A5 64GB"

                , " A3s 32GB"

                , " R17 pro"

                , " R15 pro"

                , " A57"

                , " Find x"

                , " R17"));

        s[4] = new ArrayList<>(Arrays.asList("Select Model", " V15 pro"
                ,
                " Y17"
                ,
                " V15"
                ,
                " V9"
                ,
                " Y91"
                ,
                " Y95"
                ,
                " V11 pro"
                ,
                " Y91i"
                ,
                " Y93"
                ,
                "  V11"
                ,
                " Y81"
                ,
                " Y83"
                ,
                " V9 pro"
                ,
                " Y93 64GB"
                ,
                " V5"
                ,
                " Y91i 32GB"
                ,
                " Y81i"
                ,
                " V9 Youth"
                ,
                " Y71"
                ,
                " NEX"
                ,
                " V7 Ps"
                ,
                " Y83 pro"
                ,
                " Y53"
                ,
                " V7"
                ,
                " Y81 4GB RAM"
                ,
                " V5s"
                ,
                " V5 Plus"
                ,
                " Y91 3GB RAm"
                ,
                " Y15 2019"
                ,
                " Y15 pro 8GB RAM"
                ,
                " X21"
                ,
                " Y51L"
                ,
                " Y71i"
                ,
                " Y55s"
                ,
                " Y69"
                ,
                " Y66"
                ,
                " V3"
                ,
                " Y55L"
                ,
                " V9 pro 4GB RAM"
                ,
                " Y21L"
                ,
                " Y15S"
                ,
                " Z1i"));

        s[5] = new ArrayList<>(Arrays.asList("Select Model", " XA1"
                ,
                " R1 Plus"
                ,
                " R1"
                ,
                " XA Ultra Dual"
                ,
                " XZ Premium"
                ,
                " XZ"
                ,
                " Z3"
                ,
                " L2"
                ,
                " XZ2"
                ,
                " XA Dual"
                ,
                " XA1 Ultra"
                ,
                " Z3+"
                ,
                " 1"
                ,
                " XA2"
                ,
                " 10"
                ,
                " 10 Plus"
                ,
                " XA2 plus"
                ,
                " XZ3"
                ,
                " XA1 Plus 32GB"
                ,
                " Z5 Dual"
                ,
                " Z2"
                ,
                " Z"
                ,
                " C5 ultra Dual"
                ,
                " m5 Dual"
                ,
                " Z3 Compact"
                ,
                " XZ2 premium"
                ,
                " T2 Ultra"
                ,
                " M2 Dual"
                ,
                " Xzs"
                ,
                " E4g Dual"
                ,
                " XZ1"
                ,
                " C3 Dual Sim"
                ,
                " Z5 premium Dual"
                ,
                " M4 Aqua Dual 16 GB"
                ,
                " E3 Dual"
                ,
                " x Dual"
                ,
                " XZ2 Compact"
                ,
                " T3"
                ,
                " Z1 Compact"));

        s[6] = new ArrayList<>(Arrays.asList("Select Model", " 10 Lite",
                " Play",
                " 8X",
                " 9N",
                " 9 Lite",
                " 7S (Play 7)",
                " 7A",
                " View 20",
                " 8C",
                " 7C",
                " 8X",
                " 7X",
                " 9i",
                " 9 Lite",
                " 10",
                " 10 Lite",
                " Play",
                " 8X",
                " 9N",
                " 8 Lite",
                " 6",
                " 8",
                " Holly 3",
                " 6X",
                " Holly 2 Plus",
                " 6X",
                " 5X",
                " Holly 4 Plus",
                " 6 Plus",
                " Holly 4",
                " 8 Smart",
                " Bee",
                " Bee 2",
                " Bee 4G",
                " Holly 3 Plus",
                " V10 (View 10)",
                " 4X",
                " 9",
                " 5C",
                "Huawei  3X",
                " 5",
                " 4C"));

        s[7] = new ArrayList<>(Arrays.asList("Select Model", " vibe Z2",
                " vibe Z2 pro",
                " K860",
                " vibe X",
                " vibe X3",
                " Phab 2 Pro",
                " Z5 Pro",
                " S820",
                " vibe Z",
                " S560",
                " S920",
                " A859",
                " P2",
                " K900",
                " S880",
                " S860",
                " P780",
                " S930",
                " S850",
                " Sysley S90",
                " P770",
                " A526",
                " S650",
                " S890",
                " vibe S1 Lite",
                " Vibe S1",
                " K3 Note Music",
                " Phab",
                " Phab 2 Plus",
                " Lemon 3",
                " S580",
                " K5 Note",
                " vibe k5 Note",
                " Vibe k4 Note",
                " K8 Note",
                " A5",
                " K8 Plus",
                " A5",
                " Z2 Plus 64GB (Zuk Z2)",
                " K6 Power",
                " K6 Note",
                " K9",
                " K9 Note",
                " A1000",
                " A2010",
                " A6600",
                " A7000",
                " Vibe P1",
                " K3 Note",
                " P2",
                " Vibe B",
                " Vibe P1 Turbo",
                " K8",
                " A6600 Plus",
                " S60",
                " Vibe X2",
                " A7000 Turbo",
                " Z6 Pro"));

        s[8] = new ArrayList<>(Arrays.asList("Select Model", "Motorola One Power (P30 Note)",
                " E5 Plus",
                " X4",
                " G7",
                " E4 Plus",
                " G7 Power",
                " G6 Plus",
                " E3 Power",
                " G5S Plus",
                "Motorola Moto G 3rd Gen",
                " E5",
                " G6",
                " C Plus",
                "Motorola One",
                " Z2 Force",
                " G4 Plus",
                " Z2 Play",
                " G5 Plus",
                " Z Play",
                " G6 Play",
                " M",
                "Motorola Moto X Play"));

        s[9] = new ArrayList<>(Arrays.asList("Select Model", "  Redmi Note 7",
                " Redmi Note 7S",
                " Redmi 7",
                " Redmi Note 6 Pro",
                " Redmi 6 Pro",
                " Redmi 6A",
                " Redmi Note 5 Pro",
                " Redmi Go",
                " Redmi 6",
                " Redmi Note 4",
                " Redmi Y2",
                " Redmi Note 5",
                " Redmi 5A",
                " Redmi Note 3",
                " Redmi Y1",
                " Redmi Y1 Lite",
                " Redmi 3S Prime",
                " Redmi 3S",
                " Redmi 2",
                " Redmi 1S",
                " Redmi 4A",
                " Redmi 2 Prime",
                " Mi A2 (Mi 6X)",
                " Mi A1",
                " Mi Max 2",
                " Mi A2",
                " Mi4i",
                " Mi3",
                " Mi5",
                " Mi4",
                " Poco f1"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.RadioButton_samsung:
                        assignAdapter(s[0]);
                        break;
                    case R.id.RadioButton_apple:
                        assignAdapter(s[1]);
                        break;
                    case R.id.RadioButton_lenovo:
                        assignAdapter(s[7]);
                        break;
                    case R.id.RadioButton_Moto:
                        assignAdapter(s[8]);
                        break;
                    case R.id.RadioButton_oneplus:
                        assignAdapter(s[2]);
                        break;
                    case R.id.RadioButton_Sony:
                        assignAdapter(s[5]);
                        break;
                    case R.id.RadioButton_vivo:
                        assignAdapter(s[4]);
                        break;
                    case R.id.RadioButton_xiaom:
                        assignAdapter(s[9]);
                        break;
                    case R.id.RadioButton_oppo:
                        assignAdapter(s[3]);
                        break;
                    case R.id.RadioButton_honor:
                        assignAdapter(s[6]);
                        break;
                }
            }
        });

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = radioGroup.getCheckedRadioButtonId();
                Log.d(TAG, "onClick: SelectedId" + selectedId);
                if (selectedId == -1) {
                    Toast.makeText(MobileForm2.this, "Please select any one option.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Model = spinner_model.getSelectedItem().toString();
                if (Model.equals("Select Model")) {
                    Toast.makeText(MobileForm2.this, "Please Select Model.", Toast.LENGTH_SHORT).show();
                    return;
                }


                SelectedButton = findViewById(selectedId);

//                if (SelectedButton.getText().equals("Other")) {
//                   // textView.setVisibility(View.VISIBLE);
//                  //  editText.setVisibility(View.VISIBLE);
//
//                  //  OtherBrand = editText.getText().toString();
//
//                    if (OtherBrand.isEmpty()) {
//                     //   editText.setError("Field's can't be empty!");
//                        return;
//                    }
//                    Intent i = new Intent(MobileForm2.this, MobileForm3.class);
//                    i.putExtra("Brand", OtherBrand + " " + Model);
//                    startActivity(i);
//                } else {
                    String Brand = SelectedButton.getText().toString();
                    Intent i = new Intent(MobileForm2.this, MobileForm3.class);
                    i.putExtra("Brand", Brand + " " + Model);
                    startActivity(i);
                //}
            }
        });

    }

    private void assignAdapter(ArrayList<String> strings) {
        List<String> list = new ArrayList<>(strings);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MobileForm2.this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_model.setAdapter(dataAdapter);
    }
}
