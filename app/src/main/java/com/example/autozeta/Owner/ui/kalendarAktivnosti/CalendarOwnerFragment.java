package com.example.autozeta.Owner.ui.kalendarAktivnosti;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.alamkanak.weekview.WeekView;
import com.example.autozeta.Adapters.CalendarAdapter;
import com.example.autozeta.Basic.UI.termini.TerminActivity;
import com.example.autozeta.Basic.UI.workshops.workshopPages.CalendarFragment;
import com.example.autozeta.Basic.UI.workshops.workshopPages.WorkshopDataHolder;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import data.Car;
import data.Termin;
import data.WorkDaysAndHours;
import data.Workshop;
import data.ZakazanTermin;

public class CalendarOwnerFragment extends Fragment {

    FloatingActionButton fab;
    private WorkshopDataHolder holder;
    private Workshop workshop;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private String userID;

    private WeekView mWeekView;
    private List<ZakazanTermin> mEvents;

    private BasicViewModel viewModel;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_calendar_owner, container, false);

        mAuth= FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        workshop = ((HomeOwnerActivity)getActivity()).workshop;

        if(workshop==null){
            mFirestore.collection("workshops").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    workshop = documentSnapshot.toObject(Workshop.class);
                    mWeekView = root.findViewById(R.id.kalendar);
                    CalendarAdapter calendarAdapter =new CalendarAdapter();
                    mWeekView.setAdapter(calendarAdapter);
                    mWeekView.setTimeFormatter(CalendarOwnerFragment.this::Time);
                    mWeekView.setDateFormatter(CalendarOwnerFragment.this::Date);
                    mWeekView.setMinHour(Logic.getMinHour(workshop));
                    mWeekView.setMaxHour(Logic.getMaxHour(workshop));
                    mWeekView.setMinDate(Calendar.getInstance());

                    viewModel=new BasicViewModel(userID);
                    viewModel.events().observe(getViewLifecycleOwner(),events->{
                        calendarAdapter.submitList(events);
                    });
                }
            });
        }
        else{
            mWeekView = root.findViewById(R.id.kalendar);
            CalendarAdapter calendarAdapter =new CalendarAdapter();
            mWeekView.setAdapter(calendarAdapter);
            mWeekView.setTimeFormatter(this::Time);
            mWeekView.setDateFormatter(this::Date);
            mWeekView.setMinHour(Logic.getMinHour(workshop));
            mWeekView.setMaxHour(Logic.getMaxHour(workshop));
            mWeekView.setMinDate(Calendar.getInstance());

            viewModel=new BasicViewModel(userID);
            viewModel.events().observe(getViewLifecycleOwner(),events->{
                calendarAdapter.submitList(events);
            });
        }




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
}
class BasicViewModel extends ViewModel {
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
