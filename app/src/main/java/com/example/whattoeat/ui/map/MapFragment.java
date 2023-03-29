package com.example.whattoeat.ui.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder; //convert from address to long/latt
import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.whattoeat.ui.home.homepage;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth auth;

    private List<String> markerNames = new ArrayList<>();
    private List<String> markerImages = new ArrayList<>();
    private List<String> markerIds = new ArrayList<>();
    private List<Double> markerLongs = new ArrayList<>();
    private List<Double> markerLats = new ArrayList<>();
    private List<String> markerPhones = new ArrayList<>();
    private List<String>  markerStyles = new ArrayList<>();

    private List<String>  markerSites = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();


    private FragmentMapBinding binding;
    MyLocationNewOverlay locationOverlay;

    MapView map = null;

    FirebaseDatabase database;

    protected DatabaseReference myRef;
    IMapController mapController = null;
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
        mapController = map.getController();

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

        fetchData();

        GeoPoint startPoint = new GeoPoint(51.442164898, 5.487164718);
        mapController.animateTo(startPoint);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapController.animateTo(locationOverlay.getMyLocation());
            }
        });


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


    public void setupMarkers(int i) {

        map = binding.mapView;
        mapController = map.getController();
        Log.d("Size", "Size: " + markerLats.size());

        Marker restaurantMarker = new Marker(map);
        GeoPoint restaurantLocation = new GeoPoint(markerLats.get(i), markerLongs.get(i));
        restaurantMarker.setPosition(restaurantLocation);
        restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        restaurantMarker.setIcon(this.getResources().getDrawable(R.drawable.logo));
        //restaurantMarker.setImage(this.getResources().getDrawable(R.drawable.logo));
        restaurantMarker.setTitle(markerNames.get(i));
        restaurantMarker.setSubDescription("Phone: " + markerPhones.get(i) + "<br>Style: " + markerStyles.get(i) + "<br>Website: " + markerSites.get(i));
        map.getOverlays().add(restaurantMarker);
        
    }
    private void fetchData(){

        //progressBar.setVisibility(View.VISIBLE);
        //Connect to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Restaurant");

        Log.d("Homepage: ", "Loading data........");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markerIds.clear();
                markerNames.clear();
                markerImages.clear();
                for(DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    // retrieve data for each recipe
                    String markerId = restaurantSnapshot.getKey();
                    String markerName = restaurantSnapshot.child("Name").getValue(String.class);
                    String markerImage = restaurantSnapshot.child("Image").getValue(String.class);
                    Double markerLong = restaurantSnapshot.child("Longitude").getValue(Double.class);
                    Double markerLat = restaurantSnapshot.child("Latitude").getValue(Double.class);
                    String markerPhone = restaurantSnapshot.child("Phone").getValue(String.class);
                    String markerStyle = restaurantSnapshot.child("Style").getValue(String.class);
                    String markerSite = restaurantSnapshot.child("Hyperlink").getValue(String.class);

                    markerIds.add(markerId);
                    markerNames.add(markerName);
                    markerImages.add(markerImage);
                    markerLongs.add(markerLong);
                    markerLats.add(markerLat);
                    markerPhones.add(markerPhone);
                    markerStyles.add(markerStyle);
                    markerSites.add(markerSite);
                    Log.d("Firebase", "Recipe Name: " + markerName +
                            ", Image source: " + markerImage + markerLong + markerLat);
                }
                int i = 0;
                for(String name : markerNames) {
                    setupMarkers(i);
                    i++;
                }
                //progressBar.setVisibility(View.GONE); // Hide the progress bar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Homepage could not fetch data from recipes: " + databaseError.getMessage());
            }
        });
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
