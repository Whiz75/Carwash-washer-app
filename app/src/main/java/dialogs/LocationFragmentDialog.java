package dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.carwasher.R;
import com.google.android.material.button.MaterialButton;

public class LocationFragmentDialog extends DialogFragment {

    private MaterialButton frag_button;

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
        context = view.getContext();
        //call methods here
        init(view);
        popDialog(view);

        return view;
    }

    private void init(View view)
    {
        //initialize var
        frag_button = view.findViewById(R.id.pop_dialog);
    }

    private void popDialog(View view)
    {
        /*getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);*/

        getDialog().getWindow().setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);

        frag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}