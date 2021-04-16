package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.carwasher.R;
import com.example.carwasher.adapters.CompletedAdapter;
import com.example.carwasher.models.CompletedReqModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CompletedRequestsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<CompletedReqModel> requestsList;
    private CompletedAdapter adapter;

    private DatabaseReference completedRef;

    private android.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_requests);

        completedRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Completed");

        /*-----call methods------*/
        init();
        initToolbar();
        populateCompletedRequests();
    }

    private void init()
    {
        toolbar = findViewById(R.id.completed_toolbar);
        rv = findViewById(R.id.completed_requests_rv);
    }

    private void initToolbar()
    {
        toolbar.setTitle("COMPLETED REQUESTS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RequestsActivity.class));
                finish();
            }
        });
    }

    private void populateCompletedRequests()
    {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        requestsList = new ArrayList<>();

        try
        {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            completedRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        for (DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            CompletedReqModel reqModel = snapshot1.getValue(CompletedReqModel.class);
                            requestsList.add(reqModel);
                        }

                        Toast.makeText(getApplicationContext(),String.valueOf(snapshot.getChildrenCount()), Toast.LENGTH_LONG).show();
                        adapter = new CompletedAdapter(requestsList);
                        rv.setAdapter(adapter);
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"No data available yet!!!", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
