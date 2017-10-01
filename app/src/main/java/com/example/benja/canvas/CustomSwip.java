package com.example.benja.canvas;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jhosua on 01/10/2017.
 */

public class CustomSwip extends PagerAdapter {
    private int[] imageResources;
    private Context context;
    private LayoutInflater layoutInflater;

    public CustomSwip(Context context, int[]imageResources) {
        this.context = context;
        this.imageResources = imageResources;
    }

    @Override
    public int getCount() {
        return imageResources.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.activity_custom_swip,container,false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.swip_image_view);
        TextView textView = (TextView) itemView.findViewById(R.id.imageCount);
        imageView.setImageResource(imageResources[position]);
        textView.setText((position+1)+"/"+imageResources.length);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return  (view==object);
    }
}
