package com.example.managebudget.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.managebudget.LoginActivity;
import com.example.managebudget.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileFragment extends DialogFragment {
    private UserViewModel userViewModel;
    FirebaseDatabase database;

    private FirebaseUser userId;
    private EditText nameEditText;
    private EditText emailEditText;
    private FloatingActionButton saveButton;
    private ImageButton closeButton;
    private User currentUser;
    private Context mContext; // Переменная для доступа к контексту

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        userId = FirebaseAuth.getInstance().getCurrentUser();
        nameEditText = rootView.findViewById(R.id.editTextName);
        emailEditText = rootView.findViewById(R.id.editTextEmail);
        saveButton = rootView.findViewById(R.id.buttonAdd);
        closeButton = rootView.findViewById(R.id.closeBtt);

        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    nameEditText.setText(user.getUsername());
                    emailEditText.setText(user.getUserEmail());
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString();
                String newEmail = emailEditText.getText().toString();

                currentUser = userViewModel.getUserLiveData().getValue();

                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(mContext, "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                } else {
                    if (!newName.equals(currentUser.getUsername())) {
                        // Если изменилось
                        updateUserName(newName);
                    }

                    if (!newEmail.equals(currentUser.getUserEmail())) {
                        // Если изменилось
                        try {
                            updateEmail(newEmail);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("EditProfileFragment", "Произошла ошибка: " + ex.getMessage());
                            Toast.makeText(mContext, "Произошла ошибка" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    dismiss();
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void updateUserName(String newName) {
        DatabaseReference userRef = database.getReference("users").child(userId.getUid());
        userRef.child("username").setValue(newName);
    }

    private void updateEmail(String newEmail) {
        if (isAdded()) {
            if (userId == null) {
                Log.e("MyApp", "FirebaseUser is null");
                return;
            }

            userId.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendEmailVerification(userId, newEmail);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(mContext, "Пользователь с таким адресом электронной почты уже существует", Toast.LENGTH_SHORT).show();
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(mContext, "Некорректный формат адреса электронной почты", Toast.LENGTH_SHORT).show();
                        } else if (exception instanceof FirebaseAuthRecentLoginRequiredException) {
                            // Требуется повторная аутентификация пользователя
                            FirebaseAuth.getInstance().signOut();
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        } else {
                            // Обработка других исключений
                            Toast.makeText(mContext, "Ошибка при обновлении почты: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("MyApp", "Ошибка при обновлении почты: " + exception.getMessage(), exception);
                        }
                    }
                }
            });
        } else {
            Log.e("EditProfileFragment", "Фрагмент не привязан к активности");
        }
    }

    private void sendEmailVerification(FirebaseUser firebaseUser, String newEmail) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Письмо с подтверждением отправлено на вашу электронную почту", Toast.LENGTH_SHORT).show();
                    DatabaseReference userRef = database.getReference("users").child(firebaseUser.getUid());
                    userRef.child("email").setValue(newEmail);
                } else {
                    Toast.makeText(mContext, "Ошибка при отправке письма с подтверждением: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MyApp", "Ошибка при отправке письма с подтверждением: " + task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}

