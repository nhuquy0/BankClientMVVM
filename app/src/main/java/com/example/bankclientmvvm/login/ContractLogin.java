package com.example.bankclientmvvm.login;

public interface ContractLogin {

    interface LoginView{
        //Lấy thông báo Toast từ View
        void getToast(String notification);

        //Lưu username vào SharedPreferences
        void saveAccountIDSharePref(String accountID);

        //Thay đổi qua AccountActivity
        void changeToAccountActivity();

        //Lấy uuid từ SharePreferences
        String getUUIDSharePref();

        String getAccountIDSharePref();

    }

//    interface LoginViewModel{
//
//        //AutoLogin khi vào ứng dụng
//        void sendAutoLogin();
//    }
}
