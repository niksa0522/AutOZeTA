package com.example.autozeta.Basic.UI.zakazaniTermini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.ActivityCheckClass;
import com.example.autozeta.Adapters.TerminRecyclerViewAdapter;
import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.Basic.UI.termini.TerminActivity;
import com.example.autozeta.Logic;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Termin;
import data.Workshop;
import data.ZakazanTermin;

public class ZakazanTerminActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, userID,terminID;

    private TextView userName,carInfo,price,serviceType,startTime,endTime;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Button btnCancel,btnModify;

    private ZakazanTermin zakazanTermin;

    private Calendar date;
    private Workshop workshop;
    private boolean losDatum=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakazan_termin);

        mAuth= FirebaseAuth.getInstance();
        userID= mAuth.getCurrentUser().getUid();

        InitControllers();


        messageReceiverID=getIntent().getExtras().get("receiver_id").toString();
        messageReceiverName=getIntent().getExtras().get("receiver_name").toString();
        terminID=getIntent().getExtras().get("termin_id").toString();

        userName.setText(messageReceiverName);
        mFirestore.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").document(terminID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                zakazanTermin = documentSnapshot.toObject(ZakazanTermin.class);
                SetData();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ZakazanTerminActivity.this);
                builder.setTitle("Obrisi termin");
                final View stadardLayout = getLayoutInflater().inflate(R.layout.alert_dialog_cancel_termin,null);
                builder.setView(stadardLayout);
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
                                EditText etKil = stadardLayout.findViewById(R.id.kilometraza);
                                String kil = etKil.getText().toString();
                                mFirestore.collection("zakazaniTermini").document(zakazanTermin.getWorkshopId()).collection("zakazaniTermini").document(terminID).delete();
                                mFirestore.collection("zakazaniTermini").document(zakazanTermin.getUserId()).collection("zakazaniTermini").document(terminID).delete();
                                mDatabase.getReference().child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        final String[] Fname = new String[1];
                                        final String retUT = snapshot.child("userType").getValue().toString();
                                        if(retUT.equals("Vlasnik")){
                                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                                            Logic.SendNotification(messageReceiverID,"Obrisan termin","Radionica " +retName+ " je odustao od predloga za termin. \n"+kil,getApplicationContext(), ZakazanTerminActivity.class,userID);
                                            dialog.dismiss();
                                            finish();
                                        }
                                        else{
                                            final String retFName = snapshot.child("ime").getValue().toString();
                                            final String retLName = snapshot.child("prezime").getValue().toString();
                                            final String retName = retFName + " "+ retLName;
                                            Logic.SendNotification(messageReceiverID,"Obrisan termin","Korisnik " +retName+ " je odustao od predloga za termin. \n"+kil,getApplicationContext(), ZakazanTerminActivity.class,userID);
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });
                dialog.show();

            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ZakazanTerminActivity.this);
                builder.setTitle("Modifikuj Termin");
                date=Calendar.getInstance();
                date.setTime(zakazanTermin.getStartDate());
                final View stadardLayout = getLayoutInflater().inflate(R.layout.alert_dialog_termin_standard_user,null);

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
                                        Toast.makeText(ZakazanTerminActivity.this,"Unesite datum", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        AddTermin(stadardLayout,dialog);
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();

            }
        });
    }
    private void InitDTP(View layout){
        Button btnDTP = layout.findViewById(R.id.btnDate);
        mFirestore.collection("workshops").document(zakazanTermin.getWorkshopId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workshop = documentSnapshot.toObject(Workshop.class);
            }
        });
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        btnDTP.setText(format.format(date.getTime()));
        btnDTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate= Calendar.getInstance();
                date = Calendar.getInstance();
                date.setTime(zakazanTermin.getStartDate());
                new DatePickerDialog(ZakazanTerminActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        if (Logic.CheckIfOpen(date,workshop) && date.after(currentDate)) {
                            new TimePickerDialog(ZakazanTerminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    date.set(Calendar.MINUTE, minute);
                                    date.set(Calendar.SECOND,0);
                                    date.set(Calendar.MILLISECOND,0);
                                    if(Logic.CheckIfOpenDay(date,workshop)){
                                        Logic.CheckIfDateIsNotBusy(date.getTime(), zakazanTermin.getWorkshopId(),zakazanTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                                            @Override
                                            public void onCallback(boolean Value) {
                                                if(Value){
                                                    losDatum=false;
                                                    btnDTP.setText(format.format(date.getTime()));
                                                }
                                                else{
                                                    losDatum=true;
                                                    Toast.makeText(ZakazanTerminActivity.this,"Izabrani datum za pocetak termina je zauzet", Toast.LENGTH_SHORT).show();
                                                    btnDTP.setText("Izaberi Datum");
                                                }
                                            }
                                        },false);
                                    }
                                    else{
                                        losDatum=true;
                                        Toast.makeText(ZakazanTerminActivity.this,"Izabrali ste pogresno vreme", Toast.LENGTH_SHORT).show();
                                        btnDTP.setText("Izaberi Datum");
                                    }
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                        }
                        else{
                            losDatum=true;
                            btnDTP.setText("Izaberi Datum");
                            Toast.makeText(ZakazanTerminActivity.this,"Izabrali ste pogresan dan", Toast.LENGTH_SHORT).show();
                        }
                    }
                },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }
        });
    }

    private void AddTermin(View root,DialogInterface dialog){
        EditText etporuka= root.findViewById(R.id.etPoruka);
        String poruka= etporuka.getText().toString();
        String workshopId= zakazanTermin.getWorkshopId();
        Termin noviTermin = new Termin();
        noviTermin.setToUserId(messageReceiverID);
        noviTermin.setFromUserId(userID);
        noviTermin.setTimeNeeded(zakazanTermin.getTimeNeeded());
        noviTermin.setPrice(zakazanTermin.getPrice());
        noviTermin.setCar(zakazanTermin.getCar());
        noviTermin.setCarId(zakazanTermin.getCarId());
        noviTermin.setUserId(zakazanTermin.getUserId());
        noviTermin.setWorkshopId(zakazanTermin.getWorkshopId());
        noviTermin.setTerminId(zakazanTermin.getTerminId());
        noviTermin.setServiceType(zakazanTermin.getServiceType());
        noviTermin.setMessage(poruka);
        if(zakazanTermin.getStartDate().equals(date.getTime())) {
            Toast.makeText(ZakazanTerminActivity.this,"Unesite novi datum",Toast.LENGTH_LONG).show();
        }
        else{
            noviTermin.setStartDate(date.getTime());
            Date endDate = Logic.ConvertToCorrectEndDate(noviTermin,workshop);
            noviTermin.setEndDate(endDate);
            Logic.CheckIfDateIsNotBusy(noviTermin.getStartDate(), noviTermin.getWorkshopId(),terminID ,mFirestore, new Logic.TerminCallback() {
                @Override
                public void onCallback(boolean Value) {
                    if(Value){
                        Logic.CheckIfDateIsNotBusy(noviTermin.getEndDate(), noviTermin.getWorkshopId(),terminID, mFirestore, new Logic.TerminCallback() {
                            @Override
                            public void onCallback(boolean Value) {
                                if(Value){

                                    CollectionReference terminiRef = mFirestore.collection("zakazaniTermini").document(zakazanTermin.getWorkshopId()).collection("zakazaniTermini");
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
                                                DatabaseReference ref = mDatabase.getReference().child("termini");
                                                DatabaseReference senderRef = ref.child(userID).child(terminID);
                                                DatabaseReference receiverRef = ref.child(messageReceiverID).child(terminID);

                                                Map workshopMap = new HashMap();
                                                workshopMap.put("userID", userID);
                                                workshopMap.put("carInfo", noviTermin.getCar());

                                                Map userMap = new HashMap();
                                                userMap.put("userID", workshopId);
                                                userMap.put("carInfo", noviTermin.getCar());

                                                receiverRef.setValue(workshopMap);
                                                senderRef.setValue(userMap);

                                                senderRef = senderRef.child("poruke").push();
                                                receiverRef =receiverRef.child("poruke").child(senderRef.getKey());

                                                senderRef.setValue(noviTermin);
                                                receiverRef.setValue(noviTermin);
                                                dialog.dismiss();

                                                Intent terminIntent = new Intent(ZakazanTerminActivity.this, TerminActivity.class);
                                                terminIntent.putExtra("termin_id",terminID);
                                                terminIntent.putExtra("receiver_id",messageReceiverID);
                                                terminIntent.putExtra("receiver_name", messageReceiverName);


                                                mDatabase.getReference().child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        final String[] Fname = new String[1];
                                                        final String retUT = snapshot.child("userType").getValue().toString();
                                                        if(retUT.equals("Vlasnik")){
                                                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                                                            Logic.SendNotification(messageReceiverID,"Promena termina","Radionica " +retName+ " predlaze promenu zakazanog termina",getApplicationContext(), ZakazanTerminActivity.class,userID);

                                                            startActivity(terminIntent);
                                                            mFirestore.collection("zakazaniTermini").document(zakazanTermin.getWorkshopId()).collection("zakazaniTermini").document(terminID).delete();
                                                            mFirestore.collection("zakazaniTermini").document(zakazanTermin.getUserId()).collection("zakazaniTermini").document(terminID).delete();


                                                            finish();
                                                        }
                                                        else{
                                                            final String retFName = snapshot.child("ime").getValue().toString();
                                                            final String retLName = snapshot.child("prezime").getValue().toString();
                                                            final String retName = retFName + " "+ retLName;
                                                            Logic.SendNotification(messageReceiverID,"Promena termina","Korisnik " +retName+ " predlaze promenu zakazanog termina",getApplicationContext(), ZakazanTerminActivity.class,userID);

                                                            startActivity(terminIntent);
                                                            mFirestore.collection("zakazaniTermini").document(zakazanTermin.getWorkshopId()).collection("zakazaniTermini").document(terminID).delete();
                                                            mFirestore.collection("zakazaniTermini").document(zakazanTermin.getUserId()).collection("zakazaniTermini").document(terminID).delete();


                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            else{
                                                Toast.makeText(ZakazanTerminActivity.this,"Pocetak i kraj termina sadrze drugi termin",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(ZakazanTerminActivity.this,"Kraj termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                                }
                            }
                        },true);
                    }
                    else{
                        Toast.makeText(ZakazanTerminActivity.this,"Pocetak termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                    }
                }
            },false);

        }
    }


    private void InitControllers(){
        userName = findViewById(R.id.name);
        carInfo = findViewById(R.id.carInfo);
        price = findViewById(R.id.tvCena);
        serviceType = findViewById(R.id.tvServisTip);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        btnCancel=findViewById(R.id.otkazi);
        btnModify=findViewById(R.id.modifikuj);
    }
    private void SetData(){
        String[] carInfoString = zakazanTermin.getCar().split(",");
        carInfo.setText(carInfoString[0]+"\n"+carInfoString[1]+"\n"+carInfoString[2]);
        price.setText(String.valueOf(zakazanTermin.getPrice()));
        serviceType.setText(zakazanTermin.getServiceType());
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        startTime.setText(format.format(zakazanTermin.getStartDate()));
        endTime.setText(format.format(zakazanTermin.getEndDate()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCheckClass.SetActivity(this);
        ActivityCheckClass.setOtherUser(messageReceiverID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityCheckClass.clearOtherUser(messageReceiverID);
        ActivityCheckClass.ClearActivity(this);
    }
}
