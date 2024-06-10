package com.example.managebudget.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> user;

    public UserViewModel() {
        user = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void loadUserData(String userId, DatabaseReference databaseReference) {
        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profileImage = snapshot.child("profileImage").getValue(String.class);
                    String userEmail = snapshot.child("email").getValue(String.class);

                    if (username != null && userEmail != null) {
                        User user = new User(username, userEmail, profileImage);
                        setUser(user);
                    } else {
                        Log.e("UserViewModel", "Ошибка: username или email равны null");
                    }
                } else {
                    Log.e("UserViewModel", "Пользователь не найден");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserViewModel", "Ошибка загрузки данных пользователя: " + error.getMessage());
            }
        });
    }
}

