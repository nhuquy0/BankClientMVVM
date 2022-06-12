package com.example.bankclientmvvm.register;

import android.content.res.Resources;

import com.example.bankclientmvvm.R;

public interface ContractRegister {

    String[] getCitiesArrayXML(String country);

    String[] getCountriesArrayXML();
    String[] getPrefixPhoneNumberArrayXML();


    void changeToLoginActivity();
    void showToast(String message);
}
