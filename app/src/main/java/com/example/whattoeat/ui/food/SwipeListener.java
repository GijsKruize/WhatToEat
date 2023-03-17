package com.example.whattoeat.ui.food;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yalantis.library.KolodaListener;

public class SwipeListener implements KolodaListener {
    private Context context;

    public SwipeListener(Context context){
        this.context = context;
    }
    @Override
    public void onCardDoubleTap(int i) {

    }

    @Override
    public void onCardDrag(int i, @NonNull View view, float v) {

    }

    @Override
    public void onCardLongPress(int i) {

    }

    @Override
    public void onCardSingleTap(int i) {
    }

    @Override
    public void onCardSwipedLeft(int i) {

    }

    @Override
    public void onCardSwipedRight(int i) {

    }

    @Override
    public void onClickLeft(int i) {

    }

    @Override
    public void onClickRight(int i) {

    }

    @Override
    public void onEmptyDeck() {
        Toast.makeText(context, "That was all for today!", Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void onNewTopCard(int i) {

    }
}
