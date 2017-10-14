package com.clavicusoft.wumpus.Select;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clavicusoft.wumpus.R;


public class CustomSwip extends PagerAdapter {
    private int[] imageResources;
    private Context context;
    private LayoutInflater layoutInflater;
    /**
     * Creates a CustomSwip with a given context and a given list of Images.
     * @param context to use to open or create the CustomSwip.
     * @param imageResources Images that the CustomSwip will contain.
     */
    public CustomSwip(Context context, int[]imageResources) {
        this.context = context;
        this.imageResources = imageResources;
    }

    /**
     * Yields the number of images that contains the CustomSwip
     * @return Number of images in the list.
     */
    @Override
    public int getCount() {
        return imageResources.length;
    }

    /**
     * This method instantiate the CustomSwip, and loads the respective images to it.
     * @param container This will contains the elements of the CustomSwip.
     * @param position The position of the actual element showing.
     * @return The View of the CustomSwip
     */
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

    /**
     * Destroys the a specific item in a given position.
     * @param container The container that have the element.
     * @param position The position of the element.
     * @param object The object that have the element.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    /**
     *  Yields a true of false in function of a given view and a object.
     * @param view A view to compare.
     * @param object An object to compare.
     * @return true if view and object are the same, otherwise yields false.
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {

        return  (view==object);
    }

}
