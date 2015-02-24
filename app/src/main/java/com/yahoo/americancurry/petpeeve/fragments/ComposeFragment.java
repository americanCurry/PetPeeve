package com.yahoo.americancurry.petpeeve.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.americancurry.petpeeve.R;
import com.yahoo.americancurry.petpeeve.model.Pin;

public class ComposeFragment extends DialogFragment {
    public static final int MAX_MESSAGE_LENGTH = 500;
    private EditText etCompose;
    private Pin pin;

    public ComposeFragment() {

    }

    public static ComposeFragment newInstance(String titleOfForm) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_compose, null);

        dialog.getWindow().setContentView(view);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;

        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compose, container);
        getDialog().setCanceledOnTouchOutside(true);

        pin = (Pin) this.getArguments().get("pinInfo");

        final TextView tvComposeNumChars = (TextView) view.findViewById(R.id.tvComposeNumChars);
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        Button btnTweet = (Button) view.findViewById(R.id.btSubmit);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMessage(v);
                dismiss();
            }
        });

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvComposeNumChars.setText(Integer.toString(MAX_MESSAGE_LENGTH));
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvComposeNumChars.setText(String.valueOf(MAX_MESSAGE_LENGTH - s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etCompose.addTextChangedListener(mTextEditorWatcher);

        return view;
    }

    public void composeMessage(View view) {

        pin.setText(etCompose.getText().toString());
        try {
            pin.save();
        } catch (com.parse.ParseException e) {
            Log.e("ERROR", "Unable to post message to Parse");
            Toast.makeText(getActivity(), "Unable to post message", Toast.LENGTH_SHORT).show();
        }
        this.dismiss();

    }
}