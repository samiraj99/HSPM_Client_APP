package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    Button BT_Software;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_home, container, false);


        viewPager = v1.findViewById(R.id.viewPager);
        sliderDotspanel = v1.findViewById(R.id.SliderDots);
        mdDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDataset = new ArrayList<>();
        BT_Software = v1.findViewById(R.id.Button_Software);

        BT_Software.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ServiceForm1.class);
                startActivity(i);
            }
        });


        mdDatabaseReference.child("Advertise").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                            for (int i = 0; i < dotscount; i++) {
                                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                            }

                            dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
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