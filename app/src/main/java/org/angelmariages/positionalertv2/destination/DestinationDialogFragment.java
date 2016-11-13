package org.angelmariages.positionalertv2.destination;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.angelmariages.positionalertv2.R;

public class DestinationDialogFragment extends DialogFragment {

    private OnDestinationDialogListener mListener;
    private TextView destinationText;
    private TextView destinationRadius;

    public interface OnDestinationDialogListener {
        void onOkClicked(String destinationName, int radius);
        void onDeleteClicked();
    }

    public static DestinationDialogFragment newInstance(String destinationName, int radius) {
        DestinationDialogFragment frag = new DestinationDialogFragment();
        Bundle args = new Bundle();
        args.putString("DESTINATION_NAME", destinationName);
        args.putInt("DESTINATION_RADIUS", radius);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(destinationText.getText().toString().isEmpty()) {
            if (mListener != null) {
                mListener.onDeleteClicked();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String destinationName = getArguments().getString("DESTINATION_NAME");
        int radius = getArguments().getInt("DESTINATION_RADIUS");

        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.destination_dialog);
        customDialog.setTitle("Destination options");

        Button okButton = (Button) customDialog.findViewById(R.id.dialogOkButton);
        Button deleteButton = (Button) customDialog.findViewById(R.id.dialogDeleteButton);
        destinationText = (TextView) customDialog.findViewById(R.id.destinationNameEdit);
        destinationRadius = (TextView) customDialog.findViewById(R.id.destinationRadiusEdit);

        if(destinationName != null) {
            destinationText.setText(destinationName);
            destinationRadius.setText(String.valueOf(radius));
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(destinationText.getText().toString().isEmpty()) {
                    destinationText.setError("Put some name!");
                } else {
                    if (mListener != null) {
                        int radius = 500;
                        try {
                            radius = Integer.parseInt(destinationRadius.getText().toString());
                        } catch (NumberFormatException ignored) {}
                        mListener.onOkClicked(destinationText.getText().toString(), radius);
                        customDialog.dismiss();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onDeleteClicked();
                    customDialog.dismiss();
                }
            }
        });

        return customDialog;
    }

    public void setOnDestinationDialogListener(OnDestinationDialogListener onDestinationDialogListener) {
        if(onDestinationDialogListener != null) {
            mListener = onDestinationDialogListener;
        }
    }
}
