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
import com.example.telegram.ui.InputRegistrationCodePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentMailRegistration extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    EditText editText_mailRegistration_mail_text, editText_mailRegistration_password_text;
    Button button_mailRegistration_further;
    String email, password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.mail_registration, container, false);
        editText_mailRegistration_mail_text = RootView.findViewById(R.id.editText_mailRegistration_mail_text);
        editText_mailRegistration_password_text = RootView.findViewById(R.id.editText_mailRegistration_password_text);
        button_mailRegistration_further = RootView.findViewById(R.id.button_mailRegistration_further);

        button_mailRegistration_further.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                email = editText_mailRegistration_mail_text.getText().toString();
                password = editText_mailRegistration_password_text.getText().toString();
                if (requireActivity().getIntent().getBooleanExtra("add_email", false)) {
                    if (checkMailPasswordEmpty()) {
                        addEmailCurrentAccount();
                    }
                }
                else {
                    if (checkMailPasswordEmpty()) {
                        registerNewUser();
                    }
                }
            }
        });

        return RootView;
    }

    private void addEmailCurrentAccount()
    {
        Intent intent = new Intent(requireActivity(),InputRegistrationCodePage.class);
        intent.putExtra("mail",email);
        intent.putExtra("password",password);
        startActivity(intent);
    }

    private boolean checkMailPasswordEmpty()
    {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Введите почту", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Введите пароль", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void registerNewUser()
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
