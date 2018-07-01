package com.example.jvhe.videoproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button btn_find_password, btn_login, btn_register;
    private static final int REQUEST_CODE_SIGN_UP = 12;


    // 비밀번호 보기 체크박스, 비밀번호 보기 텍스트뷰 선언
    CheckBox chk_show_password;
    TextInputEditText et_email, et_password;


    //버튼 처리를 담당하는 BtnOnClickListener 클래스 선언
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
            switch (view.getId()) {
                // 비밀번호 찾기 버튼
                case R.id.btn_find_password:
                    Toast.makeText(getApplicationContext(), "비밀번호 찾기 버튼 눌림!", Toast.LENGTH_SHORT).show();
                    //    startActivityForResult(new Intent(getApplicationContext(), FindPassword.class), 1);
                    break;
                // 로그인 버튼
                case R.id.btn_login:

                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    if (et_email.length() == 0) {
                        et_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                    } else if (et_password.length() == 0) {
                        et_password.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                    } else {
//                        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
//                        sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
//                        query = "SELECT * FROM user WHERE email = '" + et_email.getText().toString() + "' AND password = '" + et_password.getText().toString() + "'";
//                        cursor = sqLiteDatabase.rawQuery(query, null);
//
//                        // 로그인 성공
//                        if (cursor.moveToFirst()) {
//                            Intent data = new Intent();
//
//                            data.putExtra("user_id", cursor.getInt(0));
//                            data.putExtra("user_email", et_email.getText().toString());
//                            data.putExtra("user_nickname", cursor.getString(3));
//                            setResult(RESULT_OK, data);
//                            finish();
//                        }
//                        // 아이디 또는 비밀번호가 맞지 않을 경우
//                        else {
//                            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                        }

                        String data = "email=" + et_email.getText().toString() + "&password=" + et_password.getText().toString();
                        String url = "http://115.71.238.5/login_android.php";
                        HttpAsync httpasync = new HttpAsync(data, url, handler);
                        httpasync.execute();
                    }
                    break;
                // 회원가입 버튼
                case R.id.btn_register:
                    Toast.makeText(getApplicationContext(), "회원가입 버튼 눌림!", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getApplicationContext(), SignUpActivity.class), REQUEST_CODE_SIGN_UP);
                    break;
            }
        }
    }

    // 체크박스 처리를 담당하는 ChkOnClickListener 클래스 선언
    class ChkOnClickListener implements CheckBox.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
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
        setContentView(R.layout.activity_login);


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
        btn_find_password = (Button) findViewById(R.id.btn_find_password);
        btn_find_password.setOnClickListener(btnOnClickListener);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(btnOnClickListener);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(btnOnClickListener);

        // email, pw 입력란 레이아웃 매칭
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_password = (TextInputEditText) findViewById(R.id.et_password);

        // 체크박스와 텍스트뷰 매칭
        chk_show_password = (CheckBox) findViewById(R.id.chk_show_password);
        //  tv_show_password = (TextView) findViewById(R.id.tv_show_password);
        // 체크박스 리스너 설정
        chk_show_password.setOnClickListener(chkOnClickListener);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data");
               // Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(data);
                        SharedPreferences pref = getSharedPreferences("user", 0);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putBoolean("is_login", true);
                        edit.putInt("id_db", Integer.parseInt(obj.getString("id")));
                        edit.putString("email", obj.getString("email"));
                        edit.putString("name", obj.getString("name"));
                        edit.putString("password", obj.getString("password"));
                        edit.commit();

                        Toast.makeText(getApplicationContext(),"결과 "+data, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 회원가입으로부터 받아온 결과
            if (requestCode == REQUEST_CODE_SIGN_UP) {
                // 회원가입으로부터 로그인 요청을 받을 경우
                if (data.getBooleanExtra("flag_login", false)) {
                    et_email.setText(data.getStringExtra("user_email"));
                    et_password.setText(data.getStringExtra("user_pw"));
                    btn_login.callOnClick();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
