package com.example.whattoeat.ui.food;

import android.content.Context;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whattoeat.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> implements Adapter {
    private Context context;
    private List<Integer> img;

    private List<String> name;


    public SwipeAdapter(Context context, List<Integer> img, List<String> name) {
        this.context = context;
        this.img = img;
        this.name = name;
    }

    @NonNull
    @Override
    public SwipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_koloda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(name.get(position));
        holder.getImageView().setImageResource(img.get(position));
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_koloda, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            return null;
        }

        holder.getImageView().setImageResource(img.get(i));
        holder.getTextView().setText(name.get(i));

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
        return img.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView imageView;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            imageView = (ShapeableImageView) view.findViewById(R.id.mealImage);
            textView = (TextView) view.findViewById(R.id.mealText);
        }

        public TextView getTextView() {
            return textView;
        }

        public ShapeableImageView getImageView() {
            return imageView;
        }
    }
}

