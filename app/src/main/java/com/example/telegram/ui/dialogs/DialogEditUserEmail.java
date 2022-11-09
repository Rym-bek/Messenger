package com.example.telegram.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.telegram.R;
import com.example.telegram.ui.EditUserProfile;
import com.example.telegram.ui.LoginPage;
import com.example.telegram.ui.MailAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

public class DialogEditUserEmail extends DialogFragment {
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
                getString(R.string.add_email),
                getString(R.string.verify_email),
                getString(R.string.change_email),
                getString(R.string.untie_email)
        };
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.email_address);
        builder.setItems(choice_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addEmail();
                        break;
                    case 1:
                        verifyEmail();
                        break;
                    case 2:
                        changeEmail();
                        break;
                    case 3:
                        unLinkEmail();
                        break;
                }
            }
        });

        return builder.create();
    }



    private void addEmail()
    {
        if(!checkEmail())
        {
            Intent intent= new Intent(getContext(), MailAuth.class);
            intent.putExtra("add_email",true);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Почта уже привязана", Toast.LENGTH_SHORT).show();
        }
    }

    private void unLinkEmail()
    {
        if(checkEmail())
        {
            if(phoneNumber!=null && !TextUtils.isEmpty(phoneNumber))
            {
                user.unlink(EmailAuthProvider.PROVIDER_ID);
                Toast.makeText(getContext(), "Почта отвязана", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Телефон не привязан", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), "Почта не привязана", Toast.LENGTH_SHORT).show();
        }
                /*.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.isSuccessful()) {
                                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Почта отвязана", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });*/

    }

    private boolean checkEmail()
    {
        if(user!=null) {
            return email != null && !TextUtils.isEmpty(email);
        }
        return false;
    }

    private void verifyEmail()
    {
        if(checkEmail()) {
            if (!user.isEmailVerified()) {
                user.sendEmailVerification();
                Toast.makeText(getContext(), "Письмо отправлено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Почта уже подтверждена", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void changeEmail()
    {
        if(checkEmail())
        {
            Intent intent= new Intent(getContext(), MailAuth.class);
            intent.putExtra("change_email",true);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Почта не привязана", Toast.LENGTH_SHORT).show();
        }
    }

}
