package com.example.jvhe.videoproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 테스트
    private static final String VIDEONOTE = "VideoNote";

    Button btn_login, btn_view;
    SharedPreferences sharedPreferences;
    TextView tv_email, tv_name;

    String text;

    // 리스트뷰 선언
    ListView video_listview;
    // 어댑터 선언
    VideoListViewAdapter videoListViewAdapter;
    // 재생 목록 선언
    ArrayList<VideoItem> play_list;

    Button btn_making_remote,btn_delete_remote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play_list = new ArrayList<>();
        play_list.add(new VideoItem(0, "이미지링크0", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목0", "테스트내용", 15, "테스트", "테스트작성자1", true));
        play_list.add(new VideoItem(0, "이미지링크1", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목1", "테스트내용", 15, "테스트", "테스트작성자1", true));
        play_list.add(new VideoItem(1, "이미지링크2", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목2", "테스트내용", 15, "테스트", "테스트작성자1", true));
        play_list.add(new VideoItem(2, "이미지링크3", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목3", "테스트내용", 15, "테스트", "테스트작성자1", true));
        play_list.add(new VideoItem(3, "이미지링크4", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목4", "테스트내용", 15, "테스트", "테스트작성자1", true));
        play_list.add(new VideoItem(4, "이미지링크5", Environment.getExternalStoragePublicDirectory(VIDEONOTE) + "/임창정 날닮은 너 유스케.mp4", "테스트제목5", "테스트내용", 15, "테스트", "테스트작성자1", true));


        videoListViewAdapter = new VideoListViewAdapter(this, play_list);

        video_listview = findViewById(R.id.video_listview);

        video_listview.setAdapter(videoListViewAdapter);
//        videoListViewAdapter.notifyDataSetChanged();


        sharedPreferences = getSharedPreferences("user", 0);

        tv_email = findViewById(R.id.tv_email);
        text = "이메일: " + sharedPreferences.getString("email", "로그아웃 상태");
        tv_email.setText(text);

        tv_name = findViewById(R.id.tv_name);
        text = "이름: " + sharedPreferences.getString("name", "로그아웃 상태");
        tv_name.setText(text);

        btn_view = findViewById(R.id.btn_view);
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExoPlayerActivity.class));
            }
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 된 상태에서 버튼을 누르면 로그아웃 처리를 한다.
                if (sharedPreferences.getBoolean("is_login", false)) {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean("is_login", false);
                    edit.putInt("id_db", -1);
                    edit.putString("email", null);
                    edit.putString("name", null);
                    edit.putString("password", null);
                    edit.apply();
                    text = "이메일: " + sharedPreferences.getString("email", "로그아웃 상태");
                    tv_email.setText(text);
                    text = "이름: " + sharedPreferences.getString("name", "로그아웃 상태");
                    tv_name.setText(text);
                    btn_login.setText("로그인");
                    Toast.makeText(getApplicationContext(), "로그아웃!", Toast.LENGTH_SHORT).show();
                }
                // 로그인 되어있지 않은 경우
                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });

        // 리모콘 생성 버튼 레이아웃과 매칭
        btn_making_remote = findViewById(R.id.btn_make_remote);
        // 리모콘 제거 버튼 레이아웃과 매칭
        btn_delete_remote = findViewById(R.id.btn_delete_remote);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // 로그인 되어 있는 경우 로그아웃 버튼이 나오게 한다.
        if (sharedPreferences.getBoolean("is_login", false)) {
            btn_login.setText("로그아웃");
        } else {
            btn_login.setText("로그인");
        }
    }

    // 서비스 생성 메소드
    public void startServiceMethod(View v) {
        Log.e("메인", "서비스 생성");
        startService(new Intent(MainActivity.this, RecordingService.class));
        Log.e("메인", "서비스 생성 실행 끝");
    }
    // 서비스 중지 메소드
    public void stopServiceMethod(View v) {
        Log.e("메인", "서비스 중지");
        stopService(new Intent(MainActivity.this, RecordingService.class));
    }

}
