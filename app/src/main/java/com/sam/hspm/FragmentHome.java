package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sam.hspm.utils.SliderAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHome extends Fragment {

    ViewPager viewPager;
    private int dotscount;
    private ImageView[] dots;
    LinearLayout sliderDotspanel;
    private ArrayList<String> mDataset;
    int i;
    DatabaseReference mdDatabaseReference;
    private ArrayList<String> imageURL = new ArrayList<>();
    int count;
    View v1;
    CardView BT_Software, Rent, Assemble;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;
    private static final String TAG = "FragmentHome";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_home, container, false);


        viewPager = v1.findViewById(R.id.viewPager);
        sliderDotspanel = v1.findViewById(R.id.SliderDots);
        BT_Software = v1.findViewById(R.id.services);
        Rent = v1.findViewById(R.id.rent);
        Assemble = v1.findViewById(R.id.assemble);

        mdDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDataset = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        BT_Software.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ServiceForm1.class);
                startActivity(i);
            }
        });

        Rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        Assemble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Comming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        mdDatabaseReference.child("Advertise").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    try {
                        count = (int) dataSnapshot.getChildrenCount();
                        for (int i = 1; i <= count; i++) {

                            String s = dataSnapshot.child(String.valueOf(i)).child("Image").getValue().toString();
                            imageURL.add(s);
                        }

                        SliderAdapter viewPagerAdapter = new SliderAdapter(getContext(), imageURL);

                        viewPager.setAdapter(viewPagerAdapter);

                        dotscount = count;
                        dots = new ImageView[dotscount];

                        for (i = 0; i < dotscount; i++) {

                            dots[i] = new ImageView(getContext());
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(8, 0, 8, 0);

                            sliderDotspanel.addView(dots[i], params);

                        }

                        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);

                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {

                                if (getContext() != null) {
                                    for (int i = 0; i < dotscount; i++) {
                                        dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                                    }

                                    dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v1;
    }


    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < dots.length - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }

                });
            }
        }
    }
}