package com.example.carwasher.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.carwasher.R;
import com.example.carwasher.adapters.CancelledAdapter;
import com.example.carwasher.models.CompletedReqModel;

import java.util.ArrayList;
import java.util.List;

public class CancelledActivity extends AppCompatActivity {

    private android.widget.Toolbar toolbar;

    private RecyclerView rv;
    private CancelledAdapter cancelledAdapter;
    private List<CompletedReqModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);

        /*-----call methods-----*/
        init();
        initToolbar();
        populateCancelledRequest();
    }

    private void init()
    {
        toolbar = findViewById(R.id.cancelled_toolbar);
        rv = findViewById(R.id.cancelled_rv);
    }

    private void initToolbar()
    {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Canelled Requests");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    startActivity(new Intent(getApplicationContext(),RequestsActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateCancelledRequest()
    {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        try
        {
            for (int i = 0; i<10 ;i++)
            {
                CompletedReqModel model = new CompletedReqModel();
                model.setRequester("Andries Sebola");
                model.setDate("19/03/2021");
                list.add(model);
            }
            cancelledAdapter = new CancelledAdapter(list);
            rv.setAdapter(cancelledAdapter);

        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
