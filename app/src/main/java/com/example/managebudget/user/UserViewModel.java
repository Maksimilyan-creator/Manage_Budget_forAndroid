package com.example.managebudget.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel
{
    private MutableLiveData<User> userLiveData;

    public UserViewModel()
    {
        userLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData()
    {
        return userLiveData;
    }

    public void setUser(User user)
    {
        userLiveData.setValue(user);
    }
}
