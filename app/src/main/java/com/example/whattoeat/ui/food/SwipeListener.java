package com.example.whattoeat.ui.food;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.yalantis.library.KolodaListener;

import java.util.List;

public class SwipeListener implements KolodaListener {
    private Context context;
    private List<String> IDs;
    private List<String> styles;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference personalRef;
    private DatabaseReference generalRef;
    private DatabaseReference swipeRef;
    private String UID;

    private String mood;


    public SwipeListener(Context context, List<String> IDs, List<String> styles, String mood) {
        this.context = context;
        this.IDs = IDs;
        this.styles = styles;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        this.UID = mAuth.getUid();
        this.personalRef = FirebaseDatabase.getInstance().getReference().child("Personal Relation");
        this.swipeRef = FirebaseDatabase.getInstance().getReference().child("Swipe History");
        this.generalRef =FirebaseDatabase.getInstance().getReference().child("General Relation");
        this.mood = mood;
    }

    @Override
    public void onCardDoubleTap(int i) {

    }

    @Override
    public void onCardDrag(int i, @NonNull View view, float v) {

    }

    @Override
    public void onCardLongPress(int i) {
        Toast.makeText(context, "Please swipe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardSingleTap(int i) {
        Toast.makeText(context, "Please swipe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardSwipedLeft(int i) {
        String mID = IDs.get(i + 1);
        String mStyle = styles.get(i + 1);
        personalRef.child(UID).child(mood).child(mID).setValue(false);
        swipeRef.child(UID).child(mood).child(mID).setValue(true);
        generalRef.child(mood).child(mStyle).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long value = currentData.getValue(Long.class);
                if (value == null) {
                    currentData.setValue(-1);
                } else {
                    currentData.setValue(value - 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

    @Override
    public void onCardSwipedRight(int i) {
        String mID = IDs.get(i + 1);
        String mStyle = styles.get(i + 1);
        personalRef.child(UID).child(mood).child(mID).setValue(true);
        swipeRef.child(UID).child(mood).child(mID).setValue(true);
        generalRef.child(mood).child(mStyle).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long value = currentData.getValue(Long.class);
                if (value == null) {
                    currentData.setValue(+1);
                } else {
                    currentData.setValue(value + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

    @Override
    public void onClickLeft(int i) {

    }

    @Override
    public void onClickRight(int i) {

    }

    @Override
    public void onEmptyDeck() {
        Toast.makeText(context, "Sadly there is nothing to swipe", Toast.LENGTH_SHORT).show();
        ;
    }

    @Override
    public void onNewTopCard(int i) {

    }
}
