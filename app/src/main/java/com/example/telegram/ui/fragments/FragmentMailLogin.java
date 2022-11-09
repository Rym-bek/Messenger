package com.example.telegram.ui.fragments;

import static com.mikepenz.iconics.Iconics.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.telegram.ui.HomeActivity;
import com.example.telegram.R;
import com.example.telegram.ui.dialogs.DialogEditUserProfileEmailChange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentMailLogin extends Fragment {
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    EditText editView_mailLogin_email_text, editText_mailLogin_password_text;
    Button button_mailLogin_further, button_mailLogin_forgotPassword;
    String email, password;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.mail_login, container, false);
        editView_mailLogin_email_text=RootView.findViewById(R.id.editView_mailLogin_email_text);
        editText_mailLogin_password_text=RootView.findViewById(R.id.editText_mailLogin_password_text);
        button_mailLogin_further=RootView.findViewById(R.id.button_mailLogin_further);
        button_mailLogin_forgotPassword=RootView.findViewById(R.id.button_mailLogin_forgotPassword);

        boolean changeEmail=false;
        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            changeEmail =bundle.getBoolean("change_email",false);
        }
        boolean finalChangeEmail = changeEmail;
        if(finalChangeEmail)
        {
            button_mailLogin_forgotPassword.setVisibility(View.GONE);
        }
        button_mailLogin_further.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                email = editView_mailLogin_email_text.getText().toString();
                password = editText_mailLogin_password_text.getText().toString();
                if(finalChangeEmail){
                    if(checkMailPasswordEmpty())
                    {
                        updateEmailCurrentUser();
                    }
                }
                else
                {
                    if(checkMailPasswordEmpty())
                    {
                        enterCurrentUser();
                    }
                }
            }
        });

        return RootView;
    }

    private boolean checkMailPasswordEmpty()
    {
        if (TextUtils.isEmpty(email)) {Toast.makeText(getApplicationContext(), "Введите почту", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Введите пароль", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateEmailCurrentUser()
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                DialogEditUserProfileEmailChange dialogEditUserProfileEmailChange = new DialogEditUserProfileEmailChange();
                dialogEditUserProfileEmailChange.show(requireActivity().getSupportFragmentManager(),"My  Fragment3");
            }
        });
    }

    private void enterCurrentUser()
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Ошибка регистрации", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
