package com.example.telegram.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.telegram.R;
import com.example.telegram.ui.EditUserProfile;
import com.example.telegram.ui.InputRegistrationCodePage;
import com.example.telegram.ui.LoginPage;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

public class DialogEditUserPhone extends DialogFragment {
    FirebaseAuth auth;
    FirebaseUser user;
    String email, phoneNumber;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        auth= FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user!=null)
        {
            email = user.getEmail();
            phoneNumber=user.getPhoneNumber();
        }
        final String[] choice_options = {
                getString(R.string.add_number),
                getString(R.string.change_number),
                getString(R.string.unlink_number)
        };
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.phone_number);
        builder.setItems(choice_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addPhoneNumber();
                        break;
                    case 1:
                        changePhoneNumber();
                        break;
                    case 2:
                        unLinkPhoneNumber();
                        break;
                }
            }
        });

        return builder.create();
    }

    private boolean checkPhoneNumber()
    {
        if(user!=null) {
            return phoneNumber != null && !TextUtils.isEmpty(phoneNumber);
        }
        return false;
    }

    private boolean checkEmail()
    {
        if(user!=null) {
            return email != null && !TextUtils.isEmpty(email);
        }
        return false;
    }



    private void addPhoneNumber()
    {
        if(!checkPhoneNumber())
        {
            Intent intent = new Intent(getContext(), LoginPage.class);
            intent.putExtra("add_phone_to_profile",true);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Телефон уже привязан", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePhoneNumber()
    {
        if(checkPhoneNumber())
        {
            Intent intent = new Intent(getContext(), LoginPage.class);
            intent.putExtra("change_phone_number",true);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Телефон не привязан", Toast.LENGTH_SHORT).show();
        }
    }

    private void unLinkPhoneNumber()
    {
        if(checkPhoneNumber())
        {
            if(checkEmail())
            {
                user.unlink(PhoneAuthProvider.PROVIDER_ID);
                Toast.makeText(getContext(), "Телефон отвязан", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Почта не привязана", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), "Номер не привязан", Toast.LENGTH_SHORT).show();
        }
    }
}
