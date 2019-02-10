package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

     private RecyclerView mRecyclerView;
     private RecyclerView.LayoutManager mLayoutManager;
     private RecyclerView.Adapter mAdapter;
     private ArrayList<String> mDataset;
     View v1;
     Button  BT_Software;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_home,container,false);

        mDataset = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            mDataset.add("New title "+i);
        }

        mRecyclerView = v1.findViewById(R.id.Recycler_View);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);
        BT_Software = v1.findViewById(R.id.Button_Software);

        BT_Software.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ServiceForm1.class);
                startActivity(i);
            }
        });

        return v1;
    }
}
