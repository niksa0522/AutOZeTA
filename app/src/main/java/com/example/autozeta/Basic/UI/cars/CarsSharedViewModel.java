package com.example.autozeta.Basic.UI.cars;

import androidx.lifecycle.ViewModel;

public class CarsSharedViewModel extends ViewModel {
    private String carId;

    public CarsSharedViewModel() {

    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
