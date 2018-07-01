package com.example.jvhe.videoproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by 이정배 on 2018-05-16.
 * 리모콘 기능을 실행하는 서비스다.
 * 리모콘을 통해서 화면 녹화, 스크린 편집등을 할 수 있다.
 * 스크린 편집 기능은 스크린 위에 펜, 레이저 포인터, 형광펜, 지우개등의 조작을 할 수 있는 기능이다.
 * 이 외에도 포커스 오프, 올리기 화면으로 가기 등의 기능이 있다.
 *
 * 스크린 편집 원리는 자체적으로 생성한 투명한 뷰들 위에 그림을 그리는 것이다.
 */

public class RecordingService extends Service {

    private final static String TAG = "RecordingService";

    // 화면에 띄울 리모콘 뷰 변수
    // 리모콘 레이아웃 파일은 레이아웃 폴더에 선언되어 있다.
    View remote_controller;

    // 윈도우 매니져. 뷰를 화면 위에 띄울 수 있고, 매니져를 통해 뷰의 상태등을 관리할 수 있다.
    WindowManager windowManager;

    // 리모콘 뷰의 위치 계산을 위해 사용되는 변수들이다.
    private float mTouchX, mTouchY;     // 터치한 x, y좌표
    private int mViewX, mViewY;         // 현재 뷰가 띄워져 있는 x, y좌표


    // 동영상 녹화 상태인지  확인
    boolean is_recording = false;

    // 레이아웃 플래그 설정. SDK 레벨에 따라 다른 플래그를 가진다
    // 추가 공부 필요.
    // 플래그의 값은 onCreate()맨 처음에 선언된다.
    int LAYOUT_FLAG;
    // 레이아웃 인플레이터
    LayoutInflater inflater;

    // 축약어 btn: button
    Button btn_recording,   // 녹화하기 버튼
            btn_upload_activity;    // 동영상 업로드 액티비티로 이동하는 버튼

    // 축약어 ib: image button
    ImageButton ib_exit,   // 종료 버튼
            ib_pen,         // 선 그리기 기능 버튼
            ib_laser,       // 레이져 포인터 버튼
            ib_eraser,      // 지우개 버튼
            ib_clear,       // 전체 지우기 버튼
            ib_marker,     // 마커펜 버튼
            ib_focus_off,  // 포커스 off 버튼
            ib_minimize;    // 최소화 및 최대화 버튼


    // 리모콘이 최소화 또는 최대화 상태인지 확인하는 boolean 변수. true면 최소화 상태. false면 최대화 상태다.
    boolean is_minimized = false;

    // 투명한 그림판 클래스 변수. 설명은 클래스 선언 부분에서 할 것이다.
    TransparentDrawingBoard transparentDrawingBoard;

    // 그림판 화면 생성 메소드
    public void inflate(Context context) {
        transparentDrawingBoard = new TransparentDrawingBoard(context);

        // 투명 그림판에 사용할 레이아웃 매개변수 선언
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,    // width
                WindowManager.LayoutParams.MATCH_PARENT,    // height
                LAYOUT_FLAG,      // 타입 관련 설정
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // 포커스 관련 설정 공부필요
                PixelFormat.TRANSLUCENT);          // 픽셀 포멧 또한 공부 필요.


        // 윈도우 매니져 선언
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 투명 그림판변수와 레이아웃 매개변수를 이용하여 뷰를 만든다.
        windowManager.addView(transparentDrawingBoard, layoutParams);

        // 인플레이터 선언
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 인플레이터를 이용하여 리모콘 인플레이트
        remote_controller = inflater.inflate(R.layout.remote_controller, null);

        // 리모콘에 사용할 레이아웃 매개변수 선언
        layoutParams = new WindowManager.LayoutParams(
                400,
                700,
                LAYOUT_FLAG,      // 타입 관련 설정
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // 포커스 관련 설정
                PixelFormat.TRANSLUCENT);

        // 리모컨 뷰 만들기
        windowManager.addView(remote_controller, layoutParams);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "서비스 생성");

        // 레이아웃 플래그 설정. SDK 레벨에 따라 다른 플래그를 가진다. 추가 공부 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        Log.e(TAG, "인플레이트 메소드 시작");
        // 그림판 화면 생성 메소드 호출
        this.inflate(getApplicationContext());
        Log.e(TAG, "인플레이트 메소드 끝");

        // 녹화하기 버튼과 리모콘 뷰 매칭 및 반응설정
//        btn_recording = remote_controller.findViewById(R.id.btn_recording);
//        btn_recording.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!is_recording) {
//                    btn_recording.setText("녹화중지");
//                    Toast.makeText(getApplicationContext(), "중지 버튼 눌림", Toast.LENGTH_SHORT).show();
//                    is_recording = true;
//                    Upload3.clickRecord();
//                } else {
//                    btn_recording.setText("녹화하기");
//                    Toast.makeText(getApplicationContext(), "시작 버튼 눌림", Toast.LENGTH_SHORT).show();
//                    is_recording = false;
//                    Upload3.clickRecord();
//                }
//            }
//        });
        // 업로드 액티비티로 이동 버튼과 리모콘 뷰 매칭 및 반응 설정
//        btn_upload_activity = remote_controller.findViewById(R.id.btn_upload_activity);
//        btn_upload_activity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), Upload3.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });

        // 서비스 종료 버튼과 리모콘 뷰 매칭 및 반응 설정
        ib_exit = remote_controller.findViewById(R.id.ib_exit);
        ib_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(), RecordingService.class));
            }
        });

        // 최소, 최대화 버튼과 리모콘 뷰 매칭 및 반응 설정
        ib_minimize = remote_controller.findViewById(R.id.ib_minimize);
        ib_minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 애니메이션 선언. 현재 리모콘 뷰가 애니메이션 적용이 안돼서 다른 방법으로 애니메이션 효과를 주었다.
                // 원인은 아마 PixelFormat.TRANSLUCENT 속성 때문인 것 같은데 일단은 다른 방법으로 구현함.
                // 그런데 구현한 애니메이션 효과가 생각보다 좋지 않아 그냥 사용하지 않겠음.
                Animation animation;
                final WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) remote_controller.getLayoutParams();
                // 최소화 상태인 경우
                if (is_minimized) {

                    // 쓰레드를 이용하여 애니메이션 효과를 만들어 봤음. 효과가 생각보다 좋지 않아서 사용하지 않을 것이다.
//                    Thread thread = new Thread(new Runnable() {
//                        Handler handler = new Handler();
//                        @Override
//                        public void run() {
//                            ib_exit.setClickable(false);
//                            for (int i=125;i<950;i +=50) {
//                                layoutParams.height = i;
//                                layoutParams.y += 25;
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (remote_controller != null)
//                                        windowManager.updateViewLayout(remote_controller, layoutParams);
//                                    }
//                                });
//                                try {
//                                    Thread.sleep(30);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // 최대화 시 리모콘 크기 설정
//                                    layoutParams.height = 950;
////                                    layoutParams.y += 412.5;
//                                    if (remote_controller != null)
//                                        windowManager.updateViewLayout(remote_controller, layoutParams);
//                                }
//                            });
//                            ib_exit.setClickable(true);
//                        }
//                    });
//                    thread.start();

//                    // 최대화 시 리모콘 크기 설정
//                    layoutParams.height = 950;
//                    layoutParams.y += 412.5;
                    layoutParams.height = 700;
                    layoutParams.y += 295;
                    // 아이콘 모양을 최소화 버튼 모양으로 바꾼다
                    ib_minimize.setImageResource(R.drawable.minimize);
//                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    is_minimized = false;
                    Log.e(TAG, "maximize!");
                }
                // 화면이 최대화 상태인 경우
                else {
//                    Thread thread = new Thread(new Runnable() {
//                        Handler handler = new Handler();
//                        @Override
//                        public void run() {
//                            ib_exit.setClickable(false);
//                            for (int i=950;i>125;i -=50) {
//                                layoutParams.height = i;
//                                layoutParams.y -= 25;
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (remote_controller != null)
//                                        windowManager.updateViewLayout(remote_controller, layoutParams);
//                                    }
//                                });
//                                try {
//                                    Thread.sleep(30);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    layoutParams.height = 125;
////                                    layoutParams.y -= 412.5;
//                                    if (remote_controller != null)
//                                        windowManager.updateViewLayout(remote_controller, layoutParams);
//                                }
//                            });
//                            ib_exit.setClickable(true);
//                        }
//                    });
//                    thread.start();


//                    layoutParams.height = 125;
//                    layoutParams.y -= 412.5;
                    layoutParams.height = 110;
                    layoutParams.y -= 295;

                    ib_minimize.setImageResource(R.drawable.maximize);
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                    is_minimized = true;
                    Log.e(TAG, "minimize!");
                }

//                remote_controller.startAnimation(animation);

                windowManager.updateViewLayout(remote_controller, layoutParams);
            }
        });

        // 펜 기능 버튼 매칭 및 반응 설정
        ib_pen = remote_controller.findViewById(R.id.ib_pen);
        ib_pen.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.setToPen();
                Toast.makeText(getApplicationContext(), "Pen", Toast.LENGTH_SHORT).show();
            }
        });

        // 레이저 포인터 버튼 매칭 및 반응 설정
        ib_laser = remote_controller.findViewById(R.id.ib_laser);
        ib_laser.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.setToLaserPointer();
                Toast.makeText(getApplicationContext(), "Laser Pointer", Toast.LENGTH_SHORT).show();
            }
        });

        // 마커 버튼 매칭 및 반응 설정
        ib_marker = remote_controller.findViewById(R.id.ib_marker);
        ib_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.setToMarker();
                Toast.makeText(getApplicationContext(), "Marker", Toast.LENGTH_SHORT).show();
            }
        });

        // 지우개 버튼 매칭 및 반응 설정
        ib_eraser = remote_controller.findViewById(R.id.ib_eraser);
        ib_eraser.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.setToEraser();
                Toast.makeText(getApplicationContext(), "Eraser", Toast.LENGTH_SHORT).show();
            }
        });

        // 클리어 버튼 매칭 및 반응 설정
        ib_clear = remote_controller.findViewById(R.id.ib_clear);
        ib_clear.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.onClear();
                Toast.makeText(getApplicationContext(), "Clear", Toast.LENGTH_SHORT).show();
            }
        });

        // 포커스 오프 버튼 매칭 및 반응 설정
        ib_focus_off = remote_controller.findViewById(R.id.ib_focus_off);
        ib_focus_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transparentDrawingBoard.focusOff();
                Toast.makeText(getApplicationContext(), "Focus off", Toast.LENGTH_SHORT).show();
            }
        });

        // 리모콘을 눌러서 위치를 옮기기 위한 메소드
        remote_controller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) remote_controller.getLayoutParams();


                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        mTouchX = motionEvent.getRawX();
                        mTouchY = motionEvent.getRawY();

                        mViewX = layoutParams.x;
                        mViewY = layoutParams.y;

                        break;

                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int x = (int) (motionEvent.getRawX() - mTouchX);
                        int y = (int) (motionEvent.getRawY() - mTouchY);

                        layoutParams.x = mViewX + x;
                        layoutParams.y = mViewY + y;

                        windowManager.updateViewLayout(remote_controller, layoutParams);

                        break;
                }

                return true;

            }
        });

        ib_focus_off.performClick();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null) {
            if (remote_controller != null)
                windowManager.removeViewImmediate(remote_controller);
            if (transparentDrawingBoard != null)
                windowManager.removeViewImmediate(transparentDrawingBoard);
        }
    }


    // 투명 뷰를 이용하여 화면에 그림을 그려볼 생각. 화면 캡쳐 기능도 넣어보자.
    public class TransparentDrawingBoard extends View {
        Paint paint = new Paint();  // 펜 페인트
        Paint remove_paint = new Paint(); // 지우개용 페인트
        Paint marker_paint = new Paint();   // 마커펜용 페인트
        Paint laser_paint = new Paint();    // 레이저 포인터용 페인트

        // 포커스가 되어 있는지 확인하는 boolean 변수
        boolean is_focused = false;

        Path path = new Path();  // 이동 경로 저장 객체

        PorterDuffXfermode clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        Bitmap mBitmap1;
        Canvas mCanvas1;

        // 형광펜 그림용 캔버스
        Bitmap mBitmap2;
        Canvas mCanvas2;

        // 레이저 포인터용 캔버스
        Bitmap mBitmap3;
        Canvas mCanvas3;


        Paint mBitmapPaint;

        // 현재 그림판 모드 확인 변수
        String mode = "";

        // constructor
        public TransparentDrawingBoard(Context context) {
            super(context);
            // 펜 페인트 설정
            paint.setStyle(Paint.Style.STROKE); // 선이 그려질 수 있게 한다.
            paint.setStrokeWidth(10f);          // 선의 굵기 지정

            // 지우개 페인트 설정
            remove_paint.setStyle(Paint.Style.STROKE);
            remove_paint.setStrokeWidth(50f);
            remove_paint.setXfermode(clear);

            // 마커펜 페인트 설정
            marker_paint.setStyle(Paint.Style.STROKE);
            marker_paint.setStrokeWidth(40f);
            marker_paint.setColor(0X00DAB6);
            marker_paint.setAlpha(0X05);

            // 레이저 포인터 페인트 설정
            laser_paint.setStyle(Paint.Style.STROKE);
            laser_paint.setStrokeWidth(10f);
            laser_paint.setColor(0xFFD50000);

        }

        // constructor
        public TransparentDrawingBoard(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            // 펜 페인트 설정
            paint.setStyle(Paint.Style.STROKE); // 선이 그려질 수 있게 한다.
            paint.setStrokeWidth(10f);          // 선의 굵기 지정

            // 지우개 페인트 설정
            remove_paint.setStyle(Paint.Style.STROKE);
            remove_paint.setStrokeWidth(50f);
            remove_paint.setXfermode(clear);

            // 마커펜 페인트 설정
            marker_paint.setStyle(Paint.Style.STROKE);
            marker_paint.setStrokeWidth(40f);
            marker_paint.setColor(0X00DAB6);
            marker_paint.setAlpha(0X05);

            // 레이저 포인터 페인트 설정
            laser_paint.setStyle(Paint.Style.STROKE);
            laser_paint.setStrokeWidth(10f);
            laser_paint.setColor(0xFFD50000);

        }

        // constructor
        public TransparentDrawingBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            // 펜 페인트 설정
            paint.setStyle(Paint.Style.STROKE); // 선이 그려질 수 있게 한다.
            paint.setStrokeWidth(10f);          // 선의 굵기 지정

            // 지우개 페인트 설정
            remove_paint.setStyle(Paint.Style.STROKE);
            remove_paint.setStrokeWidth(50f);
            remove_paint.setXfermode(clear);

            // 마커펜 페인트 설정
            marker_paint.setStyle(Paint.Style.STROKE);
            marker_paint.setStrokeWidth(40f);
            marker_paint.setColor(0X00DAB6);
            marker_paint.setAlpha(0X05);

            // 레이저 포인터 페인트 설정
            laser_paint.setStyle(Paint.Style.STROKE);
            laser_paint.setStrokeWidth(10f);
            laser_paint.setColor(0xFFD50000);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(mBitmap1, 0, 0, mBitmapPaint);
            canvas.drawBitmap(mBitmap2, 0, 0, mBitmapPaint);
            canvas.drawBitmap(mBitmap3, 0, 0, mBitmapPaint);
        }


        public void setToPen() {
            if (!is_focused) {
                focusOn();
            }
            mode = "Pen";
        }

        public void setToEraser() {
            if (!is_focused) {
                focusOn();
            }
            mode = "Eraser";
        }
        public void setToLaserPointer() {
            if (!is_focused) {
                focusOn();
            }
            mode = "LaserPointer";
        }

        public void setToMarker() {
            if (!is_focused) {
                focusOn();
            }
            mode = "Marker";
        }

        public void onClear() {
            mCanvas1.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas2.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas3.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }

        @Override

        protected void onSizeChanged(int w, int h, int oldw, int oldh) {

            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap1 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap3 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            mCanvas1 = new Canvas(mBitmap1);
            mCanvas2 = new Canvas(mBitmap2);
            mCanvas3 = new Canvas(mBitmap3);

        }

        // 터치 이벤트를 이용하여 선을 그리는 작업을 한다.
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event != null) {
                float x = event.getX();
                float y = event.getY();

                switch (mode) {
                    case "Marker":
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
                                break;

                            case MotionEvent.ACTION_MOVE:
                                path.lineTo(x, y); // 자취에 선을 그려라
                                mCanvas2.drawPath(path, marker_paint);
                                break;
                            case MotionEvent.ACTION_UP:
                                mCanvas2.drawPath(path, marker_paint);

                                path.reset();
                                break;
                        }
                        break;
                    case "Pen":
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
//                            return true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                path.lineTo(x, y); // 자취에 선을 그려라
                                mCanvas1.drawPath(path, paint);
                                break;
                            case MotionEvent.ACTION_UP:
                                mCanvas1.drawPath(path, paint);

                                path.reset();
                                break;
                        }
                        break;
                    case "LaserPointer":
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
//                            return true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                path.lineTo(x, y); // 자취에 선을 그려라
                                mCanvas3.drawPath(path, laser_paint);
                                break;
                            case MotionEvent.ACTION_UP:
                                mCanvas3.drawPath(path, laser_paint
                                );

                                // 쓰레드를 이용하여 그려진 이후 일정 시간 후에 그려진 흔적을 사라지게 하는 코드
//                                    final Path temp_path = new Path(path);
//                                    final Handler handler = new Handler();
//                                    Thread thread = new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                 // 일정 시간 이후 사라지게 할 수 있다.
//                                                Thread.sleep(1500);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            handler.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    mCanvas3.drawPath(temp_path, remove_paint);
//                                                    invalidate();
//                                                }
//                                            });
//                                        }
//                                    });
//                                    thread.setDaemon(true);
//                                    thread.start();
                                mCanvas3.drawPath(path, remove_paint);

                                path.reset();
                                break;
                        }
                        break;
                    case "Eraser":
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
//                            return true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                path.lineTo(x, y); // 자취에 선을 그려라
                                mCanvas1.drawPath(path, remove_paint);
                                mCanvas2.drawPath(path, remove_paint);
                                break;
                            case MotionEvent.ACTION_UP:
                                mCanvas1.drawPath(path, remove_paint);
                                mCanvas2.drawPath(path, remove_paint);
                                path.reset();
                                break;
                        }
                        break;
                }

                invalidate(); // 화면을 다시그려라
            }
            return true;
        }

        // 그림판의 포커스를 다시 줘서 화면을 편집할 수 있게 만드는 메소드.
        public void focusOn() {
            if (!is_focused) {
                // 레이아웃 변수 불러온 뒤 플래그 설정을 변경시켜 준다.
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

                // 투명 그림판 레이아웃 설정 업데이트
                windowManager.updateViewLayout(this, layoutParams);
                is_focused = true;
            }
        }

        // 그림판의 포커스를 다시 줘서 화면을 편집할 수 있게 만드는 메소드.
        public void focusOff() {
            if (is_focused) {
                // 레이아웃 변수 불러온 뒤 플래그 설정을 변경시켜 준다.
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

                // 투명 그림판 레이아웃 설정 업데이트
                windowManager.updateViewLayout(this, layoutParams);
                is_focused = false;
            }
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
