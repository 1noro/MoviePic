package net.a3do.app.moviepic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class ViewPagerAdapter extends PagerAdapter {

    Context context; // Context object
    Bitmap[] images; // Array of images
    LayoutInflater mLayoutInflater; // Layout Inflater

    // Viewpager Constructor
    public ViewPagerAdapter(Context context, Bitmap[] images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item, container, false);
        // referencing the image view from the item.xml file
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);
        // setting the image in the imageView
        imageView.setImageBitmap(images[position]);
        imageView.setContentDescription(context.getString(R.string.levelFrameDescription) + position);
        // Adding the View
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
