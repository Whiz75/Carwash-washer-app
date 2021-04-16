package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carwasher.R;
import com.example.carwasher.models.WasherModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextView signin_tv;
    private TextInputLayout tl_username, tl_email,tl_password, tl_confirmpass;
    private Button registerButton;

    /*----------------firebase objects-----------------*/
    private DatabaseReference washerRef;
    private FirebaseAuth washerAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*----------------instatiate firebase object-----------------*/
        washerAuth = FirebaseAuth.getInstance();
        washerRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Washers");

        /*----------------call register methods-----------------*/
        initBinding();
        GoToLogin();
        animation();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    /*----------------method to bind components-----------------*/
    private void initBinding()
    {
        tl_username = findViewById(R.id.signup_username);
        tl_email = findViewById(R.id.signup_email);
        tl_password = findViewById(R.id.signup_password);
        tl_confirmpass = findViewById(R.id.signup_confirmpass);

        registerButton =findViewById(R.id.register_button);

        signin_tv = findViewById(R.id.signin_tv);
    }
    /*----------------method to add animations to components-----------------*/
    private void animation()
    {
        int x = 300;
        float v = 0;
        int delay = 1000;

        tl_username.setTranslationX(x);
        tl_email.setTranslationX(x);
        tl_password.setTranslationX(x);
        tl_confirmpass.setTranslationX(x);
        registerButton.setTranslationX(x);
        signin_tv.setTranslationX(x);

        tl_username.setAlpha(v);
        tl_email.setAlpha(v);
        tl_password.setAlpha(v);
        tl_confirmpass.setAlpha(v);
        registerButton.setAlpha(v);
        signin_tv.setAlpha(v);

        tl_username.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(300).start();
        tl_email.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(400).start();
        tl_password.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(500).start();
        tl_confirmpass.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(600).start();
        registerButton.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(700).start();
        signin_tv.animate().translationX(0).alpha(1).setDuration(delay).setStartDelay(800).start();

    }
    /*----------------method to register new washers-----------------*/
    private void registerUser()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Registering...Please wait");
        pd.setCancelable(true);

        final String username = Objects.requireNonNull(tl_username.getEditText()).getText().toString().trim();
        final String email = Objects.requireNonNull(tl_email.getEditText()).getText().toString().trim();
        final String password = Objects.requireNonNull(tl_password.getEditText()).getText().toString().trim();
        final String confirmPassword = Objects.requireNonNull(tl_confirmpass.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(username))
        {
            tl_username.getEditText().setError("please enter username");

        }else if (TextUtils.isEmpty(email))
        {
            tl_email.getEditText().setError("please enter a valid email");

        }else if (TextUtils.isEmpty(password))
        {
            tl_password.getEditText().setError("password field can not be empty");
        }else if (TextUtils.isEmpty(confirmPassword))
        {
            tl_confirmpass.getEditText().setError("confirm password field can not be empty");
        }else
        {
            if (TextUtils.equals(password, confirmPassword))
            {
                final ProgressDialog regDialog = new ProgressDialog(RegisterActivity.this);
                regDialog.setMessage("Registering new user...Please Wait");
                regDialog.show();

                /*-------------method to clear edittexts-------------*/
                clearEdits();
                /*-------------start create user with email and password-------------*/
                washerAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult)
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String userId = Objects.requireNonNull(user).getUid();

                                WasherModel washer = new WasherModel();
                                washer.setUsername(username);
                                washer.setEmail(email);

                                try
                                {
                                    regDialog.dismiss();
                                    washerRef.child(userId).setValue(washer);
                                    Toast.makeText(getApplicationContext(), "registration was successful...", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    /*-------method to set up the loading dialog---*/
    /*private void ShowRegistrationDialog()
    {
        *//*---show a loading dialog----*/
    /*
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Registering...Please wait");
        dialog.setCancelable(false);
        dialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(3000);
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }*/
    /*-------------method to clear edittexts-------------*/
    private void clearEdits()
    {
        Objects.requireNonNull(tl_username.getEditText()).getText().clear();
        Objects.requireNonNull(tl_email.getEditText()).getText().clear();
        Objects.requireNonNull(tl_password.getEditText()).getText().clear();
        Objects.requireNonNull(tl_confirmpass.getEditText()).getText().clear();

        tl_username.getEditText().setFocusable(true);
    }
    /*----------------method to go to login-----------------*/
    private void GoToLogin()
    {
        signin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try
        {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
