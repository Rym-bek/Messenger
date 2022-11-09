package com.example.telegram.ui;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.telegram.R;
import com.example.telegram.models.ChatMessage;
import com.example.telegram.models.UserData;
import com.example.telegram.ui.dialogs.DialogEditUserEmail;
import com.example.telegram.ui.dialogs.DialogEditUserPhone;
import com.example.telegram.ui.dialogs.DialogEditUserProfileExit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class EditUserProfile extends AppCompatActivity {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE_TIRAMISU = 42;

    Toolbar toolbar_EditUserProfile;
    LinearLayout editUserProfile_selectPhoto, editUserProfile_phoneNumber,editUserProfile_userName,editUserProfile_aboutMe,
            editUserProfile_eMail,editUserProfile_nickName;
    TextView textView_editUserProfile_phoneNumber,textView_editUserProfile_userName,textView_editUserProfile_aboutMe,
            textView_editUserProfile_eMail,textView_editUserProfile_nickName,
            textView_editUserProfile_eMail_description,textView_editUserProfile_userName_description,textView_editUserProfile_phoneNumber_description;
    ImageView editUserProfile_userPhoto,
            imageView_editUserProfile_eMail;
    FirebaseUser user;
    CollapsingToolbarLayout collapsingToolbarLayout_editUserProfile;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users"),userRefUid;
    StorageReference storageReference;
    StorageReference mountainStorageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        toolbar_EditUserProfile=findViewById(R.id.toolbar_EditUserProfile);
        collapsingToolbarLayout_editUserProfile =findViewById(R.id.collapsingToolbarLayout_editUserProfile);

        editUserProfile_selectPhoto=findViewById(R.id.editUserProfile_selectPhoto);
        editUserProfile_nickName=findViewById(R.id.editUserProfile_nickName);
        editUserProfile_phoneNumber=findViewById(R.id.editUserProfile_phoneNumber);
        editUserProfile_userName=findViewById(R.id.editUserProfile_userName);
        editUserProfile_aboutMe=findViewById(R.id.editUserProfile_aboutMe);
        editUserProfile_eMail=findViewById(R.id.editUserProfile_eMail);

        editUserProfile_userPhoto= findViewById(R.id.editUserProfile_userPhoto);
        imageView_editUserProfile_eMail= findViewById(R.id.imageView_editUserProfile_eMail);

        textView_editUserProfile_phoneNumber = findViewById(R.id.textView_editUserProfile_phoneNumber);
        textView_editUserProfile_userName= findViewById(R.id.textView_editUserProfile_userName);
        textView_editUserProfile_aboutMe= findViewById(R.id.textView_editUserProfile_aboutMe);
        textView_editUserProfile_eMail= findViewById(R.id.textView_editUserProfile_eMail);
        textView_editUserProfile_nickName=findViewById(R.id.textView_editUserProfile_nickName);

        textView_editUserProfile_eMail_description = findViewById(R.id.textView_editUserProfile_eMail_description);
        textView_editUserProfile_userName_description = findViewById(R.id.textView_editUserProfile_userName_description);
        textView_editUserProfile_phoneNumber_description = findViewById(R.id.textView_editUserProfile_phoneNumber_description);
        editUserProfile_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetName();
            }
        });

        editUserProfile_selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission())
                {
                    toSetImage();
                }
                else
                {
                    requestPermission();
                }
            }
        });

        setSupportActionBar(toolbar_EditUserProfile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar_EditUserProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editUserProfile_phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogPhone();
            }
        });
        editUserProfile_eMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogEmail();
            }
        });

        editUserProfile_nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetNickname();
            }
        });

    }

    void toSetNickname()
    {
        Intent intent = new Intent(EditUserProfile.this, EditUserNickname.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            if(storageReference!=null)
            {
                mountainStorageReference=storageReference.child("users").child(user.getUid()).child("userPhoto");
            }
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phoneNumber = user.getPhoneNumber();
            boolean emailVerified = user.isEmailVerified();

            if (name != null && !name.equals("")) {
                collapsingToolbarLayout_editUserProfile.setTitle(name);
                textView_editUserProfile_userName.setText(name);
                textView_editUserProfile_userName_description.setText(R.string.change_username);
            }

            setUserPhoto();

            if (phoneNumber != null && !phoneNumber.equals("")) {
                textView_editUserProfile_phoneNumber.setText(phoneNumber);
                textView_editUserProfile_phoneNumber_description.setText(R.string.change_or_untie_phone_number);
            }
            if (email != null && !TextUtils.isEmpty(email))
            {
                textView_editUserProfile_eMail.setText(email);
                if(emailVerified)
                {
                    imageView_editUserProfile_eMail.setImageDrawable(ContextCompat.getDrawable(EditUserProfile.this,R.drawable.ic_baseline_done_outline_24));
                    textView_editUserProfile_eMail_description.setText(R.string.change_or_untie_email);
                }
                else
                {
                    textView_editUserProfile_eMail_description.setText(R.string.confirm_change_untie_email);
                    user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (user!=null)
                            {
                                boolean isEmailVerified = user.isEmailVerified();
                                if (isEmailVerified)
                                {
                                    textView_editUserProfile_eMail_description.setText(R.string.change_or_untie_email);
                                    imageView_editUserProfile_eMail.setImageDrawable(ContextCompat.getDrawable(EditUserProfile.this,R.drawable.ic_baseline_done_outline_24));
                                }
                            }
                        }
                    });
                }
            }

            //загрузка никнейма
            String uid = user.getUid();
            userRefUid=userRef.child(uid);
            userRefUid.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if(userData!=null)
                    {
                        textView_editUserProfile_nickName.setText(userData.getNickname());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openDialogPhone()
    {
        DialogEditUserPhone dialogEditUserPhone =new DialogEditUserPhone();
        dialogEditUserPhone.show(getSupportFragmentManager(),"dialogPhone");
    }

    private void openDialogEmail()
    {
        DialogEditUserEmail dialogEditUserEmail =new DialogEditUserEmail();
        dialogEditUserEmail.show(getSupportFragmentManager(),"My  Fragment2");
    }


    public boolean checkPermission()
    {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.R)
        {
            return Environment.isExternalStorageManager();
        }
        else
        {
            int write=ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            int read=ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            return write==PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestPermission()
    {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.R && Build.VERSION.SDK_INT<Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
        else if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.READ_MEDIA_IMAGES},READ_STORAGE_PERMISSION_REQUEST_CODE_TIRAMISU);
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==READ_STORAGE_PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length>0)
            {
                boolean write = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(write && read)
                {
                    toSetImage();
                }
                else{
                    Toast.makeText(getBaseContext(), "Нет доступа к хранилищу", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode==READ_STORAGE_PERMISSION_REQUEST_CODE_TIRAMISU)
        {
            if(grantResults.length>0)
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    toSetImage();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Нет доступа к хранилищу", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    ActivityResultLauncher<Intent> imagePickerActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Uri imageUri = result.getData().getData();
                        UploadTask uploadTask = mountainStorageReference.putFile(imageUri);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(imageUri)
                                        .build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            setUserPhoto();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

    private void setUserPhoto()
    {
        if(user.getPhotoUrl()!=null)
        {
            Glide.with(getApplicationContext())
                    .load(mountainStorageReference)
                    .signature(new ObjectKey(Objects.requireNonNull(user.getPhotoUrl()).hashCode()))
                    .into(editUserProfile_userPhoto);
        }
    }

    void toSetName()
    {
        Intent intent = new Intent(EditUserProfile.this, EditUserName.class);
        startActivity(intent);
    }

    void toSetImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        imagePickerActivityResult.launch(galleryIntent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.menu_edit_user_profile, menu);
        return true;
    }

    public void startDialogExit()
    {
        DialogEditUserProfileExit dialogEditUserProfileExit =new DialogEditUserProfileExit();
        dialogEditUserProfileExit.show(getSupportFragmentManager(),"My  Fragment");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_changeName:
                toSetName();
                return true;
            case R.id.action_selectPhoto:
                toSetImage();
                return true;
            case R.id.action_exit:
                startDialogExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
