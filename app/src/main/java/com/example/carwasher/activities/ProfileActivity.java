package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carwasher.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputLayout username_lo,firstname_lo, lastname_lo, email_lo;
    private TextView fullnames_tv;
    private MaterialButton update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*---instatiate firebase objects---*/
        //reference = FirebaseDatabase.getInstance().getReference("CarWash").child("Washers");
        /*----call method-----*/
        init();
        toolbar();
        getProfileDetails();
    }

    private void init()
    {
        toolbar = findViewById(R.id.profile_toolbar);
        username_lo = findViewById(R.id.profile_username);
        firstname_lo = findViewById(R.id.profile_name);
        lastname_lo = findViewById(R.id.profile_surname);
        email_lo = findViewById(R.id.profile_email);

        fullnames_tv = findViewById(R.id.profile_fullname);

        update = findViewById(R.id.update_button);
    }

    private void toolbar()
    {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("PROFILE");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                finish();
            }
        });
    }

    private void getProfileDetails()
    {
        String email = this.getIntent().getStringExtra("emailKey");
        String username = this.getIntent().getStringExtra("usernameKey");

        username_lo.getEditText().setText(username);
        email_lo.getEditText().setText(email);
        fullnames_tv.setText(username);
    }

    private void updateProfile()
    {
       DatabaseReference reference = FirebaseDatabase
               .getInstance()
               .getReference("CarWash")
               .child("Washers");

       String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
       String username = username_lo.getEditText().getText().toString();
       String email = email_lo.getEditText().getText().toString();

       HashMap<String, String> updateUsers = new HashMap<String, String>();
       updateUsers.put("username", username);
       updateUsers.put("email",email);

       reference.child(uid)
               .setValue(updateUsers)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText(getApplicationContext(),"Data was successfully updated...", Toast.LENGTH_LONG).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
           }
       });

    }
}
