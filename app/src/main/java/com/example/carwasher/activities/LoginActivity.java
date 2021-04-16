package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carwasher.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView signup_tv;
    private MaterialButton signinButton;
    private TextInputLayout tl_email, tl_password;

    /*----------firebase object----------*/
    DatabaseReference reference;
    private FirebaseAuth loginAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*----------instatiate fb object----------*/
        loginAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("CarWash").child("Washers");

        /*-----------call methods------------*/
        initBinding();
        GoToRegister();
        animations();
        //GoToRequests();
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAuthentication();
            }
        });
    }

    /*----------------method to bind components-----------------*/
    private void initBinding()
    {
        tl_email = findViewById(R.id.signin_email);
        tl_password = findViewById(R.id.signin_password);

        signup_tv = findViewById(R.id.signup_tv);
        signinButton = findViewById(R.id.login_button);
    }

    /*----------------method to animate components-----------------*/
    private void animations()
    {
        float v = 0;
        int x = 300;

        tl_email.setTranslationX(x);
        tl_password.setTranslationX(x);
        signup_tv.setTranslationX(x);
        signinButton.setTranslationX(x);

        tl_email.setAlpha(v);
        tl_password.setAlpha(v);
        signup_tv.setAlpha(v);
        signinButton.setAlpha(v);

        tl_email.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        tl_password.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        signinButton.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        signup_tv.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();

    }

    /*----------------method to authenticate users for logging-----------------*/
    private void UserAuthentication()
    {
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Authentcating user...Please wait");

        final String email = Objects.requireNonNull(tl_email.getEditText()).getText().toString().trim();
        final String password = Objects.requireNonNull(tl_password.getEditText()).getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tl_email.getEditText().setError("email field can not be empty");
            //pd.dismiss();

        } else if (TextUtils.isEmpty(password)) {
            tl_password.getEditText().setError("password field can not be empty");
            //pd.dismiss();
        } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Email and password fields can not be empty!", Toast.LENGTH_LONG).show();
           // pd.dismiss();
        } else {
            pd.show();
            loginAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            try
                            {
                                /*---get auth user---*/
                                String uid = Objects.requireNonNull(loginAuth.getCurrentUser()).getUid();
                                //get user email to compare it with the entered
                                reference.child(uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {
                                            String val_email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                                            /*--if emails are the same--*/
                                            if (val_email.equals(email))
                                            {
                                                pd.dismiss();
                                                startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                finish();
                                            }
                                        }
                                        else
                                        {
                                            pd.dismiss();
                                            Toast.makeText(getApplicationContext(),"There's no such wash available", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*----------------method to navigate to register----------------*/
    private void GoToRegister()
    {
        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Logging user...");
            pd.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        pd.dismiss();
                        startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
