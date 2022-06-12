package com.example.bankclientmvvm;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.bankclientmvvm.account.AccountActivity;
import com.example.bankclientmvvm.account.AccountViewModel;
import com.example.bankclientmvvm.account.ContractAccount;
import com.example.bankclientmvvm.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment{

    private AccountActivity accountActivity;
    private AccountViewModel accountViewModel;

    public EditProfileFragment(AccountViewModel accountViewModel) {
        // Required empty public constructor
        this.accountViewModel = accountViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentEditProfileBinding fragmentEditProfileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_profile,container,false);
        View view = fragmentEditProfileBinding.getRoot();
        fragmentEditProfileBinding.setAccountViewModel(accountViewModel);

        fragmentEditProfileBinding.getAccountViewModel().setFirstName(accountViewModel.getAccount().getFirstName());
        fragmentEditProfileBinding.getAccountViewModel().setLastName(accountViewModel.getAccount().getLastName());
        fragmentEditProfileBinding.getAccountViewModel().setAddress(accountViewModel.getAccount().getAddress());
        fragmentEditProfileBinding.getAccountViewModel().setPhoneNumber(accountViewModel.getAccount().getPhoneNumber());
        fragmentEditProfileBinding.getAccountViewModel().setEmail(accountViewModel.getAccount().getEmail());
        fragmentEditProfileBinding.getAccountViewModel().setAccountName(accountViewModel.getAccount().getAccountName());

        String[] countriesStrArray = accountActivity.getCountriesArrayXML();
        for(int i = 0; i < countriesStrArray.length ; i++) {
            if(countriesStrArray[i].equals(accountViewModel.getAccount().getCountry())){
                fragmentEditProfileBinding.getAccountViewModel().setSelectedCountryPosition(i);
                String[] citiesStrArray = accountActivity.getCitiesArrayXML(countriesStrArray[i]);
                for(int j = 0; j < citiesStrArray.length ; j++) {
                    if(citiesStrArray[j].equals(accountViewModel.getAccount().getCity())){
                        fragmentEditProfileBinding.getAccountViewModel().setSelectedCityPosition(j);
                        break;
                    }
                }
            }
            break;
        }
        return view;
    }

    //Khi fragment Attach vào Activity sẽ nhận được đối tượng Context là mainActivity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //context có kiểu MainActivity
        //toán tử instanceof dùng để kiểm tra context có phải là đối tượng của MainActivity hay k
        if(context instanceof AccountActivity){
            accountActivity = (AccountActivity) context;
        }
    }
}