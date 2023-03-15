package com.example.whattoeat.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.whattoeat.databinding.FragmentFoodBinding;
import com.example.whattoeat.databinding.FragmentFoodBinding;

public class FoodFragment extends Fragment {

    private FragmentFoodBinding binding;
//    public View onCreateView(@NonNull LayoutInflater inflater,)
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}