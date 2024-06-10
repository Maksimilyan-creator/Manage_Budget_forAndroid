package com.example.managebudget.users;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.managebudget.budget.Budget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersViewModel extends ViewModel {
    private final MutableLiveData<List<Users>> users = new MutableLiveData<>();

    public LiveData<List<Users>> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users.setValue(users);
    }

    public void loadUsersData(DatabaseReference databaseReference, String currentUserId) {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Users> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (!userSnapshot.getKey().equals(currentUserId)) {
                        String userId = userSnapshot.getKey();
                        String username = userSnapshot.child("username").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String profileImage = userSnapshot.child("profileImage").getValue(String.class);

                        if (userId != null && username != null && userEmail != null && profileImage != null) {
                            usersList.add(new Users(userId, username, userEmail, profileImage));
                        } else {
                            Log.e("UsersViewModel", "Ошибка: Неверные данные пользователя: " + userId);
                        }
                    }
                }
                setUsers(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UsersViewModel", "Ошибка загрузки пользователей: " + error.getMessage());
            }
        });
    }
}


