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
import org.angelmariages.positionalertv2.Utils;

public class DestinationDialogFragment extends DialogFragment {

    private DestinationDialogListener mListener;
    private TextView destinationText;
    private TextView destinationRadius;

    private static final String ARG_DESTINATION = "ARG_DESTINATION";

    public interface DestinationDialogListener {
        void onOkClicked(Destination destination);

        void onDeleteClicked();
    }

    public static DestinationDialogFragment newInstance(Destination destination) {
        DestinationDialogFragment frag = new DestinationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DESTINATION, destination);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (destinationText.getText().toString().isEmpty()) {
            if (mListener != null) {
                mListener.onDeleteClicked();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Destination destination = (Destination) getArguments().getSerializable(ARG_DESTINATION);

        final Dialog customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.destination_dialog);
        customDialog.setTitle("Destination options");

        Button okButton = (Button) customDialog.findViewById(R.id.dialogOkButton);
        Button deleteButton = (Button) customDialog.findViewById(R.id.dialogDeleteButton);
        destinationText = (TextView) customDialog.findViewById(R.id.destinationNameEdit);
        destinationRadius = (TextView) customDialog.findViewById(R.id.destinationRadiusEdit);

        if (destination != null) {
            destinationText.setText(destination.getName());
            destinationRadius.setText(String.valueOf(destination.getRadius()));
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validInput = true;
                int radius = 0;

                if (destinationText.getText().toString().isEmpty()) {
                    destinationText.setError("Put some name!");
                    validInput = false;
                }
                try {
                    radius = Integer.parseInt(destinationRadius.getText().toString());
                } catch (NumberFormatException ignored) {
                    destinationRadius.setError("That's not a number!");
                    validInput = false;
                }

                if(radius == 0) {
                    destinationRadius.setError("Put a valid radius");
                    validInput = false;
                }

                if (validInput) {
                    if (destination != null) {
                        destination.setName(destinationText.getText().toString());
                        destination.setRadius(radius);
                        mListener.onOkClicked(destination);
                    }
                    customDialog.dismiss();
                }
            }
        }

        );

        deleteButton.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                if (mListener != null) {
                                                    mListener.onDeleteClicked();
                                                    customDialog.dismiss();
                                                }
                                            }
                                        }

        );

        return customDialog;
    }

    public void setOnDestinationDialogListener(DestinationDialogListener destinationDialogListener) {
        if (destinationDialogListener != null) {
            mListener = destinationDialogListener;
        }
    }
}
