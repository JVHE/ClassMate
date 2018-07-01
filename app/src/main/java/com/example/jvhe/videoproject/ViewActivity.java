package com.example.jvhe.videoproject;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;


public class ViewActivity extends AppCompatActivity {

    final static String SAMPLE_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    VideoView videoView;
    SeekBar seekBar;
    Handler updateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        EditText tvURL = findViewById(R.id.et_url);
        tvURL.setText(SAMPLE_VIDEO_URL);

        videoView = (VideoView) findViewById(R.id.videoView);
        //MediaController mc = new MediaController(this);
        //videoView.setMediaController(mc);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public void loadVideo(View view) {
        //Sample video URL : http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4
        EditText tvURL = (EditText) findViewById(R.id.et_url);
        String url = tvURL.getText().toString();

        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                                        @Override
                                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                            switch (what) {
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                                    // Progress Diaglog 출력
                                                    Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                                                    break;
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                                    // Progress Dialog 삭제
                                                    Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                                                    videoView.start();
                                                    break;
                                            }
                                            return false;
                                        }
                                    }

        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이를 한다.
         videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                long finalTime = videoView.getDuration();
                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void playVideo(View view) {
        videoView.requestFocus();
        videoView.start();

    }

    public void pauseVideo(View view) {
        videoView.pause();
    }

    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable() {
        public void run() {
            long currentPosition = videoView.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

        }
    };
}
