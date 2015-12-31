package com.owen.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Owen on 2015/12/15.
 */
public class AudioPlayer {

    private MediaPlayer mPlayer;

    public void stop() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(Context context) {
        stop();

        mPlayer = MediaPlayer.create(context, R.raw.cmsj);

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

        mPlayer.start();
    }
}
