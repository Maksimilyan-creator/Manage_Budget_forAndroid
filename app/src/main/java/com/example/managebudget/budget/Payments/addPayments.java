package com.example.managebudget.budget.Payments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.budget.Debt.Debt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class addPayments extends DialogFragment
{
    ImageView closeBt;
    EditText AmountET;
    EditText DateET;
    ImageView saveBt;
    ImageView updateBt;
    RecyclerView RecyclerViewPayments;
    TextView Amount;
    TextView PaymentAmount;
    Debt debt;
    int position;
    BudgetViewModel budgetViewModel;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser currentUser;
    Budget budget;
    PaymentAdapter paymentAdapter;
    Debt selectedDebt;
    List<Payments> paymentsList;

    public addPayments(int position, Debt selectedDebt)
    {
        this.position = position;
        this.selectedDebt = selectedDebt;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.payments, container, false);

        closeBt = rootView.findViewById(R.id.closeBt);
        AmountET = rootView.findViewById(R.id.AmountET);
        DateET = rootView.findViewById(R.id.DateET);
        DateET.setText(getCurrentDateTime());
        saveBt = rootView.findViewById(R.id.saveBt);
        updateBt = rootView.findViewById(R.id.updateBt);
        RecyclerViewPayments = rootView.findViewById(R.id.RecyclerViewPayments);
        Amount = rootView.findViewById(R.id.Amount);
        Amount.setText("0 ₽");
        PaymentAmount = rootView.findViewById(R.id.PaymentAmount);
        PaymentAmount.setText("0 ₽");
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerViewPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewPayments.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        paymentAdapter = new PaymentAdapter(getContext(), new ArrayList<>(), usersRef);
        RecyclerViewPayments.setAdapter(paymentAdapter);


        budgetViewModel.getSelectedBudget().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget_)
            {
                if(budget_ != null && selectedDebt != null && position >=0 )
                {
                    budget = budget_;
                    if (selectedDebt != null)
                    {
                        Amount.setText(String.valueOf(selectedDebt.getAmount() + " ₽"));
                        paymentsList = selectedDebt.getPayments();

                        if (paymentsList != null)
                        {
                            double paymentTotalAmount = paymentsList.stream().mapToDouble(Payments::getAmount).sum();
                            PaymentAmount.setText(String.valueOf(paymentTotalAmount + " ₽"));
                            paymentAdapter.UpdateAdapter(paymentsList);
                        }
                        else
                        {
                            paymentAdapter.UpdateAdapter(new ArrayList<>());
                        }
                    }

                }
            }
        });

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss(); }});

        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { AddPayment(); }});

        updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  }});

        return rootView;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void AddPayment()
    {
        String PaymentAmount = AmountET.getText().toString();
        String PaymentDate = DateET.getText().toString();
        String PayerId = currentUser.getUid();

        if (!PaymentAmount.trim().isEmpty() && !PaymentDate.trim().isEmpty() && !PayerId.trim().isEmpty())
        {
            Payments payment = new Payments(PayerId, Double.parseDouble(PaymentAmount), PaymentDate);
            if (paymentsList != null)
            {
                paymentsList.add(payment);
            }
            else
            {
                paymentsList = new ArrayList<>();
                paymentsList.add(payment);
            }

            double paymentTotalAmount = paymentsList.stream().mapToDouble(Payments::getAmount).sum();

            double summ = selectedDebt.getAmount() - paymentTotalAmount;

            if (summ < 0)
            {
                Toast.makeText(getContext(), "Слишком много", Toast.LENGTH_SHORT).show();

            }
            if (summ >= 0)
            {
                DatabaseReference budgetRef = database.getReference("budget").child(budget.getId());
                budgetRef.child("debts").child(String.valueOf(position)).child("payments").setValue(paymentsList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Оплата добавлена", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Ошибка добавление оплаты: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
        {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        }

    }
}
