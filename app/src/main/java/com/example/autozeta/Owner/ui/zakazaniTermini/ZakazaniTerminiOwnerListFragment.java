package com.example.autozeta.Owner.ui.zakazaniTermini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.ZakazaniTerminiOwnerRecyclerViewAdapter;
import com.example.autozeta.Adapters.ZakazaniTerminiRecyclerViewAdapter;
import com.example.autozeta.Basic.HomeBasicActivity;
import com.example.autozeta.Basic.UI.termini.TerminActivity;
import com.example.autozeta.Basic.UI.workshops.workshopPages.CalendarFragment;
import com.example.autozeta.Logic;
import com.example.autozeta.Owner.HomeOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import data.Termin;
import data.Workshop;
import data.ZakazanTermin;

public class ZakazaniTerminiOwnerListFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseAuth mAuth;
    private String userID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    private ZakazaniTerminiOwnerRecyclerViewAdapter adapter;
    private RecyclerView terminiList;

    private List<ZakazanTermin> terminList = new ArrayList<>();
    private List<String> terminiIDList = new ArrayList<>();

    private Calendar date;
    private Workshop workshop;
    private boolean losDatum = true;

    private String selectedService;

    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_zakazani_termini_owner_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        workshop = ((HomeOwnerActivity)getActivity()).workshop;

        if(workshop==null){
            db.collection("workshops").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    workshop = documentSnapshot.toObject(Workshop.class);
                }
            });
        }

        terminList = new ArrayList<>();
        terminiIDList = new ArrayList<>();

        terminiList = (RecyclerView) Root.findViewById(R.id.termini_list);
        terminiList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ZakazaniTerminiOwnerRecyclerViewAdapter(terminList, terminiIDList);
        terminiList.setAdapter(adapter);
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);

        db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                terminList.clear();
                terminiIDList.clear();
                for(QueryDocumentSnapshot snapshot:value){
                    ZakazanTermin termin =  snapshot.toObject(ZakazanTermin.class);
                    terminList.add(termin);
                    terminiIDList.add(snapshot.getId());
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                adapter.notifyDataSetChanged();
                terminiList.smoothScrollToPosition(0);
            }
        });

        /*db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        ZakazanTermin termin = snapshot.toObject(ZakazanTermin.class);
                        terminList.add(termin);
                        terminiIDList.add(snapshot.getId());
                    }
                    adapter.notifyDataSetChanged();
                    terminiList.smoothScrollToPosition(0);
                }
            }
        });*/

        fab = Root.findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Dodaj Termin");
                date = Calendar.getInstance();
                final View stadardLayout = getLayoutInflater().inflate(R.layout.alert_dialog_full_termin, null);

                List<String> services = new ArrayList<>();
                services.add("Pregled");
                for(Map.Entry<String,Boolean> entry:workshop.getServices().entrySet()){
                    services.add(entry.getKey());
                }
                services.add("Ostalo");

                Spinner spinnerServices = stadardLayout.findViewById(R.id.spinnerServiceType);

                spinnerServices.setOnItemSelectedListener(ZakazaniTerminiOwnerListFragment.this);

                ArrayAdapter adapterServices = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,services);

                adapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerServices.setAdapter(adapterServices);


                builder.setView(stadardLayout);
                InitDTP(stadardLayout);
                builder.setPositiveButton("Zavrsi", null);
                builder.setNegativeButton("Otkazi", null);
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (losDatum) {
                                    Toast.makeText(getContext(), "Unesite datum", Toast.LENGTH_SHORT).show();
                                } else {
                                    AddTermin(stadardLayout, dialog);
                                }
                            }
                        });
                    }
                });
                dialog.show();
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

        return Root;
    }

    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void InitDTP(View layout) {
        Button btnDTP = layout.findViewById(R.id.btnDate);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        btnDTP.setText(format.format(date.getTime()));
        btnDTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                date = Calendar.getInstance();
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        if (Logic.CheckIfOpen(date, workshop) && date.after(currentDate)) {
                            new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    date.set(Calendar.MINUTE, minute);
                                    date.set(Calendar.SECOND,0);
                                    date.set(Calendar.MILLISECOND,0);
                                    if (Logic.CheckIfOpenDay(date, workshop)) {
                                        Logic.CheckIfDateIsNotBusy(date.getTime(), userID,null ,db, new Logic.TerminCallback() {
                                            @Override
                                            public void onCallback(boolean Value) {
                                                if (Value) {
                                                    losDatum = false;
                                                    btnDTP.setText(format.format(date.getTime()));
                                                } else {
                                                    losDatum = true;
                                                    Toast.makeText(getContext(), "Izabrani datum za pocetak termina je zauzet", Toast.LENGTH_SHORT).show();
                                                    btnDTP.setText("Izaberi Datum");
                                                }
                                            }
                                        },false);
                                    } else {
                                        losDatum = true;
                                        Toast.makeText(getContext(), "Izabrali ste pogresno vreme", Toast.LENGTH_SHORT).show();
                                        btnDTP.setText("Izaberi Datum");
                                    }
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                        } else {
                            losDatum = true;
                            btnDTP.setText("Izaberi Datum");
                            Toast.makeText(getContext(), "Izabrali ste pogresan dan", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }
        });
    }

    private void AddTermin(View root, DialogInterface dialogInterface) {
        EditText etCarInfo = root.findViewById(R.id.carInfo);
        EditText etUserInfo = root.findViewById(R.id.userInfo);
        EditText etPrice = root.findViewById(R.id.etCena);
        EditText etWorkTime = root.findViewById(R.id.etVremeTrajanja);
        String carInfo = etCarInfo.getText().toString();
        String userInfo =etUserInfo.getText().toString();
        String price = etPrice.getText().toString();
        String workTime = etWorkTime.getText().toString();
        if(carInfo.isEmpty())
            Toast.makeText(getActivity(),"Unesi Automobil", Toast.LENGTH_SHORT).show();
        else if(userInfo.isEmpty())
            Toast.makeText(getActivity(),"Unesi Korisnika", Toast.LENGTH_SHORT).show();
        else if(price.isEmpty())
            Toast.makeText(getActivity(),"Unesi Cenu", Toast.LENGTH_SHORT).show();
        else if(workTime.isEmpty())
            Toast.makeText(getActivity(),"Unesi Vreme Rada", Toast.LENGTH_SHORT).show();
        else{
            ZakazanTermin noviTermin = new ZakazanTermin();
            noviTermin.setPrice(Double.valueOf(price));
            noviTermin.setServiceType(selectedService);
            noviTermin.setCar(carInfo);
            noviTermin.setUserId("");
            noviTermin.setCarId(userInfo);
            noviTermin.setStartDate(date.getTime());
            noviTermin.setTimeNeeded(Double.valueOf(workTime));
            noviTermin.setWorkshopId(userID);
            noviTermin.setStartDate(date.getTime());
            Date endDate = Logic.ConvertToCorrectEndDate(date.getTime(),Double.valueOf(workTime), workshop);
            noviTermin.setEndDate(endDate);
            Logic.CheckIfDateIsNotBusy(noviTermin.getStartDate(), noviTermin.getWorkshopId(),null, db, new Logic.TerminCallback() {
                @Override
                public void onCallback(boolean Value) {
                    if (Value) {
                        Logic.CheckIfDateIsNotBusy(noviTermin.getEndDate(), noviTermin.getWorkshopId(),null ,db, new Logic.TerminCallback() {
                            @Override
                            public void onCallback(boolean Value) {
                                if (Value) {
                                    CollectionReference terminiRef = db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini");
                                    Query terminBetween = terminiRef.whereGreaterThanOrEqualTo("startDate",noviTermin.getStartDate()).whereLessThanOrEqualTo("startDate",noviTermin.getEndDate());
                                    terminBetween.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<ZakazanTermin> recoveredTermin = new ArrayList<>();
                                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                ZakazanTermin zakazanTermin = documentSnapshot.toObject(ZakazanTermin.class);
                                                recoveredTermin.add(zakazanTermin);
                                            }
                                            if(recoveredTermin.size()==0){
                                                db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").add(noviTermin).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").document(documentReference.getId()).update("terminId",documentReference.getId());
                                                    }
                                                });
                                                dialogInterface.dismiss();
                                            }
                                            else{
                                                Toast.makeText(getActivity(),"Pocetak i kraj termina sadrze drugi termin",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Kraj termina se poklapa sa zakaznim terminom", Toast.LENGTH_LONG).show();
                                }
                            }
                        },true);
                    } else {
                        Toast.makeText(getContext(), "Pocetak termina se poklapa sa zakaznim terminom", Toast.LENGTH_LONG).show();
                    }
                }
            },false);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerServiceType:
                selectedService = parent.getItemAtPosition(position).toString();
                break;
            default:
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
