package com.example.bankclientmvvm.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bankclientmvvm.EditProfileFragment;
import com.example.bankclientmvvm.R;
import com.example.bankclientmvvm.TransferMoneyDialog;
import com.example.bankclientmvvm.databinding.ActivityAccountBinding;
import com.example.bankclientmvvm.login.LoginActivity;

public class AccountActivity extends AppCompatActivity implements ContractAccount.AccountView {

    private SharedPreferences sharedPreferences;
    private FragmentManager fragmentManager;

    private EditProfileFragment editProfileFragment;
    private TransferMoneyDialog transferMoneyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        fragmentManager = getSupportFragmentManager();

        AccountViewModel accountViewModel = new AccountViewModel(this);
        editProfileFragment = new EditProfileFragment(accountViewModel);
        transferMoneyDialog = new TransferMoneyDialog(accountViewModel);
        ActivityAccountBinding activityAccountBinding = DataBindingUtil.setContentView(this,R.layout.activity_account);
        activityAccountBinding.setAccountViewModel(accountViewModel);

        accountViewModel.getAccountFromServer();
        activityAccountBinding.getAccountViewModel().setAccountName(accountViewModel.getAccount().getAccountName());
        activityAccountBinding.getAccountViewModel().setLabelAccountBalance();
        accountViewModel.setAccountBalance(accountViewModel.getAccount().getAccountBalance());

    }

    @Override
    public String getAccountIDSharePref() {
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        String accountID = sharedPreferences.getString("accountID", null);
        sharedPreferences = null;
        return accountID;
    }

    @Override
    public void deleteAccountIDSharePref(){
        sharedPreferences = getSharedPreferences("DataPreferences", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("accountID",null);
        edit.commit();
    }

    @Override
    public String[] getCitiesArrayXML(String country){
        String[] citiesStringArray = null;
        if(country.equals("Viet Nam")) {
            citiesStringArray = getResources().getStringArray(R.array.cities_vietnam_array);
        }
        if(country.equals("Singapore")) {
            citiesStringArray = getResources().getStringArray(R.array.cities_singapore_array);
        }
        if(country.equals("Thailand")) {
            citiesStringArray = getResources().getStringArray(R.array.cities_thailand_array);
        }
        if(country.equals("China")) {
            citiesStringArray = getResources().getStringArray(R.array.cities_china_array);
        }
        return citiesStringArray;
    }

    @Override
    public String[] getCountriesArrayXML(){
        String[] countriesStringArray = getResources().getStringArray(R.array.countries_array);
        return countriesStringArray;
    }

    @Override
    public void showToast(String message) {
        if (message != null)
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeToLoginActivity() {
        Intent it = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(it);
        Log.v("BankClient","Changed activity");
        finish();
    }

    @Override
    public void showEditProfileFragment(){
        fragmentManager.beginTransaction().add(R.id.editProfileFragment, (Fragment) editProfileFragment).commit();
    }

    @Override
    public void removeEditProfileFragment(){
        if(editProfileFragment != null) {
            fragmentManager.beginTransaction().remove((EditProfileFragment) editProfileFragment).commit();
        }
    }

    @Override
    public void showTransferMoneyDialog() {
        transferMoneyDialog.show(fragmentManager, "TransferMoneyDialog");
    }
}