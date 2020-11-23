package com.theappIdea.practical.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.theappIdea.practical.R;
import com.theappIdea.practical.adapter.UserAdapter;
import com.theappIdea.practical.databinding.ActivityMainBinding;
import com.theappIdea.practical.model.User;
import com.theappIdea.practical.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserItemClick {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initview();
    }

    private void initview() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        initRecyclerview();


        userViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList = users;
                userAdapter.setUserList(userList);
            }
        });
        userViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList = users;
                userAdapter.setUserList(userList);
                if (userList.isEmpty()) {
                    binding.txtEmpty.setVisibility(View.VISIBLE);
                    binding.rvUser.setVisibility(View.GONE);
                } else {
                    binding.txtEmpty.setVisibility(View.GONE);
                    binding.rvUser.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRecyclerview() {
        userAdapter = new UserAdapter(this);
        userAdapter.setOnUserItemClick(this);
        userAdapter.setUserList(userList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvUser.setLayoutManager(layoutManager);
        binding.rvUser.setHasFixedSize(true);
        binding.rvUser.setAdapter(userAdapter);
    }

    @Override
    public void onUserItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        intent.putExtra("data", (Parcelable) userList.get(position));
        startActivity(intent);
    }
}