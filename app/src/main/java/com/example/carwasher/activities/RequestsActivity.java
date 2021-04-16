package com.example.carwasher.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.example.carwasher.R;
import com.example.carwasher.adapters.RequestsAdapter;
import com.example.carwasher.adapters.RequestsAdapter.ButtonsClickListener;
import com.example.carwasher.models.RequestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static android.widget.Toast.LENGTH_LONG;

public class RequestsActivity extends AppCompatActivity implements ButtonsClickListener {

    private android.widget.Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private List<RequestModel> requestsList;
    private RecyclerView rv;
    private RequestsAdapter mAdapter;

    private FirebaseAuth auth;
    private DatabaseReference requestRef,reference;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        /*-------instantiate firebase objects------*/
        auth = FirebaseAuth.getInstance();
        requestRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Requests");
        reference = FirebaseDatabase.getInstance().getReference("CarWash").child("Washers");
        auth = FirebaseAuth.getInstance();

        /*--- initialize firestore object ---*/
        firestore = FirebaseFirestore.getInstance();

        /*-------call methods----------*/
        init();
        initNavDrawer();
        //populateRequests();
        getRequestsFromFireStore();
        AnimationFadeIn();
    }

    /*--------------method to bind components--------------------*/
    private void init()
    {
        toolbar = findViewById(R.id.tool_bar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        rv= findViewById(R.id.requests_rv);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        /*---show a loading dialog----*/
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        /*---set dialog display duration---*/

        /*---run dialog for 3 secs---*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*---run dialog for 3 secs---*/
                    Thread.sleep(5000);
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /*--------------method to initialize the navigation drawer--------------------*/
    private void initNavDrawer()
    {
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setTitle("REQUESTS");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                        break;
                    case R.id.nav_profile:
                        getProfile();
                        break;
                    case R.id.nav_logout:
                        eventLogout();
                        break;
                    case R.id.nav_rate:
                        Toast.makeText(getApplicationContext(),"Cancelled option selected", LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),CancelledActivity.class));
                        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                        finish();
                        break;
                    case R.id.nav_share:
                        Toast.makeText(getApplicationContext(),"share option selected", LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),CompletedRequestsActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    /*--- method to populate the recyclerview ---*/
    private void populateRequests()
    {
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(this));
        requestsList = new ArrayList<>();

        try
        {
            requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try
                    {
                        if (snapshot.exists()){
                            for (DataSnapshot npsnapshot : snapshot.getChildren()){
                                RequestModel l = npsnapshot.getValue(RequestModel.class);
                                l.setId(snapshot.getKey());
                                requestsList.add(l);
                            }
                            mAdapter = new RequestsAdapter(requestsList, RequestsActivity.this);
                            rv.setAdapter(mAdapter);
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"snapshot doesn't exist", LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),error.getMessage(), LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfile()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = auth.getCurrentUser();

        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (user != null)
                    {
                        try
                        {
                            String email = snapshot.child("email").getValue().toString();
                            String username = snapshot.child("username").getValue().toString();

                            //go to profile activity
                            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                            intent.putExtra("emailKey", email);
                            intent.putExtra("usernameKey", username);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), LENGTH_LONG).show();
            }
        });
    }
    /*------------method is called on drawer logout clicked---------*/
    private void eventLogout()
    {
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setMessage("Are you sure you want to logout?");
        logoutDialog.setCancelable(true);
        logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog pd = new ProgressDialog(RequestsActivity.this);
                pd.setMessage("Logging out...Please wait");
                pd.setCancelable(false);
                pd.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        auth.signOut();
                        pd.dismiss();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                }, 2000);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        logoutDialog.show();
    }

    /*------------method to setup the recyclerview animation-------------*/
    private void AnimationFadeIn()
    {
        AnimationSet set = new AnimationSet(true);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        set.addAnimation(fadeIn);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.2f);
        rv.setLayoutAnimation(controller);
    }

    private void setUpDialog(final int pos, final List<RequestModel> requestsList, final RecyclerView rv, final RequestsAdapter adpter)
    {
        /*--------test test test test dialog------*/
        final Dialog dialog = new Dialog(RequestsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog);
        dialog.setCancelable(false);

        /*---------------set the dialog height and width---------------*/
        WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
        layout.copyFrom(dialog.getWindow().getAttributes());
        layout.width = WindowManager.LayoutParams.MATCH_PARENT;
        layout.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layout.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(layout);

        /*-------bind the dialog components---------*/
        ImageButton confirmButton = dialog.findViewById(R.id.default_confirm_button);
        ImageButton cancelButton = dialog.findViewById(R.id.default_cancel_button);
        ImageView cancel_dialogImage = dialog.findViewById(R.id.default_cancel_img);
        dialog.show();

        /*-------dialog buttons actions---------*/
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try
                {
                    //try new method
                    rv.removeViewAt(pos);
                    adpter.notifyItemRemoved(pos);
                    adpter.notifyItemRangeChanged(pos, requestsList.size());
                    /*------update the recyclerview-------*/
                    adpter.notifyDataSetChanged();

                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getMessage(), LENGTH_LONG).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try
                {
                    dialog.dismiss();
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_dialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    dialog.dismiss();
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpDialog()
    {
        /*---show a loading dialog----*/
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText("Are you sure?");
        dialog.setContentText("Deleting the request can not be undone!");
        dialog.setConfirmText("yes, delete it!");
        dialog.setCancelText("No, cancel");
        dialog.showConfirmButton(true);
        dialog.showCancelButton(true);
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Toast.makeText(getApplicationContext(), "deleted!", LENGTH_LONG).show();
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        dialog.show();

       /* new SweetAlertDialog(getApplicationContext(),SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Deleting the request can not be undone!")
                .setCancelText("No, cancel")
                .setConfirmText("yes, delete it!")
                .showConfirmButton(true)
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Toast.makeText(getApplicationContext(),"deleting items", LENGTH_LONG).show();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Toast.makeText(getApplicationContext(),"cancel dialog", LENGTH_LONG).show();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();*/
    }

    @Override
    public void AccepterClick(int position)
    {
        //Toast.makeText(getApplicationContext(),"accept testing interface, no:"+position, Toast.LENGTH_LONG).show();
        try
        {
            //String id = firestore.collection("Requests").document().getId();

            Intent intent = new Intent(getApplicationContext(),RequestsDetails.class);
            intent.putExtra("requestsKey", requestsList.get(position).getRequester());
            intent.putExtra("requestsDate", requestsList.get(position).getDate());
            intent.putExtra("requestsAddress",requestsList.get(position).getLocation());
            intent.putExtra("requestLat",requestsList.get(position).getLatitude());
            intent.putExtra("requestLon",requestsList.get(position).getLongitude());
            intent.putExtra("requestProfile",requestsList.get(position).getProfile());
            intent.putExtra("requestKey",requestsList.get(position).getId());
            intent.putExtra("requestPrice",requestsList.get(position).getPrice());
            intent.putExtra("requestItems", String.valueOf(requestsList.get(position).getItems()));
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RejectClick(final int position)
    {
        //setUpDialog(position, requestsList,rv,mAdapter);
        setUpDialog();
        //try to delete by querying
        requestRef.child(requestsList.get(position).getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Request removed!", LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(),"reject testing interface, no: "+position, LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        try
        {
            if (snapshot.exists()){
                for (DataSnapshot npsnapshot : snapshot.getChildren()){
                    RequestModel l = npsnapshot.getValue(RequestModel.class);
                    requestsList.add(l);
                }
                mAdapter = new RequestsAdapter(requestsList, RequestsActivity.this);
                rv.setAdapter(mAdapter);
            }else
            {
                Toast.makeText(getApplicationContext(),"snapshot doesn't exist", LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(getApplicationContext(),error.getMessage(), LENGTH_LONG).show();
    }





    /*--- MIGRATE TO FIRESTORE ----*/

    private void getRequestsFromFireStore()
    {
        //set recycler view
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(this));
        requestsList = new ArrayList<>();

        //thima
        /*FirebaseFirestore.getInstance()
                .collection("Requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!(value == null)){
                            for(final DocumentChange dc : value.getDocumentChanges()){
                                switch (dc.getType()) {
                                    case ADDED:
                                        RequestModel rm = dc.getDocument().toObject(RequestModel.class);
                                        rm.setId(dc.getDocument().getId());
                                        requestsList.add(rm);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    case MODIFIED:
                                        break;
                                    case REMOVED:
                                        if(requestsList.removeIf(new Predicate<RequestModel>() {
                                            @Override
                                            public boolean test(RequestModel d) {
                                                return d.getId().equals(dc.getDocument().getId());
                                            }
                                        }))
                                        {

                                        }
                                        break;
                                }
                            }
                        }
                    }
                });*/



        /*final CollectionReference docRef = firestore.collection("Requests");
        docRef*/
        FirebaseFirestore.getInstance()
                .collection("Requests")
                .whereEqualTo("status", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot snapshot: task.getResult())
                            {
                                RequestModel l = snapshot.toObject(RequestModel.class);
                                //get the document id
                                String id = firestore.collection("Requests").document().getId();

                                l.setId(id);
                                requestsList.add(l);
                            }
                            mAdapter = new RequestsAdapter(requestsList, RequestsActivity.this);
                            rv.setAdapter(mAdapter);
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"Could not load the data...please try again", LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),LENGTH_LONG).show();
                    }
                });

    }

}
