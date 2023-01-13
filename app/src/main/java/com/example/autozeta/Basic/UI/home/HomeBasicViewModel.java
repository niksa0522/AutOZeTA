package com.example.autozeta.Basic.UI.home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeBasicViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeBasicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is basic fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

