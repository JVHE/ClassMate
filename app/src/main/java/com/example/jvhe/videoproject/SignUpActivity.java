package com.example.jvhe.videoproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    // 이메일 중복체크, 닉네임 중복체크 여부 확인 변수
    boolean flag_duplication_check_email, flag_duplication_check_name;


    // 버튼 변수 선언
    // btn_duplication_check_email: 이메일 중복 체크 버튼
    // btn_duplication_check_nickname: 닉네임 중복 체크 버튼
    // btn_register: 회원가입 버튼
    Button btn_duplication_check_email, btn_duplication_check_name, btn_register;

    // 텍스트 입력칸 변수 선언
    // 단축어 설명 - et: edit text
    // et_email: email id 입력란
    // et_password: password 입력란
    // et_nickname: nickname 입력란
    TextInputEditText et_email, et_password, et_name;

    // 비밀번호 보기 체크박스, 비밀번호 보기 텍스트뷰 선언
    CheckBox chk_show_password;
    TextView tv_show_password;

    // 이메일과 닉네임을 감싸는 레이아웃
    LinearLayout layout_nickname, layout_email;

    // 알림창 생성 클래스와 알림창 클래스. 텍스트 입력 도중 어떤 사유로 restart 되었을 때 알림을 통해서 이전에 작성한 내용을 이어서 작성할지 말지 물어본다.
    AlertDialog.Builder alert_builder;
 //    버튼 처리를 담당하는 BtnOnClickListener 클래스 선언
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
            String data;
            String url = "http://115.71.238.5/sign_up_android.php";
            HttpAsync httpasync;
            switch (view.getId()) {
                // 이메일 중복 체크
                case R.id.btn_duplication_check_email:
                    data = "email=" + et_email.getText().toString() + "&type=duplication_check_email";
                    httpasync = new HttpAsync(data, url, handler);
                    httpasync.execute();
                    break;
                // 이름 중복 체크
                case R.id.btn_duplication_check_name:
                    data = "name=" + et_name.getText().toString() + "&type=duplication_check_name";
                    url = "http://115.71.238.5/sign_up_android.php";
                    httpasync = new HttpAsync(data, url, handler);
                    httpasync.execute();
                    break;
                // 회원 가입 버튼
                case R.id.btn_register:
                    // 입력 조건 확인
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    if (et_email.length() == 0) {
                        et_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    else if (!flag_duplication_check_email) {
                        btn_duplication_check_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디 중복체크를 해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    else if (et_password.length() == 0) {
                        et_password.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (et_name.length() == 0) {
                        et_name.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    else if (!flag_duplication_check_name) {
                        btn_duplication_check_name.startAnimation(shake                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         );
                        Toast.makeText(getApplicationContext(), "닉네임 중복체크를 해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"가입 조건 성공 "+et_email.getText().toString()+et_password.getText().toString()+  et_name.getText().toString(), Toast.LENGTH_SHORT).show();
                        data = "email=" + et_email.getText().toString()+"&password=" + et_password.getText().toString() +"&name=" + et_name.getText().toString() + "&type=register";
                        url = "http://115.71.238.5/sign_up_android.php";
                        httpasync = new HttpAsync(data, url, handler);
                        httpasync.execute();
                    }

//                    else {
//
//                        // DB에 INSERT 할 일 추가
//                        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
//                        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
//                        query = "INSERT INTO user (email, password, nickname) VALUES ('" + et_email.getText().toString() + "', '" + et_password.getText().toString() + "', '" + et_name.getText().toString() + "')";
//                        sqLiteDatabase.execSQL(query);
//
//                        query = "SELECT * FROM user WHERE email = '" + et_email.getText() + "'";
//                        cursor = sqLiteDatabase.rawQuery(query, null);
//                        cursor.moveToFirst();
//
//                        Log.e("SQL쿼리 관련", cursor.getString(0) + " / " + cursor.getString(1) + " / " + cursor.getString(2));
//
//                        Toast.makeText(getApplicationContext(), cursor.getInt(0) + " / " + cursor.getString(1) + " / " + cursor.getString(2) + " / " + cursor.getString(3), Toast.LENGTH_SHORT).show();
//
//
//
////                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
//                        alert_builder.setTitle("회원가입 완료!")
//                                .setMessage("회원 가입이 완료되었습니다. 가입된 아이디와 비밀번호로 로그인 하시겠습니까?")
//                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Intent data = new Intent();
//                                        data.putExtra("flag_login", true);
//                                        data.putExtra("user_id", cursor.getInt(0));
//                                        data.putExtra("user_email", et_email.getText().toString());
//                                        data.putExtra("user_pw", et_password.getText().toString());
//                                        data.putExtra("user_nickname", et_name.getText().toString());
//                                        setResult(RESULT_OK, data);
//                                        finish();
//                                    }
//                                })
//                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Intent data = new Intent();
//                                        data.putExtra("flag_login", false);
//                                        setResult(RESULT_OK, data);
//                                        finish();
//                                    }
//                                });
//                        alert_builder.create().show();
//                    }
                    break;
            }
        }
    }

    // 체크박스 처리를 담당하는 ChkOnClickListener 클래스 선언
    class ChkOnClickListener implements CheckBox.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // 비밀번호 보기 체크박스
                case R.id.chk_show_password:
                    // 체크박스가 체크되어 있을 경우 입력한 비밀번호를 볼 수 있다.
                    if (((CheckBox) view).isChecked()) {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    // 체크박스가 체크되어 있지 않을 경우 패스워드 입력칸의 비밀번호를 볼 수 없다.
                    else {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // BtnOnClickListener 객체 생성
        BtnOnClickListener btnOnClickListener = new BtnOnClickListener();
        // ChkOnClickListener 객체 생성
        ChkOnClickListener chkOnClickListener = new ChkOnClickListener();

        // 버튼들과 레이아웃 매칭 그리고 리스너 설정
        btn_duplication_check_email = (Button) findViewById(R.id.btn_duplication_check_email);
        btn_duplication_check_email.setOnClickListener(btnOnClickListener);
        btn_duplication_check_name = (Button) findViewById(R.id.btn_duplication_check_name);
        btn_duplication_check_name.setOnClickListener(btnOnClickListener);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(btnOnClickListener);

        // email, password, nickname 입력란 레이아웃 매칭
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_password = (TextInputEditText) findViewById(R.id.et_password);
        et_name = (TextInputEditText) findViewById(R.id.et_name);


        // 체크박스와 텍스트뷰 매칭
        chk_show_password = (CheckBox) findViewById(R.id.chk_show_password);
        tv_show_password = (TextView) findViewById(R.id.tv_show_password);
        // 체크박스 리스너 설정
        chk_show_password.setOnClickListener(chkOnClickListener);

        // 이메일 중복체크, 닉네임 중복체크 초기화
        flag_duplication_check_email = false;
        flag_duplication_check_name = false;

        // 이메일과 닉네임 감싸는 레이아웃 매칭
        layout_email = findViewById(R.id.layout_email);
        layout_nickname = findViewById(R.id.layout_name);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 이메일과 패스워드, 닉네임 입력창에 내용이 하나라도 있을 경우 이어서 작성할 것인지 묻는 알림창을 띄우게 된다.
        if (!(et_email == null && et_password == null && et_name == null)) {
            // 다른 액티비티에 있다 올 경우 입력중이던 정보를 계속 입력할지 확인해 주는 알림
            alert_builder = new AlertDialog.Builder(SignUpActivity.this);
            alert_builder.setTitle("작성중이던 정보가 있습니다. 불러오시겠습니까?");
            // 예일 경우 이전에 기입한 내용 그대로 진행한다.
            alert_builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            // 아니오일 경우 작성중이던 내용을 모두 지운다.
            alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    layout_email.setBackgroundColor(0xffffff);
                    et_email.setText("");
                    et_email.setEnabled(true);
                    btn_duplication_check_email.setEnabled(true);
                    et_password.setText("");
                    layout_nickname.setBackgroundColor(0xffffff);
                    et_name.setText("");
                    et_name.setEnabled(true);
                    btn_duplication_check_name.setEnabled(true);
                    flag_duplication_check_email = false;
                    flag_duplication_check_name = false;
                }
            });
            alert_builder.create().show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data");
                // 이메일 중복체크 반응
                if (data.contains("!!duplication_check_email!!")) {
                    if (data.contains("impossible")) {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this).
                                setMessage("이메일이 중복되었습니다. 다른 이메일을 입력해 주세요.").setPositiveButton("확인", null);
                        alert_builder.create().show();
                    } else {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                        alert_builder.setMessage("사용 가능한 이메일 주소입니다. 이 주소를 사용하시겠습니까?\n* 확인을 누르면 더 이상 변경할 수 없습니다.");
                        alert_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                layout_email.setBackgroundColor(0x6b70f3ff);
                                et_email.setEnabled(false);
                                flag_duplication_check_email = true;
                                btn_duplication_check_email.setEnabled(false);
                            }
                        });
                        alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        alert_builder.create().show();
                    }
                }
                // 닉네임 중복체크 반응
                else if (data.contains("!!duplication_check_name!!")) {
                    if (data.contains("impossible")) {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this).
                                setMessage("이름이 중복되었습니다. 다른 이메일을 입력해 주세요.").setPositiveButton("확인", null);
                        alert_builder.create().show();
                    } else {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                        alert_builder.setMessage("사용 가능한 이름입니다. 이 이름을 사용하시겠습니까?\n* 확인을 누르면 더 이상 변경할 수 없습니다.");
                        alert_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                layout_nickname.setBackgroundColor(0x6b70f3ff);
                                et_name.setEnabled(false);
                                flag_duplication_check_name = true;
                                btn_duplication_check_name.setEnabled(false);
                            }
                        });
                        alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        alert_builder.create().show();
                    }
                }
                // 회원가입 실행
                else if(data.contains("!!register!!")) {
                    if(data.contains("success")) {
                        Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                        alert_builder.setTitle("회원가입 완료!")
                                .setMessage("회원 가입이 완료되었습니다. 가입된 아이디와 비밀번호로 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent data = new Intent();
                                        data.putExtra("flag_login", true);
                                        data.putExtra("user_email", et_email.getText().toString());
                                        data.putExtra("user_pw", et_password.getText().toString());
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent data = new Intent();
                                        data.putExtra("flag_login", false);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                });
                        alert_builder.create().show();
                    } else {

                    }
                }



//
//                String data = msg.getData().getString("data");
//                Log.e("!!!!!!!!!!!!!", data);
//                if (data.contains("!!fail!!")) {
////                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data);
//                        SharedPreferences pref = getSharedPreferences("user", 0);
//                        SharedPreferences.Editor edit = pref.edit();
//                        edit.putString("id_db", obj.getString("id"));
//                        edit.putString("email", obj.getString("email"));
//                        edit.putString("name", obj.getString("name"));
//                        edit.putString("password", obj.getString("password"));
//                        edit.commit();
//
////                        deleteDatabase(MySQLiteOpenHelper.DBFILE_NAME);
////                        MySQLiteOpenHelper mydbHelper = new MySQLiteOpenHelper(getApplicationContext());
////                        mydbHelper.syncRecord( obj.getString("primarykey") );
//
////                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        Toast.makeText(getApplicationContext(),"결과 "+data,Toast.LENGTH_SHORT).show();
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }



            }
        }
    };

}
