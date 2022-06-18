package com.example.bankclientmvvm.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.bankclientmvvm.R;
import com.example.bankclientmvvm.register.RegisterActivity;
import com.example.bankclientmvvm.account.AccountActivity;
import com.example.bankclientmvvm.databinding.ActivityLoginBinding;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity implements ContractLogin.LoginView{

    private SharedPreferences sharedPreferences;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginViewModel = new LoginViewModel(this);
        activityLoginBinding.setLoginViewModel(loginViewModel);

        createUUID();

        String accoundID = getAccountIDSharePref();
        String uuid = getUUIDSharePref();
        if(accoundID != null && uuid != null){
            loginViewModel.sendAutoLogin(accoundID, uuid);
        }
    }

    @Override
    public void getToast(String notification) {

    }

    @Override
    public void saveAccountIDSharePref(String accountID) {
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("accountID",accountID);
        edit.commit();
        sharedPreferences = null;
    }

    @Override
    public void changeToAccountActivity() {
        Intent it = new Intent(LoginActivity.this, AccountActivity.class);
        startActivity(it);
        Log.v("LoginActivity","Changed to AccountActivity");
        finish();
    }

    public void changeToRegisterActivity(View view) {
        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(it);
        Log.e("BankClient","Changed to RegisterActivity");
        finish();
    }

    @Override
    public String getUUIDSharePref() {
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        String uuid = sharedPreferences.getString("UUID", null);
        sharedPreferences = null;
        return uuid;
    }

    @Override
    public String getAccountIDSharePref() {
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        String accountID = sharedPreferences.getString("accountID", null);
        sharedPreferences = null;
        return accountID;
    }

    private void createUUID(){
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        //This method only run a times
        if (!sharedPreferences.getBoolean("FrstTime", false)) {
            // Do update you want here
            String uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("FrstTime", true);
            edit.putString("UUID",uuid);
            edit.commit();
        }
        sharedPreferences = null;
    }


}