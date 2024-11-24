package com.run.demo2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends AndroidViewModel
//        ViewModel
{

    private final MutableLiveData<Boolean> socketStatus = new MutableLiveData<>(false);
    private final MutableLiveData<Pair<Boolean, String>> message = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
    }
    // Getter for LiveData
    public LiveData<Boolean> getSocketStatus() {
        return socketStatus;
    }

    public LiveData<Pair<Boolean, String>> getMessage() {
        return message;
    }

    // Method to update the message safely
    public void updateMessage(Pair<Boolean, String> newMessage) {
        message.postValue(newMessage); // Use postValue for background thread
    }

    public void setStatus(Boolean status) {
        socketStatus.postValue(status);
    }
}
