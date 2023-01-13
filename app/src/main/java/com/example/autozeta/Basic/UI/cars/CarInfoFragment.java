package com.example.autozeta.Basic.UI.cars;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.CarsRecyclerViewAdapter;
import com.example.autozeta.Adapters.ZavrseniServisiRecyclerViewAdapter;
import com.example.autozeta.Basic.UI.zakazaniTermini.ZakazanTerminActivity;
import com.example.autozeta.Logic;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import data.Car;
import data.Workshop;
import data.ZavrsenServis;

public class CarInfoFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private String userID;
    private Car car;
    private String carID;
    private CarsSharedViewModel SharedViewModel;
    private Calendar date;
    private Boolean losDatum=true;

    private ZavrseniServisiRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private List<ZavrsenServis> servisList;

    private String selectedPowerType;

    private TextView tvCarInfo,tvVIN,tvYear,tvEngine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_info, container, false);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(CarsSharedViewModel.class);

        servisList = new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        carID = SharedViewModel.getCarId();

        InitControllers(view);

        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.carList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new ZavrseniServisiRecyclerViewAdapter(servisList,getContext(),userID, carID);
        recyclerView.setAdapter(adapter);

        mDatabase.getReference().child("Cars").child(userID).child(carID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    car = task.getResult().getValue(Car.class);
                    UpdateFields();
                }
            }
        });

        DatabaseReference ref = mDatabase.getReference().child("Cars").child(userID).child(carID).child("servisi");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ZavrsenServis car = snapshot.getValue(ZavrsenServis.class);
                servisList.add(car);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ZavrsenServis car = snapshot.getValue(ZavrsenServis.class);
                servisList.remove(car);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.car_info_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                DeleteCar();
                return true;
            case R.id.addServis:
                AddService();
                return true;
            case R.id.azurirajAuto:
                AzurirajAuto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AzurirajAuto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Azuriraj Automobil");

        final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog_add_car,null);
        builder.setView(customLayout);

        String[] powerType = {"KW","HP"};
        Spinner spinner = customLayout.findViewById(R.id.spinnerPowerType);
        spinner.setOnItemSelectedListener(CarInfoFragment.this);
        ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,powerType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText etMake = customLayout.findViewById(R.id.etMake);
        EditText etModel = customLayout.findViewById(R.id.etModel);
        EditText etVIN  = customLayout.findViewById(R.id.etVIN);
        EditText etEngine  = customLayout.findViewById(R.id.etEngine);
        EditText etPower  = customLayout.findViewById(R.id.etPower);
        EditText etYear  = customLayout.findViewById(R.id.etYear);
        String make = car.getMake();
        String model = car.getModel();
        String VIN = car.getVIN();
        String engine = car.getEngine();
        String power = String.valueOf(car.getPower());
        selectedPowerType = car.getPowerType();
        String year = String.valueOf(car.getYear());

        etMake.setText(make);
        etModel.setText(model);
        etVIN.setText(VIN);
        etEngine.setText(engine);
        etPower.setText(power);
        etYear.setText(year);

        builder.setPositiveButton("Zavrsi",null);

        builder.setNegativeButton("Otkazi",null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateCar(customLayout,dialog);
                    }
                });
            }
        });
        dialog.show();
    }

    private void UpdateCar(View root,DialogInterface dialogInterface){
        EditText etMake = root.findViewById(R.id.etMake);
        EditText etModel = root.findViewById(R.id.etModel);
        EditText etVIN  = root.findViewById(R.id.etVIN);
        EditText etEngine  = root.findViewById(R.id.etEngine);
        EditText etPower  = root.findViewById(R.id.etPower);
        EditText etYear  = root.findViewById(R.id.etYear);

        String make = etMake.getText().toString();
        String model = etModel.getText().toString();
        String VIN = etVIN.getText().toString();
        String engine = etEngine.getText().toString();
        String power = etPower.getText().toString();
        String year = etYear.getText().toString();

        if(make.isEmpty())
            Toast.makeText(getActivity(),"Unesi Marku", Toast.LENGTH_SHORT).show();
        else if(model.isEmpty())
            Toast.makeText(getActivity(),"Unesi Model", Toast.LENGTH_SHORT).show();
        else if(engine.isEmpty())
            Toast.makeText(getActivity(),"Unesi Motor", Toast.LENGTH_SHORT).show();
        else if(power.isEmpty())
            Toast.makeText(getActivity(),"Unesi Snagu Motora", Toast.LENGTH_SHORT).show();
        else if(year.isEmpty())
            Toast.makeText(getActivity(),"Unesi Godiste", Toast.LENGTH_SHORT).show();
        else{
            DatabaseReference ref = mDatabase.getReference();
            ref = ref.child("Cars").child(userID).child(carID);
            HashMap<String,Object> map = new HashMap<>();
            map.put("make",make);
            map.put("model",model);
            map.put("power",Integer.valueOf(power));
            map.put("powerType",selectedPowerType);
            map.put("vin",VIN);
            map.put("year",Integer.valueOf(year));
            map.put("engine",engine);
            ref.updateChildren(map);
            dialogInterface.dismiss();
            UpdateFields();
        }
    }

    private void AddService(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifikuj Termin");
        date= Calendar.getInstance();
        final View stadardLayout = getLayoutInflater().inflate(R.layout.alert_dialog_add_full_servis,null);
        builder.setView(stadardLayout);

        InitDTP(stadardLayout);

        builder.setPositiveButton("Zavrsi",null);
        builder.setNegativeButton("Otkazi",null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(losDatum){
                            Toast.makeText(getActivity(),"Unesite datum", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            AddServis(stadardLayout,dialog);
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void InitDTP(View root)
    {
        Button btnDTP = root.findViewById(R.id.btnDate);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Calendar currentDate = Calendar.getInstance();
        btnDTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        losDatum = false;
                        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        btnDTP.setText(format.format(date.getTime()));
                    }
                },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }
        });
    }
    private void AddServis(View root,DialogInterface dialog){
        EditText etKil=root.findViewById(R.id.etKil);
        EditText etServis=root.findViewById(R.id.etServisName);
        EditText etServisType = root.findViewById(R.id.etServisType);
        String kil = etKil.getText().toString();
        String servis = etServis.getText().toString();
        String servisType = etServisType.getText().toString();
        if(kil.isEmpty())
            Toast.makeText(getContext(),"Unesi kilometrazu", Toast.LENGTH_SHORT).show();
        else if(servis.isEmpty())
            Toast.makeText(getContext(),"Unesi naziv radionice", Toast.LENGTH_SHORT).show();
        else if(servisType.isEmpty())
            Toast.makeText(getContext(),"Unesi naziv servisa", Toast.LENGTH_SHORT).show();
        else{
            ZavrsenServis zavrsenServis = new ZavrsenServis(date.getTime(),Integer.valueOf(kil),servis,servisType);
            DatabaseReference ref = mDatabase.getReference().child("Cars").child(userID).child(carID).child("servisi").push();
            zavrsenServis.setServisID(ref.getKey());
            ref.setValue(zavrsenServis);
            dialog.dismiss();
        }
    }

    private void DeleteCar(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Obrisi Automobil");
            builder.setMessage("Da li zelite da obrisete izabrani Automobil");
            builder.setPositiveButton("Obrisi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDatabase.getReference().child("Cars").child(userID).child(carID).removeValue();
                    getActivity().onBackPressed();
                }
            });
            builder.setNegativeButton("Odustani",null);
            builder.show();

    }


    private void UpdateFields() {
        tvCarInfo.setText(car.getMake()+" "+car.getModel());
        if(car.getVIN()!=null && !car.getVIN().equals(""))
            tvVIN.setText(car.getVIN());
        else
            tvVIN.setText("Nepoznat VIN");
        tvYear.setText(String.valueOf(car.getYear()));
        tvEngine.setText(car.getEngine()+", " +String.valueOf(car.getPower())+" "+car.getPowerType() );
        selectedPowerType=car.getPowerType();
    }

    private void InitControllers(View root) {
        tvCarInfo = root.findViewById(R.id.tvCarInfo_value);
        tvVIN=root.findViewById(R.id.tvVIN_value);
        tvYear=root.findViewById(R.id.tvYearValue);
        tvEngine=root.findViewById(R.id.tvEngineValue);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPowerType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
