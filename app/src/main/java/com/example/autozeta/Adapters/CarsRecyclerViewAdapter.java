package com.example.autozeta.Adapters;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.autozeta.Basic.UI.cars.CarsFragment;
import com.example.autozeta.Basic.UI.cars.CarsSharedViewModel;
import com.example.autozeta.R;

import java.util.List;

import data.Car;

public class CarsRecyclerViewAdapter extends RecyclerView.Adapter<CarsRecyclerViewAdapter.ViewHolder> {

    private final List<Car> carList;
    private final List<String> carIDs;
    private CarsSharedViewModel SharedViewModel;
    private NavController navController;

    public CarsRecyclerViewAdapter(List<Car> items, List<String> carIDs, CarsSharedViewModel svm, NavController nc) {
        this.navController=nc;
        carList = items;
        this.carIDs=carIDs;
        this.SharedViewModel=svm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.carInfo.setText(car.getMake()+" " + car.getModel()+", Godiste: "+String.valueOf(car.getYear()));
        holder.engine.setText(car.getEngine()+ ", "+ car.getPower() +" " +car.getPowerType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedViewModel.setCarId(carIDs.get(position));
                navController.navigate(R.id.action_carsFragment_to_carInfoFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView carInfo,engine;

        public ViewHolder(View view) {
            super(view);
            carInfo = view.findViewById(R.id.carInfo);
            engine = view.findViewById(R.id.engine);
        }
    }
}