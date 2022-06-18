package com.example.bankclientmvvm.login;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.bankclientmvvm.Account;
import com.example.bankclientmvvm.api.ApiService;
import com.example.bankclientmvvm.BR;
import com.example.bankclientmvvm.api.EmptyCallback;
import com.example.bankclientmvvm.NetworkImpl;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginViewModel extends BaseObservable{

    private String accountID, password;
    private boolean flagShowHide;

    public ObservableField<String> statusLogin = new ObservableField<>();

    private final ContractLogin.LoginView mainLoginView;
    private final NetworkImpl modelNetwork;
    Account account;

    public LoginViewModel(ContractLogin.LoginView mainLoginView) {
        this.mainLoginView = mainLoginView;
        modelNetwork = new NetworkImpl();
        account = new Account();
        flagShowHide = true;

    }

    @Bindable
    public boolean getFlagShowHide(){
        return flagShowHide;
    }

    public void setFlagShowHide(){
        if(this.flagShowHide) {
            this.flagShowHide = false;
        }else{
            this.flagShowHide = true;
        }
        notifyPropertyChanged(BR.flagShowHide);
    }

    @Bindable
    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
        account.setAccountID(accountID);
        notifyPropertyChanged(BR.accountID);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void Login(){
        if(password.length() < 6){
            statusLogin.set("AccountID and password invalid");
            return;
        }
        //Call api get password
        ApiService.apiService.login(getAccountID()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String passbcrypt = response.body();
                if (passbcrypt != null) {
                    Log.e("user", passbcrypt);
                    boolean valuate = BCrypt.checkpw(password, passbcrypt);
                    //If successful check then call api autologin and change Activity, otherwise set label incorrect password
                    if (valuate) {
                        Log.e("Login","LoginSuccess");
                        ArrayList<String> accountStrArrList = new ArrayList<>();
                        accountStrArrList.add(getAccountID());
                        accountStrArrList.add(mainLoginView.getUUIDSharePref());
                        //Call api create autologin
                        ApiService.apiService.loginSuccess(accountStrArrList).enqueue(new EmptyCallback());
                        mainLoginView.saveAccountIDSharePref(getAccountID());
                        //Change to AccountActivity here
                        mainLoginView.changeToAccountActivity();
                    } else {
                        statusLogin.set("AccountID and Password incorrect");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Login", String.valueOf(t));
            }
        });
    }

    public void sendAutoLogin(String accountID, String uuid){
        ArrayList<String> accountStrArrList = new ArrayList<>();
        accountStrArrList.add(accountID);
        accountStrArrList.add(uuid);
        ApiService.apiService.autologin(accountStrArrList).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String messRecv = response.body();
                if(messRecv != null){
                    Log.e("Login", messRecv);
                    if(messRecv.equals("AutoLoginSuccess")) {
                        //Change to AccountActivity here
                        mainLoginView.changeToAccountActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Login", String.valueOf(t));
            }
        });

    }
}
