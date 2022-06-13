package com.example.bankclientmvvm.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bankclientmvvm.R;
import com.example.bankclientmvvm.databinding.ActivityRegisterBinding;
import com.example.bankclientmvvm.login.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity implements ContractRegister{

    private static RegisterViewModel registerViewModel;
    private static Thread threadDelay;
    private static boolean startObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = new RegisterViewModel(this);

        ActivityRegisterBinding activityRegisterBinding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        activityRegisterBinding.setRegisterViewModel(registerViewModel);

        //Khởi tạo các giá trị
        activityRegisterBinding.getRegisterViewModel().setStatusAccountID(false);
        activityRegisterBinding.getRegisterViewModel().setStatusPassword(false);
        activityRegisterBinding.getRegisterViewModel().setStatusConfPassword(false);
        activityRegisterBinding.getRegisterViewModel().setStatusLastName(false);
        activityRegisterBinding.getRegisterViewModel().setStatusFirstName(false);
        activityRegisterBinding.getRegisterViewModel().setStatusCountry(false);
        activityRegisterBinding.getRegisterViewModel().setStatusCity(false);
        activityRegisterBinding.getRegisterViewModel().setStatusAddress(false);
        activityRegisterBinding.getRegisterViewModel().setStatusPhoneNumber(false);
        activityRegisterBinding.getRegisterViewModel().setStatusEmail(true);
        activityRegisterBinding.getRegisterViewModel().displayPrgBar.set(false);

    }

    @BindingAdapter("accountID")
    public static void setErrorAccountID(TextInputLayout view, String accountID) {
        if(accountID == null){
            return;
        }
        //Ngắt threadDelay nếu thread đang chạy
        if(threadDelay != null) {
            if(threadDelay.isAlive()) {
                threadDelay.interrupt();
            }
        }
        Context context = view.getContext();
        registerViewModel.displayPrgBar.set(false);
        startObservable = false;
        boolean statusFirstChar = false;
        boolean statusLength = false;
        boolean statusNumber = false;
        String s = "";
        view.setErrorIconDrawable(0);
        if(accountID.length()>0) {
            String firstChar = String.valueOf(accountID.charAt(0));
            if(firstChar.matches("-?\\d+(\\.\\d+)?")){
                String s1 ="Ký tự đầu tiên phải là chữ\n";
                s = s + s1;
                view.setError(s);
                registerViewModel.setStatusAccountID(false);
            }else {
                statusFirstChar = true;
            }
        }
        if(!accountID.matches("^[a-zA-Z0-9]{6,20}$")) {
            s = s + "Độ dài ký tự ít nhất 6 và tối đa 20\n";
            view.setError(s);
            registerViewModel.setStatusAccountID(false);
        }else {
            statusLength = true;
        }
        if(!accountID.matches("^(?=.*[0-9])([a-zA-Z0-9]+)$")){
            s = s + "Có ít nhất 1 chữ số\n";
            view.setError(s);
            registerViewModel.setStatusAccountID(false);
        }else {
            statusNumber = true;
        }
        if(statusFirstChar && statusLength && statusNumber) {
            view.setError(null);
            view.setHelperText("");
            //Sau khi dừng nhập dữ liệu 3s AccountID sẽ chạy Observable gửi dữ liệu về Server để check AccountID hợp lệ
            threadDelay = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        registerViewModel.displayPrgBar.set(true);
                        Thread.sleep(1500);
                        startObservable = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(startObservable) {
                        // Create the Observable
                        Observable<String> singleTaskObservable = Observable
                            .create(new ObservableOnSubscribe<String>() {
                                @Override
                                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                    if (!emitter.isDisposed()) {
                                        emitter.onNext(registerViewModel.checkAccountID());
                                        emitter.onComplete();
                                    }
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());

                        // Subscribe to the Observable and get the emitted object
                        singleTaskObservable.subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String messFromServer) {
                                if (messFromServer.equals("AccountIDValid")) {
//                                    view.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,
//                                            R.color.green)));
                                    view.setHelperText("AccountID hợp lệ");
                                    registerViewModel.setStatusAccountID(true);
                                    registerViewModel.displayPrgBar.set(false);
                                } else {
                                    view.setError("AccountID đã tồn tại");
                                    registerViewModel.setStatusAccountID(false);
                                    registerViewModel.displayPrgBar.set(false);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                }
            });
            threadDelay.start();

        }
    }

    @BindingAdapter({"showPrgBar"})
    public static void setDisplayPrgBar(ProgressBar view, boolean showPrgBar){
        if(showPrgBar){
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"password"})
    public static void setErrorPassword(TextInputLayout view, String password) {
        if (password == null) {
            return;
        }
        view.setErrorIconDrawable(0);
        boolean b = password.matches("^(?=.*[a-z])" +
                "(?=.*[A-Z])(?=.*\\d)" +
                "[a-zA-Z\\d]{8,20}$");
        if(!b) {
            view.setError("Độ dài ký tự ít nhất 8 và tối đa 20\nCó ít nhất 1 chữ viết hoa\nCó ít nhất 1 chữ số");
            registerViewModel.setStatusPassword(false);
        }else{
            view.setError(null);
            registerViewModel.setStatusPassword(true);
        }
    }

    @BindingAdapter({"password","confPassword"})
    public static void setErrorConfPassword(TextInputLayout view, String passwod, String ConfPassword) {
        if(ConfPassword == null) {
            return;
        }
        view.setErrorIconDrawable(0);
        if (!ConfPassword.equals(passwod)) {
            view.setError("Password không trùng khớp");
            registerViewModel.setStatusConfPassword(false);
        } else {
            view.setError(null);
            registerViewModel.setStatusConfPassword(true);
        }
    }

    @BindingAdapter("firstName")
    public static void setErrorFirstName(TextInputLayout view, String firstName) {
        if(firstName == null) {
            return;
        }
        view.setErrorIconDrawable(0);
        if (!firstName.matches("^[a-zA-Z\\p{L} ]*$")) {
            view.setError("First Name phải là chữ");
            registerViewModel.setStatusFirstName(false);
        } else {
            view.setError(null);
            registerViewModel.setStatusFirstName(true);
        }
    }

    @BindingAdapter("lastName")
    public static void setErrorLastName(TextInputLayout view, String lastName) {
        if(lastName == null) {
            return;
        }
        view.setErrorIconDrawable(0);
        if (!lastName.matches("^[a-zA-Z]+\\p{L}$")) {
            view.setError("Last Name phải là chữ");
            registerViewModel.setStatusLastName(false);
        } else {
            view.setError(null);
            registerViewModel.setStatusLastName(true);
        }
    }

    @BindingAdapter("phoneNumber")
    public static void setErrorPhoneNumber(TextInputLayout view, String phoneNumber) {
        if(phoneNumber == null) {
            return;
        }
        view.setErrorIconDrawable(0);
        if (!phoneNumber.matches("^[0-9]+$")) {
            view.setError("Phone Number phải là số");
            registerViewModel.setStatusPhoneNumber(false);
        } else {
            view.setError(null);
            registerViewModel.setStatusPhoneNumber(true);
        }
    }

    @BindingAdapter("countryPhoneNumber")
    public static void setPrefixPhoneNumber(TextInputLayout view, String country) {
        if(country == null) {
            return;
        }
        Context context = view.getContext();
        String[] prefixPhoneNumberStrArray = context.getResources().getStringArray(R.array.prefix_phoneNumber_array);
        String[] countriesStrArray = context.getResources().getStringArray(R.array.countries_array);
        for(int i = 0; i < prefixPhoneNumberStrArray.length; i++) {
            if (country.equals(countriesStrArray[i])) {
                view.setPrefixText(prefixPhoneNumberStrArray[i]);
            }
        }
    }

    @BindingAdapter("email")
    public static void setErrorEmail(TextInputLayout view, String email) {
        if(email == null) {
            return;
        }
        if(!email.equals("")) {
            view.setErrorIconDrawable(0);
            if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$")) {
                view.setError("Định dạng email không đúng.\n\"example@email.com\"");
                registerViewModel.setStatusEmail(false);
            } else {
                view.setError(null);
                registerViewModel.setStatusEmail(true);
            }
        }else {
            registerViewModel.setStatusEmail(true);
        }
    }

    @BindingAdapter("address")
    public static void setErrorAddress(TextInputLayout view, String address) {
        if(address == null) {
            return;
        }
        if(!address.equals("")){
            registerViewModel.setStatusAddress(true);
        }else {
            registerViewModel.setStatusAddress(false);
        }
    }

    @BindingAdapter("country")
    public static void setErrorCountry(TextInputLayout view, String country) {
        view.setErrorIconDrawable(0);
        view.setError("Xin hãy chọn mục này");
        registerViewModel.setStatusCountry(false);
        if(country == null) {
            return;
        }
        if (!country.equals("")) {
            view.setError(null);
            registerViewModel.setStatusCountry(true);
        }
    }

    @BindingAdapter({"city","country"})
    public static void setErrorCity(TextInputLayout view, String city, String country) {
        if(country != null) {
            if (!country.equals("")) {
                view.setErrorIconDrawable(0);
                view.setError("Xin hãy chọn mục này");
                registerViewModel.setStatusCity(false);
            }
        }
        if(city != null) {
            if (!city.equals("")) {
                view.setError(null);
                registerViewModel.setStatusCity(true);
            }
        }
    }

    @BindingAdapter({"statusAccountID", "statusFristName", "statusLastName", "statusPassword", "statusConfPassword",
            "statusCountry", "statusCity", "statusAddress", "statusPhoneNumber", "statusEmail"})
    public static void setButtonOK(Button view, boolean statusAccountID, boolean statusFristName, boolean statusLastName, boolean statusPassword, boolean statusConfPassword,
                                   boolean  statusCountry, boolean statusCity, boolean statusAddress, boolean statusPhoneNumber, boolean statusEmail) {
        if(statusAccountID && statusFristName && statusLastName && statusPassword && statusConfPassword &&
                statusCountry && statusCity && statusAddress && statusPhoneNumber && statusEmail) {
            registerViewModel.statusRegister.set("");
            view.setEnabled(true);
        }else{
            registerViewModel.statusRegister.set("Xin hãy nhập đầy đủ các vị trí đánh dấu *");
            view.setEnabled(false);
        }
    }

    @BindingAdapter("simpleItems")
    public static void setCountriesArray(AutoCompleteTextView view, String[] countriesStrArray) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.dropdown_country, countriesStrArray);
        view.setAdapter(arrayAdapter);
    }

    @BindingAdapter("countrySelected")
    public static void setCitiesArray(AutoCompleteTextView view, String country) {
        Context context = view.getContext();
        if(country != null) {
            if (country.equals("Viet Nam")) {
                String[] citiesStrArray = context.getResources().getStringArray(R.array.cities_vietnam_array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.dropdown_country, citiesStrArray);
                view.setAdapter(arrayAdapter);
            }else if(country.equals("Singapore")) {
                String[] citiesStrArray = context.getResources().getStringArray(R.array.cities_singapore_array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.dropdown_country, citiesStrArray);
                view.setAdapter(arrayAdapter);
            }else if(country.equals("Thailand")) {
                String[] citiesStrArray = context.getResources().getStringArray(R.array.cities_thailand_array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.dropdown_country, citiesStrArray);
                view.setAdapter(arrayAdapter);
            }else if(country.equals("China")) {
                String[] citiesStrArray = context.getResources().getStringArray(R.array.cities_china_array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.dropdown_country, citiesStrArray);
                view.setAdapter(arrayAdapter);
            }
        }
    }

    @Override
    public String[] getCitiesArrayXML(String country){
        String[] citiesStrArray = null;
        if(country.equals("Viet Nam")) {
            citiesStrArray = getResources().getStringArray(R.array.cities_vietnam_array);
        }else if(country.equals("Singapore")) {
            citiesStrArray = getResources().getStringArray(R.array.cities_singapore_array);
        }else if(country.equals("Thailand")) {
            citiesStrArray = getResources().getStringArray(R.array.cities_thailand_array);
        }else if(country.equals("China")) {
            citiesStrArray = getResources().getStringArray(R.array.cities_china_array);
        }
        return citiesStrArray;
    }

    @Override
    public String[] getCountriesArrayXML(){
        String[] countriesStrArray = getResources().getStringArray(R.array.countries_array);
        return countriesStrArray;
    }

    @Override
    public String[] getPrefixPhoneNumberArrayXML(){
        String[] prefixPhoneNumberStrArray = getResources().getStringArray(R.array.prefix_phoneNumber_array);
        return prefixPhoneNumberStrArray;
    }

    @Override
    public void changeToLoginActivity() {
        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(it);
        Log.e("BankClient","Changed to LoginActivity");
        finish();
    }

    public void changeToLoginActivity(View view) {
        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(it);
        Log.e("BankClient","Changed to LoginActivity");
        finish();
    }

    @Override
    public void showToast(String message) {
        if (message != null)
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
