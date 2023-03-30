package com.example.whattoeat.ui.map;

import android.util.Log;

public class ImagesNotLoaded extends Exception {
    /**
     * This Exception can be callen when images don't load.
     * @param location of the error
     */
    public ImagesNotLoaded(String location) {
        super(location);
        Log.e("Image Not Loaded Exception!", location);
    }
}
