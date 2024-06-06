package com.example.managebudget.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.managebudget.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTransactionIncome extends DialogFragment
{
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ImageButton closeBt;
    EditText TransactionAmountEt;
    Spinner TransactionCategorySpinner;

    FloatingActionButton BudgetCreateBt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_transaction_income, container, false);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        closeBt = rootView.findViewById(R.id.closeBt);
        TransactionAmountEt = rootView.findViewById(R.id.TransactionAmountEt);
        TransactionCategorySpinner = rootView.findViewById(R.id.TransactionCategorySpinner);
        BudgetCreateBt = rootView.findViewById(R.id.BudgetCreateBt);

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dismiss();}});

        BudgetCreateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = currentUser.getUid();
                String amount = TransactionAmountEt.getText().toString();
                String category = TransactionCategorySpinner.toString();
                String date = getCurrentDateTime();


                if (!amount.trim().isEmpty())
                {

                }
            }
        });




        return rootView;
    }
    String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
