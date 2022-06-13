package com.example.bankclientmvvm.login;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.bankclientmvvm.Account;
import com.example.bankclientmvvm.BR;
import com.example.bankclientmvvm.NetworkImpl;

import org.mindrot.jbcrypt.BCrypt;


public class LoginViewModel extends BaseObservable{

    private String accountID, password, ipAddress;
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

    @Bindable
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        notifyPropertyChanged(BR.ipAddress);
    }

    public void Login(){
        if(password.length() < 6){
            statusLogin.set("AccountID and password invalid");
            return;
        }
//        String ipAddress;
        modelNetwork.setIPAddress(ipAddress);
        modelNetwork.createConnect();
        modelNetwork.sendDataTCP("login"+ "#" + account.getAccountID() + "#" + mainLoginView.getUUIDSharePref());
        modelNetwork.readDataTCP();
        String mesRecv = modelNetwork.getMesFromServer();
        if(!mesRecv.equals("")) {
            boolean valuate = BCrypt.checkpw(password, mesRecv);
            if (valuate) {
                modelNetwork.sendDataTCP("LoginSuccess");
                mainLoginView.saveAccountIDSharePref(accountID);
                //Change activity here
                mainLoginView.changeToAccountActivity();
            } else {
                modelNetwork.sendDataTCP("LoginFailed");
                statusLogin.set("AccountID and Password incorrect");
            }
        }else{
            statusLogin.set("Không thể kết nối đến máy chủ");
        }
    }

    public void sendAutoLogin(){
        modelNetwork.setIPAddress(ipAddress);
        modelNetwork.createConnect();
        modelNetwork.sendDataTCP("autologin"+ "#" + mainLoginView.getAccountIDSharePref() + "#" + mainLoginView.getUUIDSharePref());
        modelNetwork.readDataTCP();
        String mesRecv;
        mesRecv = modelNetwork.getMesFromServer();
        if(mesRecv.equals("LoginSuccess")) {
            //Change activity here
            mainLoginView.changeToAccountActivity();
        }
    }
}
