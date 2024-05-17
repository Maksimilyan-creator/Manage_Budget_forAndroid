package com.example.managebudget.bottomnav;

import static com.bumptech.glide.Glide.*;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managebudget.LoginActivity;
import com.example.managebudget.R;
import com.example.managebudget.budget.Budget;
import com.example.managebudget.budget.BudgetDetailsFragment;
import com.example.managebudget.budget.BudgetViewModel;
import com.example.managebudget.budget.BudgetsAdapter;
import com.example.managebudget.budget.CreateBudgetFragment;
import com.example.managebudget.user.EditProfileFragment;
import com.example.managebudget.user.User;
import com.example.managebudget.user.UserViewModel;
import com.example.managebudget.users.Users;
import com.example.managebudget.users.UsersAdapter;
import com.example.managebudget.users.UsersViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment
{
    FirebaseDatabase database;
    FirebaseStorage storage;
    UserViewModel userViewModel;
    BudgetViewModel budgetViewModel;
    ListView BudgetListView;
    BudgetsAdapter budgetsAdapter;
    TextView username_tv;
    TextView userEmail_tv;

    CircleImageView profile_image_view;
    ImageButton logout_btn;
    ImageButton editProfile_btn;
    Uri filePath;
    FloatingActionButton addBudget;
    DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        username_tv = rootView.findViewById(R.id.username_tv);
        userEmail_tv = rootView.findViewById(R.id.userEmail_tv);
        profile_image_view = rootView.findViewById(R.id.profile_image_view);
        logout_btn = rootView.findViewById(R.id.logout_btn);
        editProfile_btn = rootView.findViewById(R.id.editProfile_btn);
        database = FirebaseDatabase.getInstance("https://manage-budget-41977-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        storage = FirebaseStorage.getInstance("gs://manage-budget-41977.appspot.com");
        addBudget = rootView.findViewById(R.id.addBudgerBt);
        BudgetListView = rootView.findViewById(R.id.budget_listView);

        // Получаем данные из ViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        // Наблюдение за изменениями
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user)
            {
                if (user !=null)
                {
                    username_tv.setText(user.getUsername());
                    userEmail_tv.setText(user.getUserEmail());
                    Glide.with(requireContext()).load(user.getProfileImage()).into(profile_image_view);
                }
            }
        });

        budgetsAdapter = new BudgetsAdapter(new ArrayList<>(), usersRef, requireContext());
        BudgetListView.setAdapter(budgetsAdapter);

        BudgetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Budget budget = (Budget) budgetsAdapter.getItem(position);
                openDetailsBudget(view, budget, usersRef, database);
            }
        });

        budgetViewModel.getBudgets().observe(getViewLifecycleOwner(), new Observer<List<Budget>>() {
            @Override
            public void onChanged(List<Budget> budgets) {
                budgetsAdapter.updateBudgets(budgets);
            }
        });

        profile_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        editProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openEditProfile(); }
        });

        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateBudget();
            }
        });




        return rootView;
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() !=null)
                            {
                                filePath = result.getData().getData();

                                try
                                {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                            requireContext().getContentResolver(), filePath
                                    );

                                    profile_image_view.setImageBitmap(bitmap);
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                                uploadImage();
                            }

                        }
                    });
    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);

    }

    private void uploadImage()
    {
        if (filePath != null)
        {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            
            storage.getReference().child("images/" + uid)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Фото загрузилось успешно", Toast.LENGTH_SHORT).show();

                            storage.getReference().child("images/" + uid).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("profileImage").setValue(uri.toString());
                                        }
                                    });
                        }
                    });
        }
    }



    private void openCreateBudget()
    {
        CreateBudgetFragment createBudgetFragment = new CreateBudgetFragment();
        createBudgetFragment.show(getParentFragmentManager(), "CreateBudgetFragment");
    }

    private void openEditProfile()
    {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.show(getParentFragmentManager(), "EditProfileFragment");
    }
        private void openDetailsBudget(View v, Budget budget, DatabaseReference usersRef,  FirebaseDatabase database)
        {
            final Dialog dialog = new Dialog(v.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.fragment_budget_details);

            UserViewModel userViewModel1;

            final ImageButton closeBt = dialog.findViewById(R.id.closeBt);
            final ImageButton Save_edit_details_budget_bt = dialog.findViewById(R.id.Save_edit_details_budget_bt);
            final ListView participatn_listView = dialog.findViewById(R.id.participatn_listView);
            final FloatingActionButton add_participath = dialog.findViewById(R.id.add_participath);
            final EditText budget_nameEt = dialog.findViewById(R.id.text_budget_name);
            final EditText budget_description = dialog.findViewById(R.id.text_budget_description);
            final TextView budget_creator = dialog.findViewById(R.id.text_budget_creator);
            final TextView budget_creator_Email = dialog.findViewById(R.id.text_budget_creator_Email);

            budget_nameEt.setText(budget.getName());
            budget_description.setText(budget.getDesription());
            String budgetId = budget.getId();

            userViewModel1 = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
            userViewModel1.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user)
                {
                    if (user !=null)
                    {
                        budget_creator.setText(user.getUsername());
                        budget_creator_Email.setText(user.getUserEmail());

                    }
                }
            });

            closeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {dialog.dismiss();}
            });

            Save_edit_details_budget_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameBudget = budget.getName();
                    String descriptionBudget = budget.getDesription();
                    String newNameBudget = budget_nameEt.getText().toString();
                    String newDescriptionBudget = budget_description.getText().toString();
                    SaveDetails(nameBudget, descriptionBudget,newNameBudget, newDescriptionBudget, budgetId, database);
                }
            });

            add_participath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddPaticipath(v, usersRef, database);
                }
            });

            dialog.show();
        }

        private void openAddPaticipath(View v, DatabaseReference usersRef,  FirebaseDatabase databas)
        {
            final Dialog dialog = new Dialog(v.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.fragment_all_users);

            ListView usersListView = dialog.findViewById(R.id.users_rv);
            ImageButton closeBt = dialog.findViewById(R.id.closeBt_users);

            UsersViewModel usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
            UsersAdapter usersAdapter = new UsersAdapter(requireContext(), new ArrayList<>());
            usersListView.setAdapter(usersAdapter);

            usersViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
                @Override
                public void onChanged(List<Users> users) {
                    usersAdapter.clear();
                    usersAdapter.addAll(users);
                    usersAdapter.notifyDataSetChanged();
                }
            });

            closeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    private void SaveDetails(String nameBudget, String descriptionBudget, String newNameBudget, String newDescriptionBudget, String budgetId, FirebaseDatabase database) {
        if (newNameBudget.isEmpty() || newDescriptionBudget.isEmpty()) {
            Toast.makeText(getContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference budgetRef = database.getReference("budget");
            Query query = budgetRef.orderByChild("id").equalTo(budgetId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Toast.makeText(getContext(), "Подождите", Toast.LENGTH_SHORT).show();
                        String key = dataSnapshot.getKey();
                        DatabaseReference childRef = budgetRef.child(key);

                        if (!newNameBudget.equals(nameBudget)) {
                            childRef.child("name").setValue(newNameBudget);
                        }

                        if (!newDescriptionBudget.equals(descriptionBudget)) {
                            childRef.child("desription").setValue(newDescriptionBudget);
                        }
                        Toast.makeText(getContext(), "обновлено",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {Toast.makeText(getContext(), "Не удалось обновить данные: " + error.getMessage(), Toast.LENGTH_SHORT).show();}});
        }
    }

}
