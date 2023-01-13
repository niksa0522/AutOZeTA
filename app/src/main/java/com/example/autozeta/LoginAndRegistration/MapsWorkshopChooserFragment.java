package com.example.autozeta.LoginAndRegistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.autozeta.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsWorkshopChooserFragment extends Fragment {

    Double latitude,longitude;
    LatLng passablePos;
    FusedLocationProviderClient client;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng location = new LatLng(latitude, longitude);
            passablePos = location;
            googleMap.addMarker(new MarkerOptions().position(location).draggable(true).title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    passablePos = marker.getPosition();
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        latitude = getArguments().getDouble("lat");
        longitude = getArguments().getDouble("long");



        return inflater.inflate(R.layout.fragment_maps_warehouse_chooser, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("com.google.android.gms.maps.model.LatLng",passablePos);
                NavHostFragment.findNavController(MapsWorkshopChooserFragment.this)
                        .navigate(R.id.action_mapsWarehouseChooserFragment_to_WarehouseRegMainFragment,bundle);
            }
        });

    }




}