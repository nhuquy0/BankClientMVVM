package com.example.bankclientmvvm.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.bankclientmvvm.R;
import com.example.bankclientmvvm.databinding.DialogTransferMoneyBinding;

public class TransferMoneyDialog extends DialogFragment {

    AccountViewModel accountViewModel;

    public TransferMoneyDialog(AccountViewModel accountViewModel){
        this.accountViewModel = accountViewModel;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogTransferMoneyBinding dialogTransferMoneyBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout. dialog_transfer_money, null, false);

        dialogTransferMoneyBinding.setAccountViewModel(accountViewModel);
        dialogTransferMoneyBinding.getAccountViewModel().setAccountID2("");
        dialogTransferMoneyBinding.getAccountViewModel().setStrMoneyTransfer("");
        dialogTransferMoneyBinding.getAccountViewModel().transferSuccess.set(false);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogTransferMoneyBinding.getRoot()).setTitle("Transfer Money");

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Empty, because we will overide this function
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog;
        dialog = builder.create();
        dialog.show();

        //Override button positive để Dialog không biến mất khi nhấn Ok
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
//                User clicked OK button
                accountViewModel.transferMoney();
                if(Boolean.TRUE.equals(accountViewModel.transferSuccess.get())){
                    dialog.dismiss();
                };
            }
        });
        return dialog;
    }

    //
//    //Khi fragment Attach vào Activity sẽ nhận được đối tượng Context là mainActivity
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        //context có kiểu MainActivity
//        //toán tử instanceof dùng để kiểm tra context có phải là đối tượng của MainActivity hay k
//        if(context instanceof AccountActivity){
//            accountActivity = (AccountActivity) context;
//        }
//    }
}
