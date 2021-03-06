package com.example.bankclientmvvm.account;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.example.bankclientmvvm.Account;
import com.example.bankclientmvvm.BR;
import com.example.bankclientmvvm.api.ApiService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountViewModel extends BaseObservable {

    private Account account;
    private final ContractAccount.AccountView mainAccountView;

    private int selectedCityPosition;
    private int selectedCountryPosition;
    private String firstName, lastName, address, phoneNumber, email, accountName, accountID2, strMoneyTransfer;
    private BigDecimal moneyTransfer;
    private BigDecimal accountBalance;
    private final BigDecimal tenThousand;
    public ObservableField<Boolean> statusHideShow;
    public ObservableField<Boolean> transferSuccess;
    public ObservableField<String> accountBalanceShow;
    public ObservableField<String> statusEditProfile;
    public ObservableField<String> statusTransferMoney;
    public ObservableArrayList<String> citiesArrayList;

    public AccountViewModel(ContractAccount.AccountView mainAccountView) {
        this.mainAccountView = mainAccountView;
        account = new Account();
        accountBalanceShow = new ObservableField<>();
        statusHideShow = new ObservableField<>();
        statusEditProfile = new ObservableField<>();
        statusTransferMoney = new ObservableField<>();
        citiesArrayList = new ObservableArrayList<>();
        tenThousand = new BigDecimal("10000");
        moneyTransfer = new BigDecimal("0");
        statusHideShow.set(false);
        transferSuccess = new ObservableField<>();
    }

    public void setCitiesArrayList(String[] citiesStrArray) {
        citiesArrayList.addAll(Arrays.asList(citiesStrArray));
    }


    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Bindable
    public String getAccountID2(){
        return accountID2;
    }

    public void setAccountID2(String accountID2){
        this.accountID2 = accountID2;
        notifyPropertyChanged(BR.accountID2);
    }

    //T???o set get cho 10000 ????? l??m ??i???u ki???n th??ng b??o XML
    @Bindable
    public BigDecimal getTenThousand() {
        return tenThousand;
    }

    @Bindable
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    //T???o set get cho Bigdecimal moneyTransfer ????? l??m ??i???u ki???n th??ng b??o XML
    private BigDecimal getMoneyTransfer() {
        return moneyTransfer;
    }

    private void setMoneyTransfer(BigDecimal moneyTransfer) {
        this.moneyTransfer = moneyTransfer;
        if(getAccountBalance().compareTo(tenThousand) < 0){
            statusTransferMoney.set("Ti???n trong t??i kho???n qu?? ??t (<10000)");
        }else if(moneyTransfer.compareTo(accountBalance) > 0){
            statusTransferMoney.set("Ti???n trong t??i kho???n kh??ng ?????");
        }else if(moneyTransfer.compareTo(tenThousand) < 0){
            statusTransferMoney.set("Money transfer >= 10000");
        }else if(moneyTransfer.compareTo(accountBalance) <=0 ){
            statusTransferMoney.set("");
        }
    }

    @Bindable
    public String getStrMoneyTransfer() {
        return strMoneyTransfer;
    }

    public void setStrMoneyTransfer(String strMoneyTransfer) {
        this.strMoneyTransfer = strMoneyTransfer;
        if(!strMoneyTransfer.equals("")) {
            setMoneyTransfer(new BigDecimal(strMoneyTransfer));
        }
        else{
            setMoneyTransfer(new BigDecimal("0"));
        }
        notifyPropertyChanged(BR.strMoneyTransfer);
    }

    @Bindable
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
        notifyPropertyChanged(BR.accountName);
    }

    @Bindable
    public String getAccountID(){
        return account.getAccountID();
    }

    @Bindable
    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    @Bindable
    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public int getSelectedCityPosition() {
        return selectedCityPosition;
    }

    public void setSelectedCityPosition(int selectedCityPosition) {
        this.selectedCityPosition = selectedCityPosition;
        notifyPropertyChanged(BR.selectedCityPosition);
    }

    @Bindable
    public int getSelectedCountryPosition() {
        return selectedCountryPosition;
    }

    public void setSelectedCountryPosition(int selectedCountryPosition) {
        this.selectedCountryPosition = selectedCountryPosition;
        //Update cities list when selected country
        citiesArrayList.clear();
        String[] countriesStrArray = mainAccountView.getCountriesArrayXML();
        String[] citiesStrArray = mainAccountView.getCitiesArrayXML(countriesStrArray[selectedCountryPosition]);
        setCitiesArrayList(citiesStrArray);
        notifyPropertyChanged(BR.selectedCountryPosition);
    }

    private void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void getAccountFromServer(){
        ApiService.apiService.getAccount(mainAccountView.getAccountIDSharePref()).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Account account = response.body();
                if(account != null){
                    Log.e("Account",account.toString());
                    setAccount(account);
                    setAccountName(account.getAccountName());
                    setLabelAccountBalance();
                    setAccountBalance(account.getAccountBalance());
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("Account",String.valueOf(t));
            }
        });
    }

    public void editProfile() {
        String[] countriesStrArray = mainAccountView.getCountriesArrayXML();
        String country = countriesStrArray[getSelectedCountryPosition()];
        String[] citiesStrArray = mainAccountView.getCitiesArrayXML(country);
        String city = citiesStrArray[getSelectedCityPosition()];

        if (getFirstName().equals("") || getLastName().equals("") || getAddress().equals("") || city.equals("") || country.equals("") || getPhoneNumber().equals("")) {
            statusEditProfile.set("marked * fields cannot be empty");
            return;
        }

        if (!getFirstName().equals(account.getFirstName()) || !getLastName().equals(account.getLastName()) || !getAddress().equals(account.getAddress()) || !city.equals(account.getCity()) || !country.equals(account.getCountry()) || !getPhoneNumber().equals(account.getPhoneNumber()) || !getEmail().equals(account.getEmail())) {
            //T???o chu???i ph??? ????? th??m v??o chu???i truy v???n ch??nh
            //N???u c??c tr?????ng nh???p v??o kh??c account ???? load th?? th??m v??o chu???i c???p nh???t v??o DB

            String s = "";

            if (!getFirstName().equals(account.getFirstName())) {
                s = s + "firstName = " + "'" + getFirstName() + "', ";
                account.setFirstName(getFirstName());
            }
//
            if (!getLastName().equals(account.getLastName())) {
                s = s + "lastName = " + "'" + getLastName() + "', ";
                account.setLastName(getLastName());
            }

            //N???u firstName ho???c lastName thay ?????i th?? c???p nh???t lu??n accountName
            if(s.contains("lastName") || s.contains("firstName")){
                accountName = getFirstName() + " " + getLastName();
                s = s + "accountName = " + "'" + accountName + "', ";
                account.setAccountName(accountName);
            }

            if (!getAddress().equals(account.getAddress())) {
                s = s + "address = " + "'" + getAddress() + "', ";
                account.setAddress(getAddress());
            }

            if (!city.equals(account.getCity())) {
                s = s + "city = " + "'" + city + "', ";
                account.setCity(city);
            }

            if (!country.equals(account.getCountry())) {
                s = s + "country = " + "'" + country + "', ";
                account.setCountry(country);
            }

            if (!getPhoneNumber().equals(account.getPhoneNumber())) {
                s = s + "phoneNumber = " + "'" + getPhoneNumber() + "', ";
                account.setPhoneNumber(getPhoneNumber());
            }

            if (!getEmail().equals(account.getEmail())) {
                s = s + "email = " + "'" + getEmail() + "', ";
                account.setEmail(getEmail());
            }

            //B??? ??i 2 k?? t??? cu???i l?? ", "
            if (s.length() >= 2) {
                s = s.substring(0, s.length() - 2);
                Log.e("BankClient", s);


                //Send string-query to Server
                ArrayList<String> accountStrArrList = new ArrayList<>();
                accountStrArrList.add(account.getAccountID());
                accountStrArrList.add(s);
                ApiService.apiService.editProfile(accountStrArrList).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String messRecv = response.body();
                        if(messRecv != null){
                            if (messRecv.equals("EditProfileSuccess")) {
                                Log.e("Account",messRecv);
                                //C???p nh???t l???i Account trong AccountActivity
                                setAccount(account);
                                setAccountName(account.getAccountName());
                                //update account success
                                mainAccountView.showToast("Update account success");
                                //Xo?? fragment
                                removeEditProfileFragment();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        }
        //Kh??ng c?? g?? thay ?????i. Xo?? fragment
        removeEditProfileFragment();
    }

    public void transferMoney(){
        if(getAccountID2() == null || getStrMoneyTransfer() == null || getAccountID2().equals("") || getStrMoneyTransfer().equals("")){
            statusTransferMoney.set("Please input info transfer");
        }
        String moneyTransferNumber = null;
        if(getStrMoneyTransfer().contains(".") ) {
            moneyTransferNumber = getStrMoneyTransfer().replaceAll("\\.", "");
        }else{
            moneyTransferNumber = getStrMoneyTransfer();
        }
        BigDecimal moneyTransfer = new BigDecimal(moneyTransferNumber);

        if(moneyTransfer.compareTo(tenThousand) <0){
            statusTransferMoney.set("Money transfer >= 10000");
        }

        if(moneyTransfer.compareTo(account.getAccountBalance()) >0){
            statusTransferMoney.set("The account does not have enough money");
        }

        ArrayList<String> accountStrArrList = new ArrayList<>();
        accountStrArrList.add(account.getAccountID());
        accountStrArrList.add(getAccountID2());
        accountStrArrList.add(String.valueOf(moneyTransfer));

        Thread threadTransferMoney = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiService.apiService.moneyTransfer(accountStrArrList).execute();
                    if(response.code() == 200){
                        Account account = (Account) response.body();
                        if(account != null){
                            setAccount(account);
                            setLabelAccountBalance();
                            transferSuccess.set(true);
                        }
                    }else if(response.code() == 404){
                        statusTransferMoney.set("accountID incorrect");
                        transferSuccess.set(false);
                    }else if(response.code() == 424) {
                        statusTransferMoney.set("Transfer failed");
                        transferSuccess.set(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadTransferMoney.start();
        try {
            threadTransferMoney.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exitAccount(){
        mainAccountView.deleteAccountIDSharePref();
        mainAccountView.changeToLoginActivity();
    }


    public void showEditProfileFragment(){
        mainAccountView.showEditProfileFragment();
    }

    public void removeEditProfileFragment(){
        mainAccountView.removeEditProfileFragment();
    }

    public void showTrasferMoneyDialog(){
        mainAccountView.showTransferMoneyDialog();
    }

    public void setLabelAccountBalance(){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String formatted = formatter.format(account.getAccountBalance());
        accountBalanceShow.set(formatted);
    }

    public void imgHideShow() {
        if (Boolean.TRUE.equals(statusHideShow.get())) {
            statusHideShow.set(false);
        } else {
            statusHideShow.set(true);
        }
    }
}
