package org.angelmariages.positionalertv2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class StopAlarmFragment extends Fragment {
    private SeekBar stopSeekBar;
    private boolean stoped;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stop_alarm_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stopSeekBar = (SeekBar) view.findViewById(R.id.seekStop);
        stopSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!stoped && fromUser) {
                    if(progress > 95) {
                        Utils.showSToast("Finished!", view.getContext());
                        stoped = true;
                    }
                }
                //For testing purposes
                if(progress < 10) {
                    stoped = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
