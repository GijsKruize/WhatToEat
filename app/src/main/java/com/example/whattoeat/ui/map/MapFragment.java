package com.example.whattoeat.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.databinding.FragmentMapBinding;
import com.example.whattoeat.ui.account.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth auth;



    private FragmentMapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMap;
        mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //Check if user is authenticated
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}