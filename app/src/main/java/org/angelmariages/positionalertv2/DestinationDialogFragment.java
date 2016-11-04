package org.angelmariages.positionalertv2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DestinationDialogFragment extends DialogFragment {

    private OnDestinationDialogListener mListener;

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String destinationName = getArguments().getString("DESTINATION_NAME");
        int radius = getArguments().getInt("DESTINATION_RADIUS");

        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.destination_dialog);
        customDialog.setTitle("Destination options");

        Button okButton = (Button) customDialog.findViewById(R.id.dialogOkButton);
        Button deleteButton = (Button) customDialog.findViewById(R.id.dialogDeleteButton);
        final TextView destinationText = (TextView) customDialog.findViewById(R.id.destinationNameEdit);
        final TextView destinationRadius = (TextView) customDialog.findViewById(R.id.destinationRadiusEdit);

        if(destinationName != null) {
            destinationText.setText(destinationName);
            destinationRadius.setText(String.valueOf(radius));
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    int radius = 500;
                    try {
                        radius = Integer.parseInt(destinationRadius.getText().toString());
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    }
                    mListener.onOkClicked(destinationText.getText().toString(),radius);
                }
                customDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onDeleteClicked();
                }
                customDialog.dismiss();
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