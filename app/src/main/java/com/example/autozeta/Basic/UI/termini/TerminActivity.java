package com.example.autozeta.Basic.UI.termini;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.ActivityCheckClass;
import com.example.autozeta.Adapters.ChatRecycleViewAdapter;
import com.example.autozeta.Adapters.TerminRecyclerViewAdapter;
import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.Basic.UI.workshops.workshopPages.CalendarFragment;
import com.example.autozeta.Logic;
import com.example.autozeta.Owner.ui.zakazaniTermini.ZakazanTerminOwnerActivity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import data.Car;
import data.Message;
import data.Termin;
import data.WorkDaysAndHours;
import data.Workshop;
import data.ZakazanTermin;

public class TerminActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageSenderID,terminID;

    private TextView userName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Button btnCancel,btnAccept,btnModify;

    private final List<Termin> messagesList = new ArrayList<>();

    private RecyclerView messagesListView;
    private TerminRecyclerViewAdapter adapter;
    private Termin zadnjiTermin;
    private Workshop workshop;

    private Calendar date;
    private boolean losDatum=false;
    DatabaseReference refTermini;
    ValueEventListener listener;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termin);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID= mAuth.getCurrentUser().getUid();




        messageReceiverID=getIntent().getExtras().get("receiver_id").toString();
        messageReceiverName=getIntent().getExtras().get("receiver_name").toString();
        terminID=getIntent().getExtras().get("termin_id").toString();
        progressBar =(ProgressBar) findViewById(R.id.progressBar);

        InitControllers();

        userName.setText(messageReceiverName);

        refTermini = mDatabase.getReference().child("termini").child(messageSenderID).child(terminID).child("poruke");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()==0){
                    Toast.makeText(TerminActivity.this,"Termin je prihvacen",Toast.LENGTH_LONG).show();
                    finish();

                }

                messagesList.clear();
                for(DataSnapshot child:snapshot.getChildren()){
                    Termin messages = child.getValue(Termin.class);

                    messagesList.add(messages);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);


                }
                if(messagesList.size()==0){
                    Toast.makeText(TerminActivity.this,"Termin je prihvacen",Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    adapter.notifyDataSetChanged();

                    messagesListView.smoothScrollToPosition(messagesListView.getAdapter().getItemCount());
                    zadnjiTermin = messagesList.get(messagesList.size() - 1);
                    mFirestore.collection("workshops").document(zadnjiTermin.getWorkshopId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            workshop = documentSnapshot.toObject(Workshop.class);
                        }
                    });
                    if (zadnjiTermin.getPrice() != 0)
                        btnAccept.setEnabled(true);
                    if (messageSenderID.equals(zadnjiTermin.getFromUserId())) {
                        btnModify.setEnabled(false);
                        btnAccept.setEnabled(false);
                    } else {
                        btnModify.setEnabled(true);
                        if (zadnjiTermin.getPrice() != 0)
                            btnAccept.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        refTermini.addValueEventListener(listener);




        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TerminActivity.this);
                builder.setTitle("Modifikuj Termin");
                date=Calendar.getInstance();
                date.setTime(zadnjiTermin.getStartDate());
                final View ownerLayout = getLayoutInflater().inflate(R.layout.alert_dialog_termin_workshop_owner,null);
                final View stadardLayout = getLayoutInflater().inflate(R.layout.alert_dialog_termin_standard_user,null);
                if(messageSenderID.equals(zadnjiTermin.getUserId()) || zadnjiTermin.getPrice()!=0){
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
                                        Toast.makeText(TerminActivity.this,"Unesite datum", Toast.LENGTH_SHORT).show();
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
                else{
                    builder.setView(ownerLayout);
                    InitDTP(ownerLayout);
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
                                        Toast.makeText(TerminActivity.this,"Unesite datum", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        AddTerminOwner(ownerLayout,dialog);
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();

                }





            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(TerminActivity.this);
                builder.setTitle("Odustani od termina");
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
                                refTermini.removeEventListener(listener);
                                listener = null;
                                DatabaseReference ref=mDatabase.getReference().child("termini");
                                DatabaseReference senderRef = ref.child(messageSenderID).child(terminID);
                                DatabaseReference receiverRef = ref.child(messageReceiverID).child(terminID);
                                senderRef.removeValue();
                                receiverRef.removeValue();
                                mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        final String[] Fname = new String[1];
                                        final String retUT = snapshot.child("userType").getValue().toString();
                                        if(retUT.equals("Vlasnik")){
                                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                                            Logic.SendNotification(messageReceiverID,"Dogovor za termin prekinut","Radionica " +retName+ " je odustao od predloga za termin.\n"+kil,getApplicationContext(), TerminActivity.class,messageSenderID);
                                            dialog.dismiss();
                                            finish();
                                        }
                                        else{
                                            final String retFName = snapshot.child("ime").getValue().toString();
                                            final String retLName = snapshot.child("prezime").getValue().toString();
                                            final String retName = retFName + " "+ retLName;
                                            Logic.SendNotification(messageReceiverID,"Dogovor za termin prekinut","Korisnik " +retName+ " je odustao od predloga za termin. \n"+kil,getApplicationContext(), TerminActivity.class,messageSenderID);
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

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZakazanTermin zakazanTermin = new ZakazanTermin(zadnjiTermin.getWorkshopId(),zadnjiTermin.getUserId(),zadnjiTermin.getCarId(),zadnjiTermin.getServiceType(),
                        zadnjiTermin.getStartDate(),zadnjiTermin.getStartDate(),zadnjiTermin.getCar(),zadnjiTermin.getTerminId(),zadnjiTermin.getTimeNeeded(),zadnjiTermin.getPrice());
                Date endDate = Logic.ConvertToCorrectEndDate(zadnjiTermin,workshop);
                zakazanTermin.setEndDate(endDate);

                Logic.CheckIfDateIsNotBusy(zakazanTermin.getStartDate(), zakazanTermin.getWorkshopId(),zakazanTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                    @Override
                    public void onCallback(boolean Value) {
                        if(Value){
                            Logic.CheckIfDateIsNotBusy(zakazanTermin.getEndDate(), zakazanTermin.getWorkshopId(),zakazanTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                                @Override
                                public void onCallback(boolean Value) {
                                    if(Value){
                                        CollectionReference terminiRef = mFirestore.collection("zakazaniTermini").document(zakazanTermin.getWorkshopId()).collection("zakazaniTermini");
                                        Query terminBetween = terminiRef.whereGreaterThanOrEqualTo("startDate",zakazanTermin.getStartDate()).whereLessThanOrEqualTo("startDate",zakazanTermin.getEndDate());
                                        terminBetween.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<ZakazanTermin> recoveredTermin = new ArrayList<>();
                                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                    ZakazanTermin zakazanTermin = documentSnapshot.toObject(ZakazanTermin.class);
                                                    recoveredTermin.add(zakazanTermin);
                                                }
                                                if(recoveredTermin.size()==0){
                                                    refTermini.removeEventListener(listener);
                                                    DatabaseReference ref=mDatabase.getReference().child("termini");
                                                    mFirestore.collection("zakazaniTermini").document(zadnjiTermin.getWorkshopId()).collection("zakazaniTermini").document(terminID).set(zakazanTermin);
                                                    mFirestore.collection("zakazaniTermini").document(zadnjiTermin.getUserId()).collection("zakazaniTermini").document(terminID).set(zakazanTermin);
                                                    DatabaseReference senderRef = ref.child(messageSenderID).child(terminID);
                                                    DatabaseReference receiverRef = ref.child(messageReceiverID).child(terminID);

                                                    mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            final String[] Fname = new String[1];
                                                            final String retUT = snapshot.child("userType").getValue().toString();
                                                            if(retUT.equals("Vlasnik")){
                                                                final String retName = snapshot.child("imeRadionce").getValue().toString();
                                                                Logic.SendNotification(messageReceiverID,"Termin prihvacen","Radionica " +retName+ " je prihvatio Vas termin",getApplicationContext(), TerminActivity.class,messageSenderID);
                                                                receiverRef.removeValue();
                                                                senderRef.removeValue();
                                                                finish();
                                                            }
                                                            else{
                                                                final String retFName = snapshot.child("ime").getValue().toString();
                                                                final String retLName = snapshot.child("prezime").getValue().toString();
                                                                final String retName = retFName + " "+ retLName;
                                                                Logic.SendNotification(messageReceiverID,"Termin prihvacen","Korisnik " +retName+ " je prihvatio Vas termin",getApplicationContext(), TerminActivity.class,messageSenderID);

                                                                receiverRef.removeValue();
                                                                senderRef.removeValue();
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                                else{
                                                    Toast.makeText(TerminActivity.this,"Pocetak i kraj termina sadrze drugi termin",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(TerminActivity.this,"Kraj termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                                    }
                                }
                            },true);
                        }
                        else{
                            Toast.makeText(TerminActivity.this,"Pocetak termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                        }
                    }
                },false);
            }
        });

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()==progressBar.VISIBLE){
                    Toast.makeText(getApplicationContext(),"Ne postoje podaci!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 2500);
    }

    private Date ConvertToCorrectEndDate(Date date){

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(zadnjiTermin.getStartDate());
        long TimeInSecs;

        TimeInSecs=(long)(zadnjiTermin.getTimeNeeded()*60*1000);
        List<WorkDaysAndHours> workDays = workshop.getWorkDays();
        int day = startDate.get(Calendar.DAY_OF_WEEK);//get Current day
        WorkDaysAndHours currentDay;
        switch (day){
            case Calendar.SUNDAY:
                currentDay = workDays.get(2);
                break;
            case Calendar.SATURDAY:
                currentDay = workDays.get(1);
                break;
            default:
                currentDay = workDays.get(0);
                break;
        }
        String[] endTime = currentDay.getEndTime().split(":");
        int endHour = Integer.valueOf(endTime[0]);
        int endMin =Integer.valueOf(endTime[1]);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate.getTime());
        endDate.set(Calendar.HOUR_OF_DAY,endHour);
        endDate.set(Calendar.MINUTE,endMin);
        long timeToAdd=TimeInSecs;
        TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());//get time from start of term to end of work day
        while(TimeInSecs>0){
            endDate.add(Calendar.DATE,1);
            while(!CheckIfOpen(endDate)){
                endDate.add(Calendar.DATE,1); //add days until workday is open
            }
            day = endDate.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    currentDay = workDays.get(2);
                    break;
                case Calendar.SATURDAY:
                    currentDay = workDays.get(1);
                    break;
                default:
                    currentDay = workDays.get(0);
                    break;
            } //get current workday
            startDate.setTime(endDate.getTime()); //set endDate to endTimeCal
            String[] startTime= currentDay.getStartTime().split(":");
            int startHour = Integer.valueOf(startTime[0]);
            int startMin =Integer.valueOf(startTime[1]);
            endTime = currentDay.getEndTime().split(":");
            endHour = Integer.valueOf(endTime[0]);
            endMin =Integer.valueOf(endTime[1]);
            startDate.set(Calendar.HOUR_OF_DAY,startHour);
            startDate.set(Calendar.MINUTE,startMin); //set endDate to start of workDay
            endDate.set(Calendar.HOUR_OF_DAY,endHour); //set endTimeCal to endOfWorkDay
            endDate.set(Calendar.MINUTE,endMin);
            if((TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis()))<=0);
            timeToAdd=TimeInSecs;
            TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());
        }
        Date finalDate = new Date(startDate.getTimeInMillis()+timeToAdd);
        return finalDate;
    }
    private void InitDTP(View layout){
        Button btnDTP = layout.findViewById(R.id.btnDate);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        btnDTP.setText(format.format(date.getTime()));
        btnDTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDate= Calendar.getInstance();
                date = Calendar.getInstance();
                date.setTime(zadnjiTermin.getStartDate());
                new DatePickerDialog(TerminActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        if (CheckIfOpen(date) && date.after(currentDate)) {
                            new TimePickerDialog(TerminActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    date.set(Calendar.MINUTE, minute);
                                    date.set(Calendar.SECOND,0);
                                    date.set(Calendar.MILLISECOND,0);
                                    if(CheckIfOpenDay(date)){
                                        Logic.CheckIfDateIsNotBusy(date.getTime(), zadnjiTermin.getWorkshopId(),zadnjiTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                                            @Override
                                            public void onCallback(boolean Value) {
                                                if(Value){
                                                    losDatum=false;
                                                    btnDTP.setText(format.format(date.getTime()));
                                                }
                                                else{
                                                    losDatum=true;
                                                    Toast.makeText(TerminActivity.this,"Izabrani datum za pocetak termina je zauzet", Toast.LENGTH_SHORT).show();
                                                    btnDTP.setText("Izaberi Datum");
                                                }
                                            }
                                        },false);
                                    }
                                    else{
                                        losDatum=true;
                                        Toast.makeText(TerminActivity.this,"Izabrali ste pogresno vreme", Toast.LENGTH_SHORT).show();
                                        btnDTP.setText("Izaberi Datum");
                                    }
                                }
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                        }
                        else{
                            losDatum=true;
                            btnDTP.setText("Izaberi Datum");
                            Toast.makeText(TerminActivity.this,"Izabrali ste pogresan dan", Toast.LENGTH_SHORT).show();
                        }
                    }
                },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }
        });
    }


    private void AddTermin(View root,DialogInterface dialog){
        EditText etporuka= root.findViewById(R.id.etPoruka);
        String poruka= etporuka.getText().toString();
        String workshopId= zadnjiTermin.getWorkshopId();
        zadnjiTermin.setMessage(poruka);
        zadnjiTermin.setFromUserId(messageSenderID);
        zadnjiTermin.setToUserId(messageReceiverID);

            zadnjiTermin.setStartDate(date.getTime());
            Date endDate = Logic.ConvertToCorrectEndDate(zadnjiTermin,workshop);
            zadnjiTermin.setEndDate(endDate);
            Logic.CheckIfDateIsNotBusy(zadnjiTermin.getStartDate(), zadnjiTermin.getWorkshopId(),zadnjiTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                @Override
                public void onCallback(boolean Value) {
                    if(Value){
                        Logic.CheckIfDateIsNotBusy(zadnjiTermin.getEndDate(), zadnjiTermin.getWorkshopId(),zadnjiTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                            @Override
                            public void onCallback(boolean Value) {
                                if(Value){

                                    CollectionReference terminiRef = mFirestore.collection("zakazaniTermini").document(zadnjiTermin.getWorkshopId()).collection("zakazaniTermini");
                                    Query terminBetween = terminiRef.whereGreaterThanOrEqualTo("startDate",zadnjiTermin.getStartDate()).whereLessThanOrEqualTo("startDate",zadnjiTermin.getEndDate());
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
                                                DatabaseReference senderRef = ref.child(messageSenderID).child(terminID).child("poruke").push();
                                                DatabaseReference receiverRef = ref.child(messageReceiverID).child(terminID).child("poruke").child(senderRef.getKey());
                                                senderRef.setValue(zadnjiTermin);
                                                receiverRef.setValue(zadnjiTermin);
                                                dialog.dismiss();
                                                mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        final String[] Fname = new String[1];
                                                        final String retUT = snapshot.child("userType").getValue().toString();
                                                        if(retUT.equals("Vlasnik")){
                                                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                                                            Logic.SendNotification(messageReceiverID,"Novi predlog termina","Radionica " +retName+ " je predlozio novi termin",getApplicationContext(), ChatActivity.class,messageSenderID);
                                                        }
                                                        else{
                                                            final String retFName = snapshot.child("ime").getValue().toString();
                                                            final String retLName = snapshot.child("prezime").getValue().toString();
                                                            final String retName = retFName + " "+ retLName;
                                                            Logic.SendNotification(messageReceiverID,"Novi predlog termina","Korisnik " +retName+ " je predlozio novi termin",getApplicationContext(), ChatActivity.class,messageSenderID);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(TerminActivity.this,"Pocetak i kraj termina sadrze drugi termin",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(TerminActivity.this,"Kraj termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                                }
                            }
                        },true);
                    }
                    else{
                        Toast.makeText(TerminActivity.this,"Pocetak termina se poklapa sa zakaznim terminom",Toast.LENGTH_LONG).show();
                    }
                }
            },false);



    }
    private void AddTerminOwner(View root,DialogInterface dialog){
        EditText etporuka= root.findViewById(R.id.etPoruka);
        EditText etcena = root.findViewById(R.id.etCena);
        EditText etvremeTrajanja = root.findViewById(R.id.etVremeTrajanja);
        String poruka= etporuka.getText().toString();
        String cena = etcena.getText().toString();
        String vremeTrajanja = etvremeTrajanja.getText().toString();
        String workshopId= zadnjiTermin.getWorkshopId();
        if(cena.isEmpty())
            Toast.makeText(this, "Unesite cenu", Toast.LENGTH_SHORT).show();
        else if(vremeTrajanja.isEmpty())
            Toast.makeText(this,"Unesite vreme trajanja", Toast.LENGTH_SHORT).show();
        else {


            zadnjiTermin.setPrice(Double.valueOf(cena));
            zadnjiTermin.setTimeNeeded(Double.valueOf(vremeTrajanja));
            zadnjiTermin.setStartDate(date.getTime());
            zadnjiTermin.setMessage(poruka);
            zadnjiTermin.setFromUserId(messageSenderID);
            zadnjiTermin.setToUserId(messageReceiverID);

            Date endDate = Logic.ConvertToCorrectEndDate(zadnjiTermin, workshop);
            zadnjiTermin.setEndDate(endDate);


            Logic.CheckIfDateIsNotBusy(zadnjiTermin.getStartDate(), zadnjiTermin.getWorkshopId(),zadnjiTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                @Override
                public void onCallback(boolean Value) {
                    if (Value) {
                        Logic.CheckIfDateIsNotBusy(zadnjiTermin.getEndDate(), zadnjiTermin.getWorkshopId(),zadnjiTermin.getTerminId(), mFirestore, new Logic.TerminCallback() {
                            @Override
                            public void onCallback(boolean Value) {
                                if (Value) {

                                    CollectionReference terminiRef = mFirestore.collection("zakazaniTermini").document(zadnjiTermin.getWorkshopId()).collection("zakazaniTermini");
                                    Query terminBetween = terminiRef.whereGreaterThanOrEqualTo("startDate",zadnjiTermin.getStartDate()).whereLessThanOrEqualTo("startDate",zadnjiTermin.getEndDate());
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
                                                DatabaseReference senderRef = ref.child(messageSenderID).child(terminID).child("poruke").push();
                                                DatabaseReference receiverRef = ref.child(messageReceiverID).child(terminID).child("poruke").child(senderRef.getKey());
                                                senderRef.setValue(zadnjiTermin);
                                                receiverRef.setValue(zadnjiTermin);
                                                dialog.dismiss();
                                                mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        final String[] Fname = new String[1];
                                                        final String retUT = snapshot.child("userType").getValue().toString();
                                                        if(retUT.equals("Vlasnik")){
                                                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                                                            Logic.SendNotification(messageReceiverID,"Novi predlog termina","Radionica " +retName+ " je predlozio novi termin",getApplicationContext(), ChatActivity.class,messageSenderID);
                                                        }
                                                        else{
                                                            final String retFName = snapshot.child("ime").getValue().toString();
                                                            final String retLName = snapshot.child("prezime").getValue().toString();
                                                            final String retName = retFName + " "+ retLName;
                                                            Logic.SendNotification(messageReceiverID,"Novi predlog termina","Korisnik " +retName+ " je predlozio novi termin",getApplicationContext(), ChatActivity.class,messageSenderID);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(TerminActivity.this,"Pocetak i kraj termina sadrze drugi termin",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });



                                } else {
                                    Toast.makeText(TerminActivity.this, "Kraj termina se poklapa sa zakaznim terminom", Toast.LENGTH_LONG).show();
                                }
                            }
                        },true);
                    } else {
                        Toast.makeText(TerminActivity.this, "Pocetak termina se poklapa sa zakaznim terminom", Toast.LENGTH_LONG).show();
                    }
                }
            },false);

        }




    }
    private boolean CheckIfOpen(Calendar d){
        boolean IsOpen =true;
        int day = d.get(Calendar.DAY_OF_WEEK);
        switch (day){
            case Calendar.SUNDAY:
                IsOpen = workshop.getOpenDays().get("Nedelja");
                break;
            case Calendar.SATURDAY:
                IsOpen = workshop.getOpenDays().get("Subota");
                break;
            default:
                IsOpen = workshop.getOpenDays().get("Ponedeljak-Petak");
                break;
        }
        return IsOpen;
    }
    private boolean CheckIfOpenDay(Calendar d){
        List<WorkDaysAndHours> workDays = workshop.getWorkDays();
        int day = d.get(Calendar.DAY_OF_WEEK);
        int hour = d.get(Calendar.HOUR_OF_DAY);
        int minute = d.get(Calendar.MINUTE);
        WorkDaysAndHours currentDay;
        switch (day){
            case Calendar.SUNDAY:
                currentDay = workDays.get(2);
                break;
            case Calendar.SATURDAY:
                currentDay = workDays.get(1);
                break;
            default:
                currentDay = workDays.get(0);
                break;
        }
        String[] startTime= currentDay.getStartTime().split(":");
        String[] endTime = currentDay.getEndTime().split(":");
        int startHour = Integer.valueOf(startTime[0]);
        int endHour = Integer.valueOf(endTime[0]);
        int startMin =Integer.valueOf(startTime[1]);
        int endMin =Integer.valueOf(endTime[1]);
        if(startHour<hour &&hour<endHour)
        {
            return true;

        }
        else if(startHour==hour){
            if(startMin<=minute)
                return true;
            else
                return false;
        }
        else if(endHour==hour){
            if(endHour>minute)
                return true;
            else
                return false;
        }
        else{
            return false;
        }
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
        handler.removeCallbacks(runnable);
        ActivityCheckClass.clearOtherUser(messageReceiverID);
        ActivityCheckClass.ClearActivity(this);
    }

    private void InitControllers() {
        userName=findViewById(R.id.name);
        btnAccept = findViewById(R.id.finish);
        btnCancel = findViewById(R.id.cancel);
        btnModify=findViewById(R.id.modify);

        adapter=new TerminRecyclerViewAdapter(messagesList);
        messagesListView = findViewById(R.id.termin_messages);
        messagesListView.setLayoutManager(new LinearLayoutManager(this));
        messagesListView.setAdapter(adapter);
    }
}
