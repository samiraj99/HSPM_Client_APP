package com.sam.hspm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FillProfile extends Fragment {

    private TextView backcolor;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRefs;
    private EditText fname, email, address;
    private TextInputLayout fnamewrap, emailwrap, addwrap;
    private Button save;
    private DatabaseReference databaseReference;
    private View v1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.activity_fill_profile, container, false);

        backcolor = v1.findViewById(R.id.backcolor);

        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        double h1 = height * 0.35;
        backcolor.setHeight((int) h1);

        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        UserRefs = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Profile").child("ProfileInfo");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        fname = v1.findViewById(R.id.fname);
        email = v1.findViewById(R.id.email);
        address = v1.findViewById(R.id.address);
        fnamewrap = v1.findViewById(R.id.fnameWrapper);
        emailwrap = v1.findViewById(R.id.emailWrapper);
        addwrap = v1.findViewById(R.id.addWrapper);
        save = v1.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = fname.getText().toString();
                String emailid = email.getText().toString();
                String homeaddress = address.getText().toString();

                if (TextUtils.isEmpty(fullname) && (fullname.length() > 10)) {
                    fnamewrap.setError("Enter Full Number");
                } else {
                    fnamewrap.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(emailid) && !emailid.matches(Patterns.EMAIL_ADDRESS.toString())) {
                    emailwrap.setError("Enter Valid Email");
                } else {
                    emailwrap.setErrorEnabled(false);
                }

                if (TextUtils.isEmpty(homeaddress)) {
                    addwrap.setError("Enter Account Holders Name");
                } else {
                    addwrap.setErrorEnabled(false);
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("Name", fullname);
                map.put("Email", emailid);
                map.put("Address", homeaddress);
                UserRefs.updateChildren(map);

                databaseReference.child("ProfileIsComplete").setValue("True");

                Fragment fragment = new FragmentHome();

                getFragmentManager().beginTransaction()
                        .replace(R.id.Fragment_Container, fragment).commit();
            }
        });

        return v1;
    }
}
