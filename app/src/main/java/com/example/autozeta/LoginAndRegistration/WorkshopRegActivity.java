package com.example.autozeta.LoginAndRegistration;

import android.Manifest;
import android.os.Bundle;

import com.example.autozeta.R;
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

    private String psw,email,fn,ln;

    public String getFn() {
        return fn;
    }

    public String getLn() {
        return ln;
    }

    public String getPsw() {
        return psw;
    }

    public String getEmail() {
        return email;
    }

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

        email=getIntent().getExtras().get("email").toString();
        psw=getIntent().getExtras().get("password").toString();
        fn=getIntent().getExtras().get("ime").toString();
        ln=getIntent().getExtras().get("prezime").toString();

        setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);


    }

}