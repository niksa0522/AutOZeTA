package com.example.autozeta.Basic.UI.workshops;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.WorkshopsListViewAdapter;
import com.example.autozeta.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.LatLng;
import data.Workshop;
import data.WorkshopWithID;

public class WorkshopsListFragment extends Fragment {

    private WorkshopsListViewAdapter adapter;
    private RecyclerView workshopsList;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private DatabaseReference WorkshopsRef;
    private WorkshopsSharedViewModel SharedViewModel;
    private List<Workshop> workshopList = new ArrayList<>();
    private List<String> workshopIdList = new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    private Location loc;
    FusedLocationProviderClient client;

    private int minimumRating;
    private List<String> selectedServices = new ArrayList<>();
    private List<String> selectedDays = new ArrayList<>();
    private List<WorkshopWithID> sortedWorkshops = new ArrayList<>();
    private String name;
    private ProgressBar progressBar;
    private int workprice;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_workshops_list,container,false);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        getLocation();
        SharedViewModel = new ViewModelProvider(requireActivity()).get(WorkshopsSharedViewModel.class);
        minimumRating=SharedViewModel.getMinimumRating();
        selectedServices=SharedViewModel.getSelectedServices();
        selectedDays= SharedViewModel.getSelectedDays();
        name=SharedViewModel.getName();
        workprice=SharedViewModel.getWorkprice();
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);




        workshopIdList = new ArrayList<>();
        workshopList = new ArrayList<>();
        sortedWorkshops = new ArrayList<>();

        workshopsList = (RecyclerView) Root.findViewById(R.id.workshop_list);
        workshopsList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new WorkshopsListViewAdapter(workshopList,workshopIdList);
        workshopsList.setAdapter(adapter);

        List<Query> workshopQuerys = new ArrayList<>();
        CollectionReference workshopsRef = db.collection("workshops");
        Query queryFinal = workshopsRef;
        if(!(name==null || name.equals(""))){
            Query tempQuery = workshopsRef.whereArrayContains("keywords",name.toLowerCase());
                    //whereGreaterThanOrEqualTo("name",name).whereLessThanOrEqualTo("name",name+"\uF7FF");
            workshopQuerys.add(tempQuery);
        }
        if(workprice!=0) {
            Query tempQuery = workshopsRef.whereLessThanOrEqualTo("workPrice", workprice);
            workshopQuerys.add(tempQuery);
        }
        if(selectedDays.size()!=0){
            Query tempQuery = workshopsRef;
            for(String s : selectedDays){
                tempQuery = tempQuery.whereEqualTo("openDays."+s,true);
            }
            workshopQuerys.add(tempQuery);
        }
        if(selectedServices.size()!=0){
            Query tempQuery = workshopsRef;
            for(String s : selectedServices){
                tempQuery = tempQuery.whereEqualTo("services."+s,true);
            }
            workshopQuerys.add(tempQuery);
        }
        if(minimumRating!=0){
            Query tempQuery = workshopsRef.whereGreaterThanOrEqualTo("avgRating",minimumRating);
            workshopQuerys.add(tempQuery);
        }
        if(workshopQuerys.size()==0){
        queryFinal.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document :task.getResult()){
                        workshopList.add(document.toObject(Workshop.class));
                        workshopIdList.add(document.getId());
                        adapter.notifyDataSetChanged();
                        workshopsList.smoothScrollToPosition(0);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);

                    }
                    for(int i=0;i<workshopList.size();i++){
                        sortedWorkshops.add(new WorkshopWithID(workshopList.get(i),workshopIdList.get(i)));
                    }
                }
            }
        });
        }
        else{
            List<Task<QuerySnapshot>> taskList = new ArrayList<>();
            for(Query q :workshopQuerys){
                Task<QuerySnapshot> tempTask = q.get();
                taskList.add(tempTask);
            }
            Task<QuerySnapshot>[] TaskArray = new Task[taskList.size()];
            taskList.toArray(TaskArray);
            Task completedTasks = Tasks.whenAllSuccess(TaskArray).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                @Override
                public void onSuccess(List<Object> objects) {
                    List<Workshop> queryWorkshops = new ArrayList<>();
                    List<String> queryWorkshopsId = new ArrayList<>();
                    List<Workshop> recoveredWorkshops = new ArrayList<>();
                    List<String> recoveredWorkshopsId = new ArrayList<>();
                    Boolean firstArray = true;
                    for(Object o:objects){
                        QuerySnapshot snapshot = (QuerySnapshot)o;
                        for(QueryDocumentSnapshot documentSnapshot:snapshot){
                            Workshop workshop = documentSnapshot.toObject(Workshop.class);
                            if(firstArray) {
                                queryWorkshops.add(workshop);
                                queryWorkshopsId.add(documentSnapshot.getId());

                            }
                            else if(recoveredWorkshops.contains(workshop)) {
                                queryWorkshops.add(workshop);
                                queryWorkshopsId.add(documentSnapshot.getId());
                            }
                        }
                        firstArray=false;
                        recoveredWorkshops.clear();
                        recoveredWorkshopsId.clear();
                        recoveredWorkshops.addAll(queryWorkshops);
                        recoveredWorkshopsId.addAll(queryWorkshopsId);
                        queryWorkshops = new ArrayList<>();
                        queryWorkshopsId = new ArrayList<>();
                    }
                    workshopList.addAll(recoveredWorkshops);
                    workshopIdList.addAll(recoveredWorkshopsId);
                    adapter.notifyDataSetChanged();
                    workshopsList.smoothScrollToPosition(0);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    for(int i=0;i<workshopList.size();i++){
                        sortedWorkshops.add(new WorkshopWithID(workshopList.get(i),workshopIdList.get(i)));
                    }
                }
            });
        }
        setHasOptionsMenu(true);
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

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_workshop_sort, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location_rising:
                SortLocationRising();
                return true;
            case R.id.location_falling:
                SortLocationFalling();
                return true;
            case R.id.workprice_rising:
                SortPriceRising();
                return true;
            case R.id.workprice_falling:
                SortPriceFalling();
                return true;
            case R.id.rating_rising:
                SortRatingRising();
                        return true;
            case R.id.rating_falling:
                SortRatingFalling();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SortRatingFalling() {
        for(int i=0;i<sortedWorkshops.size()-1;i++){
            for(int j=i+1;j<sortedWorkshops.size();j++){
                if(sortedWorkshops.get(i).getW().getAvgRating()<sortedWorkshops.get(j).getW().getAvgRating()){
                    WorkshopWithID temp = sortedWorkshops.get(i);
                    sortedWorkshops.set(i,sortedWorkshops.get(j));
                    sortedWorkshops.set(j,temp);
                }
            }
        }
        List<Workshop> newWorkshops = new ArrayList<>();
        List<String> newIds = new ArrayList<>();
        for(WorkshopWithID w:sortedWorkshops){
            newWorkshops.add(w.getW());
            newIds.add(w.getId());
        }
        workshopList.clear();
        workshopList.addAll(newWorkshops);
        workshopIdList.clear();
        workshopIdList.addAll(newIds);
        adapter.notifyDataSetChanged();
        workshopsList.smoothScrollToPosition(0);
    }

    private void SortRatingRising() {
        for(int i=0;i<sortedWorkshops.size()-1;i++){
            for(int j=i+1;j<sortedWorkshops.size();j++){
                if(sortedWorkshops.get(i).getW().getAvgRating()>sortedWorkshops.get(j).getW().getAvgRating()){
                    WorkshopWithID temp = sortedWorkshops.get(i);
                    sortedWorkshops.set(i,sortedWorkshops.get(j));
                    sortedWorkshops.set(j,temp);
                }
            }
        }
        List<Workshop> newWorkshops = new ArrayList<>();
        List<String> newIds = new ArrayList<>();
        for(WorkshopWithID w:sortedWorkshops){
            newWorkshops.add(w.getW());
            newIds.add(w.getId());
        }
        workshopList.clear();
        workshopList.addAll(newWorkshops);
        workshopIdList.clear();
        workshopIdList.addAll(newIds);
        adapter.notifyDataSetChanged();
        workshopsList.smoothScrollToPosition(0);
    }

    private void SortLocationRising() {
        if(loc==null) {
            getLocation();
            Toast.makeText(getActivity(),"Vasa lokacija trenutno nije dosnupna, pokusajte ponovo",Toast.LENGTH_SHORT).show();
        }
        else{
            List<float[]> results = new ArrayList<>();
            for(int i=0;i<sortedWorkshops.size();i++){
                float[] resultsi = new float[3];
                LatLng lli = sortedWorkshops.get(i).getW().getLocation();
                Location.distanceBetween(lli.getLatitude(),lli.getLongitude(),loc.getLatitude(),loc.getLongitude(),resultsi);
                results.add(resultsi);
            }
            for(int i=0;i<sortedWorkshops.size()-1;i++){
                for(int j=i+1;j<sortedWorkshops.size();j++){
                    if(results.get(i)[0]<results.get(j)[0]){
                        WorkshopWithID temp = sortedWorkshops.get(i);
                        sortedWorkshops.set(i,sortedWorkshops.get(j));
                        sortedWorkshops.set(j,temp);
                    }
                }
            }
            List<Workshop> newWorkshops = new ArrayList<>();
            List<String> newIds = new ArrayList<>();
            for(WorkshopWithID w:sortedWorkshops){
                newWorkshops.add(w.getW());
                newIds.add(w.getId());
            }
            workshopList.clear();
            workshopList.addAll(newWorkshops);
            workshopIdList.clear();
            workshopIdList.addAll(newIds);
            adapter.notifyDataSetChanged();
            workshopsList.smoothScrollToPosition(0);
        }


    }

    private void SortLocationFalling() {
        if(loc==null) {
            getLocation();
            Toast.makeText(getActivity(),"Vasa lokacija trenutno nije dosnupna, pokusajte ponovo",Toast.LENGTH_SHORT).show();
        }
        else{
            List<float[]> results = new ArrayList<>();
            for(int i=0;i<sortedWorkshops.size();i++){
                float[] resultsi = new float[3];
                LatLng lli = sortedWorkshops.get(i).getW().getLocation();
                Location.distanceBetween(lli.getLatitude(),lli.getLongitude(),loc.getLatitude(),loc.getLongitude(),resultsi);
                results.add(resultsi);
            }
            for(int i=0;i<sortedWorkshops.size()-1;i++){
                for(int j=i+1;j<sortedWorkshops.size();j++){
                    if(results.get(i)[0]>results.get(j)[0]){
                        WorkshopWithID temp = sortedWorkshops.get(i);
                        sortedWorkshops.set(i,sortedWorkshops.get(j));
                        sortedWorkshops.set(j,temp);
                    }
                }
            }
            List<Workshop> newWorkshops = new ArrayList<>();
            List<String> newIds = new ArrayList<>();
            for(WorkshopWithID w:sortedWorkshops){
                newWorkshops.add(w.getW());
                newIds.add(w.getId());
            }
            workshopList.clear();
            workshopList.addAll(newWorkshops);
            workshopIdList.clear();
            workshopIdList.addAll(newIds);
            adapter.notifyDataSetChanged();
            workshopsList.smoothScrollToPosition(0);
        }


    }

    private void SortPriceRising(){
        for(int i=0;i<sortedWorkshops.size()-1;i++){
            for(int j=i+1;j<sortedWorkshops.size();j++){
                if(sortedWorkshops.get(i).getW().getWorkPrice()>sortedWorkshops.get(j).getW().getWorkPrice()){
                    WorkshopWithID temp = sortedWorkshops.get(i);
                    sortedWorkshops.set(i,sortedWorkshops.get(j));
                    sortedWorkshops.set(j,temp);
                }
            }
        }
        List<Workshop> newWorkshops = new ArrayList<>();
        List<String> newIds = new ArrayList<>();
        for(WorkshopWithID w:sortedWorkshops){
            newWorkshops.add(w.getW());
            newIds.add(w.getId());
        }
        workshopList.clear();
        workshopList.addAll(newWorkshops);
        workshopIdList.clear();
        workshopIdList.addAll(newIds);
        adapter.notifyDataSetChanged();
        workshopsList.smoothScrollToPosition(0);
    }
    private void SortPriceFalling(){
        for(int i=0;i<sortedWorkshops.size()-1;i++){
            for(int j=i+1;j<sortedWorkshops.size();j++){
                if(sortedWorkshops.get(i).getW().getWorkPrice()<sortedWorkshops.get(j).getW().getWorkPrice()){
                    WorkshopWithID temp = sortedWorkshops.get(i);
                    sortedWorkshops.set(i,sortedWorkshops.get(j));
                    sortedWorkshops.set(j,temp);
                }
            }
        }
        List<Workshop> newWorkshops = new ArrayList<>();
        List<String> newIds = new ArrayList<>();
        for(WorkshopWithID w:sortedWorkshops){
            newWorkshops.add(w.getW());
            newIds.add(w.getId());
        }
        workshopList.clear();
        workshopList.addAll(newWorkshops);
        workshopIdList.clear();
        workshopIdList.addAll(newIds);
        adapter.notifyDataSetChanged();
        workshopsList.smoothScrollToPosition(0);
    }


    private void getLocation(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(loc!=null){
                return;
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
                        loc=location;
                    }
                    else{

                        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(100)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                               loc=location;
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
