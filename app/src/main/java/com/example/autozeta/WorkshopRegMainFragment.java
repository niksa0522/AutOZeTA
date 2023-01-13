package com.example.autozeta;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autozeta.Owner.HomeOwnerActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import data.Service;
import data.WorkDaysAndHours;
import data.Workshop;

public class WorkshopRegMainFragment extends Fragment {



    FirebaseDatabase mFirebaseDB=FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app/");

    String name;
    double latitude,longitude;

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();



    FusedLocationProviderClient client;

    ArrayList<Service> services = new ArrayList<Service>();
    ArrayList<Service> tempServices;


    TextView tvSTMF,tvSTSat,tvSTSun,tvETMF,tvETSat,tvETSun;

    EditText address,Name,WorkPay;

    LatLng loc,tempLatLng;

    Switch MonFri,Sat,Sun;

    int Hour,Minute;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        latitude=0.0;
        longitude=0.0;
        services = new ArrayList<Service>();
        client = LocationServices.getFusedLocationProviderClient(getActivity());


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_warehouse_reg_main, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        tvSTMF = view.findViewById(R.id.tvSTMF);
        tvETMF = view.findViewById(R.id.tvETMF);
        tvSTSat = view.findViewById(R.id.tvSTSat);
        tvETSat = view.findViewById(R.id.tvETSat);
        tvSTSun = view.findViewById(R.id.tvSTSun);
        tvETSun = view.findViewById(R.id.tvETSun);

        Name = view.findViewById(R.id.etName);
        address = view.findViewById(R.id.etAdress);
        WorkPay = view.findViewById(R.id.etWP);

        MonFri = view.findViewById(R.id.swMF);
        Sat = view.findViewById(R.id.swSat);
        Sun = view.findViewById(R.id.swSun);

        /*Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((WorkshopRegActivity)getActivity()).SetName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        tempServices = getArguments().getParcelableArrayList("Service");
        tempLatLng = getArguments().getParcelable("com.google.android.gms.maps.model.LatLng");


        getData();

        SetTimeListener(tvSTMF);
        SetTimeListener(tvETMF);
        SetTimeListener(tvSTSat);
        SetTimeListener(tvETSat);
        SetTimeListener(tvSTSun);
        SetTimeListener(tvETSun);

        view.findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetData();
                Workshop priv = ((WorkshopRegActivity)getActivity()).getWorkshop();
                if(priv.getName().equals("")){
                    Name.setError("Please enter workshop name");
                    Name.requestFocus();
                }
                else if(priv.getLocation()==null){
                    Toast.makeText(getActivity(),"Please choose location",Toast.LENGTH_SHORT).show();
                }
                else if(priv.getWorkPrice()==0.0){
                    WorkPay.setError("Please enter workshop price");
                    WorkPay.requestFocus();
                }
                else if(priv.getServices()==null || priv.getServices().isEmpty()){
                    Toast.makeText(getActivity(),"Please choose services",Toast.LENGTH_SHORT).show();
                }
                else if(!CheckWorkTime(priv.getWorkDays())){
                    Toast.makeText(getActivity(),"Time format incorrect",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("Success","Data OK");
                    Toast.makeText(getActivity(),"Data OK",Toast.LENGTH_SHORT).show();
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(userID==null){
                        Toast.makeText(getActivity(),"Error! User not signed in. Did you complete first registration?",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Workshop toSend = priv;
                        DatabaseReference mDatabase;
                        mDatabase=mFirebaseDB.getReference();
                        mDatabase.child("workshops").child(userID).setValue(toSend);
                        startActivity(new Intent(getActivity(), HomeOwnerActivity.class));
                        getActivity().finish();
                    }
                }
            }
        });


        /*tvSTMF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvSTMF.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
        tvETMF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvETMF.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
        tvSTSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvSTSat.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
        tvETSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvETSat.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
        tvSTSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvSTSun.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
        tvETSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tvETSun.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });*/






        view.findViewById(R.id.btnAdress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        /*view.findViewById(R.id.btnAddService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter Service");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serviceStr = input.getText().toString();
                        Service newService = new Service(serviceStr);
                        services.add(newService);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });*/
        view.findViewById(R.id.btnAddService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetData();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Service",services);
                NavHostFragment.findNavController(WorkshopRegMainFragment.this)
                        .navigate(R.id.action_WarehouseRegMainFragment_to_servicesFragment,bundle);
            }
        });


    }

    private boolean CheckWorkTime(ArrayList<WorkDaysAndHours> wd){
        WorkDaysAndHours monFri,Sat,Sun;
        monFri = wd.get(0);
        Sat= wd.get(1);
        Sun=wd.get(2);
        if(!monFri.isOpen() && !Sat.isOpen() && !Sun.isOpen()){
            return false;
        }
        if(monFri.isOpen()){
            if(monFri.getStartTime().equals("Choose Time") || monFri.getEndTime().equals("Choose Time")){

                Log.d("Return","MonFri Choose Time");
                return false;
            }

            if(monFri.getStartTime().toString().compareTo(monFri.getEndTime().toString())>=0){

                Log.d("Return","MonFri StartTime higher than end time");
                return false;
            }
        }
        if(Sat.isOpen()){
            if(Sat.getStartTime().equals("Choose Time") || Sat.getEndTime().equals("Choose Time")){

                Log.d("Return","Sat Choose Time");
                return false;
            }

            if(Sat.getStartTime().toString().compareTo(Sat.getEndTime().toString())>=0){
                Log.d("Return","Sat StartTime higher than end time");
                return false;
            }
        }
        if(Sun.isOpen()){
            if(Sun.getStartTime().equals("Choose Time") || Sun.getEndTime().equals("Choose Time")){
                Log.d("Return","Sun Choose Time");
                return false;
            }

            if(Sun.getStartTime().toString().compareTo(Sun.getEndTime().toString())>=0){
                Log.d("Return","Sun StartTime higher than end time");
                return false;
            }

        }
        return true;
    }


    private void SetTimeListener(TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour=hourOfDay;
                                Minute=minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,Hour,Minute);
                                tv.setText(DateFormat.format("HH:mm",calendar));
                            }
                        },12,0,true
                );
                timePickerDialog.updateTime(Hour,Minute);
                timePickerDialog.show();
            }
        });
    }

    private void SetName() {
        if(name!=null){
            ((WorkshopRegActivity)getActivity()).SetName(name);
        }
        name=((WorkshopRegActivity)getActivity()).GetName();
        if(name!=null){
            Name.setText(name);
        }

    }

    private void SetData(){
        Workshop priv = new Workshop();
        double wp=0;
        String name = "";
        if(!Name.getText().toString().equals("Name")){
            name = Name.getText().toString();
        }
        if(tempLatLng!=null)
            loc = tempLatLng;
        if(!WorkPay.getText().toString().equals("Number")) {
            wp = Double.parseDouble(WorkPay.getText().toString());
        }
        WorkDaysAndHours MF = new WorkDaysAndHours("MonToFri",tvSTMF.getText().toString(),tvETMF.getText().toString(),MonFri.isChecked());
        WorkDaysAndHours Saturday = new WorkDaysAndHours("Saturday",tvSTSat.getText().toString(),tvETSat.getText().toString(),Sat.isChecked());
        WorkDaysAndHours Sunday = new WorkDaysAndHours("Sunday",tvSTSun.getText().toString(),tvETSun.getText().toString(),Sun.isChecked());
        if(tempServices!=null){
            services = tempServices;
        }
        MF.SetTimeIfClosed();
        Saturday.SetTimeIfClosed();
        Sunday.SetTimeIfClosed();
        priv.setName(name);
        priv.setLocation(loc);
        priv.setWorkPrice(wp);
        priv.AddWorkDay(MF);
        priv.AddWorkDay(Saturday);
        priv.AddWorkDay(Sunday);
        priv.AddServices(services);
        ((WorkshopRegActivity)getActivity()).setWorkshop(priv);
    }
    private void getData(){
        Workshop priv = ((WorkshopRegActivity)getActivity()).getWorkshop();
        if(priv != null){
            Name.setText(priv.getName());
            if(priv.getLocation()!=null || tempLatLng!=null) {
                address.setText("Location Saved");
                if(priv.getLocation()!=null)
                    loc=priv.getLocation();
                else
                    loc=tempLatLng;
            }
            WorkPay.setText(String.valueOf(priv.getWorkPrice()));
            ArrayList<WorkDaysAndHours> wd = priv.getWorkDays();
            tvSTMF.setText(wd.get(0).getStartTime());
            tvETMF.setText(wd.get(0).getEndTime());
            tvSTSat.setText(wd.get(1).getStartTime());
            tvETSat.setText(wd.get(1).getEndTime());
            tvSTSun.setText(wd.get(2).getStartTime());
            tvETSun.setText(wd.get(2).getEndTime());
            MonFri.setChecked(wd.get(0).isOpen());
            Sat.setChecked(wd.get(1).isOpen());
            Sun.setChecked(wd.get(2).isOpen());
            if(tempServices!=null)
                services = tempServices;
            else
                services = priv.getServices();
        }
    }


    private void SetLocation(){
        loc = getArguments().getParcelable("com.google.android.gms.maps.model.LatLng");
        if(loc!=null){
            ((WorkshopRegActivity)getActivity()).SetLoc(loc);
        }
        loc = ((WorkshopRegActivity)getActivity()).GetLoc();
        if(loc!=null) {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(loc.latitude,loc.longitude,5);
                address.setText( addresses.get(0).getAddressLine(0));
            }
            catch (IOException ex){
                address.setText("Location Saved");
            }
        }
    }



    private void getLocation(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(loc!=null){
                SetData();
                latitude=loc.latitude;
                longitude=loc.longitude;
                Bundle bundle = new Bundle();
                bundle.putDouble("lat",latitude);
                bundle.putDouble("long",longitude);
                NavHostFragment.findNavController(WorkshopRegMainFragment.this)
                        .navigate(R.id.action_WarehouseRegMainFragment_to_mapsWarehouseChooserFragment,bundle);
            }
            else {
                getCurrentLocation();
            }
        }
        else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100 && (grantResults.length>0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        }
        else{
            Toast.makeText(getActivity(),"Permision denied",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        SetData();
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat",latitude);
                        bundle.putDouble("long",longitude);
                        NavHostFragment.findNavController(WorkshopRegMainFragment.this)
                                .navigate(R.id.action_WarehouseRegMainFragment_to_mapsWarehouseChooserFragment,bundle);
                    }
                    else{

                        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(100)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                SetData();
                                Location location1 = locationResult.getLastLocation();
                                latitude=location1.getLatitude();
                                longitude=location1.getLongitude();
                                Bundle bundle = new Bundle();
                                bundle.putDouble("lat",latitude);
                                bundle.putDouble("long",longitude);
                                NavHostFragment.findNavController(WorkshopRegMainFragment.this)
                                        .navigate(R.id.action_WarehouseRegMainFragment_to_mapsWarehouseChooserFragment,bundle);
                            }
                        };
                        client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }
        else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}