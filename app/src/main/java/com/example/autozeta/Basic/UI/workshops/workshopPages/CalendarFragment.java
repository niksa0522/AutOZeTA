package com.example.autozeta.Basic.UI.workshops.workshopPages;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.autozeta.Adapters.CalendarAdapter;
import com.example.autozeta.Basic.UI.cars.CarsFragment;
import com.example.autozeta.Basic.UI.termini.TerminActivity;
import com.example.autozeta.Basic.UI.workshops.WorkshopActivity;
import com.example.autozeta.Basic.UI.zakazaniTermini.ZakazanTerminActivity;
import com.example.autozeta.Logic;
import com.example.autozeta.Owner.ui.zakazaniTermini.ZakazanTerminOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Car;
import data.Review;
import data.Service;
import data.Termin;
import data.WorkDaysAndHours;
import data.Workshop;
import data.ZakazanTermin;
import kotlin.jvm.functions.Function1;

public class CalendarFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    FloatingActionButton fab;
    private WorkshopDataHolder holder;
    private Workshop workshop;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private List<Car> carList;
    private List<String> carIDs;
    private String userID;
    private String selectedCarId;
    private String selectedService;
    private String selectedCar;
    private Calendar date;
    private boolean losDatum=true;

    private WeekView mWeekView;
    private List<ZakazanTermin> mEvents;

    private BasicViewModel viewModel;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_calendar, container, false);

        holder = WorkshopDataHolder.getInstance();
        workshop = holder.getWorkshop();

        carList = new ArrayList<>();
        carIDs = new ArrayList<>();

        mAuth= FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        fab=root.findViewById(R.id.newTermin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Zatrazi Termin");

                final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog_ask_termin,null);
                builder.setView(customLayout);


                List<String> services = new ArrayList<>();
                services.add("Pregled");
                for(Map.Entry<String,Boolean> entry:workshop.getServices().entrySet()){
                    services.add(entry.getKey());
                }
                services.add("Ostalo");

                carList = new ArrayList<>();
                carIDs = new ArrayList<>();

                List<String> carShort = new ArrayList<>();

                DatabaseReference ref = mDatabase.getReference().child("Cars").child(userID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child:snapshot.getChildren()){
                            Car car = child.getValue(Car.class);
                            carIDs.add(child.getKey());
                            carList.add(car);
                            String carInfo = car.getYear()+" "+car.getMake()+" "+car.getModel()+", "+car.getEngine()+", "+car.getPower()+" "+car.getPowerType();
                            carShort.add(carInfo);
                        }
                        Spinner spinnerCars = customLayout.findViewById(R.id.spinnerCar);
                        spinnerCars.setOnItemSelectedListener(CalendarFragment.this);
                        ArrayAdapter adapterCars = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,carShort);
                        adapterCars.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCars.setAdapter(adapterCars);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Button btnDTP = customLayout.findViewById(R.id.btnDate);
                btnDTP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar currentDate= Calendar.getInstance();
                        date = Calendar.getInstance();
                        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.set(year, month, dayOfMonth);
                                if (CheckIfOpen(date) && date.after(currentDate)) {
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            date.set(Calendar.MINUTE, minute);
                                            date.set(Calendar.SECOND,0);
                                            date.set(Calendar.MILLISECOND,0);
                                            if(CheckIfOpenDay(date)){
                                                DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                                Logic.CheckIfDateIsNotBusy(date.getTime(),holder.getWorkshopID(),null , mFirestore, new Logic.TerminCallback() {
                                                    @Override
                                                    public void onCallback(boolean Value) {
                                                        if(Value){
                                                            losDatum=false;
                                                            btnDTP.setText(format.format(date.getTime()));
                                                        }
                                                        else{
                                                            losDatum=true;
                                                            Toast.makeText(getContext(),"Izabrani datum za pocetak termina je zauzet", Toast.LENGTH_SHORT).show();
                                                            btnDTP.setText("Izaberi Datum");
                                                        }
                                                    }
                                                },false);
                                            }
                                            else{
                                                losDatum=true;
                                                Toast.makeText(getContext(),"Izabrali ste pogresno vreme", Toast.LENGTH_SHORT).show();
                                                btnDTP.setText("Izaberi Datum");
                                            }
                                        }
                                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                                }
                                else{
                                    losDatum=true;
                                    Toast.makeText(getContext(),"Izabrali ste pogresan dan", Toast.LENGTH_SHORT).show();
                                    btnDTP.setText("Izaberi Datum");
                                }
                            }
                            },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
                    }
                });




                Spinner spinnerServices = customLayout.findViewById(R.id.spinnerServiceType);

                spinnerServices.setOnItemSelectedListener(CalendarFragment.this);

                ArrayAdapter adapterServices = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,services);

                adapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerServices.setAdapter(adapterServices);
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
                                if(losDatum) {
                                    Toast.makeText(getContext(),"Unesite datum", Toast.LENGTH_SHORT).show();
                                }
                                else if(selectedCar==null || selectedCar.isEmpty()){
                                    Toast.makeText(getContext(),"Nemoguce zakazati termin bez automobila", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AddTermin(customLayout,dialog);
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        mWeekView = root.findViewById(R.id.kalendar);
        CalendarAdapter calendarAdapter =new CalendarAdapter();
        mWeekView.setAdapter(calendarAdapter);
        mWeekView.setTimeFormatter(this::Time);
        mWeekView.setDateFormatter(this::Date);
        mWeekView.setMinHour(Logic.getMinHour(workshop));
        mWeekView.setMaxHour(Logic.getMaxHour(workshop));
        mWeekView.setMinDate(Calendar.getInstance());

        viewModel=new BasicViewModel(holder.getWorkshopID());
        viewModel.events().observe(getViewLifecycleOwner(),events->{
            calendarAdapter.submitList(events);
        });

        return root;
    }
    private String Time(int i){
        return String.valueOf(i)+":00";
    }
    private String Date(Calendar cal){
        String datum;
        DateFormat format = new SimpleDateFormat("dd-MM");
        datum = format.format(cal.getTime());
        int num = cal.get(Calendar.DAY_OF_WEEK);
        switch (num){
            case Calendar.SUNDAY:
                datum="Ned "+datum;
                break;
            case Calendar.MONDAY:
                datum="Pon "+datum;
                break;
            case Calendar.TUESDAY:
                datum="Uto "+datum;
                break;
            case Calendar.WEDNESDAY:
                datum="Sre "+datum;
                break;
            case Calendar.THURSDAY:
                datum="Cet "+datum;
                break;
            case Calendar.FRIDAY:
                datum="Pet "+datum;
                break;
            case Calendar.SATURDAY:
                datum="Sub "+datum;
                break;
        }
        return datum;
    }


    private void AddTermin(View root,DialogInterface dialogInterface){
        EditText poruka= root.findViewById(R.id.etPoruka);
        String workshopId= holder.getWorkshopID();
        Termin termin = new Termin(workshopId,userID,workshopId,userID,selectedCarId,selectedService,poruka.getText().toString(),date.getTime(),selectedCar);
        DatabaseReference ref= mDatabase.getReference().child("termini");
        DatabaseReference workshopRef = ref.child(workshopId).push();
        String terminId = workshopRef.getKey();
        DatabaseReference userRef = ref.child(userID).child(terminId);
        termin.setTerminId(terminId);

        Map workshopMap = new HashMap();
        workshopMap.put("userID", userID);
        workshopMap.put("carInfo", selectedCar);

        Map userMap = new HashMap();
        userMap.put("userID", workshopId);
        userMap.put("carInfo", selectedCar);

        workshopRef.setValue(workshopMap);
        userRef.setValue(userMap);

        workshopRef = workshopRef.child("poruke").push();
        userRef =userRef.child("poruke").child(workshopRef.getKey());

        workshopRef.setValue(termin);
        userRef.setValue(termin);

        Intent terminIntent = new Intent(getContext(), TerminActivity.class);
        terminIntent.putExtra("termin_id",terminId);
        terminIntent.putExtra("receiver_id",workshopId);
        terminIntent.putExtra("receiver_name", workshop.getName());

        mDatabase.getReference().child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String[] Fname = new String[1];
                final String retUT = snapshot.child("userType").getValue().toString();
                if(retUT.equals("Vlasnik")){
                    final String retName = snapshot.child("imeRadionce").getValue().toString();
                    Logic.SendNotification(workshopId,"Predlog termina","Radionica " +retName+ " je podneo predlog za termin",getContext(), WorkshopActivity.class,userID);
                    startActivity(terminIntent);
                    dialogInterface.dismiss();
                    getActivity().finish();
                }
                else{
                    final String retFName = snapshot.child("ime").getValue().toString();
                    final String retLName = snapshot.child("prezime").getValue().toString();
                    final String retName = retFName + " "+ retLName;
                    Logic.SendNotification(workshopId,"Predlog termina","Korisnik " +retName+ " je podneo predlog za termin",getContext(), WorkshopActivity.class,userID);
                    startActivity(terminIntent);
                    dialogInterface.dismiss();
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.spinnerCar:
                    selectedCar=parent.getItemAtPosition(position).toString();
                    selectedCarId = carIDs.get(position);

                    break;
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
class BasicViewModel extends ViewModel{
    public String workshopID;
    public BasicViewModel(String workshopID){
        super();
        this.workshopID=workshopID;
    }
    private MutableLiveData<List<ZakazanTermin>> _events;
    LiveData<List<ZakazanTermin>> events(){
        if(_events==null){
            _events=new MutableLiveData<List<ZakazanTermin>>();
            loadEvents();
        }
        return _events;
    }
    private void loadEvents(){
        FirebaseFirestore mFirestore =FirebaseFirestore.getInstance();
        mFirestore.collection("zakazaniTermini").document(workshopID).collection("zakazaniTermini").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<ZakazanTermin> termini = new ArrayList<>();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        ZakazanTermin termin =  snapshot.toObject(ZakazanTermin.class);
                        termini.add(termin);
                    }
                    _events.setValue(termini);
                }
            }
        });
    }
}
