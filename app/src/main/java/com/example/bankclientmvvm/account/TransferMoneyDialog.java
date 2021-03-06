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

        //Override button positive ????? Dialog kh??ng bi???n m???t khi nh???n Ok
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
//    //Khi fragment Attach v??o Activity s??? nh???n ???????c ?????i t?????ng Context l?? mainActivity
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        //context c?? ki???u MainActivity
//        //to??n t??? instanceof d??ng ????? ki???m tra context c?? ph???i l?? ?????i t?????ng c???a MainActivity hay k
//        if(context instanceof AccountActivity){
//            accountActivity = (AccountActivity) context;
//        }
//    }
}
