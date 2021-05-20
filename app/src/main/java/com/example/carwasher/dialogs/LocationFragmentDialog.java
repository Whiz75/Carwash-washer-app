package com.example.carwasher.dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carwasher.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationFragmentDialog extends DialogFragment {

    private MaterialButton frag_button;
    private TextInputLayout bidTextInputLayout;
    private AppCompatImageButton iv_cancel_dialog;

    public LocationFragmentDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle);
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_location_dialog, container, false);

        //dialog context
        context = view.getContext();

        //call methods here
        init(view);
        popDialog(view);
        cancelDialog(view);

        return view;
    }

    private void init(View view)
    {
        //initialize var
        frag_button = view.findViewById(R.id.pop_dialog);
        bidTextInputLayout = view.findViewById(R.id.input_bid_textLayout);
        iv_cancel_dialog = view.findViewById(R.id.cancel_dialog_imageview);
    }

    private void popDialog(View view)
    {
        /*getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);*/

        frag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String bidText = bidTextInputLayout.getEditText().getText().toString();

                if (TextUtils.isEmpty(bidText))
                {
                    bidTextInputLayout.getEditText().setError("Please enter a price");
                }else
                {
                    Toast.makeText(getActivity(), "You placed a bid", Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
    }

    private void cancelDialog(View view)
    {
        iv_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        //set dialog width and height
        getDialog()
                .getWindow()
                .setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);

        //set dialog background
        getDialog()
                .getWindow()
                .setBackgroundDrawableResource(R.drawable.dialog_bg);

    }
}