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

import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> implements Adapter {
    private Context context;
    private List<String> IDs;
    private List<String> name;
    private List<String> img;




    public SwipeAdapter(Context context, List<String> IDs, List<String> name, List<String> img ) {
        this.context = context;
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
        holder.getTextView().setText(name.get(position));
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/what-to-eat-tue.appspot.com/o/the-best-caesar-salad-recipe-06-40e70f549ba2489db09355abd62f79a9.jpg?alt=media&token=220ba356-b1d6-4a21-ad0e-f305c19b46c2").into(holder.getImageView());
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

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
        Picasso.with(context).load(img.get(i)).into(holder.getImageView());
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
        return name.size();
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
