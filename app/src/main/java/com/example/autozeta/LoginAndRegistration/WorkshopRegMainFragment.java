package com.example.autozeta.LoginAndRegistration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autozeta.Basic.HomeBasicActivity;
import com.example.autozeta.Owner.HomeOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import data.Service;
import data.Termin;
import data.User;
import data.WorkDaysAndHours;
import data.Workshop;

public class WorkshopRegMainFragment extends Fragment {



    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    String name;
    double latitude,longitude;

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    List<String> selectedServices = new ArrayList<>();
    List<String> services = new ArrayList<>();


    FusedLocationProviderClient client;

    FirebaseFirestore db= FirebaseFirestore.getInstance();



    String strSTMF,strSTSat,strSTSun,strETMF,strETSat,strETSun;

    EditText telefon,Name,WorkPay;

    Button btnServices,btnMonFri,btnSat,btnSun,btnAddress;

    LatLng loc,tempLatLng;

    CheckBox MonFri,Sat,Sun;

    int Hour,Minute;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        latitude=0.0;
        longitude=0.0;
        client = LocationServices.getFusedLocationProviderClient(getActivity());


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_warehouse_reg_main, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        services=new ArrayList<>();
        btnServices = view.findViewById(R.id.btnAddService);
        btnMonFri = view.findViewById(R.id.btnMonFri);
        btnSat=view.findViewById(R.id.btnSat);
        btnSun =view.findViewById(R.id.btnSun);

        Name = view.findViewById(R.id.etName);
        telefon = view.findViewById(R.id.etTelefon);
        WorkPay = view.findViewById(R.id.etWP);

        MonFri = view.findViewById(R.id.swMF);
        Sat = view.findViewById(R.id.swSat);
        Sun = view.findViewById(R.id.swSun);
        btnAddress=view.findViewById(R.id.btnAdress);


        MonFri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnMonFri.setEnabled(true);
                }
                else{
                    btnMonFri.setEnabled(false);
                }
            }
        });
        Sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnSat.setEnabled(true);
                }
                else{
                    btnSat.setEnabled(false);
                }
            }
        });
        Sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnSun.setEnabled(true);
                }
                else{
                    btnSun.setEnabled(false);
                }
            }
        });

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

        DatabaseReference refServices = mDatabase.getReference().child("Services");

        refServices.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    Service service = child.getValue(Service.class);
                    services.add(service.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SetServicesButton();



        tempLatLng = getArguments().getParcelable("com.google.android.gms.maps.model.LatLng");


        getData();

        SetTimeListenerMonFri(btnMonFri);
        SetTimeListenerMonFri(btnSat);
        SetTimeListenerMonFri(btnSun);


        view.findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetData();
                Workshop priv = ((WorkshopRegActivity)getActivity()).getWorkshop();
                if(priv.getName().isEmpty() || priv.getName().equals("")){
                    Toast.makeText(getActivity(),"Unesite Naziv Radionice",Toast.LENGTH_SHORT).show();
                    Name.requestFocus();
                }
                else if(priv.getLocation()==null){
                    Toast.makeText(getActivity(),"Izaberite Lokaciju",Toast.LENGTH_SHORT).show();
                }
                else if(priv.getWorkPrice()==0.0){
                    Toast.makeText(getActivity(),"Unesite Cenu Rada",Toast.LENGTH_SHORT).show();
                    WorkPay.requestFocus();
                }
                else if(priv.getServices()==null || priv.getServices().isEmpty()){
                    Toast.makeText(getActivity(),"Izaberite Servise",Toast.LENGTH_SHORT).show();
                }
                else if(!CheckWorkTime(priv.getWorkDays())){
                    Toast.makeText(getActivity(),"Uneto Vreme Rada Lose",Toast.LENGTH_SHORT).show();
                }
                else if(priv.getPhoneNum().isEmpty()||priv.getPhoneNum().equals("")){
                    Toast.makeText(getActivity(),"Unesite Telefon",Toast.LENGTH_SHORT).show();
                    telefon.requestFocus();
                }
                else{
                    Log.d("Success","Data OK");
                        Workshop toSend = priv;
                        WorkshopRegActivity activity = (WorkshopRegActivity)getActivity();
                        String fN=activity.getFn();
                        String lN=activity.getLn();
                        String email=activity.getEmail();
                        String psw=activity.getPsw();
                        if(email.isEmpty()||psw.isEmpty()||fN.isEmpty()||lN.isEmpty()){
                            Toast.makeText(getActivity(),"Problem! Ugasite aplikaciju u pokusajte registraciju ispocetka",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mFirebaseAuth.createUserWithEmailAndPassword(email, psw).
                                    addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Nije moguce napraviti nalog.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                User user = new User(fN, lN, "Vlasnik");
                                                mDatabase.getReference().child("users").child(userID).setValue(user);
                                                db.collection("workshops").document(userID).set(toSend);
                                                mDatabase.getReference().child("users").child(userID).child("imeRadionce").setValue(toSend.getName());
                                                Intent i = new Intent(getActivity(),HomeOwnerActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                getActivity().finish();
                                            }
                                        }
                                    });

                        }
                    }
                }

        });

        btnAddress.findViewById(R.id.btnAdress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetData();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                String[] listservices = new String[services.size()];
                services.toArray(listservices);
                boolean[] checkedServices = new boolean[services.size()];
                int index=0;
                for(String s : services){
                    if(selectedServices.contains(s))
                        checkedServices[index]=true;
                    else
                        checkedServices[index]=false;
                    index++;
                }

                builder.setTitle("Izaberi servise").setMultiChoiceItems(listservices, checkedServices, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            selectedServices.add(services.get(which));
                        }
                        else if(selectedServices.contains(services.get(which))){
                            selectedServices.remove(services.get(which));
                        }

                    }
                });


                builder.setPositiveButton("Izaberi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SetServicesButton();
                    }
                });
                builder.show();
            }
        });


    }

    private void SetServicesButton() {
        String services = "";
        if(selectedServices.size()==0)
            services="Izaberi servise";
        else {
            for (String s : selectedServices)
                services += s + ", ";
            services=services.substring(0,services.length()-2);
        }
        btnServices.setText(services);
    }

    private boolean CheckWorkTime(List<WorkDaysAndHours> wd){
        WorkDaysAndHours monFri,Sat,Sun;
        monFri = wd.get(0);
        Sat= wd.get(1);
        Sun=wd.get(2);
        if(!monFri.isOpen() && !Sat.isOpen() && !Sun.isOpen()){
            return false;
        }
        if(monFri.isOpen()){
            if(monFri.getStartTime()==null|| monFri.getEndTime()==null){

                Log.d("Return","MonFri Choose Time");
                return false;
            }

            if(monFri.getStartTime().toString().compareTo(monFri.getEndTime().toString())>=0){

                Log.d("Return","MonFri StartTime higher than end time");
                return false;
            }
        }
        if(Sat.isOpen()){
            if(Sat.getStartTime()==null || Sat.getEndTime()==null){

                Log.d("Return","Sat Choose Time");
                return false;
            }

            if(Sat.getStartTime().toString().compareTo(Sat.getEndTime().toString())>=0){
                Log.d("Return","Sat StartTime higher than end time");
                return false;
            }
        }
        if(Sun.isOpen()){
            if(Sun.getStartTime()==null || Sun.getEndTime()==null){
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


    private void SetTimeListenerMonFri(Button btn){

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String ST;
                                Hour = hourOfDay;
                                Minute = minute;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, Hour, Minute);
                                ST = String.valueOf(DateFormat.format("HH:mm", calendar));
                                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String ET;
                                        Hour = hourOfDay;
                                        Minute = minute;
                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar.set(0, 0, 0, Hour, Minute);
                                        ET = String.valueOf(DateFormat.format("HH:mm", calendar));
                                        btn.setText(ST+"-"+ET);
                                    }
                                }, Hour, Minute, true).show();
                            }},12,0,true);
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
        String phone ="";
        if(!Name.getText().toString().isEmpty()){
            name = Name.getText().toString();
        }
        if(tempLatLng!=null)
            loc = tempLatLng;
        if(!WorkPay.getText().toString().isEmpty()) {
            wp = Double.parseDouble(WorkPay.getText().toString());
        }
        if(!telefon.getText().toString().isEmpty()){
            phone = telefon.getText().toString();
        }
        if(MonFri.isChecked()&& !btnMonFri.getText().toString().equals("Unesi vreme rada")) {
            String[] monFri = btnMonFri.getText().toString().split("-");
            strSTMF=monFri[0];
            strETMF=monFri[1];
        }
        if(Sat.isChecked() && !btnSat.getText().toString().equals("Unesi vreme rada")) {
            String[] sun = btnSat.getText().toString().split("-");
            strSTSat=sun[0];
            strETSat=sun[1];
        }
        if(Sun.isChecked() && !btnSun.getText().toString().equals("Unesi vreme rada")) {
            String[] sat = btnSun.getText().toString().split("-");
            strSTSun=sat[0];
            strETSun=sat[1];
        }
        else{

        }
        WorkDaysAndHours MF = new WorkDaysAndHours("Ponedeljak-Petak",strSTMF,strETMF,MonFri.isChecked());
        WorkDaysAndHours Saturday = new WorkDaysAndHours("Subota",strSTSat,strETSat,Sat.isChecked());
        WorkDaysAndHours Sunday = new WorkDaysAndHours("Nedelja",strSTSun,strETSun,Sun.isChecked());
        MF.SetTimeIfClosed();
        Saturday.SetTimeIfClosed();
        Sunday.SetTimeIfClosed();
        priv.setName(name);
        if(loc!=null) {
            data.LatLng dataLoc = new data.LatLng(loc.latitude, loc.longitude);
            priv.setLocation(dataLoc);
        }
        priv.setWorkPrice(wp);
        priv.AddWorkDay(MF);
        priv.AddWorkDay(Saturday);
        priv.AddWorkDay(Sunday);
        priv.setPhoneNum(phone);
        priv.AddServices(selectedServices);
        ((WorkshopRegActivity)getActivity()).setWorkshop(priv);
    }
    private void getData(){
        Workshop priv = ((WorkshopRegActivity)getActivity()).getWorkshop();
        if(priv != null){
            Name.setText(priv.getName());
            if(priv.getLocation()!=null || tempLatLng!=null) {
                btnAddress.setText("Lokacija zapamcena");
                if(priv.getLocation()!=null){
                    data.LatLng dataLoc = priv.getLocation();
                    loc = new LatLng(dataLoc.getLatitude(),dataLoc.getLatitude());
                }

                else
                    loc=tempLatLng;
            }
            telefon.setText(priv.getPhoneNum());
            if(priv.getWorkPrice()!=0)
            WorkPay.setText(String.valueOf(priv.getWorkPrice()));
            List<WorkDaysAndHours> wd = priv.getWorkDays();
            if(wd.get(0).isOpen() && wd.get(0).getStartTime()!=null)
            btnMonFri.setText(wd.get(0).getStartTime()+"-"+wd.get(0).getEndTime());
            if(wd.get(1).isOpen()&&wd.get(1).getStartTime()!=null)
            btnSat.setText(wd.get(1).getStartTime()+"-"+wd.get(1).getEndTime());
            if(wd.get(2).isOpen()&&wd.get(2).getStartTime()!=null)
            btnSun.setText(wd.get(2).getStartTime()+"-"+wd.get(2).getEndTime());
            MonFri.setChecked(wd.get(0).isOpen());
            Sat.setChecked(wd.get(1).isOpen());
            Sun.setChecked(wd.get(2).isOpen());

            selectedServices = getServicesList(priv.getServices());
            String services = "";
            if(selectedServices.size()==0)
                services="Izaberi servise";
            else {
                for (String s : selectedServices)
                    services += s + ", ";
                services=services.substring(0,services.length()-2);
            }
            btnServices.setText(services);
        }
    }


    private List<String> getServicesList(Map<String,Boolean> Services){
        List<String> list =new ArrayList<>();
        for(Map.Entry<String,Boolean> entry:Services.entrySet()){
            list.add(entry.getKey());
        }
        return list;
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
                btnAddress.setText( addresses.get(0).getAddressLine(0));
            }
            catch (IOException ex){
                btnAddress.setText("Lokacija Zapamcena");
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