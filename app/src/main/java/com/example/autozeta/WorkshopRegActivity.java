package com.example.autozeta;

import android.Manifest;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;

import data.Workshop;

public class WorkshopRegActivity extends AppCompatActivity {

    private Workshop workshop = null;

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    private LatLng SavedLocation = null;
    private String SavedName = null;

    public void SetLoc(LatLng loc){
        SavedLocation = loc;
    }
    public LatLng GetLoc(){
        return SavedLocation;
    }
    public String GetName(){
        return SavedName;
    }
    public void SetName(String Name){
        SavedName = Name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_reg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);


    }

}