package com.example.bankclientmvvm.register;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.example.bankclientmvvm.Account;
import com.example.bankclientmvvm.BR;
import com.example.bankclientmvvm.api.ApiService;

import org.mindrot.jbcrypt.BCrypt;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends BaseObservable {
    private String regAccountID, regPassword, regConfirmPassword, regFirstName, regLastName, regCountry, regCity, regAddress, regPhoneNumber, regEmail;
    private boolean statusAccountID, statusFirstName, statusLastName, statusPassword, statusConfPassword,
            statusCountry, statusCity, statusAddress, statusPhoneNumber, statusEmail;

    public ObservableField<String> statusRegister;
    public ObservableArrayList<String> citiesArrayList;
    public ObservableArrayList<String> countriessArrayList;
    public ObservableField<Boolean> statusCheckAccountID, displayAccountIDPrgBar, displayEmailPrgBar;

    private final ContractRegister registerActivity;

    public RegisterViewModel(ContractRegister registerActivity) {
        this.registerActivity = registerActivity;
        citiesArrayList = new ObservableArrayList<>();
        countriessArrayList = new ObservableArrayList<>();
        statusRegister = new ObservableField<>();
        statusCheckAccountID = new ObservableField<>();
        displayAccountIDPrgBar = new ObservableField<>();
        displayEmailPrgBar = new ObservableField<>();
    }

    public void setStatusAccountID(boolean statusAccountID) {
        this.statusAccountID = statusAccountID;
        notifyPropertyChanged(BR.statusAccountID);
    }
    public void setStatusFirstName(boolean statusFirstName) {
        this.statusFirstName = statusFirstName;
        notifyPropertyChanged(BR.statusFirstName);
    }
    public void setStatusLastName(boolean statusLastName) {
        this.statusLastName = statusLastName;
        notifyPropertyChanged(BR.statusLastName);
    }
    public void setStatusPassword(boolean statusPassword) {
        this.statusPassword = statusPassword;
        notifyPropertyChanged(BR.statusPassword);
    }
    public void setStatusConfPassword(boolean statusConfPassword) {
        this.statusConfPassword = statusConfPassword;
        notifyPropertyChanged(BR.statusConfPassword);
    }
    public void setStatusCountry(boolean statusCountry) {
        this.statusCountry = statusCountry;
        notifyPropertyChanged(BR.statusCountry);
    }
    public void setStatusCity(boolean statusCity) {
        this.statusCity = statusCity;
        notifyPropertyChanged(BR.statusCity);
    }
    public void setStatusAddress(boolean statusAddress) {
        this.statusAddress = statusAddress;
        notifyPropertyChanged(BR.statusAddress);
    }
    public void setStatusPhoneNumber(boolean statusPhoneNumber) {
        this.statusPhoneNumber = statusPhoneNumber;
        notifyPropertyChanged(BR.statusPhoneNumber);
    }
    public void setStatusEmail(boolean statusEmail) {
        this.statusEmail = statusEmail;
        notifyPropertyChanged(BR.statusEmail);
    }
    @Bindable
    public boolean getStatusAccountID() {
        return statusAccountID;
    }
    @Bindable
    public boolean getStatusFirstName() {
        return statusFirstName;
    }
    @Bindable
    public boolean getStatusLastName() {
        return statusLastName;
    }
    @Bindable
    public boolean getStatusPassword() {
        return statusPassword;
    }
    @Bindable
    public boolean getStatusConfPassword() {
        return statusConfPassword;
    }
    @Bindable
    public boolean getStatusCountry() {
        return statusCountry;
    }
    @Bindable
    public boolean getStatusCity() {
        return statusCity;
    }
    @Bindable
    public boolean getStatusAddress() {
        return statusAddress;
    }
    @Bindable
    public boolean getStatusPhoneNumber() {
        return statusPhoneNumber;
    }
    @Bindable
    public boolean getStatusEmail() {
        return statusEmail;
    }

    @Bindable
    public String getRegCountry() {
        return regCountry;
    }

    public void setRegCountry(String regCountry) {
        setRegCity("");
        this.regCountry = regCountry;
        notifyPropertyChanged(BR.regCountry);
    }

    @Bindable
    public String getRegCity() {
        return regCity;
    }

    public void setRegCity(String regCity) {
        this.regCity = regCity;
        notifyPropertyChanged(BR.regCity);
    }

    @Bindable
    public String getRegAccountID() {
        return regAccountID;
    }

    public void setRegAccountID(String regAccountID) {
        this.regAccountID = regAccountID;
        notifyPropertyChanged(BR.regAccountID);
    }

    @Bindable
    public String getRegPassword() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword = regPassword;
        notifyPropertyChanged(BR.regPassword);
    }

    @Bindable
    public String getRegConfirmPassword() {
        return regConfirmPassword;
    }

    public void setRegConfirmPassword(String regConfirmPassword) {
        this.regConfirmPassword = regConfirmPassword;
        notifyPropertyChanged(BR.regConfirmPassword);
    }

    @Bindable
    public String getRegFirstName() {
        return regFirstName;
    }

    public void setRegFirstName(String regFirstName) {
        this.regFirstName = regFirstName;
        notifyPropertyChanged(BR.regFirstName);
    }

    @Bindable
    public String getRegLastName() {
        return regLastName;
    }

    public void setRegLastName(String regLastName) {
        this.regLastName = regLastName;
        notifyPropertyChanged(BR.regLastName);
    }

    @Bindable
    public String getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress;
        notifyPropertyChanged(BR.regAddress);
    }

    @Bindable
    public String getRegPhoneNumber() {
        return regPhoneNumber;
    }

    public void setRegPhoneNumber(String regPhoneNumber) {
        this.regPhoneNumber = regPhoneNumber;
        notifyPropertyChanged(BR.regPhoneNumber);
    }

    @Bindable
    public String getRegEmail() {
        return regEmail;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail = regEmail;
        notifyPropertyChanged(BR.regEmail);
    }

    public void register(){
        String regPassword = BCrypt.hashpw(String.valueOf(getRegPassword()),BCrypt.gensalt(12));
        Account account = new Account(getRegAccountID(), regPassword, getRegFirstName(),getRegLastName(),getRegAddress(),getRegCity(),getRegCountry(),getRegPhoneNumber(),getRegEmail());

        //Gửi Account qua Server
        ApiService.apiService.register(account).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                //Nếu thành công thì hiện Toast
                if(Boolean.TRUE.equals(response.body())){
                    registerActivity.showToast("Register Success");
                    registerActivity.changeToLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Register",String.valueOf(t));
            }
        });
    }
}
