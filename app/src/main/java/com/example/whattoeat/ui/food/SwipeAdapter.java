package com.example.whattoeat.ui.food;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whattoeat.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> implements Adapter {
    private Context context;
    private List<String> IDs;
    private List<String> name;
    private List<String> img;

    /**
     * Generator for the class SwipeAdapter, initializes all global varaiables
     *
     * @param context the current context
     * @param IDs a list of strings
     * @param name a list of strings
     * @param img a list of strings
     * @param styles a list of strings
     */
    public SwipeAdapter(Context context, List<String> IDs, List<String> name, List<String> img, List<String> styles) {
        this.context = context;
        // Randomize the inputted Lists with the same seed, so they stay coherent
        long seed = System.nanoTime();
        Collections.shuffle(img, new Random(seed));
        Collections.shuffle(name, new Random(seed));
        Collections.shuffle(IDs, new Random(seed));
        Collections.shuffle(styles, new Random(seed));
        // Set given variables
        this.img = img;
        this.name = name;
        this.IDs = IDs;
    }

    @NonNull
    @Override
    public SwipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_koloda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeAdapter.ViewHolder holder, int position) {
        // No functionality added
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    // No functionality added
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        // No functionality added
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int i) {
        return IDs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // Give function to the getView function of the adapter
    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            // If there is no view, generate a new item_koloda
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_koloda, parent, false);
            holder = new ViewHolder(view); // Generate new holder
            view.setTag(holder); // Bind ViewHolder to view
        } else {
            return null;
        }
        Picasso.with(context).load(img.get(i)).into(holder.getImageView()); // Load the i'th image into the image view
        holder.getTextView().setText(name.get(i)); // Set the recipe name in the textbox
        return view;
    }


    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imageView;
        private final TextView textView;

        // Generator for class ViewHolder
        public ViewHolder(View view) {
            super(view);
            // Get the image and TextView from the current view.
            imageView = (ShapeableImageView) view.findViewById(R.id.mealImage);
            textView = (TextView) view.findViewById(R.id.mealText);
        }

        // Method that returns textView
        public TextView getTextView() {
            return textView;
        }

        // Method that returns imageView
        public ShapeableImageView getImageView() {
            return imageView;
        }
    }
}
