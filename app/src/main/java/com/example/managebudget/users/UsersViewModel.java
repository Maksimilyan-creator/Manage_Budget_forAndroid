package com.example.managebudget.users;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.managebudget.budget.Budget;

import java.util.List;


public class UsersViewModel extends AndroidViewModel
{
    private MutableLiveData<List<Users>> usersLiveData = new MutableLiveData<>();

    public UsersViewModel(Application application) {super(application);}

    public LiveData<List<Users>> getUsersLiveData()
    {
        return usersLiveData;
    }

    public void setUsers(List<Users> usersList)
    {
        usersLiveData.setValue(usersList);
    }
}
