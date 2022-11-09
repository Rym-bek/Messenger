package com.example.telegram.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.telegram.R;
import com.example.telegram.ui.EditUserProfile;
import com.example.telegram.ui.InputRegistrationCodePage;
import com.example.telegram.ui.LoginPage;
import com.example.telegram.ui.MailAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DialogEditUserProfileEmailChange extends DialogFragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText editText;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        editText = new EditText(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        editText.setHint(R.string.enter_email);
        editText.setTextSize(18);
        editText.requestFocus();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(50,0,50,0);
        editText.setLayoutParams(params);
        linearLayout.addView(editText);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.change_email_long);
        builder.setView(linearLayout);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(user!=null)
                {
                    updateUser();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }

    private void updateUser()
    {
        user.updateEmail(editText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reloadUser();
                        }
                    }
                });
    }

    private void reloadUser()
    {
        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Почта обновлена", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), EditUserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}