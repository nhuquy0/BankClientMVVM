package com.example.bankclientmvvm.account;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.example.bankclientmvvm.Account;
import com.example.bankclientmvvm.BR;
import com.example.bankclientmvvm.NetworkImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class AccountViewModel extends BaseObservable {

    private Account account;
    private final NetworkImpl modelNetwork;
    private final ContractAccount.AccountView mainAccountView;

    private int selectedCityPosition;
    private int selectedCountryPosition;
    private String firstName, lastName, address, phoneNumber, email, accountName, accountID2, strMoneyTransfer;
    private BigDecimal moneyTransfer;
    private BigDecimal accountBalance;
    private final BigDecimal tenThousand;
    public ObservableField<String> accountBalanceShow;
    public ObservableField<Boolean> statusHideShow;
    public ObservableField<String> statusEditProfile;
    public ObservableField<String> statusTransferMoney;
    public ObservableArrayList<String> citiesArrayList;

    public AccountViewModel(ContractAccount.AccountView mainAccountView) {
        this.mainAccountView = mainAccountView;
        account = new Account();
        modelNetwork = new NetworkImpl();
        accountBalanceShow = new ObservableField<>();
        statusHideShow = new ObservableField<>();
        statusEditProfile = new ObservableField<>();
        statusTransferMoney = new ObservableField<>();
        citiesArrayList = new ObservableArrayList<>();
        tenThousand = new BigDecimal("10000");
        moneyTransfer = new BigDecimal("0");
        statusHideShow.set(false);
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

    //Tạo set get cho 10000 để làm điều kiện thông báo XML
    @Bindable
    public BigDecimal getTenThousand() {
        return tenThousand;
    }

    @Bindable
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    //Tạo set get cho Bigdecimal moneyTransfer để làm điều kiện thông báo XML
    private BigDecimal getMoneyTransfer() {
        return moneyTransfer;
    }

    private void setMoneyTransfer(BigDecimal moneyTransfer) {
        this.moneyTransfer = moneyTransfer;
        if(getAccountBalance().compareTo(tenThousand) < 0){
            statusTransferMoney.set("Tiền trong tài khoản quá ít (<10000)");
        }else if(moneyTransfer.compareTo(accountBalance) > 0){
            statusTransferMoney.set("Tiền trong tài khoản không đủ");
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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void getAccountFromServer(){
        modelNetwork.createConnect();
        modelNetwork.sendDataTCP("account" + "#" + mainAccountView.getAccountIDSharePref());
        modelNetwork.readDataTCP();
        String mesRecv = "";
        mesRecv = modelNetwork.getMesFromServer();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            setAccount(objectMapper.readValue(mesRecv, Account.class));
            System.out.println(account);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
            //Tạo chuỗi phụ để thêm vào chuỗi truy vấn chính
            //Nếu các trường nhập vào khác account đã load thì thêm vào chuỗi cập nhật vào DB

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

            //Nếu firstName hoặc lastName thay đổi thì cập nhật luôn accountName
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

            //Bỏ đi 2 ký tự cuối là ", "
            if (s.length() >= 2) {
                s = s.substring(0, s.length() - 2);
                Log.e("BankClient", s);

                //Send string-query to Server
                modelNetwork.createConnect();
                modelNetwork.sendDataTCP("editprofile#" + account.getAccountID() + "#" + s);
                modelNetwork.readDataTCP();
                String mesRecv = "";
                mesRecv = modelNetwork.getMesFromServer();
                if (mesRecv.equals("editprofilesuccess")) {
                    //Cập nhật lại Account trong AccountActivity
                    setAccount(account);
                    setAccountName(account.getAccountName());
                    //update account success
                    mainAccountView.showToast("Update account success");
                    //Xoá fragment
                    removeEditProfileFragment();
                }
            }
        }
        //Xoá fragment
        removeEditProfileFragment();
    }

    public boolean transferMoney(){
        if(getAccountID2() == null || getStrMoneyTransfer() == null || getAccountID2().equals("") || getStrMoneyTransfer().equals("")){
            statusTransferMoney.set("Please input info transfer");
            return false;
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
            return false;
        }

        if(moneyTransfer.compareTo(account.getAccountBalance()) >0){
            statusTransferMoney.set("The account does not have enough money");
            return false;
        }

        modelNetwork.createConnect();
        modelNetwork.sendDataTCP("transfermoney#" + account.getAccountID() + "#" + getAccountID2() + "#" + moneyTransfer);
        modelNetwork.readDataTCP();
        String mesRecv = "";
        mesRecv = modelNetwork.getMesFromServer();
        String[] messRecvArray = mesRecv.split("#");
        if(messRecvArray[0].equals("transfermoneysuccess")){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                account = objectMapper.readValue(messRecvArray[1], Account.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            setLabelAccountBalance();
            return true;
        }else if(mesRecv.equals("transfermoneyWrongID")){
            statusTransferMoney.set("accountID incorrect");
        }
        return false;
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
