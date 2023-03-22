package com.example.whattoeat.ui.map;

import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.databinding.FragmentMapBinding;
import com.example.whattoeat.ui.account.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth auth;



    private FragmentMapBinding binding;
    MyLocationNewOverlay locationOverlay;

    MapView map = null;
    Button button;
    LocationManager locationManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        button = binding.currentLocationButton;
        map = binding.mapView;

        map.setUseDataConnection(true);
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getContext()), map);
        locationOverlay.enableMyLocation();
        map.getOverlays().add(locationOverlay);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        IMapController mapController = map.getController();
        mapController.setZoom((long) 15);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(51.442164898, 5.487164718);
        mapController.animateTo(startPoint);

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          mapController.animateTo(locationOverlay.getMyLocation());
                                      }
                                  });


        //final TextView textView = binding.textMap;
       // mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}