package com.sam.hspm.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sam.hspm.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    int count;
    private Context context;
    private ArrayList<String> imageURL;


    public SliderAdapter(Context context, ArrayList<String> imageurl) {
        this.context = context;
        this.imageURL=imageurl;
    }

    @Override
    public int getCount() {
        return imageURL.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView imageView = view.findViewById(R.id.imageView);

        Picasso.get().load(imageURL.get(position)).into(imageView);
        Log.d("SliderAdap", "instantiateItem: ImageUrl"+imageURL.get(position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}