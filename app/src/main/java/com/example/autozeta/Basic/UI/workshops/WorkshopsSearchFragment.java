package com.example.autozeta.Basic.UI.workshops;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autozeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import data.Service;

public class WorkshopsSearchFragment extends Fragment {


    EditText name,price;
    int minimumRating=-1;
    Button btnServices,btnRating,btnWorkDays,btnSearch;
    List<String> selectedServices = new ArrayList<>();
    List<String> selectedDays = new ArrayList<>();
    List<String> services = new ArrayList<>();
    List<String> days = new ArrayList<>();
    WorkshopsSharedViewModel SharedViewModel;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_workshop_search,container,false);



        name= Root.findViewById(R.id.etNaziv);
        price=Root.findViewById(R.id.etCena);
        btnServices=Root.findViewById(R.id.button_services);
        btnRating=Root.findViewById(R.id.buttonOcena);
        btnWorkDays=Root.findViewById(R.id.buttonRD);
        btnSearch=Root.findViewById(R.id.btnPretrazi);

        SharedViewModel = new ViewModelProvider(requireActivity()).get(WorkshopsSharedViewModel.class);
        services=new ArrayList<>();
        DatabaseReference refServices = mDatabase.getReference().child("Services");

        SetWorkDaysButton();
        SetServicesButton();
        SetRatingButton();

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
        days = new ArrayList<>();
        days.add("Ponedeljak-Petak");
        days.add("Subota");
        days.add("Nedelja");


        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnWorkDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                String[] listdays = new String[days.size()];
                days.toArray(listdays);
                boolean[] checkedDays = new boolean[days.size()];
                int index=0;
                for(String s : days){
                    if(selectedDays.contains(s))
                        checkedDays[index]=true;
                    else
                        checkedDays[index]=false;
                    index++;
                }

                builder.setTitle("Izaberi radne dane").setMultiChoiceItems(listdays, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            selectedDays.add(days.get(which));
                        }
                        else if(selectedDays.contains(days.get(which))){
                            selectedDays.remove(days.get(which));
                        }

                    }
                });


                builder.setPositiveButton("Izaberi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SetWorkDaysButton();
                    }
                });
                builder.show();
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                String[] ratings = new String[]{
                        "1","2","3","4","5"
                };

                builder.setTitle("Izaberi minimalnu ocenu").setSingleChoiceItems(ratings, minimumRating, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        minimumRating=which;
                        btnRating.setText(String.valueOf(minimumRating+1));
                        dialog.dismiss();
                    }
                });



                builder.setPositiveButton("Ponisti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        minimumRating=-1;
                        btnRating.setText("Izaberi minimalnu ocenu");
                    }
                });
                builder.show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedViewModel.setMinimumRating(minimumRating+1);
                SharedViewModel.setName(name.getText().toString());
                SharedViewModel.setSelectedDays(selectedDays);
                SharedViewModel.setSelectedServices(selectedServices);
                if(price.getText()!=null && !price.getText().toString().equals(""))
                SharedViewModel.setWorkprice(Integer.parseInt(price.getText().toString()));
                else
                    SharedViewModel.setWorkprice(0);
                NavHostFragment.findNavController(WorkshopsSearchFragment.this)
                        .navigate(R.id.action_nav_workshops_to_nav_workshops_list);
            }
        });

        return Root;
    }


    void SetRatingButton(){
        if(minimumRating!=-1)
            btnRating.setText(String.valueOf(minimumRating+1));
        else
            btnRating.setText("Izaberi minimalnu ocenu");
    }
    void SetWorkDaysButton(){
        String services = "";
        if(selectedDays.size()==0)
            services="Izaberi radne dane";
        else {
            for (String s : selectedDays)
                services += s + ", ";
            services=services.substring(0,services.length()-2);
        }
        btnWorkDays.setText(services);
    }

    void SetServicesButton(){
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
