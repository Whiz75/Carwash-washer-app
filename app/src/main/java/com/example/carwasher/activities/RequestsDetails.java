package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.example.carwasher.R;
import com.example.carwasher.adapters.SpecsAdapter;
import com.example.carwasher.models.CompletedReqModel;
import com.example.carwasher.models.SpecsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsDetails extends AppCompatActivity {

    private TextView name_tv, date_tv,address_tv, list_spacs_tv;
    private CircleImageView profile;
    private MaterialButton confirmButton;

    //DialogFragmentH h = new DialogFragmentH();

    private DatabaseReference completedRef, servicesRef;

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_details);

        /*--- dialog dialog ---*/
        //h.show(getSupportFragmentManager().beginTransaction(), null);

        completedRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Completed");
        servicesRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Requests").child("services");
        /*---------call methods here---------*/
        init();
        setRequestDetails();
        populateServices();
        GoToMap();
    }

    private void init()
    {
        name_tv = findViewById(R.id.requester_name_tv);
        date_tv = findViewById(R.id.request_date_tv);
        address_tv = findViewById(R.id.request_address_tv);
        //addditional
        list_spacs_tv = findViewById(R.id.list_specs_text);

        profile = findViewById(R.id.requester_img);

        rv = findViewById(R.id.services_rv);

        confirmButton = findViewById(R.id.request_confirm_btn);

        android.widget.Toolbar toolbar = findViewById(R.id.details_toolbar);
        toolbar.setTitle("REQUESTS DETAILS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
                    finish();
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void  setRequestDetails()
    {
        Intent intent = getIntent();
        String key = intent.getStringExtra("requestsKey");
        String date = intent.getStringExtra("requestsDate");
        String address = intent.getStringExtra("requestsAddress");
        String photo = intent.getStringExtra("requestProfile");
        String items = intent.getStringExtra("requestItems");

        name_tv.setText(key);
        date_tv.setText(date);
        address_tv.setText(address);
        //Toast.makeText(getApplicationContext(),items, Toast.LENGTH_LONG).show();
        list_spacs_tv.setText(items);
        try
        {
            Picasso
                    .get()
                    .load(photo)
                    .into(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  setCompleteRequest()
    {
        Intent intent = getIntent();
        String key = intent.getStringExtra("requestsKey");
        String profile = intent.getStringExtra("requestProfile");
        String address = intent.getStringExtra("requestsAddress");
        String datenow = new SimpleDateFormat("HH:MM:SS").format(Calendar.getInstance().getTime());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*RequestModel model = new RequestModel();
        model.setDate(date);
        model.setLocation(address);*/
        CompletedReqModel model1 = new CompletedReqModel();
        model1.setRequester(key);
        model1.setDate(datenow);
        model1.setProfile(profile);

        try
        {
            completedRef.child(uid).push().setValue(model1);
            Toast.makeText(getApplicationContext(), "task is completed", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateServices()
    {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        final List<SpecsModel> list = new ArrayList<>();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*FirebaseFirestore
                .getInstance()
                .collection("Requests")
                .whereEqualTo("userId",uid)
                .get();*/

        FirebaseFirestore
                .getInstance()
                .collection("Requests")
                .document()
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                                //String group_string= document.getData().toString();
                                List<String> list1 = (List<String>) documentSnapshot.get("items");
                                Toast.makeText(getApplicationContext(), list1.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        for (int i = 0; i<5; i++)
        {
            SpecsModel mdl = new SpecsModel();
            mdl.setItem("exterior wash");
            list.add(mdl);
        }
        SpecsAdapter adapter = new SpecsAdapter(list);
        rv.setAdapter(adapter);

        rv.setVisibility(View.GONE);

    }


    private void GoToMap()
    {
       final SweetAlertDialog dialog =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
       dialog.setTitleText("Good job!");
        dialog.setContentText("You clicked the button!");
        //dialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    //setCompleteRequest();
                    Intent intent = getIntent();
                    String address = intent.getStringExtra("requestsAddress");
                    String key = intent.getStringExtra("requestsKey");
                    String profile = intent.getStringExtra("requestProfile");
                    String lat = intent.getStringExtra("requestLat");
                    String lon = intent.getStringExtra("requestLon");
                    String reqKey = intent.getStringExtra("requestKey");
                    String datenow = new SimpleDateFormat("HH:MM:SS").format(Calendar.getInstance().getTime());

                    //check toast
                    Toast.makeText(getApplicationContext(), reqKey, Toast.LENGTH_LONG).show();
                    Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                    mapIntent.putExtra("addressKey", address);
                    mapIntent.putExtra("requesterKey", key);
                    mapIntent.putExtra("requestProfile",profile);
                    mapIntent.putExtra("requestLat",lat);
                    mapIntent.putExtra("requestLon",lon);
                    mapIntent.putExtra("requestkey",reqKey);
                    startActivity(mapIntent);
                    //dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
        finish();
    }
}
