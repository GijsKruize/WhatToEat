package com.example.whattoeat.ui.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder; //convert from address to long/latt
import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.whattoeat.MainActivity;
import com.example.whattoeat.R;
import com.example.whattoeat.databinding.FragmentMapBinding;
import com.example.whattoeat.ui.account.Login;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.List;

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
        IMapController mapController = map.getController();

        map.setUseDataConnection(true);
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getContext()), map);
        locationOverlay.enableMyLocation();
        map.setTilesScaledToDpi(true);
        map.getOverlays().add(locationOverlay);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel((double)20);
        map.setMinZoomLevel((double)12);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
        map.setScrollableAreaLimitLongitude(MapView.getTileSystem().getMinLongitude(), MapView.getTileSystem().getMaxLongitude(), 0);
        mapController.setZoom((long) 15);
        map.setMultiTouchControls(true);

        Marker startMarker2 = new Marker(map);
        LatLng house = getLocationFromAddress(this.getContext(), "White House");
        GeoPoint startPoint3 = new GeoPoint(house.latitude, house.longitude);
        startMarker2.setPosition(startPoint3);
        startMarker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker2.setIcon(this.getResources().getDrawable(R.drawable.logo));
        map.getOverlays().add(startMarker2);

        mapController.animateTo(startPoint3);

        Marker startMarker = new Marker(map);
        GeoPoint startPoint2 = new GeoPoint(51.43616,5.42219);
        startMarker.setPosition(startPoint2);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(this.getResources().getDrawable(R.drawable.logo));
        map.getOverlays().add(startMarker);

        GeoPoint startPoint = new GeoPoint(51.442164898, 5.487164718);
        //mapController.animateTo(startPoint);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapController.animateTo(locationOverlay.getMyLocation());
            }
        });


        //final TextView textView = binding.textMap;
        // mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //Check if user is authenticated
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
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


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}

//check if the address is the correct address such as captical
