package org.angelmariages.positionalertv2;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.io.IOException;

public class StopAlarmFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private String ringtoneSaved;
    private static final String ARG_RINGTONE = "ARG_RINGTONE";

    public static StopAlarmFragment newInstance(String ringtone) {
        StopAlarmFragment stopAlarmFragment = new StopAlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RINGTONE, ringtone);
        stopAlarmFragment.setArguments(args);
        return stopAlarmFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            /*String ringtoneSaved = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(Utils.RINGTONE_PREFERENCE, null);*/
            ringtoneSaved = getArguments().getString(ARG_RINGTONE);
            startRingtone();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stop_alarm_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar stopSeekBar = (SeekBar) view.findViewById(R.id.seekStop);
        stopSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(progress > 95) {
                        Utils.showSToast("Stopped!", view.getContext());
                        stopRingtone();
                        getActivity().finish();
                    }
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

    private void startRingtone() {
        if(ringtoneSaved == null || ringtoneSaved.isEmpty()) {
            ringtoneSaved = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString();
        }
        Utils.sendLog(ringtoneSaved);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(ringtoneSaved));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Utils.sendLog("WHAT: " + i + ", EXTRA: " + i1);
                    return false;
                }
            });
            Utils.sendLog("STARTED!!");
            Utils.sendLog("URI: " + ringtoneSaved);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRingtone() {
        Utils.sendLog("STOPPED!!");
        mediaPlayer.stop();
    }
}
