package com.example.jvhe.videoproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;

/**
 * Created by 이정배 on 2018-04-20.
 */

public class VideoListViewAdapter extends BaseAdapter {

    private static final int REQUEST_CODE_EDIT = 21;
    private static final String VIDEONOTE = "VideoNote";

    private static final String TAG = "VideoListViewAdapter";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // 인플레이터 선언
    LayoutInflater inflater;
    // 비디오 아이템 어레이 리스트 선언
    ArrayList<VideoItem> data;
    // 컨텍스트
    Context context;

    // 제목
    TextView title, play_time;

    // 옵션 버튼
    ImageButton ib_option;

    // 액티비티 이름 집어넣어서 이 리스트뷰가 어떤 액티비티에서 보여지는지 확인한다.
//    String activity_name;

    // 메인 리스트뷰 슬림 옵션 확인용
    boolean option_narrow;

    // 썸네일 이미지 비트맵 파일
    // 임시로 썸네일을 가져온다. 나중에 데이터베이스로 이미지와 이미지 링크를 저장하면 그땐 썸네일 이미지를 링크로 가져올 것이다.
    Bitmap thumbnail_bitmap;
    // 썸네일 이미지뷰
    ImageView thumbnail;

    // 정보 뷰
    TextView tv_information;
    // 정보 문자열
    String information;

    // 재생 시간

    AsyncTask asyncTask;

    // 이미지 저장용 SparseArray
    SparseArray<Bitmap> sparseArray_image;
    // AsyncTask 저장용 SparseArray
    SparseArray<AsyncTask> sparseArray_async;


    // 리스트뷰 어댑터 생성자
    VideoListViewAdapter(Context context, ArrayList<VideoItem> data) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.sparseArray_image = new SparseArray<>();
        this.sparseArray_async = new SparseArray<>();
    }


//    // 리스트뷰 어댑터 생성자
//    VideoListViewAdapter(Context context, ArrayList<VideoItem> data, String activity_name) {
//        this.context = context;
//        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.data = data;
//        this.activity_name = activity_name;
//        this.sparseArray_image = new SparseArray<>();
//        this.sparseArray_async = new SparseArray<>();
//    }


//    final static class MyClass {
//        static int pos;
//    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

//        MyClass.pos = i;
        Log.e("어댑터 겟뷰", ""+i);
        //포지션 값 받아옴.
        final int position = i;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.video_item, null);
        }

        // 제목을 뷰의 레이아웃과 연결한다.
        title = ViewHolder.get(view, R.id.title);
        // 제목 설정
        title.setText(data.get(position).getTitle());


//        title.setText("현재 포지션: " + i + " position: " + position);

        // 재생 시간을 뷰의 레이아웃과 연결한다.
        play_time = ViewHolder.get(view, R.id.play_time);
        // 파일이 가지고 있는 재생 시간을 변환해 준다.
        play_time.setText(timeToString(data.get(position).getPlay_time()));

        // 작성자 및 재생시간 설정
        tv_information = ViewHolder.get(view, R.id.tv_information);

        information = data.get(position).getWriter() + " · " + data.get(i).getDate();

        tv_information.setText(information);


        // 옵션 버튼을 뷰의 레이아웃과 연결한다.
        ib_option = ViewHolder.get(view, R.id.ib_option);
        // 버튼 반응 처리
        ib_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Toast.makeText(context, "버튼눌림!", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(context, view);

                // 로그인한 유저가 본인인지 아닌지 확인
                sharedPreferences = context.getSharedPreferences("current_user", Context.MODE_PRIVATE);
                // 유저와 같을 경우
                if (sharedPreferences.getString("user_nickname", "").equals(data.get(position).getWriter())) {

                    ((Activity) context).getMenuInflater().inflate(R.menu.video_option, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.edit:
                                    Toast.makeText(context, "편집하기 버튼 눌림", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(view.getContext(), Upload3.class);
//                                    intent.putExtra("video_item", data.get(position));
//                                    intent.putExtra("video_id", data.get(position).getVideo_id());
//                                    intent.putExtra("position", position);
//                                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EDIT);
                                    break;
                                case R.id.delete:
                                    Toast.makeText(context, "삭제하기 버튼 눌림", Toast.LENGTH_SHORT).show();
//                                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(view.getContext());
//                                    alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("네",
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    // 'yes'
//                                                    // 데이터베이스에 삭제
//                                                    MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context);
//                                                    SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
//                                                    String query = "DELETE FROM videoitem WHERE id = " + data.get(position).getVideo_id();
//                                                    sqLiteDatabase.execSQL(query);
//                                                    data.remove(position);
//                                                    notifyDataSetChanged();
//                                                }
//                                            }).setNegativeButton("아니오",
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    // 'no'
//                                                    return;
//                                                }
//                                            });
//                                    AlertDialog alert = alert_confirm.create();
//                                    alert.show();
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                // 작성자와 시청자가 다를 경우
                else {
                    ((Activity) context).getMenuInflater().inflate(R.menu.video_option_other, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                // 관심 없음
                                case R.id.not_interested:
                                    Toast.makeText(context, "관심없음 버튼 눌림", Toast.LENGTH_SHORT).show();
//                                    data.remove(position);
//                                    notifyDataSetChanged();
                                    break;
                                // 신고
                                case R.id.report:
                                    final int[] selected_item = {0};
                                    final String[] items = new String[]{"성적인 콘텐츠", "폭력적 또는 혐오스러운 콘텐츠", "증오 또는 악의적인 콘텐츠", "유해하거나 위험한 행위", "스팸 또는 사용자를 현혹하는 콘텐츠"};
                                    AlertDialog.Builder alert_report = new AlertDialog.Builder(view.getContext());
                                    alert_report.setTitle("이미지 또는 제목 신고")
                                            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    selected_item[0] = i;
                                                }
                                            })
                                            .setCancelable(false)
                                            .setPositiveButton("신고",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            // 'yes'
                                                            Toast.makeText(context, "신고 완료. 사유: " + items[selected_item[0]], Toast.LENGTH_SHORT).show();
                                                            // 신고에 따른 처리 필요
//                                                            data.remove(position);
//                                                            notifyDataSetChanged();
                                                        }
                                                    }).setNegativeButton("취소",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // 'no'
                                                }
                                            });
                                    alert_report.create().show();

                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            }
        });

        // 뷰 클릭 반응 처리
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // 작동중인 AsyncTask 전부 정리
//                for (int i = 0; i < sparseArray_async.size(); i++) {
//                    int key = sparseArray_async.keyAt(i);
//                    if (!sparseArray_async.get(key).isCancelled()) {
//                        sparseArray_async.get(key).cancel(true);
//                    }
//                }

                Toast.makeText(context, "뷰눌림! " + position, Toast.LENGTH_SHORT).show();

                context.startActivity(new Intent(context, ExoPlayerActivity.class));

                // 눌림 처리 필요. 밑에 주석은 나중에 관리하자.
//                if (activity_name.equals("MainActivity")) {
//                    Toast.makeText(context, "뷰눌림! " + position, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, ShowActivity.class);
//                    intent.putExtra("video_item", data.get(position));
//                    ArrayList<VideoItem> arr = new ArrayList<>();
//                    for (int i = position + 1; i < data.size(); i++) {
//                        arr.add(data.get(i));
//                    }
//                    intent.putExtra("arr", (Serializable) arr);
//                    context.startActivity(intent);
//                } else if (activity_name.equals("ShowActivity")) {
//                    ((ShowActivity) context).itemClick(data.get(position));
//                }
            }
        });


//        final Handler handler = new Handler();
//        final View finalView = view;

        // 썸네일 이미지 gif 적용
        thumbnail = ViewHolder.get(view, R.id.thumbnail);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(thumbnail);
        Glide.with(context).load(R.drawable.loading).into(gifImage);

        //썸네일 이미지에 관한 처리. 나중에 데이터베이스로 이미지와 이미지 링크를 저장하면 그땐 썸네일 이미지를 링크로 가져올 것이다.
//        if (sparseArray_image.get(i) != null) {
//            thumbnail.setImageBitmap(sparseArray_image.get(i));
//        } else if (sparseArray_async.get(i) == null) {
//
//            asyncTask = new MyAsyncTask(position);
//            sparseArray_async.append(i, asyncTask);
//            asyncTask.execute();
//        } else {
//            thumbnail.setImageResource(R.drawable.loading);
//        }


//        // 임시로 썸네일을 가져온다. 나중에 데이터베이스로 이미지와 이미지 링크를 저장하면 그땐 썸네일 이미지를 링크로 가져올 것이다.
//        // 썸네일 이미지 만들기
//        thumbnail_bitmap = ThumbnailUtils.createVideoThumbnail(data.get(position).getLink_video(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
////        // 썸네일 이미지뷰 레이아웃과 매칭
////        thumbnail = view.findViewById(R.id.thumbnail);
//        thumbnail = ViewHolder.get(view, R.id.thumbnail);
//        thumbnail.setImageBitmap(thumbnail_bitmap);


        return view;
    }

    // 주어진 초를 스트링 형의 시간 단위로 변환해 주는 메소드
    // 15 -> 00:15
    // 417 -> 06:57
    // 604 -> 10:04
    // 3863 -> 1:04:23
    public String timeToString(int time) {
        String string_time = "";

        // 1시간이 넘을 경우 시간 단위와 ":"를 붙인다.
        if (time / 3600 != 0) {
            string_time = Integer.toString(time / 3600) + ":";
            time = time % 3600;
        }

        // 분단위 계산
        // 10분~59분까지
        if (time / 60 >= 10) {
            string_time = string_time + Integer.toString(time / 60);
        }
        // 1분~9분까지
        else if (time / 60 > 0) {
            string_time = string_time + "0" + Integer.toString(time / 60);
        }
        // 0분일 경우
        else if (time / 60 == 0) {
            string_time = string_time + "00";
        }
        // 의도하지 않은 경우
        else {
            Log.e("시간 변환", "초 -> 분 계산이 잘못되었습니다.");
            return "계산오류";
        }

        string_time = string_time + ":";
        time = time % 60;

        // 초단위 계산
        // 10초 ~ 59초까지
        if (time >= 10) {
            string_time = string_time + Integer.toString(time);
        }
        // 1초 ~ 9초까지
        else if (time > 0) {
            string_time = string_time + "0" + Integer.toString(time);
        }
        // 0초일 경우
        else if (time == 0) {
            string_time = string_time + "00";
        }
        // 의도하지 않은 경우
        else {
            Log.e("시간 변환", "초 계산이 잘못되었습니다.");
            return "계산오류";
        }

        return string_time;
    }

    // 썸네일 빠르게 가져오기 위한 AsyncTask 이제 쓸모 없을 것이다.
    public class MyAsyncTask extends AsyncTask {

        int position;

        public MyAsyncTask(int position) {
            this.position = position;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            // 임시로 썸네일을 가져온다. 나중에 데이터베이스로 이미지와 이미지 링크를 저장하면 그땐 썸네일 이미지를 링크로 가져올 것이다.
            // 썸네일 이미지 만들기
            thumbnail_bitmap = ThumbnailUtils.createVideoThumbnail(data.get(position).getLink_video(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);

            sparseArray_image.append(position, thumbnail_bitmap);
//        // 썸네일 이미지뷰 레이아웃과 매칭
//        thumbnail = view.findViewById(R.id.thumbnail);
            return thumbnail_bitmap;
        }

        @Override
        protected void onPostExecute(Object o) {
//                    if (position == MyClass.pos) {
//                        thumbnail.setImageBitmap((Bitmap) o);
//                    ((ImageView)ViewHolder.get(finalView,R.id.thumbnail)).setImageBitmap((Bitmap)o);
//                    }
//                    title.setText("현재 포지션!: " + MyClass.pos + " position: " + position);
            //   notifyDataSetChanged();
            notifyDataSetChanged();
            Log.e(TAG, "" + data.size());
        }

    }

}
