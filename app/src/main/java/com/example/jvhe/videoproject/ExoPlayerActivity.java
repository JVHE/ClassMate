package com.example.jvhe.videoproject;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunk;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExoPlayerActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    SimpleExoPlayerView exo_view;
    SimpleExoPlayer player;
    Button startButton, stopButton;
    TextView tv_signal;

    LineChart chart, chart_bitrate;

    TextView tv_bitrate, tv_size;

    Button b1;

    int linkspeed;
    float newRssi;
    int level;
    int percentage;

    LineDataSet lineDataSet;
    LineData lineData;

    List<Entry> array;
    BandwidthMeter bandwidthMeter;
    DefaultBandwidthMeter defaultBandwidthMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

//        bandwidthMeter = new DefaultBandwidthMeter();
        defaultBandwidthMeter = new DefaultBandwidthMeter();
        DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
        TrackSelection.Factory track_factory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(track_factory);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "VideoProject"), defaultBandwidthMeter);


//        Uri uri = Uri.parse("http://aaq1212.vps.phps.kr/videos/encoding/cat_eats_octopus.mp4");
        Uri uri = Uri.parse("http://aaq1212.vps.phps.kr/videos/encoding/cat_eats_octopus_dash.mpd");
//        Uri uri = Uri.parse("http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/4sec/BigBuckBunny_4s_onDemand_2014_05_09.mpd");


        tv_size = findViewById(R.id.tv_size);

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                dataSourceFactory,
                defaultExtractorsFactory,
                null,
                null);


        AdaptiveMediaSourceEventListener adaptiveMediaSourceEventListener = new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {

            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
                if (trackFormat != null)
                    tv_size.setText("" + trackFormat.bitrate + " | " + trackFormat.width);
            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {

            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {

            }
        };




        DashMediaSource dashMediaSource = new DashMediaSource(uri,
                dataSourceFactory,
                new DefaultDashChunkSource.Factory(dataSourceFactory),
                null,
                null);


//        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));



        player = ExoPlayerFactory.newSimpleInstance(
                this,
                trackSelector);


        player.addListener(this);


//        player.prepare(mediaSource);
        player.prepare(dashMediaSource);
        Log.e("TES7T", "playing state : " + player.getPlaybackState());

        exo_view = findViewById(R.id.exo_view);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setPlayWhenReady(true);
            }
        });

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setPlayWhenReady(false);
            }
        });


        player.setVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                tv_size.setText("width: " + width + "\nheight: " + height);
            }

            @Override
            public void onRenderedFirstFrame() {
                tv_size.setText("start");
            }
        });


        // Bind the player to the view.
        exo_view.setPlayer(player);


//        player.release();

        tv_bitrate = findViewById(R.id.tv_bitrate);

        tv_signal = findViewById(R.id.tv_signal);


        b1 = findViewById(R.id.scan);
        b1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiMan.startScan();
                linkspeed = wifiMan.getConnectionInfo().getLinkSpeed();
                newRssi = wifiMan.getConnectionInfo().getRssi();
                level = wifiMan.calculateSignalLevel((int) newRssi, 10);
                percentage = (int) ((level / 10.0) * 100);
                String macAdd = wifiMan.getConnectionInfo().getBSSID();
                String text = "링크 스피드 : " + linkspeed + " / 신호 감도 : " + newRssi + "dbm" + " / 퍼센티지 : " + percentage + " / 맥어드레스 : " + macAdd;
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                tv_signal.setText(text);


//                array.remove(0);
                for (int i = 1; i < 11; i++) {
                    array.set(i - 1, array.get(i));
                    array.get(i).setX(i);
                }
                array.set(10, new Entry(10, newRssi / 100));

                lineDataSet.notifyDataSetChanged();
//                chart.animateY(2000, Easing.EasingOption.EaseInCubic);
                chart.invalidate();
            }
        });


        chart = findViewById(R.id.chart);
//        chart.setVisibleYRange(-30,-130, YAxis.AxisDependency.LEFT);


        array = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            array.add(new Entry(i, 0));
        }

        lineDataSet = new LineDataSet(array, "감도");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        lineData = new LineData(lineDataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("와이파이 신호");

        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(description);
        chart.animateY(2000, Easing.EasingOption.EaseInCubic);
        chart.invalidate();

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 120; i++) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String data;
                            long bitrate = defaultBandwidthMeter.getBitrateEstimate();
                            if (bitrate > 1024 * 1024)
                                data = "" + Long.toString(Math.round(bitrate * 100 / (1024 * 1024)) / 100) + "Mbps";
                            else
                                data = "" + Long.toString(Math.round(bitrate * 100 / 1024) / 100) + "Kbps";
                            b1.performClick();
                            tv_bitrate.setText("Rssi:" + newRssi + "dbm\nbitrate:" + data);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.i("TEST", "onLoadingChanged: " + isLoading + "");
        Log.i("TEST", "Buffered Position: " + player.getBufferedPosition() + "");
        Log.i("TEST", "Buffered Percentage: " + player.getBufferedPercentage() + "");

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == ExoPlayer.STATE_READY) {
            Log.i("TEST", "ExoPlayer State is: READY");
        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            Log.i("TEST", "ExoPlayer State is: BUFFERING");
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            Log.i("TEST", "ExoPlayer State is: ENDED");
        } else if (playbackState == ExoPlayer.STATE_IDLE) {
            Log.i("TEST", "ExoPlayer State is: IDLE");
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.setPlayWhenReady(false);
    }
}
