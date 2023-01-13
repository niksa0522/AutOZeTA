package com.example.autozeta.Basic.UI.cars;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.autozeta.Adapters.CarsRecyclerViewAdapter;
import com.example.autozeta.Adapters.WorkshopsListViewAdapter;
import com.example.autozeta.Basic.UI.zakazaniTermini.ZakazanTerminActivity;
import com.example.autozeta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Car;
import data.Contacts;

public class CarsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

private CarsRecyclerViewAdapter adapter;
private RecyclerView recyclerView;

private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
private List<Car> carList;
private List<String> carIDs;
private String userID;

private String selectedPowerType="KW";
private CarsSharedViewModel SharedViewModel;

private FloatingActionButton fab;
    private Handler handler;
    private Runnable runnable;
    private ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(CarsSharedViewModel.class);

        mAuth=FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        carList = new ArrayList<>();
        carIDs = new ArrayList<>();


        recyclerView = (RecyclerView) view.findViewById(R.id.carList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar =(ProgressBar) view.findViewById(R.id.progressBar);

        adapter=new CarsRecyclerViewAdapter(carList,carIDs,SharedViewModel, NavHostFragment.findNavController(CarsFragment.this));
        recyclerView.setAdapter(adapter);

        fab = view.findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Unesi Novi Automobil");

                final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog_add_car,null);
                builder.setView(customLayout);

                String[] powerType = {"KW","HP"};
                Spinner spinner = customLayout.findViewById(R.id.spinnerPowerType);
                spinner.setOnItemSelectedListener(CarsFragment.this);
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,powerType);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

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
                                AddCar(customLayout,dialog);
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        DatabaseReference ref = mDatabase.getReference().child("Cars").child(userID);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Car car = snapshot.getValue(Car.class);
                if(carIDs.contains(snapshot.getKey())){

                }
                else {
                    carIDs.add(snapshot.getKey());
                    carList.add(car);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()==progressBar.VISIBLE){
                    Toast.makeText(getContext(),"Ne postoje podaci!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 2500);


        return view;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void AddCar(View root, DialogInterface dialog){
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
            ref = ref.child("Cars").child(userID).push();
            Car car = new Car(make,model,VIN,Integer.valueOf(year),engine,selectedPowerType,Integer.valueOf(power));
            ref.setValue(car);
            dialog.dismiss();

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPowerType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}