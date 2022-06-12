package com.example.bankclientmvvm.account;

import android.app.AlertDialog;

import com.example.bankclientmvvm.Account;

public interface ContractAccount {
    interface AccountView{

        //Lấy accountID từ SharePreferences
        String getAccountIDSharePref();

        void deleteAccountIDSharePref();

        void changeToLoginActivity();

        void showEditProfileFragment();

        String[] getCitiesArrayXML(String country);
        String[] getCountriesArrayXML();
        void removeEditProfileFragment();

        void showToast(String message);

        void showTransferMoneyDialog();

    }

//    interface EditProfileFragment{
//        void setLabelNoti(String notification, int color );
//    }

    interface TransferMoneyDialog {
        void setLabelNoti(String notification);
        AlertDialog getDialog();
    }


//    interface AccountViewModel{
//        Account getAccount();
//
//        void getAccountFromServer();
//
//        void editProfile(String firstName, String lastName, String address, String phoneNumber, String email, String city, String country);
//        void transferMoney(String accountID2, String sMoneyTransfer);
//
//        void removeEditProfileFragmenttt();
//
//        void setTransferMoneyDialog(TransferMoneyDialog transferMoneyDialog);
//
//    }
}
