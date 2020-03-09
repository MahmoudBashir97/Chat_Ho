package com.chatho.chatho.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedModel extends ViewModel {

    public MutableLiveData<String> text=new MutableLiveData<>();

    public void setText(String input){
        text.setValue(input);
    }

    public LiveData<String> getText(){
        return text;
    }
}
