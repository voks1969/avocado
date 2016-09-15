/**
 * Created by voks1969 on 12/22/2015.
 */

/* This is sample of audio media player that uses stream as input.
 * API level 23 provides MediaDataSource. however id support of API < 23 is required
 * it is not acceptable.
 * The workarouns is to use "fake" local media server -
 * see see com.example.voks1969.audioplayer.MediaStreamProxy class
*/

package com.example.voks1969.audioplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
//import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;
import android.util.Log;

//import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "SampleMediaPlayer";

    private MediaPlayer m_mediaPlayer;
    boolean m_isMediaPlayerReleased=false;
    private TextView m_duration;
    private double m_timeElapsed, m_finalTime;
    private int m_forwardTime = 2000, m_backwardTime = 2000;
    private SeekBar m_seekbar;
    private Handler m_durationHandler = new Handler();

    /*
      This class is required API level 23 if the min API < 23 then use
      MediaStreamProxy solution instead
     */
    /*
    private MultimediaFileDataSource  m_datasource;
    private class MultimediaFileDataSource extends MediaDataSource {
            InputStream m_sis;
            long m_currentposition;
            @Override
            public long getSize() {
                long size=0;
                try {
                    size = m_sis.available();
                }
                catch (Exception e) {

                }
                return size;
            }

            @Override
            public  int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
                long bytestotheend = m_sis.available() - position;
                if(bytestotheend<=0)
                    return -1;
                int bytestoread = bytestotheend>size?size:(int)bytestotheend;
                int bytesdone;
                try {
                    if (m_currentposition < position) {
                        m_sis.skip(position-m_currentposition);
                    }
                    else if (m_currentposition > position) {
                        m_sis.reset();
                        m_sis.skip(position);
                    }
                    else {

                    }
                    m_currentposition = position;

                    bytesdone = m_sis.read(buffer, offset, bytestoread);
                    m_currentposition += bytesdone;
                } catch (IOException e) {
                    throw new IOException("SecureInputStream: could not read", e);
                }
                return bytesdone;
            }

            @Override
            public void close() throws IOException {
            try {
                m_sis.close();
            } catch (IOException e) {
                throw new IOException("SecureInputStream: could not close", e);}
            }

            public void initStream(InputStream sis) throws IOException {
                m_sis = sis;
                if(m_sis.markSupported()==false) {
                    throw new IOException("SecureInputStream: mark is not supported");
                }
                m_sis.mark(Integer.MAX_VALUE);
            }
        }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_mediaPlayer = new MediaPlayer();
        // just play the file as is
        //m_mediaPlayer = MediaPlayer.create(this, R.raw.over_the_horizon_2013);
        m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // API >= 23
        //m_datasource = new MultimediaFileDataSource();

        m_mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Got error=" + what + " extra=" + extra);
                return false;
            }
        });

        try {
            InputStream  is = getResources().openRawResource(R.raw.over_the_horizon_2013);
            new MediaStreamProxy(is).start();


            // API >= 23
            //m_datasource.initStream(is);
            //m_mediaPlayer.setDataSource(m_datasource);

            m_mediaPlayer.setDataSource("http://127.0.0.1:8888/mypermission123");

        } catch(Exception e) {
            Log.e(TAG, "Got exception:"+e.getMessage());
        }

        m_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                if (mp.isPlaying()) {
                    mp.seekTo(mp.getCurrentPosition() + 10000);
                }

                m_finalTime = mp.getDuration();
                m_duration = (TextView) findViewById(R.id.songDuration);
                m_seekbar = (SeekBar) findViewById(R.id.seekBar);
                m_seekbar.setMax((int) m_finalTime);
                m_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser == true) {
                            m_mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                play(null);
            }
        });

        try {
            m_mediaPlayer.prepareAsync();
        } catch(Exception e) {
            Log.e(TAG, "Media Player prepareAsync() failure:"+e.getMessage());
        }

    }

    public void play(View view) {
        try {
            m_mediaPlayer.start();
        } catch(Exception e) {
            Log.e(TAG, "Play exception:" + e.getMessage());
        }
        m_timeElapsed = m_mediaPlayer.getCurrentPosition();
        m_seekbar.setProgress((int) m_timeElapsed);
        m_durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            synchronized (m_mediaPlayer) {
                if(m_isMediaPlayerReleased==true) {
                    return;
                }
                //get current position
                m_timeElapsed = m_mediaPlayer.getCurrentPosition();
                //set seekbar progress
                m_seekbar.setProgress((int) m_timeElapsed);
                //set time remaing
                double timeRemaining = m_finalTime - m_timeElapsed;
                m_duration.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
                //repeat yourself that again in 100 miliseconds
                m_durationHandler.postDelayed(this, 100);
            }
        }
    };

    public void pause(View view) {
        m_mediaPlayer.pause();
    }

    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song endes
        if ((m_timeElapsed + m_forwardTime) <= m_finalTime) {
            m_timeElapsed = m_timeElapsed + m_forwardTime;

            boolean kuku = m_mediaPlayer.isPlaying();
            try {
                //seek to the exact second of the track
                m_mediaPlayer.seekTo((int) m_timeElapsed);
            } catch(Exception e) {
                Log.e(TAG, "seekTo exception:" + e.getMessage());
            }
        }
    }

    // go backwards at backwardTime seconds
    public void rewind(View view) {
        //check if we can go back at backwardTime seconds after song starts
        if ((m_timeElapsed - m_backwardTime) > 0) {
            m_timeElapsed = m_timeElapsed - m_backwardTime;

            //seek to the exact second of the track
            m_mediaPlayer.seekTo((int) m_timeElapsed);
        }
    }

    @Override
    protected void  onDestroy() {
        synchronized (m_mediaPlayer) {
            m_isMediaPlayerReleased = true;
            m_mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
