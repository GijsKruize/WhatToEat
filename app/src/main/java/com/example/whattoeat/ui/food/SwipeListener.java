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

    /**
     * Generator for the SwipeListener, initializes all global variables
     *
     * @param context current context
     * @param IDs list of strings
     * @param mood list of strings
     * @param styles string
     */
    public SwipeListener(Context context, List<String> IDs, List<String> styles, String mood) {
        // Define global variables with parameters
        this.context = context;
        this.IDs = IDs;
        this.styles = styles;
        this.mood = mood;
        // Get instances of Firebase's Database and Authentication
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // Get authentication's user ID
        this.UID = mAuth.getUid();
        // Set references for the 3 used tables in the database
        this.personalRef = FirebaseDatabase.getInstance().getReference().child("Personal Relation");
        this.swipeRef = FirebaseDatabase.getInstance().getReference().child("Swipe History");
        this.generalRef = FirebaseDatabase.getInstance().getReference().child("General Relation");

    }

    @Override
    public void onCardDoubleTap(int i) {
        // No functionality added
    }

    @Override
    public void onCardDrag(int i, @NonNull View view, float v) {
        // No functionality added
    }

    @Override
    public void onCardLongPress(int i) {
        // If the user presses instead of swipes, display toast message
        Toast.makeText(context, "Please swipe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardSingleTap(int i) {
        // If the user taps instead of swipes, display toast message
        Toast.makeText(context, "Please swipe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardSwipedLeft(int i) {
        // Set the ID and style
        String mID = IDs.get(i + 1);
        String mStyle = styles.get(i + 1);
        // Update the personal database and set recommendation for this mood false
        personalRef.child(UID).child(mood).child(mID).setValue(false);
        // Update the swipehistory database to indicate user has swiped this card
        swipeRef.child(UID).child(mood).child(mID).setValue(true);

        // Create transaction handler to handle decrementing the value in general reference
        generalRef.child(mood).child(mStyle).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                // Temporarily save the value of the rating
                Long value = currentData.getValue(Long.class);
                if (value == null) {
                    // If current rating is 0, set it to -1
                    currentData.setValue(-1);
                } else {
                    // Else decrement the current value
                    currentData.setValue(value - 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // do nothing
            }
        });
    }

    @Override
    public void onCardSwipedRight(int i) {
        // Set the ID and style
        String mID = IDs.get(i + 1);
        String mStyle = styles.get(i + 1);
        // Update the personal database and set recommendation for this mood true
        personalRef.child(UID).child(mood).child(mID).setValue(true);
        // Update the swipehistory database to indicate user has swiped this card
        swipeRef.child(UID).child(mood).child(mID).setValue(true);

        // Create transaction handler to handle decrementing the value in general reference
        generalRef.child(mood).child(mStyle).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                // Temporarily save the value of the rating
                Long value = currentData.getValue(Long.class);
                if (value == null) {
                    // If current rating is 0, set it to 1
                    currentData.setValue(+1);
                } else {
                    // Else increment the current value
                    currentData.setValue(value + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Do nothing
            }
        });
    }

    @Override
    public void onClickLeft(int i) {
        // No functionality added
    }

    @Override
    public void onClickRight(int i) {
        // No functionality added
    }

    @Override
    public void onEmptyDeck() {
        // If all cards are swiped, display toast message
        Toast.makeText(context, "That was all to swipe for now!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNewTopCard(int i) {
        // No functionality added
    }
}
